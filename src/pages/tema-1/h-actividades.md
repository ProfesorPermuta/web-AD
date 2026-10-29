---
title: 'Actividades de la UD1'
description: Actividades A1.1 a A1.4 de gestión de ficheros y directorios sobre el proyecto MULTAGAL, con las pruebas a implementar y sus criterios de aceptación.
---

import DownloadButton from '@site/src/components/DownloadButton';

# Actividades de la UD1

Cuatro actividades, 8 horas. Al terminarlas tendrás en MULTAGAL un componente
de archivado jerárquico de expedientes, un servicio de purga por
prescripción, un vigilante de directorio que procesa en segundo plano los CSV
de captación de los radares y registra las multas detectadas, y una
jerarquía de excepciones propia del dominio, todo ello accesible a través de
los controladores REST correspondientes y cubierto por pruebas unitarias y de
integración.

:::info[Pruebas]
Cada actividad lista las pruebas que debes implementar. Se escriben con
JUnit 5, AssertJ y Mockito, usan `@TempDir` para cualquier fichero y se
ejecutan todas con `./mvnw test`. Una actividad sin sus pruebas no se
considera terminada.
:::

:::info[Entrega]

El conjunto de la UD1 forma parte de la **ENTREGA 1** y se materializa como
una rama `hito/ud1-ficheros` fusionada en `main`. Evalúa **RA1**.

:::

---

## A1.1 — Componente `AlmacenExpedientes`

**Objetivo:** organizar el archivado de expedientes en una estructura de
directorios jerárquica y predecible, guardando cada expediente como un
documento de texto legible, con el aspecto de la notificación que recibiría
la persona expedientada.

### Tareas

1. Diseña la jerarquía de directorios `expedientes/<año>/<mes>/<provincia>/`
   a partir de la fecha del expediente y la provincia de la denuncia, usando
   `Path` y `Files.createDirectories` según el
   [apartado b](/tema-1/b-path-files). Cada expediente se guarda en un
   fichero `<numeroExpediente>.txt`.
2. Crea una **plantilla de texto externa** (por ejemplo
   `src/main/resources/plantillas/expediente.txt`) con el formato de la
   notificación y marcadores de `String.format` (`%s`, `%n`, o mejor
   posicionales como `%1$s`) donde irán los datos. El código Java no debe
   contener el texto del documento. Ejemplo orientativo:

   ```text
   ==============================================================
     NOTIFICACIÓN DE EXPEDIENTE SANCIONADOR
   ==============================================================
   Expediente:        %1$s
   Fecha de incoación: %2$s
   Estado:            %3$s
   Instructor:        %4$s

   DATOS DEL DENUNCIADO
     Nombre:          %5$s
     Matrícula:       %6$s

   HECHOS DENUNCIADOS
     Lugar:           %7$s (%8$s)
     Infracción:      %9$s
     Importe:         %10$.2f €

   Dispone de 20 días hábiles para presentar alegaciones.
   ==============================================================
   ```

   Carga la plantilla una sola vez (con `Files.readString` o desde el
   classpath) y rellénala con `String.format` o `formatted(...)`, fijando un
   `Locale` explícito para fechas e importes.
3. Implementa `AlmacenExpedientes` con al menos:
   - `archivar(Expediente expediente)` — calcula la ruta jerárquica, genera
     el texto a partir de la plantilla y lo escribe en su interior con
     `Files.writeString` (codificación UTF-8 explícita).
   - `buscar(String numeroExpediente)` — localiza el fichero
     correspondiente (`<numeroExpediente>.txt`) sin necesidad de conocer de
     antemano año, mes o provincia.
   - `mover(String numeroExpediente, EstadoExpediente nuevoEstado)` — cuando
     el estado cambie, mueve el fichero a una subcarpeta que refleje el
     estado actual (por ejemplo, separando `activos/` de `resueltos/`).
4. Al cambiar el estado en `mover`, el contenido del documento debe
   reflejar el nuevo estado: regenera el texto desde la plantilla (no
   edites el fichero a base de reemplazos sobre el texto).
5. Usa `Files.exists`, `Files.createDirectories` y `Files.move` con la
   opción adecuada para no perder datos si el destino ya existe.
6. Externaliza mediante `@ConfigurationProperties` tanto el directorio raíz
   de archivado (`expedientes/`) como la ubicación de la plantilla, según el
   [apartado f](/tema-1/f-excepciones-config#gestión-de-rutas-por-configuración).
7. Expón `AlmacenExpedientes` a través de un `ExpedienteController` con, como
   mínimo:
   - `GET /expedientes/{numero}/archivo` — devuelve el contenido del
     documento archivado como `text/plain; charset=UTF-8`, de modo que el
     navegador lo muestre directamente (404 si no existe).
   - `POST /expedientes/{numero}/estado` — invoca `mover` con el nuevo
     estado recibido.

   El controlador delega toda la lógica en el servicio: no accede él mismo
   al sistema de ficheros. Ninguna respuesta debe exponer la ruta física
   del servidor.

### Entregable

Clase `AlmacenExpedientes`, la plantilla de texto del expediente y
`ExpedienteController`, junto con las pruebas descritas a continuación.

Un ADR que justifique en qué capa del proyecto (`controller`, `service`,
`repository`...) has colocado `AlmacenExpedientes` y, si lo separas, la
generación del texto desde la plantilla, y por qué descartaste las demás
([apartado f del tema 0](/tema-0/f-inyeccion-dependencias#la-regla-de-dependencia)).

### Pruebas unitarias

`AlmacenExpedientesTest` (JUnit 5 + AssertJ, sin contexto de Spring, con un
directorio `@TempDir` en lugar de rutas reales de tu disco):

1. `archivar` crea el fichero en `<raiz>/<año>/<mes>/<provincia>/<numero>.txt`
   aunque ninguno de los directorios intermedios exista.
2. El contenido generado incluye todos los datos del expediente, con la fecha
   y el importe (dos decimales) con el formato esperado, y se lee
   correctamente en UTF-8 con caracteres como «ñ» y tildes.
3. `buscar` devuelve la ruta de un expediente archivado en cualquier
   subcarpeta, y falla con `ExpedienteNoEncontradoException` (A1.4) si no
   existe.
4. `mover` cambia el fichero de carpeta y el texto del documento refleja el
   nuevo estado.
5. `mover` con un destino ya ocupado no pierde ningún dato.
6. `adjuntarFoto` (A1.3) deja `<numeroExpediente>.jpg` junto al `.txt`, y
   tras `mover` la foto sigue junto al documento en su nueva carpeta.

Los controladores no requieren pruebas automáticas en esta unidad.

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
| 8 | El formato del documento está en un fichero de plantilla externo; el código solo aporta los datos mediante `String.format` |
| 9 | El `.txt` generado contiene todos los datos del expediente, correctamente formateados (fechas, importe con dos decimales) y en UTF-8 |
| 10 | Tras `mover`, el documento archivado muestra el nuevo estado |
| 11 | El ADR indica en qué capa se ha colocado cada componente y justifica la elección frente a las alternativas, de acuerdo con la regla de dependencia |
| 12 | `GET .../archivo` responde con el texto del expediente y `Content-Type: text/plain; charset=UTF-8`, o `404` si no existe; ninguna respuesta expone la ruta física del servidor |
| 13 | Las pruebas 1 a 6 existen, se ejecutan sin Docker ni `@SpringBootTest` y pasan con `./mvnw test` |

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
   gravedad (3 meses leves, 6 meses graves y muy graves). Como los
   expedientes se archivan en formato de texto (A1.1), la fecha y la
   gravedad se obtienen leyendo las líneas correspondientes del `.txt`
   (por ejemplo con `Files.readAllLines`), o bien añadiéndolas como campos
   fácilmente localizables en la plantilla.
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

Clase `ServicioPurgaExpedientes` y el endpoint de purga, junto con las
pruebas descritas a continuación.

### Pruebas unitarias

`ServicioPurgaExpedientesTest` (JUnit 5 + AssertJ, `@TempDir`, con un
`Clock` fijo inyectado para que el resultado no dependa del día en que se
ejecuten):

1. Un expediente **leve** de hace 4 meses se purga; uno de hace 2 meses se
   conserva.
2. Un expediente **grave** o **muy grave** de hace 4 meses se conserva; uno
   de hace 7 meses se purga.
3. Casos límite: expediente leve exactamente en el límite de 3 meses y grave
   exactamente en el de 6 (decide y documenta si el límite es inclusivo).
4. Tras purgar el único expediente de un mes/provincia, el directorio queda
   eliminado; si quedaba otro expediente, el directorio se conserva.
5. Un árbol vacío devuelve `0` y no lanza excepción.
6. El log (o el colaborador que lo registra, simulado con Mockito) recibe la
   entrada de cada expediente **antes** de su borrado (`InOrder`).

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
| 8 | Las pruebas 1 a 6 existen y pasan con `./mvnw test`, sin depender de la fecha real del sistema |

:::warning
[Sobre el borrado de directorios]

`Files.delete` sobre un directorio solo funciona si está **vacío**. Si tu
purga borra ficheros y directorios en la misma pasada, hazlo en el orden
correcto: primero los ficheros prescritos, y solo después comprueba si el
directorio que los contenía ha quedado vacío.
:::

---

## A1.3 — Vigilante e ingesta del buzón de entrada de denuncias

**Objetivo:** detectar automáticamente los ficheros CSV de captación
depositados por los radares con `WatchService`, **procesarlos en un hilo
distinto al del vigilante** y **registrar cada multa** detectada en el
sistema de datos de MULTAGAL.

### Formato del fichero de captación

En esta unidad cada radar deposita un CSV de texto, en UTF-8, separado por
`;`, con una línea de cabecera y un registro por vehículo captado. Cada
registro lleva la información necesaria para construir una `Denuncia` con su
`Radar`, su `Vehiculo` y su `Infraccion`:

```text
idRadar;tipoRadar;ubicacionRadar;limiteVelocidad;fechaHora;lugar;provincia;latitud;longitud;matricula;marca;modelo;tipoVehiculo;velocidadMedida
R-0042;FIJO;Ronda de Outeiro;50;2026-03-14T10:32:05;Ronda de Outeiro 120, A Coruña;A Coruña;43.3623;-8.4115;1234ABC;Seat;Ibiza;TURISMO;87
R-0107;MOVIL;N-640 km 3;100;2026-03-14T11:05:12;N-640 km 3, Lugo;Lugo;43.0097;-7.5568;4455QRS;Yamaha;MT-07;MOTOCICLETA;140
```

| Columna | Se usa para | Validación |
|---------|-------------|------------|
| `idRadar` | `Radar.identificador` y `Denuncia.agenteODispositivo` | no vacío |
| `tipoRadar` | `Radar.tipo` | `FIJO` o `MOVIL` |
| `ubicacionRadar` | `Radar.ubicacion` | no vacío |
| `limiteVelocidad` | `Radar.limiteVelocidadControlado` | entero positivo |
| `fechaHora` | `Denuncia.fechaHora` | ISO-8601 (`yyyy-MM-ddTHH:mm:ss`) |
| `lugar` | `Denuncia.lugar` | no vacío |
| `provincia` | directorio de archivado de A1.1 | no vacío |
| `latitud`, `longitud` | `Denuncia.coordenadas` | decimales con punto; latitud entre -90 y 90, longitud entre -180 y 180 |
| `matricula` | `Vehiculo.matricula` | no vacío |
| `marca`, `modelo` | `Vehiculo.marca`, `Vehiculo.modelo` | no vacíos |
| `tipoVehiculo` | `Vehiculo.tipo` | `TURISMO` o `MOTOCICLETA` |
| `velocidadMedida` | decidir si hay infracción | entero positivo |

Datos de la `Denuncia` que **no** vienen en el CSV y cómo se completan:

- `tipoDenunciante` es siempre `RADAR`, y `agenteODispositivo` el `idRadar`.
- `radarId` / `radar`: se busca el radar por su identificador; si no existe,
  se da de alta con los datos del CSV.
- `vehiculoId` / `vehiculo`: se busca por matrícula; si no existe, se da de
  alta. El resto de sus datos (fecha de matriculación, ITV) quedan sin
  informar.
- `infraccionId` / `infraccion`: se obtiene del catálogo según el exceso de
  velocidad (tabla siguiente).
- `conductorId` / `conductor`: **nulo**. Un radar capta una matrícula, no una
  persona; la identificación del conductor es un paso posterior.

Solo se sanciona la captación cuya `velocidadMedida` supera
`limiteVelocidad`. Para esta actividad usa este catálogo simplificado (no es
la normativa real), cargado al arrancar en el repositorio en memoria:

| Exceso sobre el límite | Código | Gravedad | Importe | Puntos |
|------------------------|--------|----------|---------|--------|
| hasta 20 km/h | `VEL-L` | `LEVE` | 100,00 € | 0 |
| de 21 a 40 km/h | `VEL-G` | `GRAVE` | 300,00 € | 2 |
| más de 40 km/h | `VEL-MG` | `MUY_GRAVE` | 500,00 € | 4 |

El siguiente fichero de ejemplo contiene 15 registros: 7 infracciones (de las
tres gravedades, en dos radares), 2 captaciones que no se sancionan (una por
debajo del límite y otra justo en el límite) y 6 registros inválidos (fecha
mal formada, velocidad no numérica, columnas de menos, velocidad negativa,
tipo de vehículo desconocido y latitud fuera de rango). Úsalo como base de tus
pruebas y amplíalo si lo necesitas.

<DownloadButton file="plantillas/captaciones-radar-ejemplo.csv" filename="captaciones-radar-ejemplo.csv">
Descargar CSV de ejemplo
</DownloadButton>

### Fotografías de las captaciones

Además del CSV, el radar deposita **una fotografía por cada vehículo
captado** en otro directorio, `buzon-entrada/fotos/`, en formato JPEG y con
el nombre `<matricula>_<idRadar>.jpg`. Para el ejemplo anterior, las
infracciones generarían ficheros como `1234ABC_R-0042.jpg` o
`2468RST_R-0042.jpg`.

- El vigilante **solo vigila el directorio de los CSV**: el CSV es el que
  dispara el procesamiento. Las fotos se buscan por nombre cuando se procesa
  cada registro.
- Solo hay foto de las captaciones que se sancionan, pero pueden llegar
  fotos de vehículos que no lleguen a sancionarse: no es un error.
- Si la foto de una infracción **no existe** (o aún no ha terminado de
  subirse), el expediente se crea igualmente y se deja constancia en el log
  y en el contador de «expedientes sin foto». Documenta en el diario técnico
  esta decisión y la alternativa que has descartado.

:::info[Formato provisional]
Aquí lees el CSV a mano (`BufferedReader` y `String.split`). En la UD2
(actividad A2.1) lo sustituirás por un lector robusto con OpenCSV y por el
formato real del fabricante; el diseño que hagas ahora debe permitir ese
cambio sin tocar el vigilante.
:::

### Tareas

1. Implementa `VigilanteBuzonEntrada`, un componente que registre un
   directorio `buzon-entrada/` (configurable, como en
   [A1.1](#a11--componente-almacenexpedientes)) con `WatchService` según el
   [apartado d](/tema-1/d-watchservice), atendiendo como mínimo a
   `ENTRY_CREATE`. Ignora todo lo que no sea un `*.csv`.
2. El hilo del vigilante **solo detecta**: no lee ni procesa el contenido.
   Por cada CSV nuevo entrega su ruta a un `ExecutorService` (por ejemplo,
   un *pool* fijo pequeño) y vuelve de inmediato a esperar eventos, de modo
   que un fichero grande no retrase la detección de los siguientes.
3. Implementa `ProcesadorCapturas`, que ejecuta cada tarea del *pool* y:
   1. Espera a que el fichero esté completo (por ejemplo, comprobando que
      su tamaño se mantiene estable; véase la nota del apartado d).
   2. Lee el CSV línea a línea con `Files.newBufferedReader` (UTF-8).
   3. Valida cada registro según la tabla de columnas (número de columnas,
      enumerados, fecha ISO, rangos de coordenadas, velocidades numéricas y
      positivas). Un registro inválido **no aborta** el
      fichero: se registra en el log con su número de línea y se continúa.
   4. Descarta las captaciones que no superan el límite.
   5. Por cada captación infractora, **crea y registra el expediente**
      invocando `ServicioExpedientes.registrar` (A0.4), a partir de la
      `Denuncia` completa (radar, vehículo e infracción resueltos como se
      indica arriba, conductor nulo) y con el importe del catálogo, y lo **archiva** con
      `AlmacenExpedientes.archivar` (A1.1). Genera el número de
      expediente de forma que dos hilos no puedan repetirlo.
   6. Busca la fotografía `buzon-entrada/fotos/<matricula>_<idRadar>.jpg`
      (construye la ruta con `Path.resolve`, comprobando que el nombre no
      contiene separadores de ruta ni `..`). Si existe, **muévela** al
      directorio del expediente archivado, junto a su `.txt`, con el nombre
      `<numeroExpediente>.jpg`. Añade a `AlmacenExpedientes` un método
      (por ejemplo `adjuntarFoto(numeroExpediente, rutaFoto)`) para ello;
      el procesador no mueve ficheros del archivo por su cuenta. Si el
      expediente cambia de carpeta en `mover`, la foto debe ir con él.
   7. Cuando termina, mueve el CSV a `buzon-entrada/procesados/`; si el
      fichero entero es ilegible, a `buzon-entrada/errores/`. Nunca lo
      dejes en el buzón, o se reprocesaría al reiniciar.
4. Registra en el log, al terminar cada fichero, un resumen: registros
   leídos, expedientes creados, registros inválidos y descartados por no
   superar el límite, y expedientes con y sin foto. El log es información adicional: **no sustituye** al
   registro de la multa en el sistema de datos.
5. Arranca el vigilante en un hilo propio al iniciar la aplicación y
   deténlo de forma ordenada al pararla: cierra el `WatchService`, llama a
   `shutdown()` sobre el *pool* y espera con `awaitTermination` a que
   terminen las tareas en curso.
6. Decide y documenta en el diario técnico si usas `take()` o `poll()` con
   tiempo de espera, y por qué.
7. Expón un endpoint de solo lectura `GET /buzon-entrada/estado` en un
   `BuzonEntradaController` que informe de si el vigilante está activo, el
   último fichero detectado y los contadores acumulados (ficheros
   procesados, expedientes creados, registros inválidos, expedientes sin
   foto). El controlador
   consulta el estado del vigilante y del procesador; no accede
   directamente al directorio ni al `WatchService`.

### Entregable

`VigilanteBuzonEntrada`, `ProcesadorCapturas` y `BuzonEntradaController`
integrados en el arranque de la aplicación, un CSV de ejemplo con registros
válidos, inválidos e infracciones de las tres gravedades, algunas fotos de
prueba generadas por ti (cualquier JPEG pequeño sin datos reales) y las pruebas
descritas a continuación.

### Pruebas unitarias

`ProcesadorCapturasTest` (JUnit 5 + AssertJ, `@TempDir`; `ServicioExpedientes`
y `AlmacenExpedientes` simulados con Mockito; sin hilos ni `WatchService`:
se invoca el procesador directamente):

1. Un CSV con tres captaciones, una de ellas sin superar el límite, crea
   **exactamente dos** expedientes (`verify(servicio, times(2))`).
2. Cada tramo del catálogo produce la infracción y el importe correctos;
   cubre los límites de 20/21 y de 40/41 km/h. Un exceso de 0 km/h no
   sanciona.
3. La `Denuncia` creada tiene `tipoDenunciante = RADAR`,
   `agenteODispositivo` igual al `idRadar`, coordenadas, lugar, radar y
   vehículo del CSV, y conductor nulo.
4. Una línea con columnas de menos, fecha mal formada, velocidad no
   numérica, tipo de vehículo desconocido o coordenadas fuera de rango se
   cuenta como inválida y **no impide** procesar las demás.
5. Cada expediente registrado se archiva (`InOrder`: primero `registrar`,
   luego `archivar`).
6. Un CSV correcto acaba en `procesados/` y deja de estar en el buzón; uno
   ilegible acaba en `errores/`.
7. Un CSV solo con cabecera o vacío no crea expedientes ni lanza excepción.
8. Con la foto `<matricula>_<idRadar>.jpg` presente, se pide adjuntarla al
   expediente correspondiente y deja de estar en `fotos/`.
9. Sin foto, el expediente se crea igualmente y se cuenta como «sin foto».
10. La foto de otro radar con la misma matrícula no se asocia a la captación
   (el nombre incluye el radar).

`VigilanteBuzonEntradaTest` (con un directorio `@TempDir` real y un
procesador simulado):

11. Al copiar un `.csv` en el directorio vigilado, el procesador recibe su
   ruta. Espera el resultado con **Awaitility** (`await().atMost(...)`), no
   con `Thread.sleep`.
12. Un fichero `.txt` o un `.csv.tmp` depositado no llega al procesador.
13. Tras depositar dos CSV seguidos, **ambos** llegan al procesador (prueba
   de que se hace `reset()`).
14. `detener()` cierra el vigilante y el *pool*; el hilo ya no está vivo
    (`Thread.isAlive`) pasado un tiempo razonable.

`IngestaBuzonIT` (integración, `@SpringBootTest` con el directorio del buzón
apuntando a un `@TempDir` mediante `@DynamicPropertySource`):

15. Se deposita el CSV de ejemplo; con Awaitility se comprueba que
    `ServicioExpedientes.obtener(...)` encuentra los expedientes esperados,
    que existen sus `.txt` archivados y que el CSV se ha movido a
    `procesados/`.
16. Se comprueba que la ejecución del procesamiento ocurre en un hilo
    distinto del hilo del vigilante (por ejemplo, registrando el nombre del
    hilo con un *pool* con `ThreadFactory` propia).

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El vigilante se registra sobre el directorio correcto al arrancar |
| 2 | Se detecta la creación de un fichero CSV nuevo en un tiempo razonable; los que no son CSV se ignoran |
| 3 | La `WatchKey` se reinicia (`reset()`) tras procesar cada tanda de eventos |
| 4 | El procesamiento del CSV se hace en un hilo distinto al del vigilante, que no se bloquea mientras tanto |
| 5 | Cada captación que supera el límite queda registrada como expediente en el sistema de datos y archivada en disco, con la gravedad y el importe correctos |
| 6 | Un registro inválido se registra en el log con su línea y no impide procesar el resto del fichero |
| 7 | El CSV procesado se mueve a `procesados/` (o `errores/`) y no se reprocesa |
| 8 | El vigilante y el *pool* se detienen de forma ordenada al parar la aplicación, sin hilos colgados |
| 9 | La decisión `take()`/`poll()` está justificada en el diario técnico |
| 10 | El endpoint de estado refleja si el vigilante está activo, el último fichero detectado y los contadores |
| 11 | La fotografía `<matricula>_<idRadar>.jpg` se asocia al expediente de su captación y se archiva junto a su `.txt`; sin foto, el expediente se crea igualmente y queda contado |
| 12 | Las pruebas 1 a 16 existen y pasan con `./mvnw test`; ninguna usa `Thread.sleep` para esperar |

:::warning[Concurrencia]
`ServicioExpedientes` y `RepositorioExpedientesMemoria` serán invocados
desde varios hilos. Comprueba que el repositorio (basado en
`ConcurrentHashMap`) y la generación del número de expediente son seguros
ante accesos concurrentes, y que los contadores del estado usan
`AtomicInteger`/`LongAdder` o equivalente.
:::

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
2. Revisa `AlmacenExpedientes`, `ServicioPurgaExpedientes`,
   `VigilanteBuzonEntrada` y `ProcesadorCapturas`: en cualquier punto donde captures una
   `IOException` o subclase de `java.nio.file`, tradúcela a la excepción de
   dominio correspondiente, conservando la causa original.
3. Usa `try-with-resources` en todos los puntos donde abras un recurso
   (`DirectoryStream`, canales, `WatchService`).
4. Documenta en el diario técnico por qué la raíz de la jerarquía es una
   excepción no comprobada y no una `Exception` comprobada.

### Entregable

Paquete `domain.exception` con la jerarquía completa, y el código de A1.1,
A1.2 y A1.3 actualizado para lanzar excepciones de dominio en lugar de
excepciones de `java.nio.file` sin traducir, junto con las pruebas
descritas a continuación.

### Pruebas unitarias

`MultagalExceptionTest` y pruebas de traducción (JUnit 5 + AssertJ,
`@TempDir`):

1. `ExpedienteNoEncontradoException` y `AlmacenamientoExpedienteException`
   son instancias de `MultagalException` y de `RuntimeException`.
2. Una excepción de almacenamiento creada con una `IOException` como causa
   la conserva (`getCause()`) y su mensaje identifica el expediente o la
   ruta lógica, no una ruta física completa.
3. `AlmacenExpedientes.archivar` sobre un destino imposible (por ejemplo,
   un fichero donde debería haber un directorio) lanza
   `AlmacenamientoExpedienteException` con la `IOException` original como
   causa, no la `IOException` directamente.
4. `AlmacenExpedientes.buscar` de un expediente inexistente lanza
   `ExpedienteNoEncontradoException`.
5. Un fallo de E/S durante la purga (`ServicioPurgaExpedientes`) se
   traduce a la excepción de dominio.
6. `ProcesadorCapturas` ante un CSV ilegible no propaga la excepción al
   hilo del *pool*: la traduce, la registra y mueve el fichero a `errores/`.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `MultagalException` extiende `RuntimeException`, no `Exception` |
| 2 | Cada excepción de dominio conserva la excepción original como causa |
| 3 | Ningún método público de `AlmacenExpedientes`, `ServicioPurgaExpedientes`, `VigilanteBuzonEntrada` o `ProcesadorCapturas` deja escapar una `IOException` sin traducir |
| 4 | Todos los recursos `AutoCloseable` se abren con `try-with-resources` |
| 5 | El diario técnico justifica la decisión de diseño de la jerarquía |
| 6 | Las pruebas 1 a 6 existen y pasan con `./mvnw test` |

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
11. ¿Por qué el hilo que vigila el directorio no debe procesar el contenido
    de los ficheros? ¿Qué pasaría con los eventos mientras tanto?
12. ¿Qué problemas de concurrencia aparecen cuando varios hilos registran
    expedientes a la vez, y cómo los has evitado?
13. ¿Por qué una prueba que espera a un hilo en segundo plano debe usar
    Awaitility y no `Thread.sleep`?

## Ampliación (opcional)

Para quien avance más rápido. No es exigible, pero se valora en el nivel de
sobresaliente:

- **Métricas del archivado.** Añade a `AlmacenExpedientes` un método que
  calcule cuántos expedientes hay archivados por provincia, recorriendo el
  árbol con `Files.walk` y agrupando los resultados.
- **Filtro con `PathMatcher`.** Sustituye la comprobación de extensión de
  `VigilanteBuzonEntrada` por un `PathMatcher` configurable (p. ej.
  `glob:*.csv`) que ignore también ficheros temporales o parciales.
- **Reintentos.** Si el registro de una multa falla por un error transitorio,
  reintenta con espera creciente antes de enviar el fichero a `errores/`.
- **Purga simulada (*dry run*).** Añade a `ServicioPurgaExpedientes` un
  modo que solo informe de qué se purgaría, sin borrar nada, útil para
  verificar la regla antes de aplicarla en serio.
