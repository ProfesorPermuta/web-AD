---
title: 'Actividades de la UD1'
description: Actividades A1.1 a A1.5 de gestión de ficheros y directorios sobre el proyecto MULTAGAL, con las pruebas a implementar y sus criterios de aceptación.
---

import DownloadButton from '@site/src/components/DownloadButton';

# Actividades de la UD1

Cinco actividades, 8 horas. Al terminarlas tendrás en MULTAGAL un componente
de archivado jerárquico de expedientes, un servicio de purga por
prescripción, un servicio que crea denuncias a partir de una petición
(`DenunciaRequest`) con su repositorio, un vigilante de directorio que
procesa en segundo plano los CSV de captación de los radares y registra las
multas detectadas, y una jerarquía de excepciones propia del dominio, todo
ello accesible a través de los controladores REST correspondientes y cubierto
por pruebas unitarias y de integración.

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

## Decisiones comunes a toda la unidad

Estas decisiones valen para las cinco actividades. Están fijadas aquí para
que todas las soluciones coincidan en lo que se puede observar desde fuera
(rutas, códigos HTTP, formato de los documentos); lo que no se fija aquí
(capas, nombres internos, estructura de clases) lo decides tú y lo
justificas en el diario técnico.

### Orden de trabajo recomendado

A1.5 no es una actividad aislada: es una **revisión transversal**. Empieza
por diseñar la jerarquía de excepciones (tareas 1 y 4 de A1.5 y sus pruebas 1
y 2), construye después A1.1, A1.2, A1.3 y A1.4 lanzando ya excepciones de
dominio, y cierra con A1.5 comprobando que ninguna `IOException` se escapa sin
traducir (tareas 2 y 3 y pruebas 3 a 6). A1.3 (denuncias) no toca ficheros,
pero A1.4 depende de ella: constrúyela antes del vigilante.

### Rutas HTTP

El proyecto ya expone sus recursos bajo el prefijo `/api`. Todos los
endpoints de esta unidad cuelgan de él (`/api/expedientes/...`,
`/api/denuncias`, `/api/buzon-entrada/...`). Las rutas del enunciado sin `/api` se entienden con
ese prefijo.

### Estructura del archivo en disco

Un expediente se guarda según su estado:

```text
<raíz>/<año>/<mes>/<provincia>/<numero>.txt              expediente en tramitación
<raíz>/<año>/<mes>/<provincia>/resueltos/<numero>.txt    expediente resuelto
<raíz>/<año>/<mes>/<provincia>/<numero>.jpg              fotografía (junto a su .txt)
```

- `<año>` y `<mes>` (dos cifras) salen de la `fechaIncoacion` del expediente.
- `<provincia>` sale de `Denuncia.provincia` (campo que debes añadir al modelo
  `Denuncia`, porque hoy no existe). Si el expediente no tiene denuncia o la
  provincia está vacía, se usa la carpeta `sin-provincia`.
- Están en `resueltos/` los expedientes en estado `RESUELTO`, `FIRME`,
  `PRESCRITO` o `SOBRESEIDO`. Los demás (`INCOADO`, `NOTIFICADO`,
  `EN_PLAZO_ALEGACIONES`, `ALEGADO`) están en tramitación.
- La foto va siempre junto al `.txt` y viaja con él cuando el expediente
  cambia de carpeta.

### Nada se sobrescribe

Ninguna operación de ficheros de esta unidad puede sobrescribir un fichero
existente ni perder datos. Si el destino está ocupado:

- `archivar` sobre un expediente ya archivado y `mover` hacia un destino
  ocupado lanzan `ExpedienteYaExistenteException` y no tocan ningún fichero.
- `adjuntarFoto` sobre una foto ya adjuntada lanza
  `AlmacenamientoExpedienteException`.

### Errores HTTP

Un único punto (`@RestControllerAdvice`) traduce las excepciones de dominio:

| Excepción | Código |
|-----------|--------|
| `NoEncontradaException` y sus subclases (`ExpedienteNoEncontradoException`) | `404` |
| `YaExistenteException` y sus subclases (`ExpedienteYaExistenteException`) | `409` |
| `AlmacenamientoExpedienteException` | `500` |

El cuerpo de error es `{"mensaje": "..."}`. Ese mensaje nunca contiene rutas
físicas del servidor (véase A1.5).

### Formato del documento del expediente

- Fechas en `dd/MM/yyyy` y `Locale` `es-ES` fijo, con independencia de la
  configuración regional del servidor.
- Importe con dos decimales y coma decimal, seguido de ` €` (por ejemplo
  `60,00 €`).
- El estado y la gravedad se escriben con el nombre del enumerado cambiando
  `_` por espacio (`EN PLAZO ALEGACIONES`, `MUY GRAVE`).
- Un dato que el expediente no tiene (por ejemplo, el nombre del denunciado
  de un expediente sin conductor) se escribe como `-`.

---

## A1.1 — Componente `AlmacenExpedientes`

**Objetivo:** organizar el archivado de expedientes en una estructura de
directorios jerárquica y predecible, guardando cada expediente como un
documento de texto legible, con el aspecto de la notificación que recibiría
la persona expedientada.

### Tareas

1. Implementa la jerarquía de directorios descrita en
   [Estructura del archivo en disco](#estructura-del-archivo-en-disco),
   usando `Path` y `Files.createDirectories` según el
   [apartado b](/tema-1/b-path-files). Cada expediente se guarda en un
   fichero `<numeroExpediente>.txt`. Añade el campo `provincia` a `Denuncia`.
2. Crea una **plantilla de texto externa** (por ejemplo
   `src/main/resources/plantillas/expediente.txt`) con el formato de la
   notificación y marcadores de `String.format` (`%s`, `%n`, o mejor
   posicionales como `%1$s`) donde irán los datos. El código Java no debe
   contener el texto del documento. Ejemplo orientativo:

   ```text
   ==============================================================
     NOTIFICACIÓN DE EXPEDIENTE SANCIONADOR
   ==============================================================
   Expediente:          %1$s
   Fecha de incoación:  %2$s
   Estado:              %3$s
   Instructor:          %4$s

   DATOS DEL DENUNCIADO
     Nombre:            %5$s
     Matrícula:         %6$s

   HECHOS DENUNCIADOS
     Lugar:             %7$s (%8$s)
     Infracción:        %9$s
     Gravedad:          %12$s
     Fecha de los hechos: %11$s
     Importe:           %10$.2f €

   Dispone de 20 días hábiles para presentar alegaciones.
   ==============================================================
   ```

   Las posiciones son: 1 número, 2 fecha de incoación, 3 estado, 4 instructor,
   5 nombre del denunciado, 6 matrícula, 7 lugar, 8 provincia, 9 infracción,
   10 importe, 11 fecha de los hechos (`Denuncia.fechaHora`, solo la fecha) y
   12 gravedad de la infracción. Los marcadores posicionales permiten
   colocarlos en el orden que quieras.

   :::warning[Contrato con A1.2]
   Las líneas que empiezan por `Fecha de los hechos:` y `Gravedad:` las lee la
   purga de A1.2. Mantén esas dos etiquetas exactamente así.
   :::

   Carga la plantilla una sola vez (con `Files.readString` o desde el
   classpath) y rellénala con `String.format` o `formatted(...)`, con el
   formato descrito en
   [Formato del documento del expediente](#formato-del-documento-del-expediente).
3. Implementa `AlmacenExpedientes` con al menos:
   - `archivar(Expediente expediente)` — calcula la ruta jerárquica, genera
     el texto a partir de la plantilla y lo escribe en su interior con
     `Files.writeString` (codificación UTF-8 explícita). Devuelve la ruta
     creada.
   - `buscar(String numeroExpediente)` — localiza el fichero
     correspondiente (`<numeroExpediente>.txt`) en cualquier subcarpeta, sin
     necesidad de conocer de antemano año, mes o provincia. Devuelve su ruta.
   - `leer(String numeroExpediente)` — devuelve el texto del documento, para
     que el servicio y el controlador no necesiten tocar el sistema de
     ficheros.
   - `mover(Expediente expediente, EstadoExpediente nuevoEstado)` — **recibe
     el expediente y no solo su número**, porque para regenerar el documento
     (tarea 4) necesita todos sus datos y el almacén no los guarda. Cuando el
     estado cambie, mueve el fichero a `resueltos/` o lo saca de ella según
     el [estado](#estructura-del-archivo-en-disco). Devuelve la nueva ruta.
   - `adjuntarFoto(String numeroExpediente, Path rutaFoto)` — **mueve** la
     fotografía (no la copia) y la deja como `<numeroExpediente>.jpg` junto
     al `.txt`. Tras la llamada la foto ya no está en su ubicación original.
     La usará A1.4.
4. Al cambiar el estado en `mover`, el contenido del documento debe
   reflejar el nuevo estado: regenera el texto desde la plantilla (no
   edites el fichero a base de reemplazos sobre el texto). La foto, si
   existe, se mueve con el documento.
5. Cumple la regla [Nada se sobrescribe](#nada-se-sobrescribe): comprueba el
   destino y usa `Files.move` sin `REPLACE_EXISTING`, y `CREATE_NEW` al
   escribir.
6. Externaliza mediante `@ConfigurationProperties` tanto el directorio raíz
   de archivado (`multagal.archivo.directorio`) como la ubicación de la
   plantilla (`multagal.archivo.plantilla`), según el
   [apartado f](/tema-1/f-excepciones-config#gestión-de-rutas-por-configuración).
   Si el proyecto ya tenía un valor para la raíz (por ejemplo `./datos/archivo`
   en `application-dev.yml`), úsalo en lugar del `expedientes/` del ejemplo.
7. Expón `AlmacenExpedientes` a través de los controladores de expedientes
   con, como mínimo:
   - `POST /api/expedientes/{numero}/archivo` — archiva el documento de un
     expediente ya registrado. `201` si lo archiva, `404` si el expediente no
     está registrado, `409` si ya estaba archivado.
   - `GET /api/expedientes/{numero}/archivo` — devuelve el contenido del
     documento archivado como `text/plain; charset=UTF-8`, de modo que el
     navegador lo muestre directamente. `404` si no existe o no está
     archivado.
   - `POST /api/expedientes/{numero}/estado` — cuerpo
     `{"estado": "RESUELTO"}`. Cambia el estado del expediente y mueve su
     documento con `mover`. Devuelve el expediente actualizado. `404` si el
     expediente no existe **o no está archivado** (en ese caso el estado
     registrado no cambia), `409` si el destino ya está ocupado.

   El orden importa: mueve el documento **antes** de persistir el nuevo
   estado, para que un fallo no deje ambos desincronizados.

   El controlador delega toda la lógica en un servicio (por ejemplo
   `ServicioArchivoExpedientes`) que coordina `ServicioExpedientes` con
   `AlmacenExpedientes`: no accede él mismo al sistema de ficheros. Ninguna
   respuesta debe exponer la ruta física del servidor.

### Datos que faltan

Los expedientes creados por la API todavía no tienen `Denuncia`, así que su
documento mostrará `-` en los datos del denunciado, matrícula, lugar,
infracción, gravedad y fecha de los hechos, y se archivarán en
`sin-provincia`. Es el comportamiento esperado en esta unidad. Si falta un
dato **imprescindible** (número, fecha de incoación, estado o importe), el
generador lanza `IllegalArgumentException`: es un error de programación, no
de dominio, y no hace falta traducirlo.

### Entregable

Clase `AlmacenExpedientes`, la plantilla de texto del expediente, el servicio
que la coordina y los endpoints descritos, junto con las pruebas descritas a
continuación.

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
   subcarpeta, y falla con `ExpedienteNoEncontradoException` (A1.5) si no
   existe.
4. `mover` cambia el fichero de carpeta y el texto del documento refleja el
   nuevo estado.
5. `mover` con un destino ya ocupado lanza `ExpedienteYaExistenteException`
   y no pierde ningún dato: ni el fichero de origen ni el que ocupaba el
   destino cambian.
6. `adjuntarFoto` deja `<numeroExpediente>.jpg` junto al `.txt`, y tras
   `mover` la foto sigue junto al documento en su nueva carpeta y ya no está
   en la anterior.
7. `archivar` sobre un expediente ya archivado lanza
   `ExpedienteYaExistenteException` y deja intacto el documento existente.
8. Un número de expediente o una provincia con `../` no permiten escribir
   fuera de la raíz de archivado.

Los controladores no requieren pruebas automáticas en esta unidad.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | La ruta de archivado se calcula, nunca se concatena con `+` |
| 2 | `archivar` crea los directorios intermedios que falten |
| 3 | `buscar` localiza un expediente sin recorrer manualmente todo el árbol a mano en el código cliente |
| 4 | El directorio raíz de archivado es configurable, no está escrito en el código |
| 5 | Las pruebas usan un directorio temporal y lo limpian al terminar |
| 6 | Los endpoints devuelven el código HTTP indicado en cada caso (`201`, `200`, `404`, `409`) |
| 7 | El controlador no contiene lógica de acceso a ficheros: solo delega en el servicio |
| 8 | El formato del documento está en un fichero de plantilla externo; el código solo aporta los datos mediante `String.format` |
| 9 | El `.txt` generado contiene todos los datos del expediente, correctamente formateados (fechas, importe con dos decimales) y en UTF-8 |
| 10 | Tras `mover`, el documento archivado muestra el nuevo estado |
| 11 | El ADR indica en qué capa se ha colocado cada componente y justifica la elección frente a las alternativas, de acuerdo con la regla de dependencia |
| 12 | `GET .../archivo` responde con el texto del expediente y `Content-Type: text/plain; charset=UTF-8`, o `404` si no existe; ninguna respuesta expone la ruta física del servidor |
| 13 | Ninguna operación sobrescribe un fichero existente ni permite salir del directorio raíz |
| 14 | Las pruebas 1 a 8 existen, se ejecutan sin Docker ni `@SpringBootTest` y pasan con `./mvnw test` |

:::tip
Java ofrece `Files.createTempDirectory` para crear un directorio temporal
único en cada ejecución de las pruebas, sin interferir entre ejecuciones
paralelas.
:::

---

## A1.2 — Servicio de purga de expedientes prescritos

**Objetivo:** localizar y eliminar expedientes prescritos recorriendo el
árbol de archivo, aplicando la [regla R4](/tema-0/b-proyecto-multagal#r4--prescripción).

### Regla de prescripción aplicada

Un expediente **prescribe** cuando ha transcurrido más tiempo que el plazo de
su gravedad desde la **fecha de los hechos** (`Denuncia.fechaHora`, solo el
día):

| Gravedad | Plazo |
|----------|-------|
| `LEVE` | 3 meses |
| `GRAVE` | 6 meses |
| `MUY_GRAVE` | 6 meses |

- El plazo se cuenta en meses naturales sumados a la fecha de los hechos
  (`fechaHechos.plusMonths(plazo)`).
- El límite es **exclusivo**: el expediente prescribe cuando
  `hoy > fechaHechos + plazo`. Un leve cuyos hechos fueron hace exactamente
  3 meses **se conserva** hoy y se purga a partir de mañana. Se elige
  conservar en el límite porque borrar es irreversible y conservar un día de
  más no lo es.
- La fecha de referencia («hoy») **se recibe como parámetro**:
  `purgar(LocalDate hoy)`. El endpoint pasa `LocalDate.now()` y las pruebas pasan
  una fecha fija, de modo que el resultado no dependa del día en que se
  ejecuten.
- Los plazos son constantes del servicio; no se externalizan.

### Tareas

1. Implementa `ServicioPurgaExpedientes` que recorra el árbol creado en
   A1.1 con `Files.walk` o `Files.walkFileTree`
   ([apartado c](/tema-1/c-recorrido-arboles)) y localice los expedientes
   prescritos según la regla anterior. La fecha de los hechos y la gravedad
   se obtienen leyendo las líneas `Fecha de los hechos:` y `Gravedad:` del
   `.txt` (por ejemplo con `Files.readAllLines`); la plantilla de A1.1 ya las
   incluye.
2. Filtra los ficheros candidatos con un `PathMatcher` o con el propio
   `Stream` antes de decidir si un expediente debe purgarse.
3. **Determina todos los candidatos antes de borrar nada.** Así, si un
   fichero no se puede leer, la purga se aborta con
   `AlmacenamientoExpedienteException` sin haber borrado ningún expediente.
4. Qué hacer con los documentos problemáticos:
   - Si no se puede leer la fecha o la gravedad (por ejemplo, vale `-` porque
     el expediente se creó por la API sin denuncia, o es un documento
     anterior a esta plantilla), **no se toca** y se deja un aviso en el log.
   - Si el fichero no se puede leer por un error de E/S o de codificación,
     se lanza la excepción de dominio indicada en el punto anterior.
5. Al purgar un expediente se borran su `.txt` y su fotografía `.jpg` (si la
   hay). Después, si el directorio que los contenía queda vacío, elimínalo,
   y repite con sus directorios padre mientras queden vacíos, **sin llegar a
   borrar nunca la raíz**.
6. Antes de borrar cada expediente, anótalo en un registro: define una
   interfaz `RegistroPurga` con `registrar(numeroExpediente, motivo)` (el
   motivo indica la regla R4, la gravedad, el plazo y las fechas) y una
   implementación que escriba en el log. Así la prueba 6 puede simularla.
7. Expón la purga a través de `POST /api/expedientes/purga` en un
   `PurgaController` propio (o en el controlador de expedientes), que invoque
   el servicio y responda `200` con `{"expedientesPurgados": n}`. Al ser una
   operación destructiva, no debe ejecutarse automáticamente en cada
   arranque (ni con `@Scheduled`): solo bajo demanda desde el endpoint.

:::note[Capa]
El enunciado fija `ServicioPurgaExpedientes` como servicio y este accede
directamente a `java.nio.file` sobre la raíz del archivo, lo que no es lo que
normalmente se haría con la regla de dependencia. Es una excepción aceptada
en esta unidad; recógela en el ADR de A1.1, junto con qué cambiarías si el
archivo dejara de estar en disco.
:::

### Entregable

Clase `ServicioPurgaExpedientes`, la interfaz `RegistroPurga` con su
implementación y el endpoint de purga, junto con las pruebas descritas a
continuación.

### Pruebas unitarias

`ServicioPurgaExpedientesTest` (JUnit 5 + AssertJ, `@TempDir`, pasando a
`purgar` una fecha fija para que el resultado no dependa del día en que se
ejecuten):

1. Un expediente **leve** de hace 4 meses se purga; uno de hace 2 meses se
   conserva.
2. Un expediente **grave** o **muy grave** de hace 4 meses se conserva; uno
   de hace 7 meses se purga.
3. Casos límite, con el límite exclusivo: un leve cuyos hechos fueron
   exactamente hace 3 meses y un grave exactamente hace 6 **se conservan**;
   con un día más, **se purgan**.
4. Tras purgar el único expediente de un mes/provincia, el directorio queda
   eliminado (y sus padres vacíos, sin llegar a la raíz); si quedaba otro
   expediente, el directorio se conserva.
5. Un árbol vacío, o una raíz que no existe, devuelve `0` y no lanza
   excepción.
6. El `RegistroPurga` (simulado con Mockito) recibe la entrada de cada
   expediente **antes** de su borrado. Comprueba el orden entre expedientes
   con `InOrder` y que el documento todavía existe en el momento de
   registrar (por ejemplo con un `doAnswer`), porque `InOrder` solo ordena
   llamadas a mocks y el borrado no lo es.
7. La foto se borra junto con su documento.
8. Un expediente no prescrito no se modifica, y no se registra nada.
9. Un documento sin fecha o gravedad legibles no se toca.
10. Un fichero ilegible lanza `AlmacenamientoExpedienteException` y no se borra
    ningún expediente.
11. Un documento generado por el `AlmacenExpedientes` real con la plantilla
    real se purga correctamente (comprueba el contrato de las etiquetas).

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El recorrido del árbol usa la API de `java.nio.file`, no `java.io.File` |
| 2 | Se distingue correctamente el plazo de 3 y de 6 meses según la gravedad, con el límite exclusivo |
| 3 | Un directorio que queda vacío tras la purga también se elimina, sin borrar nunca la raíz |
| 4 | El expediente no prescrito no se toca |
| 5 | Queda constancia (log) de cada purga realizada, antes del borrado |
| 6 | El endpoint de purga responde con el número de expedientes purgados |
| 7 | La purga no se ejecuta automáticamente al arrancar la aplicación, solo al invocar el endpoint |
| 8 | Las pruebas 1 a 11 existen y pasan con `./mvnw test`, sin depender de la fecha real del sistema |

:::warning
[Sobre el borrado de directorios]

`Files.delete` sobre un directorio solo funciona si está **vacío**. Si tu
purga borra ficheros y directorios en la misma pasada, hazlo en el orden
correcto: primero los ficheros prescritos, y solo después comprueba si el
directorio que los contenía ha quedado vacío.
:::

---

## A1.3 — Servicio de denuncias y su repositorio

**Objetivo:** diseñar el servicio que **crea denuncias** a partir de una
petición (`DenunciaRequest`) y el repositorio donde se guardan, de modo que A1.4
pueda registrar las multas detectadas por los radares sin conocer cómo se
resuelven el radar, el vehículo y la infracción. Esta actividad no toca
ficheros: es lógica de negocio y persistencia en memoria.

### La petición: `DenunciaRequest`

Un `record` en `dto` con los datos de **una captación de radar**. Es lo que
recibe `ServicioDenuncias` y lo que más tarde construirá A1.4 a partir de cada
línea del CSV, de modo que los campos llevan los mismos nombres que las columnas
de ese fichero:

| Campo | Se usa para | Validación |
|-------|-------------|------------|
| `idRadar` | `Radar.identificador` y `Denuncia.agenteODispositivo` | no vacío |
| `tipoRadar` | `Radar.tipo` | `FIJO` o `MOVIL` (aunque el enumerado tenga más valores, estos son los únicos admitidos) |
| `ubicacionRadar` | `Radar.ubicacion` | no vacío |
| `limiteVelocidad` | `Radar.limiteVelocidadControlado` | entero positivo |
| `fechaHora` | `Denuncia.fechaHora` | no nulo (`LocalDateTime`) |
| `lugar` | `Denuncia.lugar` | no vacío |
| `provincia` | `Denuncia.provincia` y directorio de archivado de A1.1 | no vacío |
| `latitud`, `longitud` | `Denuncia.coordenadas` | latitud entre -90 y 90, longitud entre -180 y 180 |
| `matricula` | `Vehiculo.matricula` | no vacío |
| `marca`, `modelo` | `Vehiculo.marca`, `Vehiculo.modelo` | no vacíos |
| `tipoVehiculo` | `Vehiculo.tipo` | `TURISMO` o `MOTOCICLETA` (los únicos admitidos) |
| `velocidadMedida` | decidir si hay infracción | entero positivo |

Las restricciones se expresan con Bean Validation, como en el
[apartado g del tema 0](/tema-0/g-controlador-rest-validacion#el-dto-de-entrada).
El DTO solo valida la **forma** del dato:
no consulta ningún repositorio.

### Cómo se completa la denuncia

Datos de la `Denuncia` que **no** vienen en la petición y cómo se obtienen:

- `tipoDenunciante` es siempre `RADAR`, y `agenteODispositivo` el `idRadar`.
- `radar`: se busca por su identificador; si no existe crea un log con gravedad de error y los datos necesarios para darlo de alta
- `vehiculo`: se busca por matrícula; si no existe, se da de alta. El resto de
  sus datos (fecha de matriculación, ITV) quedan sin informar.
- `infraccion`: se obtiene del catálogo según el exceso de velocidad (tabla
  siguiente).
- `conductor`: **nulo**. Un radar capta una matrícula, no una persona; la
  identificación del conductor es un paso posterior.

Solo se sanciona la captación cuya `velocidadMedida` supera
`limiteVelocidad`. Para esta actividad usa este catálogo simplificado (no es
la normativa real).

| Exceso sobre el límite | Código | Gravedad | Importe | Puntos |
|------------------------|--------|----------|---------|--------|
| hasta 20 km/h | `VEL-L` | `LEVE` | 100,00 € | 0 |
| de 21 a 40 km/h | `VEL-G` | `GRAVE` | 300,00 € | 2 |
| más de 40 km/h | `VEL-MG` | `MUY_GRAVE` | 500,00 € | 4 |

### Tareas

1. Define el `record` `DenunciaRequest` con los campos y restricciones de la
   tabla anterior.
2. Define la interfaz `RepositorioDenuncias` en `repository`, con al menos
   `crear(Denuncia)`, `buscarPorId(UUID)` y `buscarTodos()`, e impleméntala como
   `RepositorioDenunciasMemoria` con un `ConcurrentHashMap` y `@Repository`,
   igual que `RepositorioExpedientesMemoria` en A0.4. Es provisional: la
   UD3 la sustituirá por una implementación JDBC **sin tocar el servicio**.
3. Define los repositorios auxiliares que necesita la resolución de datos, con
   el mismo estilo (interfaz + implementación en memoria):
   - `RepositorioRadares` y `RepositorioVehiculos`, con una operación
     `buscarOCrear`
4. Implementa `ServicioDenuncias` en `service`, con **inyección por
   constructor** de las interfaces anteriores, y con
   `crear(DenunciaRequest peticion)`, que devuelve `Denuncia`:
   1. Calcula el exceso. Si no supera el límite, devuelve `Optional.empty()`
      **sin guardar nada** (ni la denuncia, ni el radar, ni el vehículo).
   2. Resuelve radar, vehículo e infracción como se indica en
      [Cómo se completa la denuncia](#cómo-se-completa-la-denuncia).
   3. Construye la `Denuncia` con un `UUID` nuevo, la guarda con
      `RepositorioDenuncias.crear` y la devuelve.
5. Añade `obtener(UUID id)`, que lanza `DenunciaNoEncontradaException`
   (subclase de `NoEncontradaException`, véase A1.5) si no existe.
6. Expón el servicio con un `DenunciasController`:
   - `POST /api/denuncias` — cuerpo `DenunciaRequest` validado con `@Valid`.
     `201` con un `DenunciaResponse` (identificador, matrícula, código de
     infracción, gravedad e importe) si hay infracción, `204` si la captación
     no se sanciona, `400` con el detalle de los campos inválidos.
   - `GET /api/denuncias/{id}` — devuelve la denuncia, o `404` si no existe.

   El controlador solo delega en `ServicioDenuncias`: no accede a ningún
   repositorio.
7. Documenta en el diario técnico por qué `crear` devuelve `Optional` en lugar
   de lanzar una excepción cuando no hay infracción, y qué cambiaría si más
   adelante hubiera que guardar también las captaciones no sancionadas.

### Entregable

`DenunciaRequest`, `ServicioDenuncias`, `RepositorioDenuncias` con su
implementación en memoria, los repositorios auxiliares y el catálogo, el
`DenunciasController`, y las pruebas descritas a continuación.

### Pruebas unitarias

`ServicioDenunciasTest` (JUnit 5 + AssertJ + Mockito, sin contexto de Spring;
repositorios simulados):

1. Una captación que no supera el límite, ni siquiera justo en él (exceso 0),
   devuelve `Optional.empty()` y no guarda nada.
2. Cada tramo del catálogo produce la infracción correcta; cubre los límites
   de 20/21 y de 40/41 km/h.
3. La `Denuncia` creada tiene `tipoDenunciante = RADAR`,
   `agenteODispositivo` igual al `idRadar`, coordenadas, lugar, provincia,
   fecha y hora de la petición, y conductor nulo.
4. Si el radar o el vehículo ya existen, se reutilizan; si no, se dan de alta con
   los datos de la petición.
5. La denuncia devuelta es la que se ha guardado (`verify` sobre
   `RepositorioDenuncias.crear`, una sola vez).
6. `obtener` de un identificador inexistente lanza
   `DenunciaNoEncontradaException`.


### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `DenunciaRequest` valida solo la forma de los datos con Bean Validation y no consulta ningún repositorio |
| 2 | `ServicioDenuncias` depende de **interfaces** de repositorio, por constructor, y no tiene estado mutable |
| 3 | La captación que no supera el límite no genera denuncia y no guarda nada |
| 4 | La infracción, la gravedad y el importe corresponden al tramo del catálogo, con los límites correctos |
| 5 | La denuncia sale con tipo `RADAR`, conductor nulo y radar y vehículo reutilizados si ya existían |
| 6 | El alta de radares y vehículos («buscar o dar de alta») es atómica |
| 7 | Los endpoints devuelven `201`, `204`, `400` y `404` en los casos indicados y el controlador solo delega en el servicio |
| 8 | Las pruebas 1 a 6 existen, se ejecutan sin Docker ni `@SpringBootTest` y pasan con `./mvnw test` |

---

## A1.4 — Vigilante e ingesta del buzón de entrada de denuncias

**Objetivo:** detectar automáticamente los ficheros CSV de captación
depositados por los radares con `WatchService`, **procesarlos en un hilo
distinto al del vigilante** y **registrar cada multa** detectada en el
sistema de datos de MULTAGAL, creando cada denuncia con el `ServicioDenuncias`
de A1.3.

### Directorios del buzón

El directorio vigilado es configurable (`multagal.entrada.directorio`, por
ejemplo `./datos/entrada`); `buzon-entrada/` es el nombre lógico que se usa en
este enunciado. El vigilante crea la estructura al arrancar:

| Directorio | Contenido |
|------------|-----------|
| `buzon-entrada/` | CSV de captación (el directorio vigilado) |
| `buzon-entrada/fotos/` | Fotografías de las captaciones |
| `buzon-entrada/procesados/` | CSV ya procesados |
| `buzon-entrada/errores/` | CSV ilegibles |

### Formato del fichero de captación

En esta unidad cada radar deposita un CSV de texto, en UTF-8, separado por
`;`, con una línea de cabecera y un registro por vehículo captado. Las columnas
son exactamente los campos de `DenunciaRequest` (A1.3), en este orden, y sus
validaciones son las de esa tabla:

```text
idRadar;tipoRadar;ubicacionRadar;limiteVelocidad;fechaHora;lugar;provincia;latitud;longitud;matricula;marca;modelo;tipoVehiculo;velocidadMedida
R-0042;FIJO;Ronda de Outeiro;50;2026-03-14T10:32:05;Ronda de Outeiro 120, A Coruña;A Coruña;43.3623;-8.4115;1234ABC;Seat;Ibiza;TURISMO;87
R-0107;MOVIL;N-640 km 3;100;2026-03-14T11:05:12;N-640 km 3, Lugo;Lugo;43.0097;-7.5568;4455QRS;Yamaha;MT-07;MOTOCICLETA;140
```

Cada línea válida se convierte en un `DenunciaRequest`. Una línea inválida es
la que no se puede convertir (número de columnas distinto, fecha que no es
ISO-8601 `yyyy-MM-ddTHH:mm:ss`, valores numéricos mal formados, enumerado
desconocido) o la que no pasa las validaciones del propio `DenunciaRequest`.

Lo que **no** viene en el CSV (tipo de denunciante, conductor, radar y vehículo
resueltos, infracción) lo completa `ServicioDenuncias`. Del `Expediente` que
crea el procesador: `instructor` fijo («Ingesta automática de radares»),
`importe` el de la infracción de la denuncia y `fechaIncoacion` la fecha de
hoy (`LocalDate.now()`).

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
el nombre `<matricula>_<idRadar>_<ddMMyy>_<HHmm>.jpg`, donde la fecha y la
hora son las de la columna `fechaHora` de la captación (hora en formato de
24 horas, sin segundos). Para el ejemplo anterior, las dos captaciones
generarían `1234ABC_R-0042_140326_1032.jpg` y `4455QRS_R-0107_140326_1105.jpg`.

- El vigilante **solo vigila el directorio de los CSV**: el CSV es el que
  dispara el procesamiento. Las fotos se buscan por nombre cuando se procesa
  cada registro.
- Solo hay foto de las captaciones que se sancionan, pero pueden llegar
  fotos de vehículos que no lleguen a sancionarse: no es un error, y esas
  fotos se quedan en `fotos/`.
- El nombre incluye el radar y el minuto de la captación, así que la foto de
  otro radar, o de otro momento, no se asocia a la captación. Como la foto se
  **mueve** al expediente, si un mismo fichero tiene dos infracciones de la
  misma matrícula, el mismo radar y el mismo minuto, solo la primera recibe la
  foto; la segunda queda «sin foto».
- Si la foto de una infracción **no existe** (o aún no ha terminado de
  subirse), el expediente se crea igualmente y se deja constancia en el log
  y en el contador de «expedientes sin foto». Documenta en el diario técnico
  esta decisión y la alternativa que has descartado.

:::info[Formato provisional]
Aquí lees el CSV a mano (`BufferedReader` y `String.split`). En la UD2
(actividad A2.1) lo sustituirás por un lector robusto con OpenCSV y por el
formato real del fabricante. Para que ese cambio no toque el vigilante ni el
procesador, define una interfaz (por ejemplo `LectorCapturas`, con un método
`leer(Path)` que devuelva los registros válidos y los inválidos con su número
de línea) e implementa la lectura manual detrás de ella. El lector debe leer
**el fichero completo** antes de devolver nada, de modo que un fichero
ilegible no deje registros a medio procesar.
:::

### Tareas

1. Implementa `VigilanteBuzonEntrada`, un componente que registre el
   directorio del buzón (configurable, como en
   [A1.1](#a11--componente-almacenexpedientes)) con `WatchService` según el
   [apartado d](/tema-1/d-watchservice), atendiendo como mínimo a
   `ENTRY_CREATE`. Ignora todo lo que no sea un `*.csv` (el filtro no
   distingue mayúsculas; un `.csv.tmp` no es un CSV). Al iniciarse, entrega
   también los CSV que ya hubiera en el buzón, depositados mientras la
   aplicación estaba parada.
2. El hilo del vigilante **solo detecta**: no lee ni procesa el contenido.
   Por cada CSV nuevo entrega su ruta a un `ExecutorService` (por ejemplo,
   un *pool* fijo pequeño, con `ThreadFactory` propia que dé nombre a los
   hilos) y vuelve de inmediato a esperar eventos, de modo que un fichero
   grande no retrase la detección de los siguientes. Evita entregar dos veces
   el mismo fichero.
3. Implementa `ProcesadorCapturas`, que ejecuta cada tarea del *pool* y:
   1. Espera a que el fichero esté completo, comprobando que su tamaño se
      mantiene estable entre dos comprobaciones separadas por una pausa
      configurable (`multagal.entrada.espera-estabilidad`, 500 ms por
      defecto). Si tras 20 comprobaciones sigue cambiando, se trata como
      ilegible y va a `errores/`.
   2. Lee el CSV línea a línea con `Files.newBufferedReader` (UTF-8).
   3. Convierte cada línea en un `DenunciaRequest` y lo valida con Bean
      Validation (número de columnas, enumerados, fecha ISO, rangos de
      coordenadas, velocidades numéricas y positivas). Un registro inválido
      **no aborta** el fichero: se registra en el log con su número de línea y
      se continúa.
   4. Entrega cada petición válida a `ServicioDenuncias.crear` (A1.3). Si
      devuelve `Optional.empty()`, la captación no supera el límite: se
      descarta y se cuenta como tal.
   5. Por cada denuncia creada, **crea y registra el expediente**
      invocando `ServicioExpedientes.registrar` (A0.4), con el importe de la
      infracción de la denuncia, y lo **archiva** con
      `AlmacenExpedientes.archivar` (A1.1). Si el registro o
      el archivado de una infracción falla con una excepción de dominio, esa
      infracción se cuenta como **fallida**, se registra en el log y se
      continúa con las demás; el registro ya hecho no se deshace.
   6. Genera el número de expediente con el formato
      `EXP-<año>-<secuencia de 6 cifras>` (por ejemplo `EXP-2026-000001`), con
      un contador atómico por año (por ejemplo en una clase
      `GeneradorNumeroExpediente`), de modo que dos hilos no puedan
      repetirlo. El año es el de la fecha de incoación del expediente (por ejemplo,
      `siguiente(LocalDate fecha)`). La secuencia vive en memoria y se
      reinicia al arrancar; tras un reinicio, un número que ya esté
      archivado en disco hará fallar ese registro (cuenta como fallido).
      Esta limitación desaparece cuando haya base de datos (UD3).
   7. Busca la fotografía
      `buzon-entrada/fotos/<matricula>_<idRadar>_<ddMMyy>_<HHmm>.jpg`
      (construye la ruta con `Path.resolve`, comprobando que la matrícula y el
      identificador del radar no contienen separadores de ruta ni `..` y que
      el resultado queda dentro de `fotos/`; la fecha y la hora las genera tu
      código con un formateador, no vienen del CSV tal cual). Si existe, pide a `AlmacenExpedientes.adjuntarFoto` (A1.1)
      que la **mueva** junto al `.txt` del expediente. Si no existe, o no se
      puede adjuntar, el expediente queda «sin foto». El procesador no mueve
      ficheros del archivo por su cuenta.
   8. Cuando termina, mueve el CSV a `procesados/`; si el fichero entero es
      ilegible, a `errores/`. Si ya hay en el destino un fichero con ese
      nombre, añade una marca de tiempo al nombre para no sobrescribirlo.
      Nunca dejes el CSV en el buzón, o se reprocesaría al reiniciar.
4. Registra en el log, al terminar cada fichero, un resumen: registros
   leídos, expedientes creados, registros inválidos y descartados por no
   superar el límite, fallidos, y expedientes con y sin foto. El log es
   información adicional: **no sustituye** al registro de la multa en el
   sistema de datos.
5. Arranca el vigilante en un hilo propio al iniciar la aplicación y
   deténlo de forma ordenada al pararla: cierra el `WatchService`, llama a
   `shutdown()` sobre el *pool* y espera con `awaitTermination` a que
   terminen las tareas en curso. Permite desactivarlo con una propiedad
   (`multagal.entrada.activo`, `true` por defecto) para que las pruebas que
   no lo necesitan (como `contextLoads`) no creen directorios.
6. Decide y documenta en el diario técnico si usas `take()` o `poll()` con
   tiempo de espera, y por qué.
7. Expón un endpoint de solo lectura `GET /api/buzon-entrada/estado` en un
   `BuzonEntradaController` que responda con: `vigilanteActivo`,
   `ultimoFicheroDetectado`, `ultimoFicheroProcesado` (solo el **nombre** del
   fichero, nunca la ruta) y los contadores acumulados `ficherosProcesados`,
   `expedientesCreados`, `registrosInvalidos` y `expedientesSinFoto`. El
   controlador consulta el estado del vigilante y del procesador; no accede
   directamente al directorio ni al `WatchService`.

### Entregable

`VigilanteBuzonEntrada`, `ProcesadorCapturas` y `BuzonEntradaController`
integrados en el arranque de la aplicación, un CSV de ejemplo con registros
válidos, inválidos e infracciones de las tres gravedades, algunas fotos de
prueba generadas por ti (cualquier JPEG pequeño sin datos reales) y las pruebas
descritas a continuación.

### Pruebas unitarias

`ProcesadorCapturasTest` (JUnit 5 + AssertJ, `@TempDir`; `ServicioDenuncias`,
`ServicioExpedientes` y `AlmacenExpedientes` simulados con Mockito; sin hilos
ni `WatchService`:
se invoca el procesador directamente. Recuerda que el `registrar` simulado
debe imitar al real, como se indica en
[Entorno de pruebas](#entorno-de-pruebas)):

1. Un CSV con tres captaciones, una de ellas sin superar el límite (el
   `ServicioDenuncias` simulado devuelve `Optional.empty()`), crea
   **exactamente dos** expedientes (`verify(servicio, times(2))`) y cuenta una
   captación descartada.
2. Cada línea válida llega a `ServicioDenuncias.crear` como un
   `DenunciaRequest` con los valores de esa línea (comprueba el argumento con
   un `ArgumentCaptor`).
3. El expediente registrado usa el importe de la infracción de la denuncia
   devuelta, el instructor fijo y la fecha de hoy como fecha de incoación.
4. Una línea con columnas de menos, fecha mal formada, velocidad no
   numérica, tipo de vehículo desconocido o coordenadas fuera de rango se
   cuenta como inválida y **no impide** procesar las demás.
5. Cada expediente registrado se archiva (`InOrder`: primero `registrar`,
   luego `archivar`).
6. Un CSV correcto acaba en `procesados/` y deja de estar en el buzón; uno
   ilegible acaba en `errores/` sin propagar la excepción.
7. Un CSV solo con cabecera o vacío no crea expedientes ni lanza excepción.
8. Con la foto `<matricula>_<idRadar>_<ddMMyy>_<HHmm>.jpg` presente, se pide adjuntarla al
   expediente correspondiente (`verify` con la ruta exacta de la foto). Como
   un `AlmacenExpedientes` simulado no mueve nada, la comprobación de que la
   foto **deja de estar en `fotos/`** se hace en una prueba aparte con el
   `AlmacenExpedientes` real y un directorio `@TempDir`.
9. Sin foto, el expediente se crea igualmente y se cuenta como «sin foto».
10. La foto de otro radar con la misma matrícula, o de la misma matrícula y
   radar pero otro minuto, no se asocia a la captación (el nombre incluye el
   radar y la fecha y hora).
11. Una matrícula con separadores de ruta (`../secreto`) no permite acceder a
    ficheros fuera de `fotos/`.
12. Si falla el archivado de un registro, ese registro cuenta como fallido y
    el resto del fichero se sigue procesando.

`GeneradorNumeroExpedienteTest`:

13. Los números tienen el formato esperado, y miles de llamadas desde varios
    hilos no producen ningún número repetido.

`VigilanteBuzonEntradaTest` (con un directorio `@TempDir` real y un
procesador simulado):

14. Al copiar un `.csv` en el directorio vigilado, el procesador recibe su
   ruta. Espera el resultado con **Awaitility** (`await().atMost(...)`), no
   con `Thread.sleep`.
15. Un fichero `.txt` o un `.csv.tmp` depositado no llega al procesador.
16. Tras depositar dos CSV seguidos, **ambos** llegan al procesador (prueba
   de que se hace `reset()`).
17. Un CSV que ya estaba en el buzón al arrancar llega al procesador.
18. El procesamiento ocurre en un hilo distinto del hilo del vigilante
    (registra el nombre del hilo desde el procesador simulado y compáralo con
    el del vigilante).
19. `detener()` cierra el vigilante y el *pool*; el hilo ya no está vivo
    (`Thread.isAlive`) pasado un tiempo razonable. Llamarlo dos veces, o sin
    haber iniciado, no falla.

`IngestaBuzonIT` (integración, `@SpringBootTest` con el directorio del buzón y
el del archivo apuntando a un `@TempDir` mediante `@DynamicPropertySource`):

20. Se deposita el CSV de ejemplo; con Awaitility se comprueba que
    `ServicioExpedientes.obtener(...)` encuentra los expedientes esperados
    (7, con sus gravedades), que existen sus `.txt` archivados, que las
    fotos presentes están junto a su documento, que el CSV se ha movido a
    `procesados/` y que el endpoint de estado refleja los contadores.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El vigilante se registra sobre el directorio correcto al arrancar |
| 2 | Se detecta la creación de un fichero CSV nuevo en un tiempo razonable; los que no son CSV se ignoran |
| 3 | La `WatchKey` se reinicia (`reset()`) tras procesar cada tanda de eventos |
| 4 | El procesamiento del CSV se hace en un hilo distinto al del vigilante, que no se bloquea mientras tanto |
| 5 | Cada captación que supera el límite genera una denuncia mediante `ServicioDenuncias` y queda registrada como expediente en el sistema de datos y archivada en disco, con la gravedad y el importe correctos |
| 6 | Un registro inválido se registra en el log con su línea y no impide procesar el resto del fichero |
| 7 | El CSV procesado se mueve a `procesados/` (o `errores/`) y no se reprocesa |
| 8 | El vigilante y el *pool* se detienen de forma ordenada al parar la aplicación, sin hilos colgados |
| 9 | La decisión `take()`/`poll()` está justificada en el diario técnico |
| 10 | El endpoint de estado refleja si el vigilante está activo, el último fichero detectado y los contadores, sin exponer rutas físicas |
| 11 | La fotografía `<matricula>_<idRadar>_<ddMMyy>_<HHmm>.jpg` se asocia al expediente de su captación y se archiva junto a su `.txt`; sin foto, el expediente se crea igualmente y queda contado |
| 12 | Los números de expediente no se repiten aunque los generen varios hilos a la vez |
| 13 | Las pruebas 1 a 20 existen y pasan con `./mvnw test` (incluida `IngestaBuzonIT`); ninguna usa `Thread.sleep` para esperar |

:::warning[Concurrencia]
`ServicioExpedientes` y `RepositorioExpedientesMemoria` serán invocados
desde varios hilos, igual que `ServicioDenuncias` (cuya atomicidad se
comprobó en A1.3). Comprueba que el repositorio de expedientes (basado en
`ConcurrentHashMap`) y la generación del número de expediente son seguros
ante accesos concurrentes, y que los contadores del estado usan
`AtomicInteger`/`LongAdder` o equivalente.
:::

---

## A1.5 — Jerarquía de excepciones `MultagalException`

**Objetivo:** diseñar e implementar la jerarquía de excepciones propia del
proyecto, sustituyendo la propagación directa de excepciones de bajo nivel.
Es una actividad transversal: véase el
[orden de trabajo recomendado](#orden-de-trabajo-recomendado).

### Jerarquía de referencia

```text
MultagalException (RuntimeException)
├── NoEncontradaException
│   └── ExpedienteNoEncontradoException        → 404
├── YaExistenteException
│   └── ExpedienteYaExistenteException         → 409
└── AlmacenamientoExpedienteException          → 500
```

Puedes añadir más clases, pero estas son las que el resto de la unidad da por
hechas.

### Tareas

1. Diseña la jerarquía siguiendo el criterio del
   [apartado f](/tema-1/f-excepciones-config#diseñar-una-jerarquía-de-excepciones-de-dominio):
   `MultagalException` como raíz (no comprobada), con las clases de la
   jerarquía anterior. `AlmacenamientoExpedienteException` se construye con
   `(operacion, referenciaLogica, causa)`, donde `referenciaLogica` es el
   **número de expediente** (o una referencia como «archivo» si el fallo no
   es de un expediente concreto), y su mensaje tiene la forma
   `No se pudo <operacion> '<referenciaLogica>' en el almacenamiento de
   expedientes`. La ruta física solo aparece en la causa, que se registra en
   el log; nunca en el mensaje.
2. Revisa `AlmacenExpedientes`, `ServicioPurgaExpedientes`,
   `VigilanteBuzonEntrada` y `ProcesadorCapturas` (y `ServicioDenuncias`, que
   debe lanzar excepciones de dominio, no `IllegalStateException` ni similares): en cualquier punto donde captures una
   `IOException` o subclase de `java.nio.file`, tradúcela a la excepción de
   dominio correspondiente, conservando la causa original.
3. Usa `try-with-resources` en todos los puntos donde abras un recurso
   (`DirectoryStream`, canales, `WatchService`).
4. Registra los códigos HTTP de la tabla de
   [Errores HTTP](#errores-http) en el `@RestControllerAdvice`. Para
   `AlmacenamientoExpedienteException` basta con devolver su mensaje, que por
   construcción solo contiene referencias lógicas.
5. Documenta en el diario técnico por qué la raíz de la jerarquía es una
   excepción no comprobada y no una `Exception` comprobada.

### Entregable

Paquete `domain.exception` con la jerarquía completa, y el código de A1.1,
A1.2, A1.3 y A1.4 actualizado para lanzar excepciones de dominio en lugar de
excepciones de `java.nio.file` sin traducir, junto con las pruebas
descritas a continuación.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `MultagalException` extiende `RuntimeException`, no `Exception` |
| 2 | Cada excepción de dominio conserva la excepción original como causa |
| 3 | Ningún método público de `AlmacenExpedientes`, `ServicioPurgaExpedientes`, `VigilanteBuzonEntrada` o `ProcesadorCapturas` deja escapar una `IOException` sin traducir |
| 4 | Todos los recursos `AutoCloseable` se abren con `try-with-resources` |
| 5 | El diario técnico justifica la decisión de diseño de la jerarquía |
| 6 | Los códigos HTTP de las excepciones de dominio son los de la tabla común |
| 7 | Las pruebas 1 a 6 existen y pasan con `./mvnw test` |

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
14. ¿Por qué `Files.move` sin `REPLACE_EXISTING` es más seguro que comprobar
    antes con `Files.exists`? ¿Qué problema queda aun así entre la
    comprobación y la operación?
15. En la purga, ¿qué ventaja tiene decidir todos los candidatos antes de
    borrar el primero, y qué pasaría si se fuera borrando durante el
    recorrido?

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
- **Plazos configurables.** Externaliza los plazos de prescripción con
  `@ConfigurationProperties` en lugar de dejarlos como constantes.
- **Numeración persistente.** Haz que la secuencia de números de expediente
  sobreviva a los reinicios (por ejemplo, recuperando el último número
  archivado al arrancar).
