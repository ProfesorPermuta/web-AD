---
title: Anatomía de un proyecto Spring Boot
description: Spring Initializr, pom.xml, starters, configuración con application.yml y perfiles.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Anatomía de un proyecto Spring Boot

Todo el módulo se desarrolla sobre **Spring Boot 3.x con Java 25**, desde la
primera sesión. No hay una fase previa «en Java puro» seguida de una migración
posterior.

## Por qué Spring Boot desde el primer día

:::info
[Decisión metodológica]

- **Empleabilidad.** En el ecosistema Java profesional, la persistencia se hace
  con Spring de forma abrumadoramente mayoritaria. Retrasar su introducción al
  último trimestre reduciría el tiempo de exposición real a la herramienta que
  usarás en la FCT y en tu primer empleo.
- **Vocabulario base.** La inyección de dependencias y el patrón repositorio se
  convierten en el lenguaje común del módulo, no en un añadido final. La
  separación en capas (`controller` → `service` → `repository` → `entity`) es
  precisamente el marco conceptual que da sentido al RA6.
- **No oculta el currículo, lo enmarca.** Trabajaremos `JdbcTemplate` y
  `DataSource` sobre JDBC nativo (RA2), Hibernate como implementación de JPA
  (RA3), `spring-data-mongodb` y `MongoTemplate` (RA5).

:::

:::danger
[Exigencia explícita]

En todos los casos se te exigirá **conocer y justificar qué hace el framework
por debajo**. Que Spring Data te genere un `findByMatricula()` sin escribir SQL
no te exime de saber qué SQL se ejecuta, cuándo se abre la conexión y cuándo se
cierra. Esto se comprueba en la defensa oral y en las pruebas escritas.

:::

## Generar el proyecto: Spring Initializr

Ve a [start.spring.io](https://start.spring.io) y configura:

| Campo | Valor |
|-------|-------|
| Project | **Maven** |
| Language | **Java** |
| Spring Boot | 4.x (la última estable, no SNAPSHOT ni M) |
| Group | `es.edu.multagal` |
| Artifact | `multagal` |
| Name | `multagal` |
| Package name | `es.edu.multagal` |
| Packaging | **Jar** |
| Java | **25** |

Dependencias para arrancar la UD0 — sólo estas, ya iremos añadiendo:

- **Spring Web** (para exponer endpoints y ver que arranca)
- **Spring Boot DevTools** (recarga automática en desarrollo)
- **Lombok** (opcional; reduce el código repetitivo)
- **Validation**

También puedes generarlo desde la línea de comandos:

```bash
curl https://start.spring.io/starter.zip \
  -d type=maven-project -d language=java -d bootVersion=3.4.1 \
  -d groupId=es.edu.multagal -d artifactId=multagal \
  -d name=multagal -d packageName=es.edu.multagal \
  -d javaVersion=25 -d dependencies=web,devtools,lombok,validation \
  -o multagal.zip
unzip multagal.zip -d multagal
```

## Estructura de directorios

```
multagal/
├── .mvn/wrapper/            ← Maven Wrapper (¡NO ignorar en Git!)
├── mvnw  mvnw.cmd           ← lanzadores del wrapper
├── pom.xml                  ← descriptor Maven: dependencias y construcción
├── compose.yaml             ← servicios de datos (apartado d)
├── .env  .env.example       ← credenciales (.env NO se sube)
├── .gitignore
├── README.md
├── docs/
│   └── adr/                 ← el diario técnico
└── src/
    ├── main/
    │   ├── java/es/edu/multagal/
    │   │   └── MultagalApplication.java   ← punto de entrada
    │   └── resources/
    │       ├── application.yml            ← configuración
    │       ├── application-dev.yml        ← perfil dev
    │       ├── application-prod.yml       ← perfil prod
    │       ├── static/                    ← recursos estáticos
    │       └── db/migration/              ← migraciones Flyway (UD3)
    └── test/
        ├── java/es/edu/multagal/
        └── resources/
```

:::warning
[`src/main/resources` no es una carpeta cualquiera]

Todo lo que hay en `resources` se empaqueta **dentro del JAR**. Eso significa
que no es un directorio del sistema de ficheros en tiempo de ejecución: no
puedes escribir en él, y leerlo requiere `ClassPathResource` o
`getResourceAsStream`, no `new File(...)`. Es una confusión clásica que
reaparecerá en la UD1.

:::

## El `pom.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" ...>
    <modelVersion>4.0.0</modelVersion>

    <!-- El POM padre aporta la gestión de versiones de TODAS
         las dependencias del ecosistema Spring -->
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.4.1</version>
        <relativePath/>
    </parent>

    <groupId>es.edu.multagal</groupId>
    <artifactId>multagal</artifactId>
    <version>0.1.0-SNAPSHOT</version>
    <name>multagal</name>
    <description>Sistema de gestión de sanciones de tráfico</description>

    <properties>
        <java.version>25</java.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-devtools</artifactId>
            <scope>runtime</scope>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>
</project>
```

### Dos detalles que conviene entender ya

**Las dependencias no llevan `<version>`.** El `spring-boot-starter-parent`
declara un *dependency management* con las versiones compatibles entre sí de
cientos de artefactos. Al heredar de él, tú pides `spring-boot-starter-web` y
Maven resuelve la versión que Spring Boot 3.4.1 ha probado con el resto. Eso
evita el llamado *JAR hell*: combinaciones de librerías incompatibles.

**Qué es un *starter*.** Un starter no contiene código: es una dependencia
agregadora que arrastra el conjunto coherente de librerías necesarias para una
funcionalidad, más su **autoconfiguración**.

```bash
./mvnw dependency:tree
```

Verás que `spring-boot-starter-web`, que en el POM ocupa cuatro líneas, arrastra
Tomcat embebido, Jackson, Spring MVC, la validación y sus transitivas.

Starters que usaremos a lo largo del curso:

| Starter | Unidad | Qué aporta |
|---------|--------|------------|
| `spring-boot-starter-web` | UD0 | Spring MVC + Tomcat embebido |
| `spring-boot-starter-jdbc` | UD3 | `DataSource`, `JdbcTemplate`, HikariCP |
| `spring-boot-starter-data-jpa` | UD4 | Hibernate + repositorios JPA |
| `spring-boot-starter-data-mongodb` | UD6 | Driver de MongoDB + `MongoTemplate` |
| `spring-boot-starter-test` | todas | JUnit 5, AssertJ, Mockito, `@SpringBootTest` |

## El punto de entrada

```java
package es.edu.multagal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class MultagalApplication {

    public static void main(String[] args) {
        SpringApplication.run(MultagalApplication.class, args);
    }
}
```

Diez líneas que hacen mucho. `@SpringBootApplication` es una anotación compuesta
equivalente a tres:

| Anotación incluida | Qué hace |
|--------------------|----------|
| `@SpringBootConfiguration` | Marca la clase como fuente de definiciones de beans. |
| `@ComponentScan` | Escanea **este paquete y todos sus subpaquetes** buscando componentes que registrar. |
| `@EnableAutoConfiguration` | Configura automáticamente el contexto según lo que encuentre en el *classpath*. |

:::danger
[El paquete raíz importa]

`@ComponentScan` escanea desde el paquete de la clase anotada **hacia abajo**.
Si `MultagalApplication` está en `es.edu.multagal` y creas un servicio en
`es.edu.otro`, Spring **no lo encontrará** y obtendrás un
`NoSuchBeanDefinitionException` que no menciona el paquete por ninguna parte.

Regla: la clase de arranque va en el paquete raíz, y todo lo demás cuelga de él.

:::

### La autoconfiguración, en concreto

`@EnableAutoConfiguration` aplica configuraciones condicionadas al contenido del
classpath. En la práctica:

- ¿Hay Tomcat en el classpath? ⇒ arranca un servidor web en el puerto 8080.
- ¿Hay un driver JDBC y una `spring.datasource.url` definida? ⇒ crea un
  `DataSource` con pool HikariCP.
- ¿Hay Hibernate? ⇒ crea un `EntityManagerFactory` y un gestor de transacciones.

Para ver exactamente qué se ha autoconfigurado y qué no —y **por qué**:

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug
```

Imprime el *Condition Evaluation Report*, con las secciones `Positive matches` y
`Negative matches`. Es la herramienta para responder «¿por qué no me está
inyectando el `DataSource`?».

## Configuración: `application.yml`

Spring Boot acepta `application.properties` o `application.yml`. Usaremos YAML
por la jerarquía, que se lee mucho mejor cuando la configuración crece.

`src/main/resources/application.yml`:

```yaml
spring:
  application:
    name: multagal
  profiles:
    default: dev

server:
  port: 8080

logging:
  level:
    es.edu.multagal: DEBUG

multagal:
  entrada:
    directorio: ./datos/entrada
  archivo:
    directorio: ./datos/archivo
```

El bloque `multagal:` es configuración **propia**, no de Spring. Ahí colocaremos
los parámetros del proyecto — rutas, plazos, importes — para no escribirlos en
el código. En el [apartado siguiente](/tema-0/f-inyeccion-dependencias) veremos
cómo leerlos con `@ConfigurationProperties`.

### Perfiles

Un **perfil** es un conjunto de configuración que se activa por nombre. Es el
mecanismo que permite que el **mismo código** funcione contra H2 en desarrollo y
contra MariaDB en producción, que es exactamente lo que pide la actividad A3.2.

`application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:h2:mem:multagal;DB_CLOSE_DELAY=-1
    username: sa
    password:
  h2:
    console:
      enabled: true
logging:
  level:
    org.springframework.jdbc: DEBUG
```

`application-prod.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/multagal
    username: ${MARIADB_USER}
    password: ${MARIADB_PASSWORD}
logging:
  level:
    root: WARN
```

Activación:

```bash
# por argumento
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod

# por variable de entorno
SPRING_PROFILES_ACTIVE=prod java -jar target/multagal-0.1.0-SNAPSHOT.jar

# por propiedad del sistema
java -Dspring.profiles.active=prod -jar target/multagal.jar
```

:::tip
[Sustitución de variables de entorno]

`${MARIADB_PASSWORD}` en el YAML se resuelve contra las variables de entorno del
proceso. Ese es el mecanismo por el que **las credenciales nunca aparecen en el
repositorio**, y el que hace posible cumplir la condición formal de los
entregables.

:::

### Orden de precedencia

Cuando la misma propiedad está definida en varios sitios, Spring aplica un orden
estricto. De **mayor** a **menor** prioridad, simplificado:

1. Argumentos de línea de comandos (`--server.port=9090`)
2. Variables de entorno (`SERVER_PORT=9090`)
3. `application-{perfil}.yml`
4. `application.yml`
5. Valores por defecto en el código (`@Value("${x:valorPorDefecto}")`)

:::note[La regla de traducción de nombres]

`spring.datasource.username` en YAML equivale a `SPRING_DATASOURCE_USERNAME`
como variable de entorno: mayúsculas, puntos por guiones bajos. Esto se llama
*relaxed binding* y es lo que permite configurar un contenedor sin tocar
ficheros.

:::

## Primer arranque

```bash
./mvnw spring-boot:run
```

Salida esperada:

```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
...
 :: Spring Boot ::                (v3.4.1)

INFO  --- Starting MultagalApplication using Java 25
INFO  --- The following 1 profile is active: "dev"
INFO  --- Tomcat initialized with port 8080 (http)
INFO  --- Started MultagalApplication in 1.482 seconds
```

Comprueba en el navegador `http://localhost:8080`. Verás un error 404 de
Whitelabel: es lo correcto — el servidor responde, simplemente no hay nada
mapeado en la raíz todavía.

## Empaquetado

```bash
./mvnw clean package
java -jar target/multagal-0.1.0-SNAPSHOT.jar
```

El `spring-boot-maven-plugin` genera un **fat JAR** (o *uber JAR*): un único
archivo que contiene tu código, todas las dependencias y el servidor Tomcat
embebido. Se ejecuta con `java -jar` en cualquier máquina que tenga un JDK 25,
sin instalar nada más. En la UD7 lo meteremos en una imagen Docker.

## Problemas frecuentes

:::note
[«Web server failed to start. Port 8080 was already in use»]

Tienes otra instancia corriendo, seguramente desde el IDE. Párala, o cambia el
puerto con `--server.port=8081`.

:::

:::note
[«Failed to configure a DataSource: 'url' attribute is not specified»]

Has añadido `spring-boot-starter-data-jpa` o `-jdbc` al `pom.xml` pero no has
configurado la base. La autoconfiguración detecta el starter e intenta crear el
`DataSource`, y falla. Configura `spring.datasource.url` o retira el starter
hasta que lo necesites.

:::

:::note
[Lombok: el IDE marca errores donde `mvn` compila bien]

Lombok genera código en tiempo de compilación mediante un procesador de
anotaciones. IntelliJ necesita la opción *Enable annotation processing*
activada; VS Code, la extensión de Lombok.

:::

<NextPageButton to="/tema-0/f-inyeccion-dependencias">
IoC, inyección de dependencias y arquitectura en capas
</NextPageButton>
