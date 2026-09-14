---
title: 'Presentación de la UD1: gestión de ficheros y directorios'
description: Objetivos y contenidos de la UD1, y por qué java.nio.file sustituye a java.io como forma de trabajar con el sistema de ficheros.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Presentación de la UD1: gestión de ficheros y directorios

La **UD1** ocupa 8 horas de la 1ª evaluación y evalúa el **RA1**, el primero
de los seis resultados de aprendizaje del módulo. Es la unidad más pequeña de
la evaluación, pero sienta una base que se reutiliza en el resto del curso:
antes de guardar datos en una base de datos relacional o documental,
necesitas saber manipular con soltura el sistema de ficheros del propio
sistema operativo.

## Objetivos de la unidad

Al terminar la UD1 debes ser capaz de:

- **Manipular el sistema de ficheros** mediante la API `java.nio.file`:
  crear, borrar, copiar, mover y recorrer ficheros y directorios.
- **Discriminar entre formas de acceso** — secuencial frente a aleatorio — y
  **justificar** cuál conviene según el problema.
- **Diseñar una jerarquía de excepciones propia**, adaptada al dominio de la
  aplicación, en lugar de propagar excepciones genéricas de la plataforma.

## De `java.io` a `java.nio.file`

Java arrastra dos generaciones de API para trabajar con ficheros:

| Generación | Paquete | Año | Estado |
|------------|---------|-----|--------|
| Primera | `java.io` (`File`) | 1996 | Vigente por compatibilidad, no recomendada para código nuevo |
| Segunda | `java.nio.file` (NIO.2) | 2011 (Java 7) | Estándar actual |

`java.io.File` tiene limitaciones conocidas: los métodos que fallan
(`delete()`, `mkdir()`, `renameTo()`...) devuelven `boolean` en lugar de
lanzar una excepción informativa, no distingue bien entre "no existe" y "no
tengo permiso", y no ofrece una forma nativa de recorrer árboles de
directorios grandes sin cargarlos enteros en memoria.

`java.nio.file` resuelve esto con tres piezas que verás en detalle en los
siguientes apartados:

- **`Path`**: representa una ruta de forma abstracta e independiente del
  sistema operativo.
- **`Files`**: clase de utilidades **estática** con las operaciones reales
  (crear, copiar, mover, leer atributos...), que lanza excepciones
  específicas y comprobables en lugar de devolver `false`.
- **`FileSystem`** / **`WatchService`**: acceso al sistema de ficheros
  subyacente y capacidad de **vigilar** cambios en un directorio sin hacer
  *polling* manual.

:::info
[Principio rector]

Todo el código que escribas a partir de ahora para tocar disco debe usar
`java.nio.file`. `java.io.File` solo aparece en este curso quedará como
referencia histórica de por qué existe la API moderna.
:::

## Qué vas a aprender en esta unidad

| Apartado | Contenido |
|----------|-----------|
| [a](/tema-1/a-presentacion-tema) | Presentación de la UD1 |
| [b](/tema-1/b-path-files) | `Path`, `Paths` y `Files`: crear, borrar, copiar, mover y renombrar |
| [c](/tema-1/c-recorrido-arboles) | Recorrido de árboles de directorios, atributos y permisos |
| [d](/tema-1/d-watchservice) | `WatchService`: vigilancia de un directorio |
| [e](/tema-1/e-acceso-secuencial-aleatorio) | Acceso secuencial frente a acceso aleatorio |
| [f](/tema-1/f-excepciones-config) | Excepciones propias y gestión de rutas por configuración |
| [g](/tema-1/g-streaming-binarios) | Streaming de ficheros binarios grandes |
| [h](/tema-1/h-actividades) | Actividades A1.1 – A1.4 |

Los ejemplos de código de esta unidad trabajan sobre ficheros y directorios
genéricos (documentos de texto, registros de log, directorios de prueba).
Las actividades, en cambio, ya te pedirán aplicar estos mismos conceptos
sobre el dominio del proyecto que arrancaste en la UD0.

<NextPageButton to="/tema-1/b-path-files">
Path, Paths y Files
</NextPageButton>
