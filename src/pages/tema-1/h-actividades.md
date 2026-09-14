---
title: 'Actividades de la UD1'
description: Actividades A1.1 a A1.4 de gestión de ficheros y directorios sobre el proyecto MULTAGAL, con sus criterios de aceptación.
---

# Actividades de la UD1

Cuatro actividades, 8 horas. Al terminarlas tendrás en MULTAGAL un componente
de archivado jerárquico de expedientes, un servicio de purga por
prescripción, un vigilante de directorio para las captaciones de radar y una
jerarquía de excepciones propia del dominio, todo ello accesible a través de
los controladores REST correspondientes.

:::info[Entrega]

El conjunto de la UD1 forma parte de la **ENTREGA 1** y se materializa como
una rama `hito/ud1-ficheros` fusionada en `main`. Evalúa **RA1**.

:::

---

## A1.1 — Componente `AlmacenExpedientes`

**Objetivo:** organizar el archivado de expedientes en una estructura de
directorios jerárquica y predecible.

### Tareas

1. Diseña la jerarquía de directorios `expedientes/<año>/<mes>/<provincia>/`
   a partir de la fecha del expediente y la provincia de la denuncia, usando
   `Path` y `Files.createDirectories` según el
   [apartado b](/tema-1/b-path-files).
2. Implementa `AlmacenExpedientes` con al menos:
   - `archivar(Expediente expediente)` — calcula la ruta jerárquica y
     escribe el expediente serializado en su interior.
   - `buscar(String numeroExpediente)` — localiza el fichero
     correspondiente sin necesidad de conocer de antemano año, mes o
     provincia.
   - `mover(String numeroExpediente, EstadoExpediente nuevoEstado)` — cuando
     el estado cambie, mueve el fichero a una subcarpeta que refleje el
     estado actual (por ejemplo, separando `activos/` de `resueltos/`).
3. Usa `Files.exists`, `Files.createDirectories` y `Files.move` con la
   opción adecuada para no perder datos si el destino ya existe.
4. Externaliza el directorio raíz de archivado (`expedientes/`) mediante
   `@ConfigurationProperties`, según el
   [apartado f](/tema-1/f-excepciones-config#gestión-de-rutas-por-configuración).
5. Expón `AlmacenExpedientes` a través de un `ExpedienteController` con, como
   mínimo, `GET /expedientes/{numero}/archivo` (devuelve dónde está
   archivado el expediente o 404 si no existe) y
   `POST /expedientes/{numero}/estado` (invoca `mover` con el nuevo estado
   recibido). El controlador delega toda la lógica en el servicio: no
   accede él mismo al sistema de ficheros.

### Entregable

Clase `AlmacenExpedientes` en el paquete de repositorio del proyecto, con
pruebas unitarias sobre un directorio temporal (no sobre rutas reales de tu
disco), y `ExpedienteController` con sus endpoints probados con
`MockMvc` o pruebas de integración equivalentes.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | La ruta de archivado se calcula, nunca se concatena con `+` |
| 2 | `archivar` crea los directorios intermedios que falten |
| 3 | `buscar` localiza un expediente sin recorrer manualmente todo el árbol a mano en el código cliente |
| 4 | El directorio raíz de archivado es configurable, no está escrito en el código |
| 5 | Las pruebas usan un directorio temporal y lo limpian al terminar |
| 6 | Los endpoints del controlador devuelven el código HTTP correcto en el caso favorable y en el expediente no encontrado |
| 7 | El controlador no contiene lógica de acceso a ficheros: solo delega en `AlmacenExpedientes` |

:::tip
Java ofrece `Files.createTempDirectory` para crear un directorio temporal
único en cada ejecución de las pruebas, sin interferir entre ejecuciones
paralelas.
:::

---

## A1.2 — Servicio de purga de expedientes prescritos

**Objetivo:** localizar y eliminar expedientes prescritos recorriendo el
árbol de archivo, aplicando la [regla R4](/tema-0/b-proyecto-multagal#r4--prescripción).

### Tareas

1. Implementa `ServicioPurgaExpedientes` que recorra el árbol creado en
   A1.1 con `Files.walk` o `Files.walkFileTree`
   ([apartado c](/tema-1/c-recorrido-arboles)) y localice los expedientes
   cuya fecha de comisión supere el plazo de prescripción según su
   gravedad (3 meses leves, 6 meses graves y muy graves).
2. Filtra los ficheros candidatos con un `PathMatcher` o con el propio
   `Stream` antes de decidir si un expediente debe purgarse.
3. Antes de borrar un directorio de mes/provincia que quede vacío tras la
   purga, elimínalo también, para no dejar estructura huérfana.
4. Registra en un log qué expedientes se han purgado y por qué regla,
   antes de borrarlos definitivamente.
5. Expón la purga a través de un endpoint `POST /expedientes/purga` en
   `ExpedienteController` (o un controlador propio, por ejemplo
   `PurgaController`) que invoque el servicio y devuelva cuántos
   expedientes se han purgado. Al ser una operación destructiva, no debe
   ejecutarse automáticamente en cada arranque: solo bajo demanda desde el
   endpoint.

### Entregable

Clase `ServicioPurgaExpedientes`, con pruebas que verifiquen que se
purgan los expedientes prescritos y se conservan los que no lo están, y el
endpoint de purga probado con `MockMvc` o equivalente.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El recorrido del árbol usa la API de `java.nio.file`, no `java.io.File` |
| 2 | Se distingue correctamente el plazo de 3 y de 6 meses según la gravedad |
| 3 | Un directorio que queda vacío tras la purga también se elimina |
| 4 | El expediente no prescrito no se toca |
| 5 | Queda constancia (log) de cada purga realizada, antes del borrado |
| 6 | El endpoint de purga responde con el número de expedientes purgados |
| 7 | La purga no se ejecuta automáticamente al arrancar la aplicación, solo al invocar el endpoint |

:::warning
[Sobre el borrado de directorios]

`Files.delete` sobre un directorio solo funciona si está **vacío**. Si tu
purga borra ficheros y directorios en la misma pasada, hazlo en el orden
correcto: primero los ficheros prescritos, y solo después comprueba si el
directorio que los contenía ha quedado vacío.
:::

---

## A1.3 — Vigilante del buzón de entrada de denuncias

**Objetivo:** detectar automáticamente los nuevos ficheros de captación
depositados por los radares mediante `WatchService`.

### Tareas

1. Implementa `VigilanteBuzonEntrada`, un componente que registre un
   directorio `buzon-entrada/` con `WatchService` según el
   [apartado d](/tema-1/d-watchservice), atendiendo como mínimo a
   `ENTRY_CREATE`.
2. Al detectar un fichero nuevo, invoca un punto de procesamiento (puede
   ser, de momento, un simple registro en log con la ruta detectada; el
   procesamiento real del contenido llegará en unidades posteriores).
3. Arranca el vigilante en un hilo propio al iniciar la aplicación y
   deténlo de forma ordenada al pararla.
4. Decide y documenta en el diario técnico si usas `take()` o `poll()` con
   tiempo de espera, y por qué.
5. Expón un endpoint de solo lectura `GET /buzon-entrada/estado` en un
   `BuzonEntradaController` que informe si el vigilante está activo y,
   como mínimo, la ruta o el nombre del último fichero detectado. El
   controlador consulta el estado del vigilante; no accede directamente al
   directorio ni al `WatchService`.

### Entregable

Componente `VigilanteBuzonEntrada` integrado en el arranque de la
aplicación, con una prueba que deposite un fichero en un directorio
temporal y compruebe que el vigilante lo detecta, y el endpoint de estado
probado con `MockMvc` o equivalente.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El vigilante se registra sobre el directorio correcto al arrancar |
| 2 | Se detecta la creación de un fichero nuevo en un tiempo razonable |
| 3 | La `WatchKey` se reinicia (`reset()`) tras procesar cada tanda de eventos |
| 4 | El vigilante se detiene de forma ordenada al parar la aplicación, sin dejar hilos colgados |
| 5 | La decisión `take()`/`poll()` está justificada en el diario técnico |
| 6 | El endpoint de estado refleja si el vigilante está activo y el último fichero detectado |

---

## A1.4 — Jerarquía de excepciones `MultagalException`

**Objetivo:** diseñar e implementar la jerarquía de excepciones propia del
proyecto, sustituyendo la propagación directa de excepciones de bajo nivel.

### Tareas

1. Diseña la jerarquía siguiendo el criterio del
   [apartado f](/tema-1/f-excepciones-config#diseñar-una-jerarquía-de-excepciones-de-dominio):
   `MultagalException` como raíz (no comprobada), con al menos
   `ExpedienteNoEncontradoException` y una excepción específica para
   errores de almacenamiento en disco (por ejemplo,
   `AlmacenamientoExpedienteException`).
2. Revisa `AlmacenExpedientes`, `ServicioPurgaExpedientes` y
   `VigilanteBuzonEntrada`: en cualquier punto donde captures una
   `IOException` o subclase de `java.nio.file`, tradúcela a la excepción de
   dominio correspondiente, conservando la causa original.
3. Usa `try-with-resources` en todos los puntos donde abras un recurso
   (`DirectoryStream`, canales, `WatchService`).
4. Documenta en el diario técnico por qué la raíz de la jerarquía es una
   excepción no comprobada y no una `Exception` comprobada.

### Entregable

Paquete `domain.exception` con la jerarquía completa, y el código de A1.1,
A1.2 y A1.3 actualizado para lanzar excepciones de dominio en lugar de
excepciones de `java.nio.file` sin traducir.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `MultagalException` extiende `RuntimeException`, no `Exception` |
| 2 | Cada excepción de dominio conserva la excepción original como causa |
| 3 | Ningún método público de `AlmacenExpedientes`, `ServicioPurgaExpedientes` o `VigilanteBuzonEntrada` deja escapar una `IOException` sin traducir |
| 4 | Todos los recursos `AutoCloseable` se abren con `try-with-resources` |
| 5 | El diario técnico justifica la decisión de diseño de la jerarquía |

---

## Autoevaluación de la unidad

Antes de dar la UD1 por cerrada, comprueba que sabes responder a esto **sin
consultar los apuntes**.

1. ¿Qué diferencia hay entre `Files.exists` y capturar la excepción que
   lanza la operación real? ¿Por qué la segunda es más fiable?
2. ¿Cuándo usarías `Files.walk` y cuándo `Files.walkFileTree`?
3. ¿Por qué `Files.delete` falla sobre un directorio no vacío, y en qué
   orden hay que borrar para vaciarlo primero?
4. ¿Qué obliga a hacer `reset()` sobre una `WatchKey`, y qué pasa si se
   olvida?
5. ¿Cuándo compensa usar acceso aleatorio (`RandomAccessFile` o
   `SeekableByteChannel`) en lugar de acceso secuencial?
6. ¿Qué diferencia hay entre una excepción comprobada y una no comprobada,
   y qué obliga el compilador en cada caso?
7. ¿Por qué conviene traducir las excepciones de `java.nio.file` a
   excepciones propias del dominio, en lugar de propagarlas tal cual?
8. ¿Qué ventaja tiene `@ConfigurationProperties` frente a repetir `@Value`
   para varias rutas relacionadas?
9. ¿Por qué `try-with-resources` es preferible a cerrar recursos a mano en
   un bloque `finally`?
10. ¿Por qué el controlador no debe acceder directamente al sistema de
    ficheros, y qué capa debería hacerlo en su lugar?

## Ampliación (opcional)

Para quien avance más rápido. No es exigible, pero se valora en el nivel de
sobresaliente:

- **Métricas del archivado.** Añade a `AlmacenExpedientes` un método que
  calcule cuántos expedientes hay archivados por provincia, recorriendo el
  árbol con `Files.walk` y agrupando los resultados.
- **Vigilante con filtro por extensión.** Amplía
  `VigilanteBuzonEntrada` para que ignore ficheros temporales o parciales
  (por ejemplo, los que terminan en `.tmp`) usando un `PathMatcher`.
- **Purga simulada (*dry run*).** Añade a `ServicioPurgaExpedientes` un
  modo que solo informe de qué se purgaría, sin borrar nada, útil para
  verificar la regla antes de aplicarla en serio.
