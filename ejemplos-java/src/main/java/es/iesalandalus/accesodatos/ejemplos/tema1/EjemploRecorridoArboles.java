package es.iesalandalus.accesodatos.ejemplos.tema1;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.stream.Stream;

/**
 * Apartado c: recorrido de arboles de directorios, atributos y permisos.
 *
 * <p>Prepara un pequeno arbol de directorios de prueba con documentos y
 * ficheros de log, y muestra las distintas formas de recorrerlo:
 * {@link DirectoryStream} (un solo nivel), {@link Files#walk} (Stream
 * perezoso), {@link PathMatcher} (filtrado por patron glob) y
 * {@code walkFileTree} con un {@code FileVisitor} (control fino,
 * incluido el borrado de un arbol completo).</p>
 */
public class EjemploRecorridoArboles {

    public static void main(String[] args) throws IOException {
        Path raiz = Path.of("ejemplos-datos", "tema1-c");
        prepararArbolDePrueba(raiz);

        directoryStreamUnNivel(raiz);
        filesWalkRecursivo(raiz);
        pathMatcherPorPatron(raiz);
        walkFileTreeConVisitor(raiz);
        atributosBasicos(raiz);

        // Se borra al final para que el ejemplo se pueda relanzar limpio.
        borrarArbolCompleto(raiz);
        System.out.println("\nArbol de prueba borrado tras el ejemplo.");
    }

    private static void prepararArbolDePrueba(Path raiz) throws IOException {
        Path documentos = raiz.resolve("documentos");
        Path logs = raiz.resolve("logs");
        Files.createDirectories(documentos);
        Files.createDirectories(logs);

        Files.writeString(documentos.resolve("informe.txt"), "contenido de prueba", StandardCharsets.UTF_8);
        Files.writeString(documentos.resolve("resumen.txt"), "contenido de prueba", StandardCharsets.UTF_8);
        Files.writeString(logs.resolve("acceso.log"), "linea de log", StandardCharsets.UTF_8);
        Files.writeString(logs.resolve("errores.log"), "linea de log", StandardCharsets.UTF_8);
    }

    private static void directoryStreamUnNivel(Path raiz) throws IOException {
        System.out.println("--- DirectoryStream: solo el primer nivel de " + raiz + " ---");
        try (DirectoryStream<Path> entradas = Files.newDirectoryStream(raiz)) {
            for (Path entrada : entradas) {
                System.out.println(entrada.getFileName());
            }
        }
        System.out.println();
    }

    private static void filesWalkRecursivo(Path raiz) throws IOException {
        System.out.println("--- Files.walk: todos los ficheros .log del arbol ---");
        try (Stream<Path> rutas = Files.walk(raiz)) {
            rutas.filter(Files::isRegularFile)
                    .filter(p -> p.toString().endsWith(".log"))
                    .forEach(System.out::println);
        }
        System.out.println();
    }

    private static void pathMatcherPorPatron(Path raiz) throws IOException {
        System.out.println("--- PathMatcher: patron glob **/*.txt ---");
        PathMatcher soloTexto = FileSystems.getDefault().getPathMatcher("glob:**/*.txt");
        try (Stream<Path> rutas = Files.walk(raiz)) {
            rutas.filter(Files::isRegularFile)
                    .filter(soloTexto::matches)
                    .forEach(System.out::println);
        }
        System.out.println();
    }

    private static void walkFileTreeConVisitor(Path raiz) throws IOException {
        System.out.println("--- walkFileTree: entrada/salida de cada directorio ---");
        Files.walkFileTree(raiz, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
                System.out.println("Entrando en: " + dir);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path fichero, BasicFileAttributes attrs) {
                System.out.println("  Fichero: " + fichero.getFileName() + " (" + attrs.size() + " bytes)");
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
                System.out.println("Saliendo de: " + dir);
                return FileVisitResult.CONTINUE;
            }
        });
        System.out.println();
    }

    private static void atributosBasicos(Path raiz) throws IOException {
        Path unFichero = raiz.resolve("documentos").resolve("informe.txt");
        System.out.println("--- Atributos basicos de " + unFichero.getFileName() + " ---");
        BasicFileAttributes attrs = Files.readAttributes(unFichero, BasicFileAttributes.class);
        System.out.println("Tamaño: " + attrs.size() + " bytes");
        System.out.println("Creado: " + attrs.creationTime());
        System.out.println("Modificado: " + attrs.lastModifiedTime());
        System.out.println("¿Es directorio?: " + attrs.isDirectory());
        System.out.println();
    }

    /** Borra un arbol completo: primero los ficheros, luego los directorios vacios. */
    private static void borrarArbolCompleto(Path raiz) throws IOException {
        Files.walkFileTree(raiz, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path fichero, BasicFileAttributes attrs) throws IOException {
                Files.delete(fichero);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
