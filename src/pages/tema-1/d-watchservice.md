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

## `OVERFLOW`: cuando se pierden eventos

Si llegan demasiados eventos de golpe, la clave no puede guardarlos todos y
recibes un evento `OVERFLOW`. Significa que **se han perdido eventos** y no
trae ruta fiable, por eso el bucle de arriba lo ignora con `continue`.

## Dónde encaja `WatchService` en una aplicación Spring

El bucle con `take()` **bloquea el hilo** que lo ejecuta. Si lo lanzaras desde
el hilo principal de la aplicación, el resto del programa no arrancaría. Por
eso un vigilante de directorio vive siempre en su **propio hilo**, que se
arranca al iniciar la aplicación y se detiene de forma ordenada al pararla.

En Spring esto se resuelve con las anotaciones de ciclo de vida:
`@PostConstruct` abre el `WatchService` y lanza el hilo, y `@PreDestroy` lo
cierra.

```java
@Component
public class Vigilante {

    private static final Logger log = LoggerFactory.getLogger(Vigilante.class);

    private final Path directorio;
    private final ProcesadorFicheros procesador;
    private WatchService watcher;
    private Thread hiloVigilante;

    public Vigilante(@Value("${app.directorio-entrada}") String ruta,
                     ProcesadorFicheros procesador) {
        this.directorio = Path.of(ruta);
        this.procesador = procesador;
    }

    @PostConstruct
    void arrancar() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        directorio.register(watcher, ENTRY_CREATE);

        hiloVigilante = new Thread(this::vigilar, "vigilante-entrada");
        hiloVigilante.start();
    }

    private void vigilar() {
        try {
            while (true) {
                WatchKey clave = watcher.take();

                for (WatchEvent<?> evento : clave.pollEvents()) {
                    if (evento.kind() == OVERFLOW) {
                        continue;
                    }
                    Path nombre = (Path) evento.context();
                    Path fichero = directorio.resolve(nombre);

                    log.info("[{}] llega {}", Thread.currentThread().getName(), fichero);
                    procesador.procesar(fichero); // ¡se ejecuta en el hilo del vigilante!
                }

                if (!clave.reset()) {
                    break;
                }
            }
        } catch (ClosedWatchServiceException e) {
            // parada ordenada: @PreDestroy cerró el watcher mientras esperábamos
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    @PreDestroy
    void parar() throws IOException {
        watcher.close(); // take() lanza ClosedWatchServiceException y el hilo termina
    }
}
```

:::tip[Cómo se ve el hilo del vigilante]
El nombre `"vigilante-entrada"` no es un adorno: dar nombre a los hilos
permite distinguirlos en los *logs* y en un volcado de hilos. Al copiar un
fichero a `entrada/` verás:

```
[vigilante-entrada] llega entrada/ciudades.csv
```
:::

`watcher.close()` cumple una segunda función: es la forma de **despertar** un
hilo que está bloqueado en `take()`. Al cerrar el servicio, `take()` lanza
`ClosedWatchServiceException` y el bucle termina sin necesidad de interrumpir
el hilo a la fuerza.

## El problema: procesar en el hilo del vigilante

En el ejemplo anterior, `procesador.procesar(fichero)` se ejecuta **dentro del
bucle**. Mientras dura el procesamiento, el vigilante no vuelve a `take()` y,
por tanto, no atiende nada más:

- Los eventos nuevos se acumulan en la clave y, si son muchos, llega un
  `OVERFLOW` y se pierden.
- Si un fichero es grande, el directorio queda sin vigilar todo ese tiempo.

El vigilante debería limitarse a **detectar y delegar**, y volver
inmediatamente a esperar. La solución es procesar el fichero en **otro hilo**.

:::note[¿Un endpoint o una llamada directa?]
Otra opción de diseño sería que el vigilante avisara a un controlador REST
mediante una petición HTTP. Si vigilante y servicio viven en la misma
aplicación Spring, eso es un rodeo (la aplicación se llamaría a sí misma por
red): basta con llamar al servicio directamente, pero en otro hilo. Un
endpoint solo compensa si además quieres poder lanzar el procesamiento a mano
o desde otro sistema; en ese caso el controlador debe responder rápido
(`202 Accepted`) y trabajar en segundo plano.
:::

## Primera idea: un `new Thread` por fichero

Lo primero que se nos ocurre es crear un hilo por cada fichero:

```java
Path fichero = directorio.resolve(nombre);

new Thread(() -> procesador.procesar(fichero)).start();
```

Funciona, pero tiene tres problemas:

1. **Crear un hilo es caro**, y aquí se crea uno nuevo por cada fichero.
2. **No hay límite**: si llegan 1.000 ficheros de golpe, hay 1.000 hilos a la
   vez y la aplicación se queda sin memoria o sin CPU.
3. **Los errores se pierden**: si `procesar` lanza una excepción, muere dentro
   de ese hilo y el vigilante ni se entera.

## La solución: `ExecutorService`

Un `ExecutorService` es un **pool de hilos**: se crea una sola vez, al
arrancar, con un número fijo de hilos que se **reutilizan** de una tarea a la
siguiente.

```java
ExecutorService pool = Executors.newFixedThreadPool(4);
```

Ahora, en lugar de procesar el fichero o crear un hilo, el vigilante **entrega
la tarea** al pool con `submit()` y vuelve enseguida a `take()`:

```java
Path fichero = directorio.resolve(nombre);

pool.submit(() -> procesador.procesar(fichero));
```

| Problema con `new Thread` | Cómo lo resuelve el pool |
|---------------------------|--------------------------|
| Crear hilos es caro | Se crean una vez y se reutilizan |
| Sin límite de hilos | Como máximo trabajan 4 a la vez |
| Más tareas que hilos | Las sobrantes esperan su turno en la cola interna del pool |
| Los errores se pierden | `submit()` devuelve un `Future` con el resultado o el error |
| Parada desordenada | `shutdown()` cierra el pool de forma ordenada |

:::warning
Con `submit()`, una excepción lanzada por la tarea **no se propaga ni se
imprime**: queda guardada dentro del `Future`, y si nadie lo consulta se pierde
igual que en un `new Thread`. Captura y registra los errores dentro de la
propia tarea, o consulta el `Future`.
:::

## Configuración final: vigilante + pool

Juntando las dos piezas, la aplicación tiene **dos tipos de hilo** con
responsabilidades distintas:

| Hilo | Cuántos | Qué hace |
|------|---------|----------|
| `vigilante-entrada` | 1 | Espera en `take()`, lee los eventos y delega |
| `pool-1-thread-N` | 4 (fijo) | Ejecutan `procesador.procesar(fichero)` |

```java
@Component
public class Vigilante {

    private static final Logger log = LoggerFactory.getLogger(Vigilante.class);

    private final Path directorio;
    private final ProcesadorFicheros procesador;
    private WatchService watcher;
    private Thread hiloVigilante;
    private ExecutorService pool;

    public Vigilante(@Value("${app.directorio-entrada}") String ruta,
                     ProcesadorFicheros procesador) {
        this.directorio = Path.of(ruta);
        this.procesador = procesador;
    }

    @PostConstruct
    void arrancar() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        directorio.register(watcher, ENTRY_CREATE);

        pool = Executors.newFixedThreadPool(4);

        hiloVigilante = new Thread(this::vigilar, "vigilante-entrada");
        hiloVigilante.start();
    }

    private void vigilar() {
        try {
            while (true) {
                WatchKey clave = watcher.take();

                for (WatchEvent<?> evento : clave.pollEvents()) {
                    if (evento.kind() == OVERFLOW) {
                        continue;
                    }
                    Path fichero = directorio.resolve((Path) evento.context());

                    log.info("[{}] detectado {}", Thread.currentThread().getName(), fichero);
                    pool.submit(() -> procesar(fichero)); // delego y vuelvo a take()
                }

                if (!clave.reset()) {
                    break;
                }
            }
        } catch (ClosedWatchServiceException e) {
            // parada ordenada
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void procesar(Path fichero) {
        try {
            log.info("[{}] procesando {}", Thread.currentThread().getName(), fichero);
            procesador.procesar(fichero);
        } catch (RuntimeException e) {
            log.error("Error al procesar {}", fichero, e); // si no, el error se pierde
        }
    }

    @PreDestroy
    void parar() throws IOException {
        watcher.close();  // despierta al vigilante, que termina su bucle
        pool.shutdown();  // no admite tareas nuevas; termina las que ya tiene
    }
}
```

Si copias tres ficheros seguidos a `entrada/`, el *log* muestra el reparto de
trabajo: **un único hilo vigilante** que detecta, y **hilos distintos del
pool** que procesan:

```
[vigilante-entrada] detectado entrada/a.csv
[vigilante-entrada] detectado entrada/b.csv
[pool-1-thread-1]   procesando entrada/a.csv
[vigilante-entrada] detectado entrada/c.csv
[pool-1-thread-2]   procesando entrada/b.csv
[pool-1-thread-3]   procesando entrada/c.csv
```

El vigilante nunca se queda procesando: detecta, entrega y vuelve a dormir en
`take()`.

:::warning[El servicio debe admitir ejecución en paralelo]
Con un pool de 4 hilos, `procesador.procesar(...)` puede ejecutarse a la vez
para cuatro ficheros distintos. El servicio no debe guardar estado mutable
compartido entre llamadas (campos que se modifican), o debe sincronizarlo.
:::

:::tip
Para que el número de hilos no esté fijo en el código, puedes externalizarlo
como cualquier otra propiedad (`@Value("${app.hilos-procesado:4}")`). Ver
[configuración y excepciones](/tema-1/f-excepciones-config).
:::

:::note[«¿Por qué no me llega el evento al copiar un fichero grande?»]
Algunos programas escriben primero a un fichero temporal y luego lo renombran,
o escriben por bloques. Eso puede generar varios `ENTRY_MODIFY` seguidos, o
un `ENTRY_CREATE` del temporal seguido de un `ENTRY_DELETE`. Si el proceso
que deposita ficheros no es de confianza, conviene esperar un breve intervalo
tras el primer evento antes de dar el fichero por completo, o comprobar que
su tamaño se ha estabilizado.
:::

## Resumen

1. Registra el directorio con los eventos que te interesan.
2. Espera con `take()` (o `poll()` si el hilo tiene otras tareas).
3. Procesa los eventos con `pollEvents()`, ignora `OVERFLOW` y llama siempre
   a `reset()`.
4. Ejecuta el vigilante en **su propio hilo**, arrancado con `@PostConstruct`
   y cerrado con `@PreDestroy`.
5. **No proceses en el bucle**: delega cada fichero en un `ExecutorService` y
   vuelve a `take()`.
6. Cierra ordenadamente: `watcher.close()` y `pool.shutdown()`.

<NextPageButton to="/tema-1/e-acceso-secuencial-aleatorio">
Acceso secuencial frente a acceso aleatorio
</NextPageButton>
