---
title: 'WatchService: vigilancia de un directorio'
description: Cómo detectar automáticamente creaciones, modificaciones y borrados en un directorio con WatchService.
---

import NextPageButton from '@site/src/components/NextPageButton';

# WatchService: vigilancia de un directorio

## El problema: saber cuándo cambia algo sin *polling*

Hay procesos que necesitan reaccionar en cuanto aparece un fichero nuevo en
un directorio — por ejemplo, un directorio de entrada donde otro sistema
deposita ficheros para ser procesados. La forma ingenua de resolverlo es
comprobar el directorio en un bucle con `Thread.sleep`, pero eso desperdicia
CPU y añade latencia. `java.nio.file.WatchService` permite **suscribirse** a
los eventos del sistema de ficheros del sistema operativo, sin *polling*
manual.

## Registrar un directorio

```java
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

Path directorio = Path.of("entrada");

try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
    directorio.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

    // ... bucle de espera, ver más abajo
} catch (java.io.IOException e) {
    throw new RuntimeException("No se pudo registrar el directorio vigilado", e);
}
```

Los tres tipos de evento estándar son:

| Evento | Se dispara cuando... |
|--------|------------------------|
| `ENTRY_CREATE` | se crea una entrada (fichero o subdirectorio) dentro del directorio vigilado |
| `ENTRY_MODIFY` | se modifica el contenido de una entrada existente |
| `ENTRY_DELETE` | se elimina una entrada |

## El bucle de espera

`WatchService` funciona por **claves** (`WatchKey`): cada directorio
registrado tiene una clave, y hay que consumir esa clave y **reiniciarla**
(`reset()`) después de procesar sus eventos para seguir recibiendo avisos.

```java
try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
    directorio.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

    while (true) {
        WatchKey clave = watcher.take(); // bloquea hasta que haya eventos

        for (WatchEvent<?> evento : clave.pollEvents()) {
            WatchEvent.Kind<?> tipo = evento.kind();

            if (tipo == OVERFLOW) {
                continue; // se perdieron eventos por saturación; no hay path fiable
            }

            @SuppressWarnings("unchecked")
            WatchEvent<Path> ev = (WatchEvent<Path>) evento;
            Path nombreRelativo = ev.context();
            Path rutaCompleta = directorio.resolve(nombreRelativo);

            System.out.println(tipo.name() + ": " + rutaCompleta);
        }

        boolean sigueValida = clave.reset();
        if (!sigueValida) {
            break; // el directorio vigilado ya no es accesible (p. ej. se borró)
        }
    }
} catch (java.io.IOException e) {
    throw new RuntimeException("Error al vigilar el directorio", e);
} catch (InterruptedException e) {
    Thread.currentThread().interrupt(); // restaurar el estado de interrupción
}
```

:::warning
`clave.reset()` es obligatorio. Si se olvida, la clave deja de recibir
eventos nuevos aunque el bucle siga ejecutándose, y el vigilante deja de
funcionar en silencio, sin lanzar ninguna excepción.
:::

## `take()` frente a `poll()`

`WatchService` ofrece dos formas de esperar eventos:

- **`watcher.take()`**: bloquea indefinidamente hasta que hay una clave con
  eventos. Adecuado para un hilo dedicado en segundo plano.
- **`watcher.poll()`** / **`watcher.poll(timeout, unidad)`**: devuelve
  inmediatamente (`null` si no hay eventos) o espera como máximo el tiempo
  indicado. Útil cuando el hilo también debe atender otras tareas o
  comprobar periódicamente una condición de parada.

```java
WatchKey clave = watcher.poll(5, java.util.concurrent.TimeUnit.SECONDS);
if (clave == null) {
    // no hubo eventos en 5 segundos; comprobar si toca detenerse, etc.
}
```

## Dónde encaja `WatchService` en una aplicación Spring

Un vigilante de directorio suele vivir en su **propio hilo**, arrancado al
iniciar la aplicación y detenido de forma ordenada al pararla. En un
proyecto Spring Boot esto se hace habitualmente con un componente que
implementa `Runnable` y se lanza en un hilo gestionado, o con las
anotaciones de ciclo de vida (`@PostConstruct` / `@PreDestroy`) para abrir y
cerrar el `WatchService` junto con el contexto de la aplicación.

:::note[«¿Por qué no me llega el evento al copiar un fichero grande?»]
Algunos programas escriben primero a un fichero temporal y luego lo renombran,
o escriben por bloques. Eso puede generar varios `ENTRY_MODIFY` seguidos, o
un `ENTRY_CREATE` del temporal seguido de un `ENTRY_DELETE`. Si el proceso
que deposita ficheros no es de confianza, conviene esperar un breve intervalo
tras el primer evento antes de dar el fichero por completo, o comprobar que
su tamaño se ha estabilizado.
:::

<NextPageButton to="/tema-1/e-acceso-secuencial-aleatorio">
Acceso secuencial frente a acceso aleatorio
</NextPageButton>
