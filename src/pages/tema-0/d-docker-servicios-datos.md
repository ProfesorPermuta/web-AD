---
title: 'Docker y Docker Compose: los servicios de datos del curso'
description: Por qué usamos contenedores para las bases de datos y cómo levantar MariaDB, PostgreSQL y MongoDB con un único fichero Compose.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Docker y Docker Compose

Durante el curso vamos a necesitar **tres gestores de bases de datos distintos**:
MariaDB (UD3), PostgreSQL con PostGIS (UD5) y MongoDB (UD6). Instalarlos
nativamente en tu equipo significaría tres instaladores, tres servicios
arrancando al iniciar el sistema, tres configuraciones que ajustar y una
desinstalación complicada al terminar el curso.

Con Docker se resuelve con un fichero de texto y un comando.

## Qué es un contenedor

Un **contenedor** es un proceso aislado que se ejecuta sobre el núcleo del
sistema anfitrión, con su propio sistema de ficheros, su propia red y sus
propios límites de recursos.

:::info
[Contenedor ≠ máquina virtual]

Una máquina virtual emula hardware completo y arranca un sistema operativo
entero: consume gigabytes y tarda minutos. Un contenedor comparte el núcleo del
anfitrión y sólo empaqueta el proceso y sus dependencias: consume megabytes y
arranca en segundos.

Por eso puedes tener MariaDB, PostgreSQL y MongoDB arrancados a la vez en un
portátil sin que se note.

:::

### Vocabulario mínimo

| Término | Qué es |
|---------|--------|
| **Imagen** | Plantilla de sólo lectura: el sistema de ficheros y la configuración de arranque. `postgres:16` es una imagen. |
| **Contenedor** | Instancia en ejecución de una imagen. De una imagen se pueden lanzar N contenedores. |
| **Volumen** | Almacenamiento gestionado por Docker que **sobrevive** a la destrucción del contenedor. Aquí viven los datos. |
| **Red** | Red virtual que conecta contenedores entre sí por nombre. |
| **Registro** | Repositorio de imágenes. Por defecto, Docker Hub. |
| **Compose** | Herramienta que define varios servicios en un único fichero YAML. |

:::danger
[Sin volumen no hay datos]

Todo lo que un contenedor escribe en su propio sistema de ficheros **se pierde**
al destruirlo. Si no declaras un volumen para el directorio de datos de la base,
cada `docker compose down` borrará tu base de datos entera. Es el error número
uno de quien empieza.

:::

## Instalación

- **Windows / macOS:** [Docker Desktop](https://www.docker.com/products/docker-desktop/).
  En Windows requiere WSL 2 y virtualización habilitada en la BIOS.
- **Linux:** Docker Engine desde el repositorio oficial, y añadir tu usuario al
  grupo `docker`.

Verificación:

```bash
docker --version
docker compose version
docker run --rm hello-world
```

:::
warning[`docker compose` con espacio]

La versión antigua era `docker-compose` (con guion, un binario aparte). La
actual es `docker compose` (subcomando, plugin v2). Usa siempre la segunda; la
sintaxis del fichero es la misma pero los mensajes de error y algunas opciones
difieren.

:::

## Comandos que vas a usar

```bash
docker ps                      # contenedores en ejecución
docker ps -a                   # incluidos los parados
docker images                  # imágenes descargadas
docker logs -f multagal-mariadb    # ver la salida de un contenedor
docker exec -it multagal-mariadb bash   # abrir una shell dentro
docker volume ls               # volúmenes
docker stats                   # consumo de recursos en vivo
```

Con Compose, situado en la carpeta del `compose.yaml`:

```bash
docker compose up -d           # levanta todos los servicios en segundo plano
docker compose up -d postgres  # levanta sólo uno
docker compose ps              # estado de los servicios
docker compose logs -f mongo   # sigue el log de un servicio
docker compose stop            # para sin destruir
docker compose down            # para y destruye contenedores (conserva volúmenes)
docker compose down -v         # ⚠️ destruye TAMBIÉN los volúmenes: borra los datos
```

## El `compose.yaml` del curso

Este fichero va en la raíz del repositorio del proyecto y define los tres
servicios de datos. Créalo ahora, aunque hasta la UD3 sólo uses uno.

```yaml
name: multagal

services:

  # ─── UD3: base de datos relacional ────────────────────────────────
  mariadb:
    image: mariadb:11
    container_name: multagal-mariadb
    restart: unless-stopped
    environment:
      MARIADB_ROOT_PASSWORD: ${MARIADB_ROOT_PASSWORD}
      MARIADB_DATABASE: multagal
      MARIADB_USER: ${MARIADB_USER}
      MARIADB_PASSWORD: ${MARIADB_PASSWORD}
    ports:
      - "3306:3306"
    volumes:
      - mariadb-data:/var/lib/mysql
    healthcheck:
      test: ["CMD", "healthcheck.sh", "--connect", "--innodb_initialized"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ─── UD5: base de datos objeto-relacional + geoespacial ───────────
  postgres:
    image: postgis/postgis:16-3.4
    container_name: multagal-postgres
    restart: unless-stopped
    environment:
      POSTGRES_DB: multagal
      POSTGRES_USER: ${POSTGRES_USER}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U $${POSTGRES_USER} -d multagal"]
      interval: 10s
      timeout: 5s
      retries: 5

  # ─── UD6: base de datos documental ────────────────────────────────
  mongo:
    image: mongo:7
    container_name: multagal-mongo
    restart: unless-stopped
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_USER}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_PASSWORD}
      MONGO_INITDB_DATABASE: multagal
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db
    healthcheck:
      test: ["CMD", "mongosh", "--eval", "db.adminCommand('ping')"]
      interval: 10s
      timeout: 5s
      retries: 5

volumes:
  mariadb-data:
  postgres-data:
  mongo-data:
```

### Lectura del fichero, línea a línea

| Clave | Qué hace y por qué importa |
|-------|----------------------------|
| `image` | Imagen **con versión fija**. Nunca `latest`: mañana sería otra versión y tu proyecto dejaría de ser reproducible. |
| `container_name` | Nombre fijo para poder hacer `docker logs multagal-mariadb` sin buscar el identificador. |
| `restart: unless-stopped` | Se vuelve a levantar si el equipo se reinicia, salvo que lo pares tú explícitamente. |
| `environment` | Variables que la imagen usa para crear la base y el usuario en el primer arranque. |
| `ports` | `"3306:3306"` = puerto del anfitrión : puerto del contenedor. Es lo que permite que tu aplicación, que corre fuera de Docker, se conecte a `localhost:3306`. |
| `volumes` | `mariadb-data:/var/lib/mysql` monta un volumen persistente sobre el directorio de datos. **Esto es lo que salva tus datos.** |
| `healthcheck` | Comprobación periódica de que el servicio responde de verdad, no sólo de que el proceso existe. |

:::tip[`$$` en el healthcheck de PostgreSQL]

En `pg_isready -U $${POSTGRES_USER}` el doble dólar escapa la interpolación de
Compose: queremos que la variable la resuelva la *shell dentro del contenedor*,
no Compose al leer el fichero. Con un solo `$` Compose lo sustituiría por el
valor de tu `.env` y funcionaría igual por casualidad, pero es incorrecto.

:::

## Credenciales: el fichero `.env`

Observa que en el `compose.yaml` **no hay ni una sola contraseña escrita**. Todas
son `${VARIABLE}`, que Compose lee de un fichero `.env` situado junto a él.

`.env` — **este fichero NO se sube al repositorio**:

```bash
MARIADB_ROOT_PASSWORD=cambiaEstaClaveRoot
MARIADB_USER=multagal
MARIADB_PASSWORD=cambiaEstaClave

POSTGRES_USER=multagal
POSTGRES_PASSWORD=cambiaEstaClave

MONGO_USER=multagal
MONGO_PASSWORD=cambiaEstaClave
```

`.env.example` — **este sí se sube**, con valores de relleno, para que quien
clone el repositorio sepa qué variables necesita:

```bash
MARIADB_ROOT_PASSWORD=
MARIADB_USER=multagal
MARIADB_PASSWORD=
POSTGRES_USER=multagal
POSTGRES_PASSWORD=
MONGO_USER=multagal
MONGO_PASSWORD=
```

Y en `.gitignore`:

```
.env
```

:::danger[Criterio de evaluación]

«Ausencia de credenciales o de rutas absolutas escritas en el código fuente» es
una **condición formal de los entregables**. Una contraseña commiteada, aunque
sea de una base de datos local, penaliza. Y ten presente que borrarla en un
commit posterior **no la elimina del historial**: sigue ahí para siempre.

:::

## Comprobación de que funciona

```bash
docker compose up -d
docker compose ps
```

Los tres servicios deben aparecer como `running (healthy)`. Si alguno queda en
`starting` más de un minuto o pasa a `unhealthy`, mira su log:

```bash
docker compose logs mariadb
```

Conexión directa a cada uno para confirmar:

```bash
# MariaDB
docker exec -it multagal-mariadb mariadb -u multagal -p multagal

# PostgreSQL — comprobando además que PostGIS está disponible
docker exec -it multagal-postgres psql -U multagal -d multagal \
  -c "CREATE EXTENSION IF NOT EXISTS postgis; SELECT postgis_version();"

# MongoDB
docker exec -it multagal-mongo mongosh -u multagal -p
```

## Problemas frecuentes

:::note[«port is already allocated»]

Ya tienes algo escuchando en ese puerto — típicamente una instalación nativa de
MySQL o de PostgreSQL. O paras el servicio nativo, o cambias el puerto del
anfitrión en el `compose.yaml`: `"3307:3306"`, y entonces tu aplicación se
conecta al 3307.

:::

:::note[Cambio las variables del `.env` y no tiene efecto]

Las imágenes de base de datos sólo aplican `MARIADB_USER`, `POSTGRES_PASSWORD`,
etc. en el **primer arranque**, cuando el volumen de datos está vacío. Si ya
existe, las ignoran. Para forzar la reinicialización:

```bash
docker compose down -v && docker compose up -d
```

Y sí, eso **borra los datos**. Por eso conviene fijar las credenciales antes de
empezar a cargar nada.

:::

:::note[Docker consume demasiada memoria en Windows]

Docker Desktop sobre WSL 2 tiende a acaparar RAM. Limítalo creando
`C:\Users\<usuario>\.wslconfig`:

```ini
[wsl2]
memory=6GB
processors=4
```

Y reinicia WSL con `wsl --shutdown`.

:::

:::note[Levantar sólo lo que necesito]

No hace falta tener los tres arrancados todo el curso. En la UD3 basta con
`docker compose up -d mariadb`; el resto se quedan parados sin consumir nada.

:::

## Lo que viene después

En la UD7 daremos la vuelta al planteamiento: en lugar de contenerizar sólo las
bases de datos, contenerizaremos **también la aplicación**, y el `compose.yaml`
levantará el sistema completo. De momento, tu aplicación corre en tu máquina y
sólo las bases están en contenedores.

<NextPageButton to="/tema-0/e-anatomia-spring-boot">
Anatomía de un proyecto Spring Boot
</NextPageButton>
