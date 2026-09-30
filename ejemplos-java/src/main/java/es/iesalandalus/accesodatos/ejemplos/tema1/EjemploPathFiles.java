package es.iesalandalus.accesodatos.ejemplos.tema1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * Apartado b: Path, Paths y Files — operaciones basicas.
 *
 * <p>Ejemplo ejecutable independiente (no depende de Spring). Crea una
 * pequena estructura de directorios de prueba bajo {@code ejemplos-datos/},
 * la manipula con la API {@code java.nio.file} y la deja lista para volver
 * a ejecutarse sin fallar aunque ya exista contenido de una ejecucion
 * anterior.</p>
 *
 * <p>Ejecutar como aplicacion Java normal (no necesita Spring Boot
 * arrancado): boton derecho &gt; Run, o {@code mvn -q exec:java
 * -Dexec.mainClass=...EjemploPathFiles} si se anade el plugin exec.</p>
 */
public class EjemploPathFiles {

    public static void main(String[] args) throws IOException {
        Path base = Path.of("ejemplos-datos", "tema1-b");

        // --- Path: componer y normalizar rutas ---
        Path ruta = base.resolve("entrada").resolve("registro.csv");
        System.out.println("Ruta compuesta: " + ruta);
        System.out.println("Nombre de fichero: " + ruta.getFileName());
        System.out.println("Directorio padre: " + ruta.getParent());
        System.out.println("¿Es absoluta?: " + ruta.isAbsolute());
        System.out.println("Ruta absoluta: " + ruta.toAbsolutePath().normalize());
        System.out.println();

        // --- Files: crear directorios ---
        Path directorioEntrada = base.resolve("entrada");
        Path directorioCopias = base.resolve("copias");
        Files.createDirectories(directorioEntrada);
        Files.createDirectories(directorioCopias);
        System.out.println("Directorios preparados en: " + base.toAbsolutePath());

        // --- Crear y escribir un fichero ---
        Path documento = directorioEntrada.resolve("notas.txt");
        Files.deleteIfExists(documento); // para que el ejemplo sea repetible
        Files.createFile(documento);
        Files.writeString(documento, "primera linea" + System.lineSeparator(), StandardCharsets.UTF_8);
        Files.writeString(documento, "segunda linea" + System.lineSeparator(), StandardCharsets.UTF_8,
                StandardOpenOption.APPEND);

        // --- Leer contenido ---
        String contenidoCompleto = Files.readString(documento, StandardCharsets.UTF_8);
        System.out.println("Contenido completo:");
        System.out.println(contenidoCompleto);

        List<String> lineas = Files.readAllLines(documento, StandardCharsets.UTF_8);
        System.out.println("Numero de lineas leidas: " + lineas.size());

        // --- Copiar y mover ---
        Path copia = directorioCopias.resolve("notas-copia.txt");
        Files.copy(documento, copia, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Copiado a: " + copia);

        Path renombrado = directorioCopias.resolve("notas-final.txt");
        Files.move(copia, renombrado, StandardCopyOption.REPLACE_EXISTING);
        System.out.println("Renombrado a: " + renombrado);

        // --- Comprobaciones de existencia ---
        System.out.println("¿Existe el original?: " + Files.exists(documento));
        System.out.println("¿Existe la copia intermedia?: " + Files.exists(copia));
        System.out.println("¿Existe el renombrado?: " + Files.exists(renombrado));
    }
}
