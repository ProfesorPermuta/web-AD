---
title: 'Configuración tipada con @ConfigurationProperties'
description: Cómo externalizar parámetros en application.yml y enlazarlos a clases tipadas con @ConfigurationProperties, con validación al arrancar, perfiles y pruebas.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Configuración tipada con `@ConfigurationProperties`

En [Anatomía de un proyecto Spring Boot](/tema-0/e-anatomia-spring-boot) viste
cómo es `application.yml` y en [Inyección de
dependencias](/tema-0/f-inyeccion-dependencias) leíste una primera pincelada
de `@ConfigurationProperties`. Este apartado lo desarrolla entero: cómo
**declarar** parámetros propios, cómo **enlazarlos** a una clase, cómo
**validarlos** y cómo **probarlos**.

## El problema: parámetros dispersos

Una aplicación real tiene parámetros que **no son lógica**: rutas de
directorios, plazos, límites, direcciones de servicios externos. Si los
escribes en el código (`private static final int DIAS_PRESTAMO = 15;`),
cambiarlos exige recompilar y volver a desplegar, y no puedes tener valores
distintos en desarrollo y producción.

Si los lees con `@Value`, el problema se desplaza:

```java
@Service
public class PrestamoService {

    public PrestamoService(
            @Value("${biblioteca.prestamo.dias}") int dias,
            @Value("${biblioteca.prestamo.max-renovaciones}") int maxRenovaciones,
            @Value("${biblioteca.prestamo.multa-diaria}") BigDecimal multaDiaria) {
        // ...
    }
}
```

- Las **claves son cadenas** repetidas por el código: un error tipográfico
  no se detecta hasta que falla en ejecución.
- No hay **agrupación**: tres parámetros del mismo concepto aparecen
  sueltos, y en cada clase que los necesita hay que repetirlos.
- No hay **validación**: nada impide que `dias` sea `-3`.

## La solución: una clase por grupo de propiedades

`@ConfigurationProperties` vincula (*bind*) todas las propiedades que
cuelgan de un **prefijo** a los campos de una clase tipada. Esa clase se
inyecta después como cualquier otro bean.

### Paso 1. Declarar las propiedades en `application.yml`

```yaml
biblioteca:
  prestamo:
    dias: 15
    max-renovaciones: 2
    multa-diaria: 0.25
  portadas:
    directorio: ./datos/portadas
    tamano-maximo: 2MB
```

### Paso 2. Escribir la clase que las recibe

Con un `record` (recomendado desde Spring Boot 3) la clase es inmutable y no
necesita ni *getters* ni *setters*:

```java
package es.edu.biblioteca.config;

import java.math.BigDecimal;
import java.nio.file.Path;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.unit.DataSize;

@ConfigurationProperties(prefix = "biblioteca")
public record BibliotecaProperties(Prestamo prestamo, Portadas portadas) {

    public record Prestamo(int dias, int maxRenovaciones, BigDecimal multaDiaria) { }

    public record Portadas(Path directorio, DataSize tamanoMaximo) { }
}
```

La correspondencia es directa: el prefijo `biblioteca` selecciona el bloque,
cada componente del `record` busca la clave del mismo nombre y los *records*
anidados reflejan los niveles anidados del YAML.

### Paso 3. Activar la clase

Declarar la anotación **no basta**: Spring Boot tiene que saber que esa clase
existe. Hay dos formas:

```java
// Opción A: activar clases concretas
@SpringBootApplication
@EnableConfigurationProperties(BibliotecaProperties.class)
public class BibliotecaApplication { /* ... */ }

// Opción B: escanear el paquete y activar todas las que encuentre
@SpringBootApplication
@ConfigurationPropertiesScan
public class BibliotecaApplication { /* ... */ }
```

:::tip
[Qué opción elegir]

`@ConfigurationPropertiesScan` es más cómoda cuando hay varias clases de
propiedades en `config/`. `@EnableConfigurationProperties` es más explícita:
quien lee la clase de arranque ve de un vistazo qué grupos existen.
:::

### Paso 4. Inyectarla donde se necesite

```java
@Service
public class PrestamoService {

    private final BibliotecaProperties props;

    public PrestamoService(BibliotecaProperties props) {
        this.props = props;
    }

    public LocalDate calcularDevolucion(LocalDate inicio) {
        return inicio.plusDays(props.prestamo().dias());
    }
}
```

Si solo una parte te interesa, puedes inyectar el *record* anidado: no es un
bean, así que lo normal es inyectar la clase raíz y navegar desde ella.

:::warning
[No anotes la clase con `@Component` además de `@ConfigurationProperties`]

Con `@EnableConfigurationProperties` o `@ConfigurationPropertiesScan` la
clase ya queda registrada como bean. Añadirle `@Component` la registraría
**dos veces** y es una de las causas habituales de `NoUniqueBeanDefinitionException`.
:::

## Variante con clase clásica (JavaBean)

Antes de los *records* se usaba una clase con *getters* y *setters*. Sigue
siendo válida y es lo que verás en mucho código existente (también en
[Excepciones propias y gestión de rutas por
configuración](/tema-1/f-excepciones-config)):

```java
@ConfigurationProperties(prefix = "biblioteca.prestamo")
public class PrestamoProperties {

    private int dias = 15;          // valor por defecto
    private int maxRenovaciones = 2;

    public int getDias() { return dias; }
    public void setDias(int dias) { this.dias = dias; }
    public int getMaxRenovaciones() { return maxRenovaciones; }
    public void setMaxRenovaciones(int n) { this.maxRenovaciones = n; }
}
```

| | `record` (enlace por constructor) | Clase con *setters* |
|---|---|---|
| Mutabilidad | Inmutable: no cambia tras arrancar | Mutable: cualquiera puede llamar al *setter* |
| Código | Una línea por propiedad | *Getter* + *setter* por propiedad (o Lombok `@Getter @Setter`) |
| Valor por defecto | `@DefaultValue("15")` en el componente | Inicializador del campo |
| Recomendación | **Preferida** en código nuevo | Solo si ya hay una clase mutable que reutilizar |

:::info
[Enlace por constructor]

Un `record` se enlaza **por constructor**: Spring Boot llama al constructor
con los valores ya convertidos. Si una clase normal tiene un único
constructor con parámetros, también se enlaza así; si quieres forzarlo con
varios constructores, anota el correcto con `@ConstructorBinding`.
:::

## Qué tipos se convierten solos

Spring convierte el texto del YAML al tipo del campo. Los más útiles:

| Tipo Java | Ejemplo en YAML | Notas |
|-----------|-----------------|-------|
| `int`, `boolean`, `BigDecimal`... | `dias: 15` | Conversión estándar. |
| `Path`, `File` | `directorio: ./datos/portadas` | Rutas como cadena. |
| `Duration` | `timeout: 30s`, `PT30S`, `5m` | Sufijos `ms`, `s`, `m`, `h`, `d`. |
| `DataSize` | `tamano-maximo: 2MB` | Sufijos `B`, `KB`, `MB`, `GB`. |
| `enum` | `modo: ESTRICTO` | No distingue mayúsculas. |
| `List<String>` | `extensiones: [jpg, png]` | También lista YAML con guiones. |
| `Map<String, X>` | bloque de clave → valor | Útil para configuraciones por nombre. |
| *record* / clase anidada | bloque indentado | Un nivel de YAML por nivel de objeto. |

```yaml
biblioteca:
  portadas:
    extensiones-permitidas: [jpg, png, webp]
  limites-por-tipo:
    SOCIO: 5
    ESTUDIANTE: 3
    INFANTIL: 2
```

```java
public record Portadas(Path directorio,
                       DataSize tamanoMaximo,
                       List<String> extensionesPermitidas) { }

// Map con clave enum
Map<TipoSocio, Integer> limitesPorTipo
```

## Nombres: *relaxed binding*

La clave del YAML y el nombre en Java **no tienen que escribirse igual**:
Spring acepta varias formas para la misma propiedad.

| Dónde | Forma | Ejemplo |
|-------|-------|---------|
| `application.yml` / `.properties` | *kebab-case* (**recomendada**) | `max-renovaciones` |
| Campo o componente Java | *camelCase* | `maxRenovaciones` |
| Variable de entorno | `MAYÚSCULAS_CON_GUION_BAJO` | `BIBLIOTECA_PRESTAMO_MAXRENOVACIONES` |

:::note
[El prefijo debe estar en kebab-case]

El `prefix` de `@ConfigurationProperties` solo admite minúsculas y guiones
(`prefix = "biblioteca.prestamo"`). Un prefijo en `camelCase` falla al
arrancar.
:::

## Valores por defecto

Si una propiedad opcional no aparece en el YAML, necesita un valor por
defecto. En un `record` se declara con `@DefaultValue`:

```java
import org.springframework.boot.context.properties.bind.DefaultValue;

public record Prestamo(
        @DefaultValue("15") int dias,
        @DefaultValue("2") int maxRenovaciones,
        BigDecimal multaDiaria) { }
```

Sin `@DefaultValue`, una propiedad ausente de tipo primitivo vale `0` o
`false`, y de tipo objeto vale `null`: es un fallo silencioso que conviene
evitar. Por eso se combina con validación.

## Validación al arrancar

Anota la clase con `@Validated` y las restricciones de Jakarta Validation
(las mismas que en [Controladores REST y
validación](/tema-0/g-controlador-rest-validacion)) sobre sus componentes:

```java
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "biblioteca")
@Validated
public record BibliotecaProperties(
        @NotNull @Valid Prestamo prestamo,
        @NotNull @Valid Portadas portadas) {

    public record Prestamo(
            @Min(1) @Max(90) int dias,
            @Min(0) int maxRenovaciones,
            @NotNull @DecimalMin("0.00") BigDecimal multaDiaria) { }

    public record Portadas(
            @NotNull Path directorio,
            @NotNull DataSize tamanoMaximo) { }
}
```

Con `dias: -3` en el YAML, la aplicación **no arranca** y el error dice qué
propiedad es la incorrecta:

```
***************************
APPLICATION FAILED TO START
***************************

Description:

Binding to target ... BibliotecaProperties failed:

    Property: biblioteca.prestamo.dias
    Value: "-3"
    Reason: debe ser mayor o igual que 1

Action:

Update your application's configuration
```

:::danger
[`@Valid` en los objetos anidados]

La validación **no desciende** sola a los *records* anidados. Sin `@Valid`
sobre `Prestamo prestamo`, las restricciones `@Min` de dentro se ignoran en
silencio y un valor absurdo llega al servicio sin que nada avise.
:::

## Perfiles y precedencia

Las clases de propiedades se benefician de todo lo visto en [Perfiles y
orden de precedencia](/tema-0/e-anatomia-spring-boot#perfiles): el mismo
`BibliotecaProperties` recibe valores distintos según el perfil activo.

```yaml
# application.yml (valores comunes)
biblioteca:
  prestamo:
    dias: 15
```

```yaml
# application-dev.yml (sobrescribe solo lo que cambia)
biblioteca:
  prestamo:
    dias: 1        # en desarrollo, para probar vencimientos rápido
```

Y en producción puedes sobrescribirlo sin tocar ficheros, con una variable de
entorno o un argumento:

```bash
# variable de entorno (relaxed binding)
BIBLIOTECA_PRESTAMO_DIAS=21 java -jar target/biblioteca.jar

# argumento de línea de comandos
java -jar target/biblioteca.jar --biblioteca.prestamo.dias=21
```

## Autocompletado en el IDE

Si añades el procesador de metadatos, el IDE ofrece autocompletado y
documentación de tus propiedades al editar `application.yml`:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-configuration-processor</artifactId>
    <optional>true</optional>
</dependency>
```

Genera en compilación `META-INF/spring-configuration-metadata.json`. Los
comentarios Javadoc de cada componente se convierten en la descripción que
muestra el IDE.

:::warning
[Si usas Lombok y MapStruct]

El procesador de configuración también es un procesador de anotaciones. Si el
`pom.xml` declara `annotationProcessorPaths` (necesario para
[MapStruct](/tema-0/l-mappers-mapstruct)), **añádelo ahí también**; si no, la
declaración explícita desactiva el descubrimiento automático y el IDE dejará
de ver tus propiedades.
:::

## Probar una clase de propiedades

No hace falta arrancar toda la aplicación. Una prueba con `ApplicationContextRunner`
comprueba el enlace y la validación en milisegundos:

```java
class BibliotecaPropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Config.class);

    @EnableConfigurationProperties(BibliotecaProperties.class)
    static class Config { }

    @Test
    void enlazaLosValoresDelYaml() {
        runner.withPropertyValues(
                        "biblioteca.prestamo.dias=21",
                        "biblioteca.prestamo.max-renovaciones=1",
                        "biblioteca.prestamo.multa-diaria=0.50",
                        "biblioteca.portadas.directorio=./tmp",
                        "biblioteca.portadas.tamano-maximo=1MB")
                .run(ctx -> {
                    var props = ctx.getBean(BibliotecaProperties.class);
                    assertThat(props.prestamo().dias()).isEqualTo(21);
                    assertThat(props.portadas().tamanoMaximo().toMegabytes()).isEqualTo(1);
                });
    }

    @Test
    void rechazaUnPlazoNegativo() {
        runner.withPropertyValues(
                        "biblioteca.prestamo.dias=-3",
                        "biblioteca.prestamo.multa-diaria=0.50",
                        "biblioteca.portadas.directorio=./tmp",
                        "biblioteca.portadas.tamano-maximo=1MB")
                .run(ctx -> assertThat(ctx).hasFailed());
    }
}
```

## `@Value` frente a `@ConfigurationProperties`

| Criterio | `@Value` | `@ConfigurationProperties` |
|----------|----------|----------------------------|
| Número de valores | Uno suelto | Un grupo relacionado |
| Tipado | Una cadena que se convierte | Objeto tipado, anidable |
| Validación | No | Sí, con `@Validated` |
| Relaxed binding | Limitado | Completo |
| Autocompletado en el IDE | No | Sí, con el procesador |
| Pruebas | Hay que arrancar contexto | `ApplicationContextRunner` |
| SpEL (`#{...}`) | Sí | No |

**Regla del curso:** un parámetro suelto y puntual puede ir con `@Value`; en
cuanto haya **dos o más relacionados**, usa `@ConfigurationProperties`.

## Errores frecuentes

:::note
[«`Binding to target ... failed: Property: ... Reason: must not be null`»]

Falta la propiedad en el YAML (o está mal escrita) y el componente tiene
`@NotNull`. Revisa el prefijo y la indentación: una clave desplazada un
nivel cuelga de otro prefijo.
:::

:::note
[«Los valores del YAML no llegan a la clase y todo vale `0` o `null`»]

La clase no está activada. Añade `@EnableConfigurationProperties(...)` o
`@ConfigurationPropertiesScan` en la clase de arranque. Sin eso la anotación
`@ConfigurationProperties` no hace nada.
:::

:::note
[«`NoUniqueBeanDefinitionException` al inyectar mi clase de propiedades»]

La clase está registrada dos veces: por `@Component` y por
`@EnableConfigurationProperties`/`@ConfigurationPropertiesScan`. Quita el
`@Component`.
:::

:::note
[«Un `@Min` o `@NotBlank` de un record anidado no se aplica»]

Falta `@Valid` sobre el componente que contiene el *record* anidado, o falta
`@Validated` en la clase raíz.
:::

:::note
[«`Failed to bind properties ... failed to convert java.lang.String to int`»]

El valor del YAML no tiene el tipo esperado (`dias: quince`). El mensaje
indica la propiedad exacta y el valor recibido.
:::

:::note
[«Cambio el `application.yml` pero la aplicación sigue usando el valor viejo»]

Spring lee la configuración al arrancar. Reinicia la aplicación. Si usas
`mvn spring-boot:run`, comprueba además que no hay otro valor con mayor
precedencia (variable de entorno o argumento, véase
[Orden de precedencia](/tema-0/e-anatomia-spring-boot#orden-de-precedencia)).
:::

<NextPageButton to="/tema-1/a-presentacion-tema">
Empezar la UD1
</NextPageButton>
