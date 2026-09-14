---
title: Acceso secuencial frente a acceso aleatorio
description: Diferencias, ventajas e inconvenientes entre leer un fichero de principio a fin o acceder directamente a una posición concreta.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Acceso secuencial frente a acceso aleatorio

## Dos formas de leer un fichero

Hasta ahora, todas las operaciones vistas (`Files.readString`,
`Files.readAllLines`, un `Stream` de líneas) son de **acceso secuencial**:
se lee el fichero en orden, de principio a fin, sin poder "saltar" a una
posición arbitraria sin antes pasar por las anteriores.

El **acceso aleatorio** permite posicionarse directamente en cualquier byte
del fichero sin leer lo anterior, y leer o escribir desde ahí.

| | Acceso secuencial | Acceso aleatorio |
|---|---|---|
| Orden de lectura/escritura | De principio a fin | Cualquier posición, en cualquier orden |
| API típica | `Files.readString`, `BufferedReader`, `Stream<String>` | `RandomAccessFile`, `SeekableByteChannel` |
| Coste de acceder a un dato concreto | Proporcional a su posición en el fichero | Constante (si se conoce el desplazamiento) |
| Caso de uso natural | Procesar todo el contenido: logs, informes, exportaciones | Registros de tamaño fijo, índices, ficheros muy grandes donde solo interesa un fragmento |

## `RandomAccessFile`

`RandomAccessFile` es la clase clásica (anterior a NIO.2) para acceso
aleatorio. Permite moverse con `seek(posicion)` y leer o escribir desde ahí:

```java
import java.io.RandomAccessFile;

try (RandomAccessFile raf = new RandomAccessFile("registros.dat", "rw")) {
    long tamanoRegistro = 64; // bytes por registro, todos de igual longitud
    int indice = 3;

    raf.seek(indice * tamanoRegistro); // saltar directamente al registro 3
    byte[] buffer = new byte[(int) tamanoRegistro];
    raf.readFully(buffer);

    String contenido = new String(buffer, java.nio.charset.StandardCharsets.UTF_8).trim();
    System.out.println(contenido);
}
```

Esto solo funciona de forma práctica cuando los **registros tienen tamaño
fijo**: si cada registro ocupa siempre 64 bytes, calcular la posición del
registro *n* es una simple multiplicación. Con registros de longitud
variable, hace falta un índice aparte que guarde el desplazamiento de cada
uno.

`RandomAccessFile` también permite **escribir** en mitad de un fichero sin
reescribirlo entero:

```java
try (RandomAccessFile raf = new RandomAccessFile("registros.dat", "rw")) {
    raf.seek(2 * 64); // posición del registro 2
    raf.write("dato actualizado".getBytes(java.nio.charset.StandardCharsets.UTF_8));
}
```

:::warning
Escribir en mitad de un fichero **sobrescribe** los bytes existentes en esa
posición; no los desplaza. Si el nuevo contenido no ocupa exactamente el
mismo número de bytes que el anterior, se corrompe la estructura del
fichero. El acceso aleatorio exige que el formato del fichero esté diseñado
para ello (registros de tamaño fijo, o un índice que localice cada
registro).
:::

## `SeekableByteChannel`: la versión NIO.2

`java.nio.file.Files.newByteChannel` devuelve un `SeekableByteChannel`, la
forma moderna de hacer lo mismo integrada con `Path`:

```java
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

Path fichero = Path.of("registros.dat");

try (SeekableByteChannel canal = Files.newByteChannel(
        fichero, StandardOpenOption.READ, StandardOpenOption.WRITE)) {

    long tamanoRegistro = 64;
    int indice = 3;

    canal.position(indice * tamanoRegistro);

    ByteBuffer buffer = ByteBuffer.allocate((int) tamanoRegistro);
    canal.read(buffer);
    buffer.flip();

    byte[] datos = new byte[buffer.remaining()];
    buffer.get(datos);
    System.out.println(new String(datos, java.nio.charset.StandardCharsets.UTF_8).trim());
}
```

`SeekableByteChannel` se integra mejor con el resto de `java.nio` (buffers,
canales asíncronos) y es la opción preferible en código nuevo; `RandomAccessFile`
sigue siendo válida y se encuentra a menudo en código existente.

## Cuándo elegir cada una

- **Acceso secuencial** cuando el objetivo es procesar el fichero completo,
  el orden natural de lectura coincide con el orden de procesamiento, o el
  fichero es de texto con líneas de longitud variable (logs, CSV sin
  índice, documentos).
- **Acceso aleatorio** cuando solo interesa una parte concreta de un
  fichero grande, se necesita actualizar un registro puntual sin reescribir
  todo el fichero, o el formato ya está diseñado con registros de tamaño
  fijo o con un índice de posiciones.

:::info
El acceso aleatorio no es "más rápido" en general: es más rápido **para
llegar a un punto concreto**. Si de todas formas hay que leer el fichero
entero, el acceso secuencial suele ser más simple y no menos eficiente.
:::

<NextPageButton to="/tema-1/f-excepciones-config">
Excepciones propias y configuración de rutas
</NextPageButton>
