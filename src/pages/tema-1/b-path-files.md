---
title: 'Path, Paths y Files: operaciones básicas'
description: Cómo crear, borrar, copiar, mover y renombrar ficheros y directorios con la API java.nio.file.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Path, Paths y Files: operaciones básicas

## `Path`: una ruta, no un fichero

Un objeto `Path` representa una **ruta** del sistema de ficheros, exista o
no el fichero al que apunta. Es una estructura de datos ligera, sin coste de
E/S hasta que se usa junto a `Files`.

```java
import java.nio.file.Path;
import java.nio.file.Paths;

// A partir de Java 11, el método de fábrica preferido es Path.of(...)
Path ruta = Path.of("datos", "entrada", "registro.csv");

// Equivalente con la clase de fábrica clásica (anterior a Path.of)
Path rutaAbsoluta = Paths.get("/var/datos/registro.csv");

System.out.println(ruta);            // datos/entrada/registro.csv
System.out.println(ruta.getFileName()); // registro.csv
System.out.println(ruta.getParent());   // datos/entrada
System.out.println(ruta.isAbsolute());  // false
```

`Path` ofrece métodos para **componer y normalizar** rutas sin
concatenación manual de cadenas:

```java
Path base = Path.of("datos");
Path completa = base.resolve("entrada").resolve("registro.csv");
// datos/entrada/registro.csv

Path conVueltas = Path.of("datos/./entrada/../entrada/registro.csv");
System.out.println(conVueltas.normalize());
// datos/entrada/registro.csv

Path absoluta = completa.toAbsolutePath();
```

:::tip
Nunca construyas rutas concatenando cadenas con `+` o `File.separator`
manualmente. `resolve()` y `Path.of(...)` ya gestionan el separador correcto
según el sistema operativo (`/` en Linux/macOS, `\` en Windows).
:::

## `Files`: la clase que hace el trabajo

`Path` no toca el disco por sí solo. Todas las operaciones reales están en
los métodos **estáticos** de `java.nio.file.Files`.

### Comprobar existencia

```java
import java.nio.file.Files;

boolean existe = Files.exists(ruta);
boolean noExiste = Files.notExists(ruta);
boolean esDirectorio = Files.isDirectory(ruta);
boolean esFichero = Files.isRegularFile(ruta);
```

:::warning
`Files.exists()` puede devolver `false` tanto si el fichero no existe como
si no tienes permisos para comprobarlo. No lo uses como única defensa contra
condiciones de carrera: entre que compruebas y que actúas, el fichero puede
cambiar de estado. Prefiere manejar la excepción que lanza la operación real.
:::

### Crear directorios y ficheros

```java
Path directorio = Path.of("salida", "informes");

// Crea solo el último nivel; falla si el padre no existe
Files.createDirectory(Path.of("salida"));

// Crea todos los niveles intermedios que hagan falta
Files.createDirectories(directorio);

// Crea un fichero vacío; lanza FileAlreadyExistsException si ya existe
Path fichero = directorio.resolve("resumen.txt");
Files.createFile(fichero);
```

### Escribir y leer contenido

Para ficheros de texto pequeños, `Files` ofrece atajos que evitan gestionar
flujos manualmente:

```java
import java.util.List;
import java.nio.charset.StandardCharsets;

// Escritura completa (crea el fichero si no existe, lo trunca si existe)
Files.writeString(fichero, "primera línea\n", StandardCharsets.UTF_8);

// Añadir contenido sin truncar
Files.writeString(fichero, "segunda línea\n", StandardCharsets.UTF_8,
        java.nio.file.StandardOpenOption.APPEND);

// Lectura completa a String
String contenido = Files.readString(fichero, StandardCharsets.UTF_8);

// Lectura línea a línea a una lista
List<String> lineas = Files.readAllLines(fichero, StandardCharsets.UTF_8);
```

:::warning
`readString` y `readAllLines` cargan el fichero **entero** en memoria. Son
cómodos para configuración o ficheros pequeños, pero no los uses sobre
ficheros de gran tamaño: para eso están los flujos, que verás en la unidad
sobre E/S de bytes y caracteres.
:::

### Copiar, mover y renombrar

```java
import java.nio.file.StandardCopyOption;

Path origen = Path.of("datos", "plantilla.txt");
Path destino = Path.of("copias", "plantilla-2024.txt");

// Copiar; lanza FileAlreadyExistsException si el destino ya existe
Files.copy(origen, destino);

// Copiar sobrescribiendo si ya existe
Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);

// Mover (también sirve para renombrar dentro del mismo directorio)
Path renombrado = Path.of("copias", "plantilla-final.txt");
Files.move(destino, renombrado, StandardCopyOption.REPLACE_EXISTING);
```

`Files.move` es la operación correcta tanto para trasladar un fichero a otro
directorio como para renombrarlo: un renombrado es, en esencia, un
movimiento dentro del mismo padre.

### Borrar

```java
// Lanza NoSuchFileException si no existe
Files.delete(fichero);

// No lanza excepción si no existía; devuelve boolean
boolean seHaBorrado = Files.deleteIfExists(fichero);
```

:::danger
`Files.delete` sobre un **directorio** solo funciona si está **vacío**.
Para borrar un árbol completo con contenido necesitas recorrerlo primero
(lo verás en el siguiente apartado) y borrar de dentro hacia fuera.
:::

## Excepciones comprobadas, no booleanos

A diferencia de `java.io.File`, los métodos de `Files` **lanzan
excepciones comprobadas** (`IOException` y subclases como
`NoSuchFileException` o `FileAlreadyExistsException`) en lugar de devolver
`false` ante un fallo. Esto obliga a decidir explícitamente qué hacer con
cada error, en vez de ignorarlo silenciosamente:

```java
try {
    Files.createDirectories(directorio);
    Files.copy(origen, destino, StandardCopyOption.REPLACE_EXISTING);
} catch (java.io.IOException e) {
    // Aquí decides: reintentar, propagar como excepción de dominio,
    // registrar y continuar... según el caso de uso.
    throw new RuntimeException("No se pudo preparar el directorio de salida", e);
}
```

En el apartado sobre excepciones de esta misma unidad verás cómo convertir
estas excepciones de bajo nivel en excepciones propias del dominio de la
aplicación.

:::note[«¿Por qué `Files.copy` no crea los directorios padre?»]
Porque no es su responsabilidad: `copy` copia un fichero a una ruta que
**ya debe existir** en cuanto a estructura de directorios. Si el padre no
existe, lanza `NoSuchFileException`. Llama primero a
`Files.createDirectories(destino.getParent())`.
:::

<NextPageButton to="/tema-1/c-recorrido-arboles">
Recorrido de árboles de directorios
</NextPageButton>
