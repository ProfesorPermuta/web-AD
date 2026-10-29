# PROGRAMACIÓN DIDÁCTICA
# Módulo Profesional 0486 — Acceso a Datos

**Ciclo formativo:** CS Desarrollo de Aplicaciones Multiplataforma (DAM)
**Curso:** 2º
**Duración:** 80 horas (aprox. 4 h/semana en período lectivo ordinario)
**Créditos ECTS:** 9
**Curso académico:** 2025/2026

> **Nota de uso:** cada bloque delimitado por `═══` corresponde a un campo de la
> plataforma PROENS. Copiar el contenido situado entre el título del bloque y el
> siguiente separador.

---

═══════════════════════════════════════════════════════════
## 1. CONTEXTUALIZACIÓN Y MARCO NORMATIVO
═══════════════════════════════════════════════════════════

### 1.1. Marco normativo

El módulo profesional 0486 *Acceso a Datos* se imparte en el segundo curso del
ciclo formativo de grado superior de **Desarrollo de Aplicaciones Multiplataforma**,
regulado por:

- **Real Decreto 450/2010, de 16 de abril**, por el que se establece el título de
  Técnico Superior en Desarrollo de Aplicaciones Multiplataforma y se fijan sus
  enseñanzas mínimas.
- **Decreto 276/2011, do 15 de decembro** (DOG nº 20, do 30 de xaneiro de 2012),
  polo que se establece o currículo do ciclo formativo de grao superior
  correspondente ao título de técnico superior en desenvolvemento de aplicacións
  multiplataforma na Comunidade Autónoma de Galicia.
- **Orde do 12 de xullo de 2011** pola que se regulan o desenvolvemento, a
  avaliación e a acreditación académica do alumnado das ensinanzas de formación
  profesional inicial.
- **Lei Orgánica 3/2022, de 31 de marzo**, de ordenación e integración da
  Formación Profesional.

### 1.2. Contextualización del centro y del grupo

La presente programación se concreta para un grupo de segundo curso de DAM.
El alumnado accede al módulo tras haber superado en primer curso los módulos de
*Programación* (0485), *Bases de Datos* (0484) y *Entornos de Desarrollo* (0487),
por lo que se presuponen adquiridas las siguientes competencias previas:

- Programación orientada a objetos en Java: clases, herencia, interfaces,
  colecciones, genéricos y manejo básico de excepciones.
- Diseño de bases de datos relacionales y lenguaje SQL (DDL, DML, DQL),
  transacciones y procedimientos almacenados.
- Uso de un IDE profesional, control de versiones con Git y gestión de
  dependencias.

El aula dispone de un puesto informático por alumno/a con capacidad para
ejecutar simultáneamente un IDE, un contenedor de base de datos y un servidor
de aplicaciones, así como acceso a internet y a un repositorio Git del centro
o de plataforma externa (GitHub/GitLab).

### 1.3. Relación con el perfil profesional

Este módulo aporta la formación necesaria para desempeñar la función de
**desarrollo de la capa de persistencia** en aplicaciones multiplataforma. Es un
módulo eminentemente instrumental cuyos resultados se integran de forma directa
en los módulos *Programación de Servicios y Procesos* (0490), *Desarrollo de
Interfaces* (0489), *Programación Multimedia y Dispositivos Móviles* (0488) y,
de manera especialmente relevante, en el módulo de **Proyecto de Desarrollo de
Aplicaciones Multiplataforma** (0493) y en la **Formación en Centros de Trabajo**.

---

═══════════════════════════════════════════════════════════
## 2. OBJETIVOS GENERALES Y COMPETENCIAS
═══════════════════════════════════════════════════════════

### 2.1. Objetivos generales del ciclo a los que contribuye

Según el currículo, la formación del módulo contribuye a alcanzar los objetivos
generales **b), c), e), f), l), p), r) y t)** del ciclo formativo:

| Obj. | Enunciado |
|------|-----------|
| b) | Identificar tecnologías en desarrollo, analizando y aplicando sus características, para instalar y configurar entornos de desarrollo. |
| c) | Aplicar técnicas y procedimientos relacionados con la seguridad, para desarrollar aplicaciones que gestionan información almacenada. |
| e) | Seleccionar y emplear técnicas, motores y entornos de desarrollo, evaluando sus posibilidades, para desarrollar aplicaciones. |
| f) | Valorar y emplear herramientas específicas, atendiendo a la estructura de los contenidos, para crear aplicaciones. |
| l) | Establecer procedimientos y verificar la funcionalidad de la aplicación, para realizar pruebas de integración y validación. |
| p) | Analizar y utilizar los recursos y oportunidades de aprendizaje relacionados con la evolución científica, tecnológica y organizativa del sector. |
| r) | Identificar y proponer las acciones profesionales necesarias, para dar respuesta a la accesibilidad universal y al «diseño para todos». |
| t) | Identificar y aplicar parámetros de calidad en los trabajos y actividades realizados en el proceso de aprendizaje. |

### 2.2. Competencias profesionales, personales y sociales

Igualmente, el módulo contribuye a las competencias **b), c), e), f), l), p), r)
y t)** del título, especialmente:

- **b)** Gestionar entornos de desarrollo adaptando su configuración en cada caso
  para permitir el desarrollo y despliegue de aplicaciones.
- **c)** Desarrollar aplicaciones multiplataforma con acceso a bases de datos
  utilizando lenguajes, librerías y herramientas adecuados a las
  especificaciones.
- **l)** Realizar pruebas de integración y validación de las aplicaciones.
- **t)** Aplicar procedimientos de calidad, accesibilidad universal y «diseño
  para todos» en las actividades profesionales incluidas en los procesos de
  producción o prestación de servicios.

---

═══════════════════════════════════════════════════════════
## 3. RESULTADOS DE APRENDIZAJE Y CRITERIOS DE EVALUACIÓN
═══════════════════════════════════════════════════════════

### RA1. Desarrolla aplicaciones que gestionan información almacenada en ficheros identificando el campo de aplicación de los mismos y utilizando clases específicas.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA1.a | Se han utilizado clases para la gestión de ficheros y directorios. | Sí |
| RA1.b | Se han valorado las ventajas y los inconvenientes de las distintas formas de acceso. | Sí |
| RA1.c | Se han utilizado clases para recuperar información almacenada en ficheros. | Sí |
| RA1.d | Se han utilizado clases para almacenar información en ficheros. | Sí |
| RA1.e | Se han utilizado clases para realizar conversiones entre diferentes formatos de ficheros. | Sí |
| RA1.f | Se han previsto y gestionado las excepciones. | Sí |
| RA1.g | Se han probado y documentado las aplicaciones desarrolladas. | Sí |

### RA2. Desarrolla aplicaciones que gestionan información almacenada en bases de datos relacionales identificando y utilizando mecanismos de conexión.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA2.a | Se han valorado las ventajas e inconvenientes de utilizar conectores. | Sí |
| RA2.b | Se han utilizado gestores de bases de datos embebidos e independientes. | Sí |
| RA2.c | Se ha utilizado el conector idóneo en la aplicación. | Sí |
| RA2.d | Se ha establecido la conexión. | Sí |
| RA2.e | Se ha definido la estructura de la base de datos. | Sí |
| RA2.f | Se han desarrollado aplicaciones que modifican el contenido de la base de datos. | Sí |
| RA2.g | Se han definido los objetos destinados a almacenar el resultado de las consultas. | Sí |
| RA2.h | Se han desarrollado aplicaciones que efectúan consultas. | Sí |
| RA2.i | Se han eliminado los objetos una vez finalizada su función. | Sí |
| RA2.j | Se han gestionado las transacciones. | Sí |
| RA2.k | Se han ejecutado procedimientos almacenados en la base de datos. | No |

### RA3. Gestiona la persistencia de los datos identificando herramientas de mapeo objeto relacional (ORM) y desarrollando aplicaciones que las utilizan.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA3.a | Se ha instalado la herramienta ORM. | Sí |
| RA3.b | Se ha configurado la herramienta ORM. | Sí |
| RA3.c | Se han definido configuraciones de mapeo. | Sí |
| RA3.d | Se han aplicado mecanismos de persistencia a los objetos. | Sí |
| RA3.e | Se han desarrollado aplicaciones que modifican y recuperan objetos persistentes. | Sí |
| RA3.f | Se han desarrollado aplicaciones que realizan consultas usando el lenguaje SQL. | Sí |
| RA3.g | Se han gestionado las transacciones. | Sí |

### RA4. Desarrolla aplicaciones que gestionan la información almacenada en bases de datos objeto relacionales y orientadas a objetos valorando sus características y utilizando los mecanismos de acceso incorporados.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA4.a | Se han identificado las ventajas e inconvenientes de las bases de datos que almacenan objetos. | Sí |
| RA4.b | Se han establecido y cerrado conexiones. | Sí |
| RA4.c | Se ha gestionado la persistencia de objetos simples. | Sí |
| RA4.d | Se ha gestionado la persistencia de objetos estructurados. | Sí |
| RA4.e | Se han desarrollado aplicaciones que realizan consultas. | Sí |
| RA4.f | Se han modificado los objetos almacenados. | Sí |
| RA4.g | Se han gestionado las transacciones. | No |
| RA4.h | Se han probado y documentado las aplicaciones desarrolladas. | Sí |

### RA5. Desarrolla aplicaciones que gestionan la información almacenada en bases de datos documentales nativas evaluando y utilizando clases específicas.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA5.a | Se han valorado las ventajas e inconvenientes de utilizar bases de datos documentales nativas. | Sí |
| RA5.b | Se ha establecido la conexión con la base de datos. | Sí |
| RA5.c | Se han desarrollado aplicaciones que efectúan consultas sobre el contenido de la base de datos. | Sí |
| RA5.d | Se han añadido y eliminado colecciones de la base de datos. | Sí |
| RA5.e | Se han desarrollado aplicaciones para añadir, modificar y eliminar documentos de la base de datos. | Sí |

### RA6. Programa componentes de acceso a datos identificando las características que debe poseer un componente y utilizando herramientas de desarrollo.

| Cód. | Criterio de evaluación | Mín. exigible |
|------|------------------------|---------------|
| RA6.a | Se han valorado las ventajas e inconvenientes de utilizar programación orientada a componentes. | Sí |
| RA6.b | Se han identificado herramientas de desarrollo de componentes. | Sí |
| RA6.c | Se han programado componentes que gestionan información almacenada en ficheros. | Sí |
| RA6.d | Se han programado componentes que gestionan mediante conectores información almacenada en bases de datos. | Sí |
| RA6.e | Se han programado componentes que gestionan información usando mapeo objeto relacional. | Sí |
| RA6.f | Se han programado componentes que gestionan información almacenada en bases de datos objeto relacionales y orientadas a objetos. | No |
| RA6.g | Se han programado componentes que gestionan información almacenada en una base de datos documental nativa. | Sí |
| RA6.h | Se han probado y documentado los componentes desarrollados. | Sí |
| RA6.i | Se han integrado los componentes desarrollados en aplicaciones. | Sí |

---

═══════════════════════════════════════════════════════════
## 4. EL PROYECTO MARCO: SISTEMA DE GESTIÓN DE SANCIONES DE TRÁFICO
═══════════════════════════════════════════════════════════

### 4.1. Justificación de la metodología por proyectos

La programación se articula en torno a un **único proyecto marco común a todo el
alumnado**, que se construye de forma incremental a lo largo de las tres
evaluaciones. Esta decisión metodológica responde a los siguientes criterios:

1. **Coherencia con el desempeño profesional real.** Un desarrollador no elige
   el dominio sobre el que trabaja: recibe unos requisitos y debe adaptarse a
   ellos. Un proyecto impuesto reproduce esa condición.
2. **Comparabilidad y equidad en la evaluación.** Al ser el mismo enunciado para
   todo el grupo, las rúbricas se aplican sobre entregables homogéneos, lo que
   elimina el sesgo derivado de dominios de dificultad desigual.
3. **Eficiencia didáctica.** El tiempo de aula se dedica a resolver problemas
   técnicos comunes, no a comprender N dominios distintos. Las sesiones de
   depuración colectiva y de revisión de código entre iguales resultan
   directamente aprovechables por todo el grupo.
4. **Progresión visible.** El alumnado percibe cómo una misma necesidad de
   negocio se resuelve sucesivamente con ficheros, con JDBC, con un ORM, con
   tipos objeto-relacionales y con una base documental, comprendiendo así el
   **porqué** de cada tecnología y no sólo su sintaxis.
5. **Preparación para el módulo de Proyecto.** El artefacto resultante constituye
   una base sólida y un precedente metodológico para el módulo 0493.

### 4.2. Descripción del dominio

**MULTAGAL — Sistema de gestión de sanciones de tráfico.**

Se desarrolla el subsistema de tramitación de expedientes sancionadores de una
administración de tráfico. El sistema debe cubrir el ciclo de vida completo de
una denuncia: captación, tramitación, notificación, alegaciones, resolución,
pago y, en su caso, detracción de puntos.

**Entidades principales del dominio:**

| Entidad | Descripción |
|---------|-------------|
| `Conductor` | Titular de permiso de conducción. NIF/NIE, nombre, domicilio, fecha de expedición, saldo de puntos. |
| `PermisoConduccion` | Permiso de una única clase (A, B, C…) asociado a un conductor, con sus propias fechas de validez y estado. Un conductor tiene un permiso por cada clase que posee. |
| `Vehiculo` | Matrícula, marca, modelo, tipo, fecha de matriculación, estado de ITV. |
| `Titularidad` | Relación temporal N:M entre `Conductor` y `Vehiculo` (fecha de alta y de baja). |
| `Infraccion` | Catálogo normativo: código, artículo del Reglamento, descripción, importe base, puntos a detraer, gravedad (leve/grave/muy grave). |
| `Denuncia` | Hecho denunciado: fecha y hora, lugar (con coordenadas), tipo de denunciante (agente o radar), identificador del agente (si lo hay), velocidad medida (si la capta un radar), infracción imputada, vehículo, conductor identificado (si lo hay). |
| `Expediente` | Tramitación administrativa de una denuncia: número de expediente, estado, importe, fechas del procedimiento. |
| `Notificacion` | Intentos de notificación al interesado, con acuse y resultado. |
| `Alegacion` | Escrito presentado por el interesado, con documentos adjuntos. |
| `Resolucion` | Acto administrativo que pone fin al expediente: sanción o sobreseimiento (este último, entre otros motivos, cuando se estiman las alegaciones). |
| `Pago` | Abono del importe del expediente, con posible bonificación por pronto pago. |
| `Radar` | Dispositivo de captación automática: identificador, ubicación, tipo, límite de velocidad controlado. |

**Reglas de negocio significativas** (fuente de complejidad para transacciones,
validaciones y consultas):

- Bonificación del 50 % del importe si el pago se produce dentro de los 20 días
  naturales siguientes a la notificación, con renuncia a alegaciones.
- La detracción de puntos sólo se materializa cuando la resolución es firme.
- Un conductor con saldo de puntos igual a cero queda con todos sus
  permisos suspendidos: la actualización de saldo y el cambio de estado de
  cada uno de sus permisos deben ser **atómicos**.
- Prescripción de la infracción a los 3 meses (leves) o 6 meses (graves y muy
  graves) desde la fecha de comisión si no se ha notificado.
- Un expediente no puede resolverse mientras existan alegaciones pendientes de
  contestar.
- Un tipo de denunciante `AGENTE` exige identificador de agente y excluye radar
  y velocidad medida; un tipo `RADAR` exige radar y velocidad medida y excluye
  identificador de agente.

### 4.3. Arquitectura tecnológica: Spring Boot desde el primer día

**Decisión metodológica central:** la totalidad del módulo se desarrolla sobre
**Spring Boot 3.x con Java 25 (LTS)**, desde la primera sesión. No se plantea una
fase previa «en Java puro» seguida de una migración posterior al framework.

**Justificación:**

- La empleabilidad del titulado en DAM en el entorno tecnológico actual pasa,
  de manera abrumadoramente mayoritaria en el ecosistema Java, por Spring Boot.
  Retrasar su introducción al último trimestre reduce el tiempo de exposición
  real a la herramienta que utilizará en la FCT y en su primer empleo.
- La inyección de dependencias y el patrón repositorio se convierten en el
  **vocabulario base** del módulo, no en un añadido final. El alumnado interioriza
  desde el principio la separación en capas (`controller` → `service` →
  `repository` → `entity`), que es precisamente el marco conceptual que da
  sentido al RA6 (programación de componentes).
- Spring Boot **no oculta** los contenidos del currículo, los enmarca: se trabaja
  `JdbcTemplate` y `DataSource` sobre JDBC nativo (RA2), Hibernate como
  implementación de JPA (RA3), `spring-data-mongodb` y `MongoTemplate` (RA5). En
  todos los casos se exige al alumnado **conocer y justificar qué hace el
  framework por debajo**, lo cual se comprueba explícitamente en las pruebas.
- La infraestructura transversal (perfiles de configuración, gestión de
  propiedades, pool de conexiones HikariCP, `@Transactional`, testing con
  `@SpringBootTest` y Testcontainers) es exactamente el contenido que el
  currículo exige en «pooling de conexiones», «gestión de transacciones» y
  «prueba y documentación de las aplicaciones».

**Pila tecnológica del módulo:**

| Capa | Tecnología |
|------|------------|
| Lenguaje | Java 25 (LTS) |
| Framework | Spring Boot 3.x |
| Construcción | Maven |
| Ficheros | `java.nio.file`, Jackson (JSON), JAXB / Jackson-XML, OpenCSV |
| BD embebida | H2 (perfil `dev` y pruebas) |
| BD relacional | MariaDB / MySQL (perfil `prod`) |
| Conector | JDBC + `JdbcTemplate` + `NamedParameterJdbcTemplate`; pool HikariCP |
| ORM | Spring Data JPA sobre Hibernate |
| Objeto-relacional | PostgreSQL: tipos compuestos, `ARRAY`, `JSONB`, herencia de tablas, PostGIS opcional |
| Documental | MongoDB + Spring Data MongoDB + GridFS |
| Testing | JUnit 5, AssertJ, Mockito, Testcontainers |
| Documentación | Javadoc + springdoc-openapi (Swagger UI) |
| Control de versiones | Git + GitHub/GitLab (flujo de ramas por hito) |
| Contenedores | Docker + Docker Compose (levantado de MariaDB, PostgreSQL y MongoDB) |

### 4.4. Fases del proyecto y correspondencia con las entregas

| Fase | Denominación | Evaluación | RA principales | Entregable |
|------|--------------|------------|----------------|------------|
| 0 | Arranque del proyecto y del entorno | 1ª | — | Repositorio inicializado, proyecto Spring Boot arrancando, Docker Compose funcional |
| 1 | Ingesta y exportación de expedientes en ficheros | 1ª | RA1, RA6.c | **ENTREGA 1** |
| 2 | Persistencia relacional con JDBC | 1ª | RA2, RA6.d | **ENTREGA 1** |
| 3 | Migración a JPA/Hibernate | 2ª | RA3, RA6.e | **ENTREGA 2** |
| 4 | Datos objeto-relacionales y geoespaciales | 2ª | RA4, RA6.f | **ENTREGA 2** |
| 5 | Expedientes documentales y evidencias | 3ª | RA5, RA6.g | **ENTREGA 3** |
| 6 | Componentización, integración y despliegue | 3ª | RA6 completo | **ENTREGA 3** |
| — | Prueba práctica final | 3ª | Todos | **EXAMEN DE MODIFICACIÓN** |

---

═══════════════════════════════════════════════════════════
## 5. SECUENCIACIÓN Y TEMPORALIZACIÓN: UNIDADES DIDÁCTICAS
═══════════════════════════════════════════════════════════

### 5.1. Cuadro resumen

| UD | Título | Horas | RA | Ev. |
|----|--------|-------|----|-----|
| UD0 | Arranque: entorno, Spring Boot y el proyecto MULTAGAL | 7 | — | 1ª |
| UD1 | Gestión de ficheros y directorios | 8 | RA1 | 1ª |
| UD2 | Flujos, serialización y formatos de intercambio | 10 | RA1, RA6 | 1ª |
| UD3 | Conectores y acceso relacional con JDBC | 12 | RA2, RA6 | 1ª |
| UD4 | Mapeo objeto-relacional con JPA e Hibernate | 13 | RA3, RA6 | 2ª |
| UD5 | Bases de datos objeto-relacionales y orientadas a objetos | 10 | RA4, RA6 | 2ª |
| UD6 | Bases de datos documentales nativas | 10 | RA5, RA6 | 3ª |
| UD7 | Componentes de acceso a datos, integración y despliegue | 10 | RA6 | 3ª |
| | **TOTAL** | **80** | | |

### 5.2. Desarrollo de las unidades didácticas

---

#### UD0 — ARRANQUE: ENTORNO, SPRING BOOT Y EL PROYECTO MULTAGAL
**7 horas · 1ª evaluación**

**Objetivos**
- Configurar un entorno de desarrollo completo y reproducible.
- Comprender la estructura de un proyecto Spring Boot y el ciclo de vida de un
  bean.
- Conocer el enunciado del proyecto marco y su plan de entregas.

**Contenidos**
- Presentación del módulo, de la metodología por proyectos y de los criterios de
  calificación.
- Presentación del dominio MULTAGAL: modelo conceptual, reglas de negocio y
  glosario.
- JDK 25, Maven, IDE (IntelliJ IDEA / VS Code), Git.
- Docker y Docker Compose: levantado de los servicios de datos del curso.
- Spring Initializr. Anatomía de un proyecto Spring Boot: `pom.xml`, starters,
  `application.properties` / `application.yml`, perfiles.
- Inversión de control e inyección de dependencias. Estereotipos `@Component`,
  `@Service`, `@Repository`, `@Configuration`.
- Arquitectura en capas del proyecto y convenciones de paquetes.
- Controladores REST y validación de entrada por anotaciones (Bean
  Validation), distinguiendo validación de forma y validación de negocio.
- Flujo de trabajo con Git: ramas por hito, mensajes de commit, *pull requests*.

**Actividades**
- A0.1. Instalación y verificación del entorno (checklist de arranque).
- A0.2. Generación del esqueleto del proyecto y primer arranque.
- A0.3. Creación de los modelos de dominio (POJOs/records) de MULTAGAL.
- A0.4. Primer servicio inyectado y su prueba unitaria.
- A0.5. Controladores REST para el servicio de expedientes.
- A0.6. Publicación del repositorio y configuración de ramas.

---

#### UD1 — GESTIÓN DE FICHEROS Y DIRECTORIOS
**8 horas · 1ª evaluación · RA1**

**Objetivos**
- Manipular el sistema de ficheros mediante la API `java.nio.file`.
- Discriminar entre formas de acceso y justificar la elección.
- Diseñar una jerarquía de excepciones propia del dominio.

**Contenidos**
- `Path`, `Paths`, `Files`: creación, borrado, copia, movimiento y renombrado.
- Recorrido de árboles de directorios: `Files.walk`, `walkFileTree`,
  `DirectoryStream`, filtrado con `PathMatcher`.
- Atributos de ficheros y metadatos. Permisos.
- `WatchService`: vigilancia de un directorio de entrada.
- Formas de acceso: secuencial frente a aleatorio. Ventajas e inconvenientes.
- Excepciones comprobadas y no comprobadas. `try-with-resources`.
  Jerarquía de excepciones de la aplicación y su traducción a errores de negocio.
- Gestión de rutas por configuración: `@Value`, `@ConfigurationProperties`.

**Actividades**
- A1.1. Componente `AlmacenExpedientes`: estructura de directorios jerárquica
  por año/mes/provincia para el archivado de expedientes.
- A1.2. Servicio de purga: localización y borrado de expedientes prescritos
  recorriendo el árbol de archivo.
- A1.3. Vigilante e ingesta del *buzón de entrada* de denuncias: detección con
  `WatchService` de los CSV depositados por los radares, procesamiento en un
  hilo independiente, registro de cada multa en el sistema de datos y
  archivo de la fotografía asociada (`<matricula>_<idRadar>.jpg`).
- A1.4. Diseño e implementación de la jerarquía `MultagalException`.

---

#### UD2 — FLUJOS, SERIALIZACIÓN Y FORMATOS DE INTERCAMBIO
**10 horas · 1ª evaluación · RA1, RA6.c**

**Objetivos**
- Leer y escribir ficheros mediante flujos de bytes y de caracteres.
- Convertir información entre CSV, XML, JSON y binario.
- Empaquetar la funcionalidad de ficheros como componente reutilizable.

**Contenidos**
- Flujos de bytes (`InputStream`/`OutputStream`) y de caracteres
  (`Reader`/`Writer`). Decoradores y *buffering*. Juegos de caracteres.
- Ficheros de acceso aleatorio: `RandomAccessFile`, `SeekableByteChannel`,
  registros de longitud fija.
- Serialización nativa de Java: `Serializable`, `serialVersionUID`, campos
  `transient`. Limitaciones y riesgos de seguridad.
- XML: estructura, DTD y XSD. Analizadores sintácticos: DOM, SAX y StAX.
  Vinculación (*binding*) con JAXB.
- JSON: Jackson. `ObjectMapper`, anotaciones de mapeo, serializadores
  personalizados, tratamiento de fechas y de tipos del dominio.
- CSV con OpenCSV. Ficheros de ancho fijo.
- Conversión entre formatos. Estrategias de validación de datos de entrada.

**Actividades**
- A2.1. Lector de los ficheros de captación de radares en **CSV** (formato de
  ancho fijo del fabricante) con validación y rechazo de registros erróneos.
- A2.2. Importador del **catálogo normativo de infracciones en XML** validado
  contra XSD, con JAXB.
- A2.3. Exportador del **padrón de expedientes en JSON** para su remisión al
  organismo de recaudación.
- A2.4. **Conversor bidireccional** CSV ⇄ XML ⇄ JSON con interfaz común y
  selección de la implementación por configuración.
- A2.5. Fichero de índice de **acceso aleatorio** para la consulta directa de
  expedientes por número, sin recorrer el fichero completo.
- A2.6. Pruebas unitarias con `@TempDir` y documentación Javadoc del componente.

> **ENTREGA 1 — parte A**

---

#### UD3 — CONECTORES Y ACCESO RELACIONAL CON JDBC
**12 horas · 1ª evaluación · RA2, RA6.d**

**Objetivos**
- Establecer y gestionar conexiones a un SGBD mediante conectores JDBC.
- Ejecutar sentencias DDL, DML y consultas, tratando adecuadamente el resultado.
- Controlar transacciones y ejecutar procedimientos almacenados.

**Contenidos**
- El desfase objeto-relacional (*impedance mismatch*): naturaleza del problema.
- Protocolos de acceso a bases de datos. Tipos de controladores JDBC. Ventajas e
  inconvenientes del uso de conectores.
- Gestores embebidos (H2) frente a independientes (MariaDB/MySQL). Casos de uso
  de cada uno. Configuración por perfiles en Spring Boot.
- `DataSource`, `Connection`, `Statement`, `PreparedStatement`,
  `CallableStatement`, `ResultSet`.
- Inyección SQL: mecanismo del ataque y prevención mediante sentencias
  parametrizadas.
- **Pooling de conexiones**: HikariCP. Dimensionado, tiempos de espera y fugas de
  conexiones. Liberación de recursos.
- `JdbcTemplate` y `NamedParameterJdbcTemplate`. `RowMapper`, `ResultSetExtractor`,
  `SimpleJdbcInsert`. Correspondencia con la API JDBC subyacente.
- DTOs y objetos destinados a almacenar el resultado de las consultas.
- Transacciones: propiedades ACID, `commit`, `rollback`, puntos de recuperación,
  niveles de aislamiento. `@Transactional`: propagación y aislamiento.
- Procedimientos almacenados y funciones. Parámetros de entrada, salida y
  entrada/salida.
- Scripts de esquema y de datos. Versionado del esquema con Flyway.

**Actividades**
- A3.1. Definición del **esquema relacional** de MULTAGAL (DDL versionado con
  Flyway) y de los datos de carga inicial.
- A3.2. Configuración de **dos perfiles**: `dev` sobre H2 embebido y `prod` sobre
  MariaDB en contenedor, con el mismo código de acceso.
- A3.3. Capa `repository` con `JdbcTemplate`: CRUD completo de `Denuncia`,
  `Expediente`, `Conductor` y `Vehiculo`.
- A3.4. Consultas de negocio con `RowMapper` propios: expedientes por conductor,
  ranking de tramos con más denuncias, recaudación mensual por provincia.
- A3.5. **Caso transaccional**: resolución firme de un expediente que, en una sola
  transacción, marca la resolución, detrae los puntos del conductor y suspende
  el permiso si el saldo llega a cero. Demostración del `rollback` ante fallo.
- A3.6. **Procedimiento almacenado** `sp_recalcular_saldo_puntos` invocado desde
  la aplicación con parámetros de entrada y salida.
- A3.7. Análisis comparado: mismo caso de uso resuelto con JDBC puro y con
  `JdbcTemplate`; informe de ventajas e inconvenientes.
- A3.8. Volcado de los expedientes importados en la UD2 a la base de datos
  (integración de los componentes de ambas unidades).

> **ENTREGA 1 — parte B**

---

#### UD4 — MAPEO OBJETO-RELACIONAL CON JPA E HIBERNATE
**13 horas · 2ª evaluación · RA3, RA6.e**

**Objetivos**
- Instalar, configurar y explotar una herramienta ORM.
- Definir el mapeo de un modelo de dominio completo, incluidas sus asociaciones.
- Gestionar el ciclo de vida de los objetos persistentes y las transacciones.

**Contenidos**
- Concepto de mapeo objeto-relacional. Cómo resuelve el ORM el desfase
  objeto-relacional. Herramientas ORM más utilizadas (Hibernate, EclipseLink,
  MyBatis, jOOQ): características comparadas.
- Instalación y configuración de Hibernate a través de Spring Data JPA.
  `application.yml`: dialecto, `ddl-auto`, `show-sql`, estadísticas.
- Mapeo declarativo mediante **anotaciones**: `@Entity`, `@Table`, `@Id`,
  `@GeneratedValue`, `@Column`, `@Enumerated`, `@Temporal`, `@Embeddable`,
  `@Embedded`.
- Estructura de un fichero de mapeo XML (`orm.xml`): elementos y propiedades.
  Comparación con el mapeo por anotaciones.
- Asociaciones: `@OneToOne`, `@OneToMany`, `@ManyToOne`, `@ManyToMany`. Lado
  propietario, `mappedBy`, tablas de unión. Estrategias de herencia
  (`SINGLE_TABLE`, `JOINED`, `TABLE_PER_CLASS`).
- Clases persistentes. Sesión y contexto de persistencia. **Estados de un objeto**:
  transitorio, gestionado, separado y eliminado. `EntityManager`: `persist`,
  `merge`, `find`, `remove`, `flush`, `detach`.
- Carga perezosa frente a carga ansiosa. El problema **N+1** y su diagnóstico y
  resolución (`JOIN FETCH`, `@EntityGraph`, `@BatchSize`).
- Consultas: JPQL, Criteria API, **consultas SQL nativas**, `@Query`, consultas
  derivadas del nombre del método, proyecciones e interfaces cerradas.
  Paginación y ordenación con `Pageable`.
- Gestión de transacciones con `@Transactional`. Propagación, `readOnly`,
  bloqueo optimista (`@Version`) y pesimista.
- Caché de primer y de segundo nivel.
- Auditoría con `@CreatedDate`, `@LastModifiedDate`.

**Actividades**
- A4.1. Migración del modelo de MULTAGAL a **entidades JPA anotadas**, incluida la
  asociación N:M `Titularidad` con atributos y la jerarquía de `Infraccion`.
- A4.2. Mapeo equivalente de dos entidades mediante **`orm.xml`** y comparación
  razonada con la versión anotada.
- A4.3. Repositorios `JpaRepository`: consultas derivadas, `@Query` en JPQL y
  **consultas SQL nativas** para los informes de recaudación.
- A4.4. Reimplementación con JPA del **caso transaccional** de la UD3 y
  comparación de ambas soluciones (líneas de código, legibilidad, control).
- A4.5. Diagnóstico y corrección de un **problema N+1** deliberadamente
  introducido en la consulta de expedientes con sus notificaciones.
- A4.6. Control de **concurrencia optimista** sobre el pago de expedientes:
  simulación de dos pagos simultáneos y tratamiento de
  `OptimisticLockException`.
- A4.7. Listado paginado y filtrado de expedientes con `Specification`.

> **ENTREGA 2 — parte A**

---

#### UD5 — BASES DE DATOS OBJETO-RELACIONALES Y ORIENTADAS A OBJETOS
**10 horas · 2ª evaluación · RA4, RA6.f**

**Objetivos**
- Valorar las características de los SGBD que almacenan objetos.
- Gestionar la persistencia de objetos simples y estructurados.
- Acceder desde el lenguaje de programación a las funciones del gestor
  objeto-relacional.

**Contenidos**
- Gestores objeto-relacionales: características y ventajas. Evolución del
  estándar **ANSI SQL** (SQL:1999 y posteriores): tipos definidos por el usuario,
  tipos estructurados, tablas tipadas, herencia, colecciones y referencias.
- PostgreSQL como SGBD objeto-relacional: **tipos compuestos** (`CREATE TYPE`),
  **dominios**, **enumerados**, **arrays**, **herencia de tablas**, `JSONB` y sus
  operadores e índices GIN.
- Acceso a las funciones del gestor desde Java: `SQLData`, `Struct`, `Array`,
  correspondencia de tipos, `@Type` de Hibernate y conversores `AttributeConverter`.
- Extensión geoespacial PostGIS: tipo `geometry`, índices espaciales y consultas
  de proximidad.
- Gestores orientados a objetos puros: características y ventajas frente a los
  relacionales. **ObjectDB** y db4o. Ausencia de capa de mapeo.
- Gestión de la persistencia de objetos en una BDOO. API de programación:
  apertura y cierre de conexiones, persistencia, recuperación y modificación.
- Lenguaje de consulta **OQL** y su relación con JPQL. Comparativa sintáctica.
- Gestión de transacciones en BDOO.
- Análisis crítico: ventajas e inconvenientes de las bases de datos que
  almacenan objetos; escenarios de aplicación real y causas de su adopción
  minoritaria.

**Actividades**
- A5.1. Migración del perfil `prod` a **PostgreSQL** y adaptación del esquema.
- A5.2. Definición del tipo compuesto `direccion_postal` y del enumerado
  `gravedad_infraccion`; persistencia de objetos estructurados desde Java.
- A5.3. Almacenamiento del **atestado y las circunstancias variables** de cada
  denuncia en una columna `JSONB`, con consultas por sus atributos internos e
  índice GIN.
- A5.4. Uso de **arrays** para los documentos adjuntos de una alegación
  (`documentosAdjuntos`) y consultas con `ANY`/`@>`.
- A5.5. Localización geoespacial de radares y denuncias: consulta de las
  denuncias producidas en un radio determinado en torno a un punto (PostGIS).
- A5.6. Persistencia del mismo modelo en **ObjectDB**: objetos simples y
  estructurados, consultas OQL/JPQL, apertura y cierre explícitos de conexión y
  gestión de transacciones.
- A5.7. **Informe comparativo** documentado: relacional puro frente a
  objeto-relacional frente a orientado a objetos, con métricas del propio
  proyecto.

> **ENTREGA 2 — parte B**

---

#### UD6 — BASES DE DATOS DOCUMENTALES NATIVAS
**10 horas · 3ª evaluación · RA5, RA6.g**

**Objetivos**
- Valorar la aplicabilidad de las bases documentales nativas.
- Gestionar conexiones, colecciones y documentos.
- Desarrollar consultas sobre el contenido documental.

**Contenidos**
- Bases de datos documentales nativas: características, ventajas e
  inconvenientes. Esquema flexible. Teorema CAP y consistencia eventual.
  Comparación con el modelo relacional. Criterios de elección.
- MongoDB: documentos BSON, colecciones, bases de datos. Modelado documental:
  incrustación frente a referencia. Patrones y antipatrones de diseño.
- **Establecimiento y cierre de conexiones**: cadena de conexión, `MongoClient`,
  pool de conexiones, réplicas.
- **Creación y borrado de colecciones**: clases y métodos. Validación de esquema
  (`$jsonSchema`). Índices, incluidos los de texto y los TTL.
- **Alta, modificación y eliminación de documentos**: `insertOne`, `insertMany`,
  `updateOne`, `updateMany`, `replaceOne`, `deleteOne`, `deleteMany`, operadores
  de actualización. `upsert`.
- **Lenguajes de consulta**: operadores de filtro y proyección, *pipeline* de
  agregación (`$match`, `$group`, `$lookup`, `$unwind`, `$project`, `$sort`),
  búsqueda de texto completo.
- Spring Data MongoDB: `MongoRepository`, `@Document`, `@Field`, `@Indexed`,
  `MongoTemplate`, `Criteria` y `Query`. Correspondencia con el driver nativo.
- Almacenamiento de ficheros con **GridFS**.
- Transacciones multi-documento en conjuntos de réplica.
- Persistencia políglota: convivencia de MongoDB y del SGBD relacional en la
  misma aplicación.

**Actividades**
- A6.1. Levantado de MongoDB en contenedor y configuración de la conexión y del
  pool en el proyecto.
- A6.2. Diseño del **modelo documental del expediente completo**: alegaciones,
  notificaciones y trazas del procedimiento incrustadas en un único documento.
  Justificación del modelado frente a la alternativa referenciada.
- A6.3. Creación y borrado programático de colecciones, con validación de
  esquema e índices.
- A6.4. Servicio de **alta, modificación y eliminación** de alegaciones y de sus
  documentos adjuntos.
- A6.5. Consultas con `MongoTemplate` y `Criteria`, y **agregaciones**: número de
  alegaciones estimadas por tipo de infracción, tiempo medio de tramitación por
  provincia.
- A6.6. **Búsqueda de texto completo** sobre el contenido de las alegaciones.
- A6.7. Almacenamiento de las **fotografías de los radares** en GridFS y su
  recuperación asociada al expediente.
- A6.8. Comparación de la misma consulta resuelta en SQL y en el *pipeline* de
  agregación.

> **ENTREGA 3 — parte A**

---

#### UD7 — COMPONENTES DE ACCESO A DATOS, INTEGRACIÓN Y DESPLIEGUE
**10 horas · 3ª evaluación · RA6**

**Objetivos**
- Diseñar, empaquetar, probar, documentar y reutilizar componentes de acceso a
  datos.
- Integrar en una única aplicación los componentes desarrollados a lo largo del
  curso.

**Contenidos**
- Concepto de componente: características (encapsulación, interfaz definida,
  independencia del contexto, reutilización, sustituibilidad). Ventajas e
  inconvenientes de la programación orientada a componentes.
- Propiedades y atributos de un componente. Configuración externalizada:
  `@ConfigurationProperties`, validación de la configuración, valores por
  defecto.
- **Eventos**: publicación y escucha. `ApplicationEventPublisher`,
  `@EventListener`, `@TransactionalEventListener`. Asociación de acciones a
  eventos del dominio.
- Persistencia del componente. Serialización de su estado y de su configuración.
- Herramientas para el desarrollo de componentes: Maven multimódulo, empaquetado
  JAR, publicación en repositorio, versionado semántico, **starters** propios de
  Spring Boot y autoconfiguración.
- Patrones de la capa de persistencia: Repositorio, DAO, DTO, Unidad de Trabajo,
  Adaptador. Arquitectura hexagonal: puertos y adaptadores. Inversión de la
  dependencia sobre el mecanismo de almacenamiento.
- Prueba de componentes: unitaria con dobles (Mockito), de integración con
  **Testcontainers**, cortes de contexto (`@DataJpaTest`, `@DataMongoTest`,
  `@JdbcTest`). Cobertura con JaCoCo.
- Documentación: Javadoc, OpenAPI/Swagger, `README` técnico y manual de
  despliegue.
- Integración y despliegue: perfiles, empaquetado, contenedorización de la
  aplicación, orquestación con Docker Compose.

**Actividades**
- A7.1. **Refactorización a Maven multimódulo**: extracción de los componentes
  `multagal-ficheros`, `multagal-jdbc`, `multagal-jpa`, `multagal-objeto` y
  `multagal-documental` como artefactos independientes con su interfaz publicada.
- A7.2. Definición del **puerto** `RepositorioExpedientes` y de sus adaptadores
  JDBC, JPA y MongoDB; **selección del adaptador por configuración**, sin cambiar
  una línea del código de negocio.
- A7.3. Componente de **eventos del dominio**: la resolución firme de un
  expediente publica un evento que desencadena, de forma desacoplada, la
  notificación y el archivado documental.
- A7.4. Empaquetado de un componente como **starter propio** con su
  autoconfiguración y sus propiedades.
- A7.5. **Batería de pruebas** completa: unitarias con Mockito e integración con
  Testcontainers para MariaDB, PostgreSQL y MongoDB. Informe de cobertura.
- A7.6. **Documentación** del proyecto: Javadoc publicado, API REST documentada
  con OpenAPI y manual de despliegue.
- A7.7. **Integración final**: aplicación única que expone la funcionalidad de
  todos los componentes, contenedorizada y levantada con Docker Compose.
- A7.8. **Defensa oral** del proyecto completo.

> **ENTREGA 3 — parte B**

---

═══════════════════════════════════════════════════════════
## 6. METODOLOGÍA DIDÁCTICA
═══════════════════════════════════════════════════════════

### 6.1. Principios metodológicos

La programación se sustenta en el **Aprendizaje Basado en Proyectos (ABP)**
aplicado en su modalidad de **proyecto único, incremental y común** a todo el
grupo. Los principios que la orientan son:

- **Aprendizaje significativo.** Cada contenido se introduce cuando el proyecto
  lo demanda: el alumnado descubre la limitación de la solución anterior antes de
  recibir la herramienta que la supera. Se pasa a JDBC cuando los ficheros no
  soportan la concurrencia; se pasa a JPA cuando el código JDBC se vuelve
  repetitivo e inmanejable; se pasa a MongoDB cuando el esquema rígido no admite
  la variabilidad del atestado.
- **Progresión en espiral.** El mismo caso de uso —la resolución firme de un
  expediente— se reimplementa sucesivamente con tecnologías distintas, lo que
  permite la comparación explícita y el aprendizaje por contraste.
- **Andamiaje decreciente.** En la primera evaluación el profesorado proporciona
  la estructura y buena parte del código guiado; en la segunda se entregan
  únicamente los requisitos y las pruebas que deben pasar; en la tercera el
  alumnado diseña la solución completa a partir de una especificación funcional.
- **El error como contenido.** Se introducen deliberadamente defectos (fugas de
  conexiones, problema N+1, inyección SQL, condiciones de carrera) para que el
  alumnado los diagnostique y corrija. La depuración es una actividad
  programada, no un imprevisto.
- **Cultura profesional de desarrollo.** Control de versiones desde la primera
  sesión, revisión de código entre iguales, integración continua, pruebas
  automatizadas y documentación como parte del entregable, no como añadido.

### 6.2. Organización del aula y tipología de sesiones

Las sesiones responden a cuatro formatos, combinados según la unidad:

| Formato | Peso aprox. | Descripción |
|---------|-------------|-------------|
| Exposición y demostración en vivo | 25 % | Presentación del concepto y codificación en directo por el profesorado, con el alumnado replicando. |
| Taller guiado | 35 % | Resolución de una actividad del proyecto con apoyo docente, hitos parciales y puesta en común. |
| Trabajo autónomo sobre el proyecto | 30 % | Avance individual sobre el hito en curso, con atención personalizada. |
| Revisión, depuración colectiva y defensa | 10 % | Análisis conjunto de soluciones, revisión de código entre iguales, exposición de decisiones técnicas. |

**Modalidad de trabajo.** El proyecto se desarrolla de forma **individual**, con
el fin de garantizar que cada alumno o alumna alcanza personalmente todos los
resultados de aprendizaje. No obstante, se programan actividades cooperativas de
revisión de código entre iguales y de resolución colectiva de incidencias, y se
fomenta el uso del repositorio compartido del grupo para las cuestiones comunes.

### 6.3. El «diario técnico» del proyecto

Cada alumno o alumna mantiene en el repositorio un documento de **decisiones
técnicas** (formato ADR simplificado) en el que registra, para cada hito, qué
alternativa ha elegido, qué otras descartó y por qué. Este documento es
instrumento de evaluación de los criterios de tipo valorativo (RA1.b, RA2.a,
RA4.a, RA5.a, RA6.a), que difícilmente pueden acreditarse observando únicamente
el código.

### 6.4. Uso de asistentes de programación basados en IA

Dado que su empleo es ya práctica habitual en el sector, no se prohíbe su uso en
el trabajo autónomo, pero se establece que:

- Todo código incorporado al proyecto debe poder ser **explicado y justificado**
  por su autor o autora. La defensa oral y el examen práctico final se realizan
  **sin acceso a asistentes**.
- El uso de asistentes se declara en el diario técnico.
- Se programa una actividad específica de análisis crítico de código generado
  automáticamente, orientada a la detección de errores sutiles (fugas de
  recursos, transacciones mal delimitadas, consultas ineficientes).

---

═══════════════════════════════════════════════════════════
## 7. EVALUACIÓN
═══════════════════════════════════════════════════════════

### 7.1. Carácter de la evaluación

La evaluación es **continua, formativa e integradora**, y se realiza por
**resultados de aprendizaje**, tomando como referencia los criterios de
evaluación establecidos en el currículo. La aplicación de la evaluación continua
exige la asistencia regular a clase, en los términos previstos en la Orde do 12
de xullo de 2011.

### 7.2. Instrumentos de evaluación

| Instrumento | Descripción |
|-------------|-------------|
| **Entregables del proyecto** | Código fuente en el repositorio, en la rama y con la etiqueta correspondientes al hito, acompañado de pruebas y documentación. Se valora con rúbrica. |
| **Defensa oral** | Exposición individual de las decisiones técnicas del hito y respuesta a preguntas sobre el código propio, incluida la explicación del funcionamiento interno de lo que el framework resuelve automáticamente. |
| **Diario técnico** | Registro de decisiones y de alternativas descartadas. Instrumento principal para los criterios de carácter valorativo. |
| **Actividades de aula** | Ejercicios cortos, diagnósticos de defectos, análisis comparativos. |
| **Prueba práctica de modificación** | Prueba individual y presencial descrita en el apartado 7.4. |
| **Pruebas escritas breves** | Cuestionarios conceptuales al cierre de cada bloque, orientados a los contenidos que no se manifiestan directamente en el código. |

### 7.3. Calendario de entregas

| Entrega | UD que abarca | RA evaluados | Momento |
|---------|---------------|--------------|---------|
| **ENTREGA 1** | UD0, UD1, UD2, UD3 | RA1, RA2, RA6 (parcial) | Fin de la 1ª evaluación |
| **ENTREGA 2** | UD4, UD5 | RA3, RA4, RA6 (parcial) | Fin de la 2ª evaluación |
| **ENTREGA 3** | UD6, UD7 | RA5, RA6 (completo) | Mediados de la 3ª evaluación |
| **PRUEBA FINAL** | Todas | Todos | Fin de la 3ª evaluación |

Cada entrega se materializa mediante una etiqueta (*tag*) en el repositorio Git
en la fecha y hora límite publicadas. No se admiten modificaciones posteriores a
esa marca.

### 7.4. La prueba práctica final: modificación sustancial del proyecto

Constituye el elemento **integrador** de la evaluación y sustituye al examen
teórico tradicional.

**Naturaleza.** Se entrega al alumnado un **pliego de nuevos requisitos** que
obliga a una modificación sustancial del proyecto desarrollado durante el curso.
Cada alumno o alumna parte de **su propio proyecto**, tal como quedó en la
Entrega 3.

**Condiciones de realización.**
- Individual y presencial, en el aula, con una duración de **4 horas** repartidas
  en dos sesiones consecutivas.
- Se permite el acceso a la documentación oficial de Java, Spring, PostgreSQL y
  MongoDB, y al código del propio proyecto.
- **No se permite** el acceso a asistentes de IA, a repositorios de terceros ni a
  comunicación con otras personas.
- La entrega se realiza mediante *tag* en el repositorio dentro del plazo.

**Contenido del pliego.** Se compone de una modificación por bloque tecnológico,
de modo que la prueba cubre necesariamente todos los resultados de aprendizaje.
Ejemplo de pliego (se elabora uno distinto cada curso):

> **Reforma del sistema MULTAGAL — nuevos requisitos**
>
> 1. **(RA1)** La Jefatura Provincial remite ahora los ficheros de captación en un
>    nuevo formato **JSON anidado** con estructura distinta a la actual. Adaptar la
>    ingesta manteniendo la compatibilidad con el formato CSV antiguo, y añadir la
>    exportación del padrón en formato **XML** conforme al XSD que se adjunta.
> 2. **(RA2/RA3)** Se incorpora al dominio la entidad **`Recurso de alzada`**, con
>    su relación con `Resolucion` y con un nuevo estado del expediente. Definir su
>    esquema, su migración Flyway y su mapeo JPA, e implementar el caso de uso de
>    interposición de recurso, que debe ser **transaccional** y revertir la
>    detracción de puntos si el recurso se admite a trámite.
> 3. **(RA2)** Implementar un **procedimiento almacenado** que devuelva la
>    recaudación consolidada por provincia y trimestre, e invocarlo desde la
>    aplicación.
> 4. **(RA4)** Añadir al expediente un campo de **tipo estructurado** para la
>    representación del órgano instructor, y una consulta sobre atributos
>    contenidos en la columna `JSONB`.
> 5. **(RA5)** Modelar los recursos de alzada y sus adjuntos en **MongoDB**, e
>    implementar una **agregación** que calcule la tasa de estimación de recursos
>    por tipo de infracción.
> 6. **(RA6)** Exponer la nueva funcionalidad como un **componente independiente**
>    con su interfaz, sus pruebas de integración y su documentación, e integrarlo
>    en la aplicación sin modificar los componentes existentes.

**Evaluación de la prueba.** Se califica por resultados de aprendizaje mediante
la rúbrica del apartado 7.6, de forma que la prueba permite acreditar o
recuperar cualquier RA.

### 7.5. Criterios de calificación

**Ponderación de los resultados de aprendizaje sobre la calificación final:**

| RA | Denominación abreviada | Peso |
|----|------------------------|------|
| RA1 | Ficheros | 15 % |
| RA2 | Conectores y bases de datos relacionales | 20 % |
| RA3 | Herramientas ORM | 22 % |
| RA4 | BD objeto-relacionales y orientadas a objetos | 13 % |
| RA5 | BD documentales nativas | 15 % |
| RA6 | Componentes de acceso a datos | 15 % |
| | **TOTAL** | **100 %** |

**Ponderación de los instrumentos dentro de cada resultado de aprendizaje:**

| Instrumento | Peso |
|-------------|------|
| Entregable del proyecto (código, pruebas y documentación) | 45 % |
| Prueba práctica final de modificación | 30 % |
| Defensa oral y diario técnico | 15 % |
| Actividades de aula y pruebas escritas breves | 10 % |

**Condiciones para la superación del módulo:**

1. La calificación final es la media ponderada de los seis resultados de
   aprendizaje conforme a la tabla anterior.
2. Es necesario obtener una calificación **igual o superior a 4,0 sobre 10 en
   cada uno de los seis resultados de aprendizaje**. Un RA por debajo de ese
   umbral impide superar el módulo, con independencia de la media.
3. Es necesario obtener una calificación **igual o superior a 4,0 en la prueba
   práctica final**, por su carácter integrador y por ser el instrumento que
   garantiza la autoría individual del trabajo.
4. El módulo se supera con una calificación final igual o superior a **5,0**, que
   se expresa numéricamente de 1 a 10 sin decimales.

**Condiciones formales de los entregables.** Un entregable que no compile o que
no arranque se califica con 0 en los criterios de tipo procedimental del hito
correspondiente. Se exige asimismo:
- Ausencia de credenciales o de rutas absolutas escritas en el código fuente.
- Ausencia de concatenación de parámetros en sentencias SQL.
- Cierre correcto de todos los recursos.
- Historial de commits que evidencie un trabajo progresivo, no una entrega única
  en la fecha límite.

**Autoría.** La detección de plagio entre entregas o de código ajeno no
justificado ni comprendido conlleva la calificación de 0 en el hito
correspondiente y la obligación de acreditar los RA afectados exclusivamente
mediante la prueba práctica final.

### 7.6. Rúbrica general de evaluación por resultado de aprendizaje

| Nivel | Descriptor |
|-------|------------|
| **Insuficiente (0–4)** | No implementa la funcionalidad requerida o lo hace de forma no funcional. No gestiona excepciones ni cierra recursos. No prueba ni documenta. No justifica sus decisiones técnicas. |
| **Suficiente (5–6)** | Implementa la funcionalidad básica requerida, que funciona en el caso favorable. Gestión de errores elemental. Pruebas mínimas. Documentación escasa. Justifica sus decisiones de forma superficial. |
| **Bien / Notable (7–8)** | Funcionalidad completa y robusta ante entradas erróneas. Gestión adecuada de excepciones, recursos y transacciones. Código estructurado en capas y con nomenclatura correcta. Pruebas unitarias y de integración significativas. Documentación suficiente. Justifica las decisiones con criterio técnico. |
| **Sobresaliente (9–10)** | Solución técnicamente eficiente y bien diseñada, con separación clara de responsabilidades y ausencia de duplicación. Cobertura de pruebas amplia, incluidos casos límite y de concurrencia. Documentación completa. Analiza críticamente alternativas descartadas y aporta métricas o mejoras no exigidas. |

### 7.7. Mínimos exigibles

Se consideran contenidos mínimos para la superación del módulo:

- Manipular ficheros y directorios, y leer y escribir información en formato de
  texto, CSV, XML y JSON, gestionando las excepciones.
- Establecer una conexión con un SGBD relacional mediante un conector JDBC,
  ejecutar sentencias de definición, modificación y consulta, tratar el
  `ResultSet`, liberar los recursos y delimitar una transacción.
- Configurar una herramienta ORM, mapear un modelo de dominio con sus
  asociaciones y desarrollar una aplicación que cree, recupere, modifique y
  elimine objetos persistentes, incluyendo consultas JPQL y SQL nativas.
- Identificar las características de las bases de datos que almacenan objetos y
  gestionar la persistencia de objetos simples y estructurados.
- Conectar con una base de datos documental nativa, gestionar colecciones y
  documentos y realizar consultas sobre su contenido.
- Desarrollar un componente de acceso a datos con una interfaz definida,
  probarlo, documentarlo e integrarlo en una aplicación.

---

═══════════════════════════════════════════════════════════
## 8. RECUPERACIÓN Y EVALUACIÓN EXTRAORDINARIA
═══════════════════════════════════════════════════════════

### 8.1. Recuperación durante el curso

Al tratarse de un proyecto incremental, la recuperación es **inherente a la
metodología**: el alumnado que no supere un resultado de aprendizaje en una
entrega puede acreditarlo posteriormente, dado que el proyecto sigue vivo y las
capacidades no adquiridas vuelven a ser necesarias en los hitos siguientes.

Adicionalmente, tras cada evaluación se establece un **plan de recuperación
individualizado** que consiste en:

1. Un informe personalizado con los criterios de evaluación no superados.
2. La subsanación de los defectos del entregable correspondiente, con nueva
   fecha límite.
3. Actividades de refuerzo específicas sobre los contenidos deficitarios.
4. Una nueva defensa oral centrada en los criterios pendientes.

La calificación obtenida en la recuperación sustituye a la anterior, con el
límite de 7,0 sobre 10 en el instrumento recuperado, salvo que la causa de la no
superación sea justificada.

### 8.2. Alumnado que pierde el derecho a la evaluación continua

El alumnado que, por acumulación de faltas de asistencia conforme a lo previsto
en las normas de organización y funcionamiento del centro, pierda el derecho a
la evaluación continua, será evaluado mediante:

- La entrega del proyecto completo conforme a la especificación de los tres
  hitos, en la fecha que se determine.
- Una prueba práctica presencial de mayor extensión (6 horas) que incorpore
  requisitos de todos los resultados de aprendizaje.
- Una defensa oral del proyecto entregado.

### 8.3. Sesión de evaluación final y período extraordinario

El alumnado que no supere el módulo en la evaluación final de marzo dispondrá
del **período de recuperación** previsto en el calendario del ciclo (entre la
evaluación final del segundo curso y la evaluación final de junio), durante el
cual:

- Se le entrega un **informe individualizado** de evaluación con los resultados
  de aprendizaje y criterios pendientes, y un plan de trabajo con actividades de
  recuperación.
- Se realizan sesiones presenciales de seguimiento con la periodicidad que
  establezca el centro.
- La evaluación se realiza mediante la entrega del proyecto corregido y una
  prueba práctica de modificación análoga a la del apartado 7.4.

---

═══════════════════════════════════════════════════════════
## 9. ATENCIÓN A LA DIVERSIDAD
═══════════════════════════════════════════════════════════

### 9.1. Principio general

En la formación profesional no procede la realización de adaptaciones
curriculares significativas que afecten a los resultados de aprendizaje ni a los
criterios de evaluación, por su vinculación a las competencias profesionales del
título. Sí proceden las **adaptaciones de acceso al currículo** y las medidas
metodológicas y organizativas.

### 9.2. Medidas ordinarias previstas

- **Diversidad de ritmos.** La estructura por hitos incrementales admite ritmos
  distintos: el alumnado que avanza más rápido dispone de **requisitos
  opcionales de ampliación** en cada hito (caché de segundo nivel, pruebas de
  carga, índices y optimización de consultas, replicación, seguridad de la capa
  de datos), que no son exigibles pero elevan la calificación en el nivel de
  sobresaliente.
- **Alumnado con dificultades.** Se dispone de un **repositorio de referencia**
  con el hito anterior resuelto, de modo que una dificultad acumulada en un
  bloque no impide abordar el siguiente. Se acompaña de material de refuerzo
  guiado paso a paso y de tutoría individualizada.
- **Materiales accesibles.** Todos los materiales se publican en el aula virtual
  en formatos accesibles, con transcripción del código y sin dependencia
  exclusiva del color para transmitir información. Los enunciados se
  proporcionan por escrito además de oralmente.
- **Diversidad lingüística.** Los materiales se ofrecen en gallego y castellano;
  la terminología técnica se acompaña de su denominación en inglés, por ser la
  utilizada en la documentación profesional.

### 9.3. Adaptaciones de acceso

Para el alumnado con necesidades específicas de apoyo educativo se contemplan,
previo informe del departamento de orientación:

- Ampliación del tiempo de realización de las pruebas prácticas (hasta un 50 %).
- Uso de productos de apoyo y de software de accesibilidad en el puesto.
- Adaptación del formato de los enunciados y de la modalidad de la defensa oral.
- Ubicación preferente en el aula y adaptación del puesto informático.

---

═══════════════════════════════════════════════════════════
## 10. RECURSOS Y MATERIALES
═══════════════════════════════════════════════════════════

### 10.1. Recursos materiales

- Aula de informática con un puesto por alumno/a: mínimo 16 GB de RAM, SSD y
  virtualización habilitada.
- Proyector o pantalla para la codificación en directo.
- Conexión a internet y acceso al repositorio de dependencias Maven.
- Servidor del centro o servicio en la nube para el repositorio Git y la
  integración continua.

### 10.2. Software

Todo el software empleado es libre o dispone de licencia educativa gratuita:

| Categoría | Herramienta |
|-----------|-------------|
| JDK | OpenJDK / Temurin 25 (LTS) |
| IDE | IntelliJ IDEA Community o Ultimate (licencia educativa) / VS Code |
| Construcción | Apache Maven |
| Framework | Spring Boot 3.x, Spring Data JPA, Spring Data MongoDB |
| SGBD | H2, MariaDB, PostgreSQL (+ PostGIS), MongoDB Community |
| BDOO | ObjectDB (edición gratuita) |
| Contenedores | Docker Engine / Docker Desktop, Docker Compose |
| Pruebas | JUnit 5, AssertJ, Mockito, Testcontainers, JaCoCo |
| Cliente de BD | DBeaver, MongoDB Compass |
| Versiones | Git, GitHub o GitLab |
| Migraciones | Flyway |
| Documentación | Javadoc, springdoc-openapi |
| Aula virtual | Espazo Abalar / Moodle del centro |

### 10.3. Bibliografía y documentación

**Documentación técnica de referencia (fuente principal de consulta):**
- Documentación oficial de Spring Boot y Spring Data — `docs.spring.io`
- Java Platform SE 25 API Specification — Oracle
- Hibernate ORM User Guide
- Manual oficial de PostgreSQL y de MariaDB
- MongoDB Manual — `docs.mongodb.com`

**Bibliografía recomendada:**
- SIERRA, A.; SÁNCHEZ, A. *Acceso a datos*. Ra-Ma / Garceta.
- BAUER, C.; KING, G.; GREGORY, G. *Java Persistence with Hibernate*. Manning.
- WALLS, C. *Spring in Action*. Manning.
- BLOCH, J. *Effective Java*. Addison-Wesley.
- KARWIN, B. *SQL Antipatterns*. Pragmatic Bookshelf.
- MARTIN, R. C. *Clean Architecture*. Prentice Hall.

---

═══════════════════════════════════════════════════════════
## 11. ELEMENTOS TRANSVERSALES
═══════════════════════════════════════════════════════════

- **Protección de datos personales.** El dominio del proyecto (datos de
  conductores, infracciones, domicilios) es un vehículo natural para el
  tratamiento del RGPD y de la LOPDGDD: minimización, limitación de la finalidad,
  seudonimización, cifrado de datos en reposo, registro de accesos y derecho de
  supresión. Se exige que el proyecto contemple la seudonimización de los datos
  de prueba y no incorpore datos reales.
- **Seguridad de la información.** Prevención de inyección SQL y NoSQL, gestión
  segura de credenciales fuera del código fuente, principio de mínimo privilegio
  en los usuarios de base de datos, y tratamiento seguro de la deserialización.
- **Calidad y sostenibilidad.** Eficiencia de las consultas y su impacto en el
  consumo de recursos; el coste energético de una consulta mal indexada a escala
  de producción.
- **Igualdad y no discriminación.** Cuidado en la elaboración de los datos de
  prueba y de los ejemplos, evitando estereotipos; atención al sesgo en los
  nombres y perfiles utilizados en el conjunto de datos del proyecto.
- **Accesibilidad universal.** Aunque el módulo no aborda la interfaz de usuario,
  se trata la accesibilidad de la documentación técnica generada y de los
  formatos de exportación de datos.
- **Emprendimiento e iniciativa.** Autonomía en la toma de decisiones técnicas y
  justificación de las mismas ante terceros, mediante el diario técnico y las
  defensas orales.

---

═══════════════════════════════════════════════════════════
## 12. ACTIVIDADES COMPLEMENTARIAS Y EXTRAESCOLARES
═══════════════════════════════════════════════════════════

- Visita a una empresa del sector TIC de la comarca, con especial atención a su
  arquitectura de datos.
- Charla de una persona egresada del ciclo en activo, sobre la capa de
  persistencia en proyectos reales.
- Participación, en su caso, en competiciones de programación o *hackathons*
  autonómicos.
- Asistencia a jornadas técnicas o eventos de comunidades de desarrollo de
  Galicia.

---

═══════════════════════════════════════════════════════════
## 13. SEGUIMIENTO Y EVALUACIÓN DE LA PROGRAMACIÓN
═══════════════════════════════════════════════════════════

La programación se revisa en las reuniones ordinarias del departamento, con la
periodicidad establecida, atendiendo a los siguientes indicadores:

| Indicador | Fuente | Umbral de revisión |
|-----------|--------|--------------------|
| Grado de cumplimiento de la temporalización | Diario de aula | Desviación superior al 15 % |
| Porcentaje de alumnado que supera cada RA | Registro de calificaciones | Inferior al 60 % en algún RA |
| Porcentaje de alumnado que supera el módulo | Actas | Inferior al 70 % |
| Adecuación de la carga de trabajo del proyecto | Encuesta al alumnado | Valoración media inferior a 3/5 |
| Vigencia de la pila tecnológica | Revisión anual del sector | Versión LTS obsoleta |

Al finalizar el curso se elabora una **memoria** con las propuestas de mejora
para el curso siguiente, que incluye, como mínimo, la revisión del pliego de la
prueba práctica final —que debe ser distinto cada curso— y la actualización de
las versiones de la pila tecnológica.

---

*Programación didáctica elaborada conforme al Decreto 276/2011 (DOG nº 20, do
30/01/2012) y a la Orde do 12 de xullo de 2011.*
