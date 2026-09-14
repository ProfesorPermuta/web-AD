---
title: Recorrido de árboles de directorios, atributos y permisos
description: Files.walk, walkFileTree, DirectoryStream, PathMatcher y cómo consultar metadatos y permisos de un fichero.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Recorrido de árboles de directorios, atributos y permisos

## `DirectoryStream`: listar un único nivel

Para listar el contenido **directo** de un directorio, sin bajar a
subdirectorios, se usa `Files.newDirectoryStream`. Implementa
`AutoCloseable`, así que siempre va dentro de un `try-with-resources`:

```java
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;

Path directorio = Path.of("datos", "entrada");

try (DirectoryStream<Path> entradas = Files.newDirectoryStream(directorio)) {
    for (Path entrada : entradas) {
        System.out.println(entrada.getFileName());
    }
}
```

Se puede filtrar con un patrón *glob* directamente en la apertura del
stream:

```java
try (DirectoryStream<Path> csv =
        Files.newDirectoryStream(directorio, "*.csv")) {
    for (Path fichero : csv) {
        System.out.println(fichero);
    }
}
```

## `Files.walk`: recorrido en profundidad como `Stream`

Cuando hay que bajar por **todo el árbol** (subdirectorios incluidos), la
forma más directa es `Files.walk`, que devuelve un `Stream<Path>` perezoso:

```java
import java.util.stream.Stream;

Path raiz = Path.of("datos");

try (Stream<Path> rutas = Files.walk(raiz)) {
    rutas.filter(Files::isRegularFile)
         .filter(p -> p.toString().endsWith(".log"))
         .forEach(System.out::println);
}
```

`Files.walk` admite un segundo parámetro con la **profundidad máxima**:

```java
// Solo el directorio raíz y su primer nivel de hijos
try (Stream<Path> rutas = Files.walk(raiz, 1)) {
    rutas.forEach(System.out::println);
}
```

:::warning
`Files.walk` devuelve un `Stream` que mantiene un recurso del sistema
operativo (un descriptor de directorio) abierto mientras se consume. Debe
cerrarse siempre con `try-with-resources`; si no, se acumulan descriptores
sin liberar en procesos de larga duración.
:::

## `PathMatcher`: filtrar por patrón *glob*

Para reutilizar un mismo criterio de filtrado en varios sitios, conviene
compilar un `PathMatcher` una sola vez con `FileSystem.getPathMatcher`:

```java
import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;

PathMatcher soloLogs = FileSystems.getDefault()
        .getPathMatcher("glob:**/*.log");

try (Stream<Path> rutas = Files.walk(raiz)) {
    rutas.filter(Files::isRegularFile)
         .filter(soloLogs::matches)
         .forEach(System.out::println);
}
```

## `walkFileTree`: control fino con un `FileVisitor`

`Files.walk` es cómodo para consultas de solo lectura. Cuando el recorrido
necesita **actuar** en cada nodo — por ejemplo, borrar un árbol completo, o
distinguir explícitamente la entrada y la salida de cada directorio — se usa
`Files.walkFileTree` con un `FileVisitor`:

```java
import java.io.IOException;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.FileVisitResult;

Files.walkFileTree(raiz, new SimpleFileVisitor<Path>() {

    @Override
    public FileVisitResult visitFile(Path fichero, BasicFileAttributes attrs) {
        System.out.println("Fichero: " + fichero + " (" + attrs.size() + " bytes)");
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        System.out.println("Entrando en: " + dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
        System.out.println("Saliendo de: " + dir);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path fichero, IOException exc) {
        System.err.println("No se pudo acceder a " + fichero + ": " + exc.getMessage());
        return FileVisitResult.CONTINUE;
    }
});
```

Un uso típico de `postVisitDirectory` es **borrar un árbol completo**: hay
que borrar primero los ficheros y, al salir de cada directorio (cuando ya
está vacío), borrar el propio directorio:

```java
Files.walkFileTree(raiz, new SimpleFileVisitor<Path>() {
    @Override
    public FileVisitResult visitFile(Path fichero, BasicFileAttributes attrs)
            throws IOException {
        Files.delete(fichero);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException exc)
            throws IOException {
        Files.delete(dir);
        return FileVisitResult.CONTINUE;
    }
});
```

:::tip
Cuando el objetivo es solo **filtrar y recolectar** rutas, `Files.walk` con
`Stream` es más legible. Cuando hace falta **actuar** durante el recorrido
(borrar, distinguir entrada/salida de directorio, decidir si se desciende a
un subdirectorio o no), `walkFileTree` con `FileVisitor` da más control.
:::

## Atributos y metadatos

`Files` permite consultar atributos básicos sin crear un objeto completo:

```java
long tamano = Files.size(fichero);
java.nio.file.attribute.FileTime modificado = Files.getLastModifiedTime(fichero);
boolean esOculto = Files.isHidden(fichero);
```

Para leer **varios atributos a la vez** de forma eficiente (una sola
llamada al sistema en lugar de una por atributo), se usa
`BasicFileAttributes`:

```java
import java.nio.file.attribute.BasicFileAttributes;

BasicFileAttributes attrs = Files.readAttributes(fichero, BasicFileAttributes.class);

System.out.println("Tamaño: " + attrs.size());
System.out.println("Creado: " + attrs.creationTime());
System.out.println("Modificado: " + attrs.lastModifiedTime());
System.out.println("¿Es directorio?: " + attrs.isDirectory());
```

## Permisos POSIX

En sistemas POSIX (Linux, macOS), los permisos se consultan y modifican con
`PosixFileAttributes` y `PosixFilePermissions`:

```java
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

PosixFileAttributes posix =
        Files.readAttributes(fichero, PosixFileAttributes.class);

Set<PosixFilePermission> permisos = posix.permissions();
System.out.println(PosixFilePermissions.toString(permisos)); // p.ej. rw-r--r--

// Establecer permisos explícitos
Set<PosixFilePermission> soloLecturaPropietario =
        PosixFilePermissions.fromString("r--------");
Files.setPosixFilePermissions(fichero, soloLecturaPropietario);
```

:::note[«¿Por qué falla `PosixFileAttributes` en Windows?»]
Porque Windows no tiene un modelo de permisos POSIX (`rwx` por
propietario/grupo/otros); usa listas de control de acceso (ACL) distintas.
Intentar leer `PosixFileAttributes` en Windows lanza
`UnsupportedOperationException`. Si el código debe ser portable, comprueba
antes con `FileSystems.getDefault().supportedFileAttributeViews()`.
:::

<NextPageButton to="/tema-1/d-watchservice">
WatchService: vigilancia de un directorio
</NextPageButton>
