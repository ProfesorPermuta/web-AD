---
title: 'Mappers con MapStruct'
description: Por qué separar DTO y entidad obliga a convertir entre ambos, cómo MapStruct genera ese código en compilación y cómo integrarlo con Spring y Lombok.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Mappers con MapStruct

En [Controladores REST y validación de
entrada](/tema-0/g-controlador-rest-validacion) separaste el **DTO** de la
**entidad de dominio**. Esa separación tiene un coste: alguien tiene que
**convertir** de un tipo a otro en cada operación. Un **mapper** es la clase
cuya única responsabilidad es esa conversión. **MapStruct** es la librería
que te la escribe.

## El problema: la conversión manual

Sin ayuda, la conversión se escribe a mano. Ya viste un ejemplo en el
controlador del apartado g (`LibroResponse.desde(libro)`):

```java
// dto/LibroResponse.java
public record LibroResponse(UUID id, String isbn, String titulo,
                            String autor, int ejemplaresDisponibles) {

    public static LibroResponse desde(Libro libro) {
        return new LibroResponse(
                libro.getId(),
                libro.getIsbn(),
                libro.getTitulo(),
                libro.getAutor().getNombre(),
                libro.getEjemplares());
    }
}
```

Con un solo DTO es asumible. El problema aparece al escalar:

- Cada entidad suele tener **varios** DTO (alta, modificación, listado,
  detalle), y cada uno necesita su conversión en uno o en ambos sentidos.
- Son líneas **mecánicas** y repetitivas: `a.setX(b.getX())` decenas de veces.
- Es un código propenso a errores silenciosos: añades un campo a la entidad,
  olvidas copiarlo y nada falla; el campo simplemente llega a `null`.

:::info
[Dónde vive la conversión]

La conversión DTO ⇄ entidad **no es lógica de negocio** ni pertenece al
controlador. Si la dejas en el controlador, lo engordas con código que no es
suyo; si la metes en la entidad, el dominio pasa a depender de la API. Un
mapper en su propio paquete mantiene cada capa con una sola
responsabilidad.
:::

## Qué es MapStruct

**MapStruct** es un **procesador de anotaciones**: durante la compilación lee
una interfaz que describes y **genera la clase que la implementa**, con el
código de copia campo a campo que de otro modo escribirías tú.

| | Mapeo manual | Reflexión (ModelMapper, Dozer) | MapStruct |
|---|---|---|---|
| Quién escribe la conversión | Tú | La librería, **en ejecución** | La librería, **en compilación** |
| Rendimiento | Óptimo | Más lento (usa reflexión) | Óptimo (son simples llamadas a getters/setters) |
| Si falta un campo | Error silencioso | Error en ejecución | **Aviso o error de compilación** |
| Código generado visible | — | No | Sí, en `target/generated-sources` |

La ventaja decisiva es la tercera fila: **un mapeo roto se detecta al
compilar**, no cuando un cliente recibe un `null` en producción.

## Dependencias en el `pom.xml`

MapStruct necesita dos piezas: la **librería** (las anotaciones) y el
**procesador** (el generador, que sólo se usa al compilar).

```xml
<properties>
    <mapstruct.version>1.6.3</mapstruct.version>
</properties>

<dependencies>
    <dependency>
        <groupId>org.mapstruct</groupId>
        <artifactId>mapstruct</artifactId>
        <version>${mapstruct.version}</version>
    </dependency>
</dependencies>

<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <configuration>
                <annotationProcessorPaths>
                    <!-- El orden importa: Lombok primero -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>${lombok.version}</version>
                    </path>
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok-mapstruct-binding</artifactId>
                        <version>0.2.0</version>
                    </path>
                    <path>
                        <groupId>org.mapstruct</groupId>
                        <artifactId>mapstruct-processor</artifactId>
                        <version>${mapstruct.version}</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

:::danger
[Lombok y MapStruct: el orden de los procesadores importa]

Ambos generan código en compilación y MapStruct necesita **ver** los
getters, setters y *builders* que genera Lombok. Por eso `lombok` va
**antes** que `mapstruct-processor` y hace falta
`lombok-mapstruct-binding` entre los dos. Si falta alguno, MapStruct genera
mapeos vacíos o avisa de que «no property named X exists». Si tu `pom.xml` ya
declaraba `annotationProcessorPaths` para Lombok, **añade** las entradas;
no dupliques el bloque.
:::

:::tip
[Comprueba la última versión]

`1.6.3` es la versión con la que se escribieron estos apuntes. Consulta
[mapstruct.org](https://mapstruct.org) antes de empezar un proyecto nuevo y
usa la estable más reciente.
:::

## Tu primer mapper

Parte de estos tipos (el DTO de salida es el de arriba):

```java
// domain/model/Libro.java
@Getter @Builder
public class Libro {
    private UUID id;
    private String isbn;
    private String titulo;
    private Autor autor;
    private int ejemplares;
    private EstadoLibro estado;
}

// domain/model/Autor.java
@Getter @Builder
public class Autor {
    private UUID id;
    private String nombre;
}
```

El mapper es una **interfaz** con la firma de cada conversión. No escribes el
cuerpo:

```java
// mapper/LibroMapper.java
package es.edu.biblioteca.mapper;

import es.edu.biblioteca.domain.model.Libro;
import es.edu.biblioteca.dto.LibroResponse;
import es.edu.biblioteca.dto.NuevoLibroRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface LibroMapper {

    @Mapping(source = "autor.nombre", target = "autor")
    @Mapping(source = "ejemplares", target = "ejemplaresDisponibles")
    LibroResponse toResponse(Libro libro);
}
```

Al ejecutar `mvn compile`, MapStruct genera
`target/generated-sources/annotations/.../LibroMapperImpl.java`:

```java
@Component
public class LibroMapperImpl implements LibroMapper {

    @Override
    public LibroResponse toResponse(Libro libro) {
        if (libro == null) {
            return null;
        }
        String autor = null;
        if (libro.getAutor() != null) {
            autor = libro.getAutor().getNombre();
        }
        int ejemplaresDisponibles = libro.getEjemplares();
        UUID id = libro.getId();
        String isbn = libro.getIsbn();
        String titulo = libro.getTitulo();

        return new LibroResponse(id, isbn, titulo, autor, ejemplaresDisponibles);
    }
}
```

Es exactamente el código que habrías escrito a mano, pero con las
comprobaciones de `null` incluidas y sin errores de copia.

| Elemento | Qué hace |
|----------|----------|
| `@Mapper` | Marca la interfaz como mapper; dispara la generación de la implementación. |
| `componentModel = "spring"` | La implementación generada se anota `@Component`, así que se **inyecta** como cualquier otro bean. |
| `@Mapping(source, target)` | Declara un campo cuyo nombre **no coincide** en origen y destino, o que sale de un objeto anidado (`autor.nombre`). |

Los campos con **el mismo nombre y tipo compatible** (`id`, `isbn`, `titulo`)
se mapean solos, sin anotar nada.

## Usarlo en el controlador

Como es un bean de Spring, se inyecta por constructor igual que viste en
[Inversión de control e inyección de
dependencias](/tema-0/f-inyeccion-dependencias):

```java
@RestController
@RequestMapping("/api/libros")
@RequiredArgsConstructor
public class LibrosController {

    private final ServicioLibros servicio;
    private final LibroMapper mapper;

    @GetMapping("/{id}")
    public LibroResponse obtener(@PathVariable UUID id) {
        return mapper.toResponse(servicio.obtener(id));
    }
}
```

El método estático `LibroResponse.desde(...)` desaparece, y el DTO vuelve a
ser un simple `record` sin conocer la entidad.

## De DTO a entidad: ignorar lo que no viene del cliente

La conversión inversa (de `NuevoLibroRequest` a `Libro`) tiene un matiz: el
DTO **no trae** `id`, `estado` ni `ejemplares`, y `autorId` no equivale a un
`Autor` completo.

```java
@Mapper(componentModel = "spring")
public interface LibroMapper {

    // ...toResponse como antes

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "autor", ignore = true)
    @Mapping(target = "ejemplares", constant = "0")
    @Mapping(target = "estado", expression = "java(EstadoLibro.DISPONIBLE)")
    Libro toEntity(NuevoLibroRequest peticion);
}
```

| Atributo de `@Mapping` | Cuándo usarlo |
|------------------------|---------------|
| `ignore = true` | El campo no debe copiarse; lo rellenará otra capa (el `service` asigna el `id`, resuelve el `Autor` por su `autorId`...). |
| `constant = "..."` | Valor fijo, escrito como literal. |
| `expression = "java(...)"` | Código Java arbitrario para casos que no encajan en lo anterior. Úsalo con moderación: no se comprueba hasta compilar el código generado. |
| `defaultValue = "..."` | Valor a usar si el origen es `null`. |
| `dateFormat`, `numberFormat` | Conversión de `String` a fecha o número con un patrón. |

:::warning
[Un mapper no consulta repositorios]

Resolver `autorId` a un `Autor` implica consultar la base de datos, y eso
es lógica de negocio del `service`, igual que la comprobación de duplicados
del apartado g. Ignora el campo en el mapper y haz la búsqueda en el
servicio. (MapStruct permite inyectar repositorios en un mapper con `uses`
o clases abstractas, pero convierte una pieza pura en una que depende de
infraestructura: evítalo en este módulo.)
:::

## Actualizar una entidad existente

En un `PUT` o `PATCH` no quieres crear un objeto nuevo, sino **volcar los
datos del DTO sobre uno que ya existe**. Para eso se usa `@MappingTarget`:

```java
@Mapping(target = "id", ignore = true)
@Mapping(target = "autor", ignore = true)
@BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
void actualizar(ActualizarLibroRequest peticion, @MappingTarget Libro libro);
```

- `@MappingTarget` indica el objeto **destino ya existente**; el método es
  `void` porque lo modifica en vez de devolver uno nuevo.
- `NullValuePropertyMappingStrategy.IGNORE` hace que un campo `null` del DTO
  **no sobrescriba** el valor actual: es lo que quieres en una actualización
  parcial.

:::info
[Requiere que el destino sea modificable]

`@MappingTarget` llama a los *setters* del destino. Una clase con sólo
`@Getter @Builder` (inmutable) no los tiene. Si necesitas actualizar,
añade `@Setter` o, mejor, deja que el `service` construya una nueva
instancia. Los `record` son inmutables y **no** pueden ser `@MappingTarget`.
:::

## Colecciones y objetos anidados

Para convertir listas basta con declarar el método con el tipo de colección;
MapStruct reutiliza el mapeo de elemento:

```java
List<LibroResponse> toResponseList(List<Libro> libros);
```

Si un objeto contiene otro objeto que también necesita su propio mapeo,
MapStruct lo busca entre los métodos del mismo mapper o de los mappers
declarados con `uses`:

```java
@Mapper(componentModel = "spring", uses = AutorMapper.class)
public interface LibroDetalleMapper {
    LibroDetalleResponse toDetalle(Libro libro);   // llama a AutorMapper para el campo autor
}
```

## Mapeos personalizados

Cuando la conversión no es una simple copia, escribe **un método por defecto**
en la propia interfaz y MapStruct lo usará automáticamente cuando los tipos
coincidan:

```java
@Mapper(componentModel = "spring")
public interface SocioMapper {

    SocioResponse toResponse(Socio socio);

    default String enmascararDocumento(String documento) {
        // «12345678Z» → «****5678Z»
        return documento == null ? null : "****" + documento.substring(4);
    }
}
```

:::warning
[Un método por defecto se aplica a todos los campos de ese tipo]

El método anterior recibe y devuelve `String`, así que MapStruct podría
aplicarlo a **cualquier** campo `String` y no sólo al documento. Para acotarlo,
nómbralo con `@Named("enmascarar")` y referéncialo desde el campo concreto:
`@Mapping(target = "documentoIdentidad", qualifiedByName = "enmascarar")`.
:::

## Qué errores detecta (y cuándo)

MapStruct valida los mapeos **al compilar**. Por defecto, un campo del
destino sin origen correspondiente genera un **warning**:

```
Unmapped target property: "estado".
```

Puedes endurecerlo para que sea un error y no se te escape ninguno:

```java
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.ERROR)
```

Así, añadir un campo al DTO y olvidar su origen **rompe la compilación**, que
es justo lo que quieres.

## Probar un mapper

Un mapper es lógica pura sin dependencias: se prueba como una prueba
unitaria, sin levantar Spring.

```java
class LibroMapperTest {

    // Mappers.getMapper evita arrancar el contexto de Spring
    private final LibroMapper mapper = Mappers.getMapper(LibroMapper.class);

    @Test
    void toResponse_copiaElNombreDelAutor() {
        var libro = Libro.builder()
                .id(UUID.randomUUID())
                .isbn("9780132350884")
                .titulo("Clean Code")
                .autor(Autor.builder().nombre("Robert C. Martin").build())
                .ejemplares(3)
                .build();

        var respuesta = mapper.toResponse(libro);

        assertThat(respuesta.autor()).isEqualTo("Robert C. Martin");
        assertThat(respuesta.ejemplaresDisponibles()).isEqualTo(3);
    }
}
```

:::note
[`Mappers.getMapper` y `componentModel = "spring"`]

`Mappers.getMapper` funciona con la implementación generada, que sigue
existiendo aunque se anote `@Component`. Si el mapper usa `uses` con otros
mappers inyectados, `getMapper` no los resuelve: en ese caso prueba con
`@SpringBootTest(classes = {...})` limitado a los mappers implicados.
:::

## Errores frecuentes

:::note
[«El campo del DTO llega siempre a `null` y no hay ningún error»]

Los nombres de origen y destino no coinciden y no has declarado el
`@Mapping`. Revisa los *warnings* de compilación («Unmapped target
property») o activa `unmappedTargetPolicy = ReportingPolicy.ERROR`.
:::

:::note
[«`NoSuchBeanDefinitionException: LibroMapper`» o «`LibroMapperImpl` no existe»]

Falta `componentModel = "spring"` (la implementación no es un bean), o el
procesador no se ejecutó. Ejecuta `mvn clean compile` y comprueba que existe
`target/generated-sources/annotations/.../LibroMapperImpl.java`.
:::

:::note
[«El mapper genera objetos vacíos con Lombok»]

El orden de `annotationProcessorPaths` es incorrecto, o falta
`lombok-mapstruct-binding`. MapStruct se ejecutó antes de que Lombok
generara los getters. Revisa el bloque del `pom.xml`.
:::

:::note
[«El IDE no encuentra `LibroMapperImpl`»]

El IDE aún no ha procesado los fuentes generados. Recompila con Maven y
refresca el proyecto; en IntelliJ activa *Enable annotation processing* en
*Settings → Build → Compiler → Annotation Processors*.
:::

:::note
[«`ambiguous mapping methods found`»]

Hay dos métodos que podrían convertir el mismo par de tipos. Distínguelos
con `@Named` y `qualifiedByName`, o elimina el que sobra.
:::

<NextPageButton to="/tema-0/m-configuracion-properties">
Configuración tipada con @ConfigurationProperties
</NextPageButton>
