package es.iesalandalus.accesodatos.ejemplos.tema1;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

/**
 * Apartado d: WatchService — vigilancia de un directorio.
 *
 * <p>Ejemplo pensado para lanzarlo en clase y, mientras el proceso queda
 * bloqueado esperando eventos, crear/modificar/borrar ficheros a mano desde
 * el explorador de ficheros o desde otra terminal dentro de
 * {@code ejemplos-datos/tema1-d/buzon/} para ver los eventos en la
 * consola en tiempo real.</p>
 *
 * <p>Se detiene con Ctrl+C, o automaticamente tras
 * {@link #DURACION_DEMO_SEGUNDOS} segundos si nadie interactua, para no
 * dejar el ejemplo colgado indefinidamente durante una demo.</p>
 */
public class EjemploWatchService {

    private static final int DURACION_DEMO_SEGUNDOS = 120;

    public static void main(String[] args) throws IOException, InterruptedException {
        Path directorio = Path.of("ejemplos-datos", "tema1-d", "buzon");
        Files.createDirectories(directorio);

        System.out.println("Vigilando: " + directorio.toAbsolutePath());
        System.out.println("Crea, modifica o borra ficheros ahi dentro para ver los eventos.");
        System.out.println("La demo se detiene sola a los " + DURACION_DEMO_SEGUNDOS + " segundos.\n");

        long limiteMillis = System.currentTimeMillis() + DURACION_DEMO_SEGUNDOS * 1000L;

        try (WatchService watcher = FileSystems.getDefault().newWatchService()) {
            directorio.register(watcher, ENTRY_CREATE, ENTRY_MODIFY, ENTRY_DELETE);

            while (System.currentTimeMillis() < limiteMillis) {
                // poll con timeout: permite comprobar periodicamente si toca parar,
                // en lugar de bloquear para siempre con take().
                WatchKey clave = watcher.poll(2, java.util.concurrent.TimeUnit.SECONDS);
                if (clave == null) {
                    continue; // no hubo eventos en esta ventana; seguimos esperando
                }

                for (WatchEvent<?> evento : clave.pollEvents()) {
                    WatchEvent.Kind<?> tipo = evento.kind();

                    if (tipo == OVERFLOW) {
                        System.out.println("(se perdieron eventos por saturación)");
                        continue;
                    }

                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) evento;
                    Path nombreRelativo = ev.context();
                    Path rutaCompleta = directorio.resolve(nombreRelativo);

                    System.out.println(tipo.name() + ": " + rutaCompleta);
                }

                boolean sigueValida = clave.reset();
                if (!sigueValida) {
                    System.out.println("El directorio vigilado ya no es accesible.");
                    break;
                }
            }
        }

        System.out.println("\nFin de la demo de WatchService.");
    }
}
