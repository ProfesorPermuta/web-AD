---
title: Streaming de ficheros binarios grandes
description: Cómo leer, escribir y copiar ficheros binarios (imágenes, adjuntos) que no caben enteros en memoria, usando flujos por bloques.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Streaming de ficheros binarios grandes

## El problema: cuando el fichero no cabe en memoria

Los atajos que has usado hasta ahora (`Files.readString`,
`Files.readAllLines`, `Files.readAllBytes`) tienen algo en común: **cargan
el fichero entero en un array o en una cadena antes de devolver nada**. Para
un fichero de configuración de unos pocos kilobytes eso es irrelevante. Para
una imagen de varios megabytes, un vídeo, o un adjunto binario de tamaño
desconocido, es un problema real:

- Cada fichero que procesas a la vez consume memoria proporcional a su
  tamaño **completo**, no a lo que realmente necesitas tener en memoria en
  cada instante.
- Si el servidor atiende muchas peticiones a la vez, cada una manejando un
  fichero grande de esta forma, la memoria se agota mucho antes de saturar
  la CPU o el disco.
- Puede lanzar directamente `OutOfMemoryError` si el fichero supera lo que
  la JVM tiene disponible, sin que haya forma razonable de recuperarse de
  ese error.

La solución es no tratar el fichero como "un bloque de datos que hay que
tener entero en memoria" sino como un **flujo**: una secuencia de bytes que
se lee o se escribe **por partes**, procesando cada parte y descartándola
antes de pasar a la siguiente.

## `InputStream` y `OutputStream`: la base de todo

`java.io.InputStream` y `java.io.OutputStream` son las abstracciones
clásicas para leer y escribir **secuencias de bytes** sin cargarlas
enteras. `java.nio.file.Files` ofrece fábricas que las abren directamente
sobre un `Path`:

```java
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

Path origen = Path.of("recursos", "imagen.jpg");
Path destino = Path.of("copias", "imagen-copia.jpg");

try (InputStream entrada = Files.newInputStream(origen);
     OutputStream salida = Files.newOutputStream(destino)) {

    byte[] buffer = new byte[8192]; // 8 KB: un tamaño de bloque habitual
    int leidos;

    while ((leidos = entrada.read(buffer)) != -1) {
        salida.write(buffer, 0, leidos);
    }
}
```

En cada vuelta del bucle, la memoria ocupada por este proceso es siempre la
del `buffer` (8 KB en el ejemplo), **sin importar si el fichero pesa 1 MB o
10 GB**. Esa es la propiedad clave del streaming: el coste en memoria es
constante, no proporcional al tamaño del fichero.

:::info
`Files.copy(origen, destino)`, que ya conoces del [apartado
b](/tema-1/b-path-files), hace internamente algo equivalente a este bucle
(o usa una copia a nivel de sistema operativo cuando puede). Para copiar
un fichero sin más, sigue siendo la opción más simple; el bucle manual
importa cuando necesitas **procesar** el contenido mientras lo mueves —
por ejemplo, calcular un hash, aplicar una transformación o comprobar el
tipo real del fichero byte a byte.
:::

### Por qué el tamaño del *buffer* importa

Un `buffer` demasiado pequeño (por ejemplo, de 1 byte) funciona, pero es
lento: cada llamada a `read`/`write` tiene un coste fijo, y hacer millones
de llamadas para mover pocos bytes cada vez desperdicia ese coste. Un
tamaño típico razonable está entre 4 KB y 64 KB; por debajo de eso el
rendimiento se resiente sin necesidad, y por encima el ahorro adicional es
marginal frente al uso de memoria que vuelve a crecer.

## `BufferedInputStream` / `BufferedOutputStream`

Envolver los flujos en una versión *bufferizada* añade un búfer interno
propio que reduce el número de operaciones reales de E/S, especialmente
cuando el código de más arriba lee o escribe en trozos pequeños (por
ejemplo, byte a byte o línea a línea):

```java
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;

try (InputStream entrada = new BufferedInputStream(Files.newInputStream(origen));
     OutputStream salida = new BufferedOutputStream(Files.newOutputStream(destino))) {

    byte[] buffer = new byte[8192];
    int leidos;
    while ((leidos = entrada.read(buffer)) != -1) {
        salida.write(buffer, 0, leidos);
    }
}
```

:::tip
`BufferedInputStream`/`BufferedOutputStream` y el `buffer` manual del
bucle no son la misma cosa ni se sustituyen entre sí: el búfer interno
optimiza el patrón de llamadas al sistema operativo, mientras que el
`buffer` del bucle es la unidad de trabajo de tu propio código. En la
práctica se combinan, como en el ejemplo anterior.
:::

## Procesar mientras se transfiere: un caso real

Un caso habitual es no limitarse a copiar el fichero, sino **calcular algo**
sobre su contenido mientras se transfiere — por ejemplo, un resumen
criptográfico (*hash*) para verificar más tarde que el fichero no se ha
corrompido ni manipulado:

```java
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public String copiarYCalcularHash(Path origen, Path destino) throws Exception {
    MessageDigest digest = MessageDigest.getInstance("SHA-256");

    try (InputStream entrada = Files.newInputStream(origen);
         OutputStream salida = Files.newOutputStream(destino)) {

        byte[] buffer = new byte[8192];
        int leidos;
        while ((leidos = entrada.read(buffer)) != -1) {
            salida.write(buffer, 0, leidos);
            digest.update(buffer, 0, leidos); // el hash se actualiza por bloques
        }
    }

    byte[] hash = digest.digest();
    return java.util.HexFormat.of().formatHex(hash);
}
```

En ningún momento existe en memoria una copia completa del fichero: cada
bloque se escribe al destino y se incorpora al cálculo del *hash* antes de
descartarlo y leer el siguiente.

## Canales NIO: `FileChannel` y `transferTo`

Para transferencias entre ficheros sin necesidad de procesar el contenido
byte a byte desde tu código, `java.nio.channels.FileChannel` ofrece
`transferTo`, que delega la copia en el sistema operativo cuando es
posible, evitando incluso pasar los datos por la JVM:

```java
import java.nio.channels.FileChannel;
import java.nio.file.StandardOpenOption;

try (FileChannel entrada = FileChannel.open(origen, StandardOpenOption.READ);
     FileChannel salida = FileChannel.open(destino,
             StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

    long tamano = entrada.size();
    long transferidos = 0;
    while (transferidos < tamano) {
        transferidos += entrada.transferTo(transferidos, tamano - transferidos, salida);
    }
}
```

:::warning
`transferTo` no garantiza transferir todo el fichero en una sola llamada:
puede transferir menos bytes de los pedidos (por ejemplo, en ficheros muy
grandes o según el sistema operativo). Por eso el bucle comprueba cuánto
se ha transferido en total y repite hasta completar el fichero, en lugar
de asumir que una única llamada basta.
:::

## Recibiendo un fichero grande por HTTP en Spring

Cuando el binario grande llega como parte de una petición (por ejemplo,
una subida de fichero), Spring expone el contenido como
`MultipartFile`, cuyo método `getInputStream()` es, precisamente, un
`InputStream` que se puede volcar a disco por bloques sin necesidad de
cargarlo entero en un array intermedio:

```java
import org.springframework.web.multipart.MultipartFile;

public void guardar(MultipartFile fichero, Path destino) throws java.io.IOException {
    try (InputStream entrada = fichero.getInputStream();
         OutputStream salida = Files.newOutputStream(destino)) {

        byte[] buffer = new byte[8192];
        int leidos;
        while ((leidos = entrada.read(buffer)) != -1) {
            salida.write(buffer, 0, leidos);
        }
    }
}
```

:::danger
Llamar a `fichero.getBytes()` sobre un `MultipartFile` carga el adjunto
**entero** en un array en memoria, exactamente el problema que este
apartado busca evitar. Para ficheros de tamaño no acotado (fotografías,
documentos adjuntos de origen externo), usa siempre `getInputStream()` y
un bucle de copia por bloques.
:::

:::note[«¿Por qué no basta con aumentar la memoria de la JVM?»]
Porque no resuelve el problema, solo retrasa cuándo aparece: el consumo de
memoria de este patrón crece con el tamaño del fichero **y** con el número
de ficheros procesados a la vez. Aumentar la memoria disponible cambia el
umbral en el que falla, pero el diseño por streaming hace que el consumo
sea prácticamente independiente del tamaño del fichero y del número de
transferencias simultáneas.
:::

<NextPageButton to="/tema-1/h-actividades">
Actividades de la unidad
</NextPageButton>
