# ejemplos-java

Proyecto Maven con ejemplos de código **ejecutables** para dar las clases del
módulo Acceso a Datos (0486). No es el proyecto de las actividades (ese usa
el dominio MULTAGAL, ver `tema-0/b-proyecto-multagal`): aquí los ejemplos
trabajan sobre datos genéricos (documentos, logs, directorios de prueba),
igual que hacen los apuntes.

## Estructura

Cada paquete se corresponde con un tema, y cada clase con un apartado de los
apuntes:

```
src/main/java/.../tema1/
├── EjemploPathFiles.java                 → apartado b: Path, Paths y Files
├── EjemploRecorridoArboles.java          → apartado c: recorrido de árboles
├── EjemploWatchService.java              → apartado d: WatchService
├── EjemploAccesoSecuencialAleatorio.java → apartado e: acceso secuencial/aleatorio
├── excepciones/                          → apartado f: jerarquía de excepciones propia
├── AlmacenamientoProperties.java         → apartado f: @ConfigurationProperties
├── AlmacenDocumentos.java                → apartado f: repositorio que traduce excepciones
├── DocumentoController.java              → apartado f: controlador REST de ejemplo
└── EjemploStreamingBinarios.java         → apartado g: streaming de binarios grandes
```

## Qué demuestra cada ejemplo

- **`EjemploPathFiles`** (apartado b): operaciones básicas con `Path` y
  `Files` — componer y normalizar rutas, crear directorios, crear/escribir
  un fichero (con `APPEND`), leerlo completo o línea a línea, copiarlo,
  moverlo/renombrarlo y comprobar su existencia.

- **`EjemploRecorridoArboles`** (apartado c): las distintas formas de
  recorrer un árbol de directorios — `DirectoryStream` (un solo nivel),
  `Files.walk` (stream perezoso y recursivo), `PathMatcher` con un patrón
  glob (`**/*.txt`) para filtrar por extensión, y `walkFileTree` con un
  `FileVisitor` para control fino (entrada/salida de cada directorio y
  borrado recursivo del árbol completo al terminar). También lee atributos
  básicos de un fichero (`BasicFileAttributes`: tamaño, fechas, si es
  directorio).

- **`EjemploWatchService`** (apartado d): vigilancia de un directorio en
  tiempo real con `WatchService`. Pensado para lanzarlo en clase y, con el
  proceso esperando eventos, crear/modificar/borrar ficheros a mano desde
  fuera para ver los eventos `ENTRY_CREATE`/`ENTRY_MODIFY`/`ENTRY_DELETE`
  aparecer en consola. Se detiene con Ctrl+C o solo, pasados 120 segundos,
  para no quedar colgado durante la demo.

- **`EjemploAccesoSecuencialAleatorio`** (apartado e): diferencia entre
  acceso secuencial y acceso aleatorio a un fichero. Crea un fichero de
  registros de tamaño fijo (64 bytes cada uno) y muestra cómo leer o
  modificar directamente el registro n-ésimo sin recorrer los anteriores,
  primero con la API clásica `RandomAccessFile` y después con la API NIO.2
  `SeekableByteChannel`.

- **Apartado f — excepciones propias y configuración externa de rutas**,
  repartido en varias clases que colaboran entre sí:
  - `excepciones/AlmacenamientoException`, `RecursoNoEncontradoException`,
    `PermisoDenegadoException`: jerarquía de excepciones propia del
    dominio, no comprobadas, para no obligar a declarar/capturar a quien
    no necesite tratarlas caso por caso.
  - `AlmacenamientoProperties`: agrupa la ruta del almacén en un objeto
    inyectable con `@ConfigurationProperties` en vez de repetir `@Value`
    en cada clase; el valor por defecto vive en `application.yml`.
  - `AlmacenDocumentos`: repositorio (`@Repository`) que encapsula el
    acceso al sistema de ficheros y **traduce** las excepciones de
    `java.nio.file` (`NoSuchFileException`, `AccessDeniedException`,
    `IOException`) a la jerarquía de excepciones propia.
  - `DocumentoController`: controlador REST (`POST /documentos`,
    `GET /documentos/{nombre}`) que delega todo el acceso a ficheros en
    `AlmacenDocumentos` y traduce `RecursoNoEncontradoException` a un
    404 HTTP.
  - `Documento`: record sencillo (nombre + contenido) usado como modelo
    neutro en todo el apartado.

- **`EjemploStreamingBinarios`** (apartado g): streaming de ficheros
  binarios grandes sin cargarlos enteros en memoria. Genera un fichero de
  prueba de 5 MB (con semilla fija, para que el contenido sea el mismo en
  cada ejecución) y lo copia de tres formas: por bloques con
  `InputStream`/`OutputStream`, por bloques calculando un hash SHA-256
  mientras se transfiere, y delegando la copia en el sistema operativo con
  `FileChannel.transferTo`.

## Cómo ejecutar cada ejemplo en clase

Los apartados **b, c, d, e y g** son clases con `main()` independientes,
sin Spring: se ejecutan directamente desde el IDE (botón "Run" sobre la
clase) o con Maven:

```bash
mvn compile exec:java -Dexec.mainClass="es.iesalandalus.accesodatos.ejemplos.tema1.EjemploPathFiles"
```

> Nota: el plugin `exec` no está declarado en el `pom.xml` para no añadir
> dependencias de más; en clase basta con lanzar la clase desde el IDE.

El apartado **f** (excepciones + configuración) sí necesita el contexto de
Spring, porque demuestra `@ConfigurationProperties` y un controlador REST
real. Para probarlo:

```bash
mvn spring-boot:run
```

Y con la aplicación arrancada:

```bash
curl -X POST localhost:8080/documentos -H "Content-Type: application/json" \
     -d '{"nombre":"notas.txt","contenido":"hola mundo"}'

curl localhost:8080/documentos/notas.txt

curl -i localhost:8080/documentos/no-existe.txt   # 404
```

## Datos generados

Todos los ejemplos escriben en `ejemplos-datos/`, en la raíz de este
proyecto. Esa carpeta está en `.gitignore`: se genera y se destruye en cada
ejecución, no forma parte del código fuente.

## Tests

```bash
mvn test
```

`AlmacenDocumentosTest` sigue el mismo patrón que se exige en las
actividades: trabaja sobre un directorio temporal (`Files.createTempDirectory`)
y lo limpia al terminar, nunca sobre rutas reales del disco.
