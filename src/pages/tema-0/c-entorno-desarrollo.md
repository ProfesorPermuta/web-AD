---
title: 'Entorno de desarrollo: JDK 25, Maven, IDE y Git'
description: Instalación y verificación del entorno reproducible del módulo, con la checklist de arranque.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Entorno de desarrollo

El objetivo de este apartado es que tengas un entorno **completo y
reproducible**. «Reproducible» significa que otra persona —o tú mismo dentro de
seis meses, o el profesorado al corregir— pueda clonar tu repositorio y hacerlo
funcionar sin adivinar nada.

## La pila del módulo

| Categoría | Herramienta | Versión |
|-----------|-------------|---------|
| JDK | OpenJDK / Temurin | **25 (LTS)** |
| Construcción | Apache Maven | 3.9+ |
| IDE | IntelliJ IDEA Community/Ultimate o VS Code | actual |
| Control de versiones | Git | 2.40+ |
| Contenedores | Docker Engine / Docker Desktop + Compose | actual |
| Cliente SQL | DBeaver | actual |
| Cliente MongoDB | MongoDB Compass | actual |

Todo el software es libre o dispone de licencia educativa gratuita.

## JDK 25

### Por qué exactamente la 25

Java 25 es una versión **LTS** (*Long Term Support*): recibe soporte durante
años, que es lo que una empresa exige de la plataforma sobre la que despliega.
Spring Boot 3.x requiere como mínimo Java 17; usamos la 25 porque es la LTS
vigente y porque incorpora características que aprovecharemos —`records`,
`sealed`, *pattern matching* en `switch`, bloques de texto.

:::warning[No uses la última versión no-LTS]

Si instalas Java 26 «porque es más nueva», te encontrarás con librerías que
aún no la soportan y con mensajes de error que no te dicen eso. Instala 25.

:::

### Instalación

**Windows** (recomendado con winget):

```bash
winget install EclipseAdoptium.Temurin.25.JDK
```

**Linux (Debian/Ubuntu):**

```bash
sudo apt update
sudo apt install temurin-25-jdk
```

**macOS** (con Homebrew):

```bash
brew install --cask temurin@25
```

Alternativa multiplataforma y muy cómoda si necesitas varias versiones a la vez:
[SDKMAN!](https://sdkman.io/) en Linux/macOS.

```bash
sdk install java 25-tem
sdk default java 25-tem
```

### Verificación

```bash
java -version
javac -version
echo $JAVA_HOME     # Windows PowerShell: echo $env:JAVA_HOME
```

Debe responder algo como:

```
openjdk version "25" 2025-09-16 LTS
```

:::danger[Si `JAVA_HOME` está vacío]

Maven y muchas herramientas lo necesitan. En Windows, defínelo en *Variables de
entorno del sistema* apuntando a la carpeta del JDK (**no** a su subcarpeta
`bin`). En Linux/macOS, exporta en `~/.bashrc` o `~/.zshrc`:

```bash
export JAVA_HOME=/usr/lib/jvm/temurin-25-jdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
```

:::

## Maven

Maven es la herramienta de construcción y de gestión de dependencias. Se encarga
de descargar las librerías, compilar, ejecutar las pruebas y empaquetar.

### Instalación

```bash
# Windows
winget install Apache.Maven

# Debian/Ubuntu
sudo apt install maven

# macOS
brew install maven
```

### Verificación

```bash
mvn -version
```

Debe mostrar Maven 3.9 o superior **y** confirmar que usa el JDK 25:

```
Apache Maven 3.9.9
Java version: 25, vendor: Eclipse Adoptium
```

:::tip[El wrapper]

El proyecto que generarás incluye `mvnw` (Linux/macOS) y `mvnw.cmd` (Windows),
el **Maven Wrapper**. Descarga y usa la versión exacta de Maven que el proyecto
declara, sin depender de la que tengas instalada. Es lo que garantiza que la
construcción sea reproducible en cualquier máquina.

Por eso, en el proyecto usa siempre `./mvnw` en lugar de `mvn`, y **nunca**
añadas `mvnw` ni `.mvn/` al `.gitignore`.

:::

### El repositorio local

Maven guarda las dependencias descargadas en `~/.m2/repository`. La primera
construcción del proyecto descargará bastante (Spring Boot arrastra muchos
artefactos) y tardará varios minutos. Las siguientes son casi instantáneas.

```bash
# Comandos que usarás constantemente
./mvnw clean            # borra target/
./mvnw compile          # compila
./mvnw test             # ejecuta las pruebas
./mvnw package          # genera el JAR en target/
./mvnw spring-boot:run  # arranca la aplicación
./mvnw dependency:tree  # muestra el árbol de dependencias
```

## IDE

Vale cualquiera de los dos. La diferencia práctica en este módulo es pequeña;
IntelliJ tiene mejor soporte de Spring de fábrica, VS Code es más ligero.

### IntelliJ IDEA

La edición **Community** basta para todo el módulo. La **Ultimate** es gratuita
para estudiantes con la [licencia
educativa](https://www.jetbrains.com/community/education/) y añade soporte
específico de Spring, cliente de base de datos integrado y perfilado HTTP.

Complementos útiles: *Spring Boot Assistant*, *SonarLint*, *Rainbow Brackets*.

### VS Code

Instala el paquete de extensiones:

- **Extension Pack for Java** (Microsoft)
- **Spring Boot Extension Pack** (VMware)
- **Docker** (Microsoft)

### Configuración común mínima

Sea cual sea el IDE, configura desde el primer día:

1. **Codificación UTF-8** en todo el proyecto. Vas a leer ficheros CSV y XML con
   tildes y con eñes; una codificación mal puesta genera errores que parecen de
   lógica y no lo son.
2. **Finales de línea LF**. Si trabajas en Windows y el corrector en Linux, los
   `CRLF` ensucian los diffs de Git. Ver el apartado de [Git](/tema-0/h-flujo-git).
3. **Formateo automático al guardar**, con el estilo por defecto del IDE. El
   objetivo no es el estilo concreto, es que no aparezcan cambios de formato
   mezclados con cambios de lógica en los commits.
4. **Nivel de lenguaje 25** en la configuración del proyecto.

## Git

```bash
# Windows
winget install Git.Git

# Debian/Ubuntu
sudo apt install git

# macOS
brew install git
```

Configuración inicial obligatoria — con tu **nombre real** y el correo de la
cuenta que uses en GitHub/GitLab, porque es lo que aparecerá en el historial que
se evalúa:

```bash
git config --global user.name "Nombre Apellidos"
git config --global user.email "correo@ejemplo.com"
git config --global init.defaultBranch main
git config --global core.autocrlf input   # en Windows: true
```

El flujo de trabajo completo se trata en el [apartado h](/tema-0/h-flujo-git).

## Clientes de base de datos

No son imprescindibles para que el código funcione, pero sí para **entender qué
está pasando**. Los necesitarás a partir de la UD3.

- **DBeaver Community** — cliente universal: sirve para H2, MariaDB y
  PostgreSQL. Un único programa para las tres bases relacionales del curso.
- **MongoDB Compass** — cliente gráfico oficial de MongoDB, necesario a partir
  de la UD6.

## Checklist de arranque

Esta es la comprobación de la actividad
[A0.1](/tema-0/i-actividades#a01--instalación-y-verificación-del-entorno).
Ejecuta todo y guarda la salida.

```bash
java -version          # ⇒ 25.x
javac -version         # ⇒ 25.x
mvn -version           # ⇒ 3.9+, con Java 25
git --version          # ⇒ 2.40+
docker --version       # ⇒ instalado
docker compose version # ⇒ v2.x
docker run --rm hello-world   # ⇒ el demonio responde
```

| # | Comprobación | Estado |
|---|--------------|--------|
| 1 | `java -version` responde 25.x | ☐ |
| 2 | `JAVA_HOME` apunta al JDK 25 | ☐ |
| 3 | `mvn -version` usa el JDK 25 | ☐ |
| 4 | `git --version` responde y `user.name`/`user.email` están configurados | ☐ |
| 5 | `docker run --rm hello-world` funciona **sin sudo** | ☐ |
| 6 | `docker compose version` responde v2 | ☐ |
| 7 | El IDE abre un proyecto Maven y reconoce el nivel de lenguaje 25 | ☐ |
| 8 | DBeaver instalado | ☐ |
| 9 | Cuenta de GitHub/GitLab creada y clave SSH registrada | ☐ |

## Problemas frecuentes

:::note[`mvn` usa un JDK distinto del que esperas]

Maven no mira el `java` del `PATH`, mira `JAVA_HOME`. Si `java -version` dice 25
pero `mvn -version` dice 17, tienes `JAVA_HOME` apuntando a otro sitio.

:::

:::note[El IDE compila con otro nivel de lenguaje]

Aunque el `pom.xml` declare Java 25, el IDE mantiene su propia configuración de
proyecto. En IntelliJ: *File → Project Structure → Project SDK* y *Language
level*. Si no coinciden, verás errores en `record` o en `switch` con patrones que
no existen en la línea de comandos.

:::

:::note[Docker pide `sudo` en Linux]

Añade tu usuario al grupo `docker` y **reinicia la sesión**:

```bash
sudo usermod -aG docker $USER
```

:::

:::note[Rutas con espacios o con tildes]

Evita colocar el proyecto en rutas del tipo `C:\Users\...\Mis documentos\...`.
Algunas herramientas de la cadena (especialmente en Windows) siguen tratándolas
mal. Usa una ruta corta y sin espacios, por ejemplo `C:\dev\multagal`.

:::

<NextPageButton to="/tema-0/d-docker-servicios-datos">
Docker y los servicios de datos
</NextPageButton>
