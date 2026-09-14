---
title: Excepciones propias y gestión de rutas por configuración
description: Excepciones comprobadas y no comprobadas, try-with-resources, diseño de una jerarquía de excepciones de dominio y externalización de rutas con @Value y @ConfigurationProperties.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Excepciones propias y gestión de rutas por configuración

## Comprobadas frente a no comprobadas

Java distingue dos familias de excepciones:

| | Comprobadas (*checked*) | No comprobadas (*unchecked*) |
|---|---|---|
| Superclase | `Exception` (que no sea `RuntimeException`) | `RuntimeException` |
| El compilador obliga a... | capturarlas o declararlas con `throws` | nada; son opcionales de capturar |
| Ejemplo típico en E/S | `IOException`, `NoSuchFileException` | `NullPointerException`, `IllegalArgumentException` |
| Uso habitual | errores **recuperables**, externos al programa (el disco, la red) | errores de **programación** o de estado interno |

Casi todas las excepciones de `java.nio.file` son comprobadas: el
compilador te obliga a decidir qué hacer con ellas en el momento de escribir
el código, no a descubrirlo en producción.

## `try-with-resources`

Cualquier recurso que implemente `AutoCloseable` (un `DirectoryStream`, un
`SeekableByteChannel`, un `WatchService`...) debe abrirse con
`try-with-resources`, que garantiza el cierre incluso si se lanza una
excepción:

```java
try (var canal = Files.newByteChannel(fichero)) {
    // usar el canal
} catch (IOException e) {
    // el canal ya se ha cerrado automáticamente al llegar aquí
    throw new RuntimeException("No se pudo procesar el fichero", e);
}
```

Se pueden declarar varios recursos en la misma cláusula, separados por
`;`; se cierran en **orden inverso** al de apertura:

```java
try (var entrada = Files.newByteChannel(origen);
     var salida = Files.newByteChannel(destino, StandardOpenOption.WRITE)) {
    // ...
}
```

:::danger
Cerrar un recurso manualmente en un bloque `finally` escrito a mano es
propenso a errores (olvidar el `finally`, no comprobar si el recurso llegó
a abrirse, perder la excepción original si `close()` también falla).
`try-with-resources` existe precisamente para eliminar esa clase de fallos;
en código nuevo no hay motivo para no usarlo.
:::

## Por qué no basta con propagar `IOException`

Dejar que `IOException` (y sus subclases) se propaguen tal cual hasta la
capa que atiende una petición tiene dos problemas:

1. **Fuga de detalles de infraestructura.** La capa que llama no debería
   necesitar saber si el dato venía de un fichero, una base de datos o una
   API externa; solo le interesa si la operación de negocio pudo hacerse o
   no, y por qué.
2. **Imposibilidad de reaccionar de forma distinta según el caso.** No es
   lo mismo que un fichero no exista (quizá se pueda crear) a que falten
   permisos (no se puede hacer nada sin intervención externa), y
   `IOException` no distingue eso con claridad para quien la captura genéricamente.

La solución es **traducir** las excepciones de bajo nivel a una **jerarquía
de excepciones propia**, expresada en términos del dominio de la
aplicación.

## Diseñar una jerarquía de excepciones de dominio

Un diseño habitual parte de una excepción base **no comprobada** para el
dominio, con subclases que distinguen los casos relevantes para quien las
captura:

```java
// Excepción base del dominio de la aplicación.
// No comprobada: quien no necesite tratarla caso por caso no está
// obligado a declararla ni a capturarla.
public class AlmacenamientoException extends RuntimeException {
    public AlmacenamientoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

public class RecursoNoEncontradoException extends AlmacenamientoException {
    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}

public class PermisoDenegadoException extends AlmacenamientoException {
    public PermisoDenegadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
```

Y un punto de traducción, normalmente en la clase que encapsula el acceso
al sistema de ficheros, que convierte cada excepción de `java.nio.file` en
la excepción de dominio equivalente:

```java
import java.nio.file.NoSuchFileException;
import java.nio.file.AccessDeniedException;
import java.io.IOException;

public Documento leer(Path ruta) {
    try {
        String contenido = Files.readString(ruta);
        return new Documento(ruta.getFileName().toString(), contenido);
    } catch (NoSuchFileException e) {
        throw new RecursoNoEncontradoException("No existe el fichero: " + ruta, e);
    } catch (AccessDeniedException e) {
        throw new PermisoDenegadoException("Sin permisos para leer: " + ruta, e);
    } catch (IOException e) {
        throw new AlmacenamientoException("Error de E/S al leer: " + ruta, e);
    }
}
```

:::tip
Conserva siempre la excepción original como **causa** (segundo argumento del
constructor, propagado con `super(mensaje, causa)`). Perder la causa
original hace mucho más difícil depurar el problema real cuando llegue a
producción.
:::

:::info
Elegir que la jerarquía de dominio sea **no comprobada** es una decisión de
diseño extendida en aplicaciones modernas: evita que cada capa intermedia
tenga que declarar `throws` para excepciones que en la mayoría de los casos
solo puede propagar, y deja la decisión de "capturar o no" a las capas que
de verdad pueden reaccionar (por ejemplo, la que traduce errores a una
respuesta para quien hizo la petición).
:::

## Gestión de rutas por configuración

Escribir una ruta de directorio directamente en el código (`Path.of("/var/datos/entrada")`)
acopla el programa a una máquina concreta. La alternativa es **externalizar**
la ruta a la configuración de la aplicación.

### `@Value`

Para un valor suelto, `@Value` lee una propiedad de configuración e la
inyecta directamente:

```java
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.nio.file.Path;

@Component
public class LectorDirectorioEntrada {

    private final Path directorioEntrada;

    public LectorDirectorioEntrada(@Value("${app.directorio-entrada}") String ruta) {
        this.directorioEntrada = Path.of(ruta);
    }
}
```

```yaml
# application.yml
app:
  directorio-entrada: /var/datos/entrada
```

### `@ConfigurationProperties`

Cuando hay **varias rutas relacionadas**, agruparlas en una clase de
propiedades es más mantenible que repetir `@Value` en cada sitio:

```java
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.almacenamiento")
public class AlmacenamientoProperties {

    private String directorioEntrada;
    private String directorioProcesados;
    private String directorioErrores;

    // getters y setters
    public String getDirectorioEntrada() { return directorioEntrada; }
    public void setDirectorioEntrada(String v) { this.directorioEntrada = v; }
    public String getDirectorioProcesados() { return directorioProcesados; }
    public void setDirectorioProcesados(String v) { this.directorioProcesados = v; }
    public String getDirectorioErrores() { return directorioErrores; }
    public void setDirectorioErrores(String v) { this.directorioErrores = v; }
}
```

```yaml
# application.yml
app:
  almacenamiento:
    directorio-entrada: /var/datos/entrada
    directorio-procesados: /var/datos/procesados
    directorio-errores: /var/datos/errores
```

Hay que habilitar el escaneo de esta clase de propiedades una vez en la
configuración de arranque:

```java
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(AlmacenamientoProperties.class)
```

:::tip
`@ConfigurationProperties` valida el tipo de cada propiedad al arrancar la
aplicación (falla rápido si falta una propiedad obligatoria o tiene un tipo
incorrecto) y permite agrupar propiedades relacionadas en un único objeto
inyectable, en lugar de dispersar `@Value` por todo el código.
:::

:::note[«¿Por qué no basta con una constante `static final` en el código?»]
Porque entonces cambiar la ruta exige recompilar y volver a desplegar la
aplicación. Externalizar la ruta a un fichero de configuración permite
ajustarla por entorno (desarrollo, pruebas, producción) sin tocar el
código fuente.
:::

<NextPageButton to="/tema-1/g-streaming-binarios">
Streaming de ficheros binarios grandes
</NextPageButton>
