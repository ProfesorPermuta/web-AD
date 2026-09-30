package es.iesalandalus.accesodatos.ejemplos.tema1;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * Apartado e: acceso secuencial frente a acceso aleatorio.
 *
 * <p>Crea un fichero de "registros" de tamaño fijo (64 bytes cada uno,
 * rellenos con espacios) y muestra como acceder directamente al registro
 * n-esimo sin leer los anteriores, primero con {@link RandomAccessFile}
 * (API clasica) y despues con {@link SeekableByteChannel} (API NIO.2).</p>
 */
public class EjemploAccesoSecuencialAleatorio {

    private static final int TAMANO_REGISTRO = 64;

    public static void main(String[] args) throws IOException {
        Path fichero = Path.of("ejemplos-datos", "tema1-e", "registros.dat");
        Files.createDirectories(fichero.getParent());

        crearFicheroDeRegistros(fichero, "primer registro", "segundo registro",
                "tercer registro", "cuarto registro");

        System.out.println("--- RandomAccessFile: leer el registro 2 sin leer los anteriores ---");
        leerConRandomAccessFile(fichero, 2);

        System.out.println("\n--- RandomAccessFile: modificar el registro 1 sin reescribir el fichero ---");
        escribirConRandomAccessFile(fichero, 1, "registro actualizado");
        leerConRandomAccessFile(fichero, 1);

        System.out.println("\n--- SeekableByteChannel: leer el registro 3 (API NIO.2) ---");
        leerConSeekableByteChannel(fichero, 3);
    }

    private static void crearFicheroDeRegistros(Path fichero, String... contenidos) throws IOException {
        Files.deleteIfExists(fichero); // para que el ejemplo sea repetible
        try (RandomAccessFile raf = new RandomAccessFile(fichero.toFile(), "rw")) {
            for (String contenido : contenidos) {
                raf.write(registroDeTamanoFijo(contenido));
            }
        }
        System.out.println("Fichero de " + contenidos.length + " registros de "
                + TAMANO_REGISTRO + " bytes creado en " + fichero.toAbsolutePath());
    }

    /** Rellena o trunca el contenido para que ocupe siempre TAMANO_REGISTRO bytes. */
    private static byte[] registroDeTamanoFijo(String contenido) {
        byte[] resultado = new byte[TAMANO_REGISTRO];
        byte[] datos = contenido.getBytes(StandardCharsets.UTF_8);
        System.arraycopy(datos, 0, resultado, 0, Math.min(datos.length, TAMANO_REGISTRO));
        for (int i = datos.length; i < TAMANO_REGISTRO; i++) {
            resultado[i] = ' ';
        }
        return resultado;
    }

    private static void leerConRandomAccessFile(Path fichero, int indice) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fichero.toFile(), "r")) {
            raf.seek((long) indice * TAMANO_REGISTRO);
            byte[] buffer = new byte[TAMANO_REGISTRO];
            raf.readFully(buffer);
            System.out.println("Registro " + indice + ": \"" + new String(buffer, StandardCharsets.UTF_8).trim() + "\"");
        }
    }

    private static void escribirConRandomAccessFile(Path fichero, int indice, String nuevoContenido) throws IOException {
        try (RandomAccessFile raf = new RandomAccessFile(fichero.toFile(), "rw")) {
            raf.seek((long) indice * TAMANO_REGISTRO);
            raf.write(registroDeTamanoFijo(nuevoContenido));
        }
    }

    private static void leerConSeekableByteChannel(Path fichero, int indice) throws IOException {
        try (SeekableByteChannel canal = Files.newByteChannel(fichero, StandardOpenOption.READ)) {
            canal.position((long) indice * TAMANO_REGISTRO);

            ByteBuffer buffer = ByteBuffer.allocate(TAMANO_REGISTRO);
            canal.read(buffer);
            buffer.flip();

            byte[] datos = new byte[buffer.remaining()];
            buffer.get(datos);
            System.out.println("Registro " + indice + ": \"" + new String(datos, StandardCharsets.UTF_8).trim() + "\"");
        }
    }
}
