package es.iesalandalus.accesodatos.ejemplos.tema1;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Random;

/**
 * Apartado g: streaming de ficheros binarios grandes.
 *
 * <p>Genera un fichero binario de prueba de varios megabytes (para que el
 * ejemplo sea visible sin depender de un fichero real del disco del
 * profesor) y muestra tres formas de moverlo sin cargarlo entero en
 * memoria: copia por bloques con {@link InputStream}/{@link OutputStream},
 * copia por bloques calculando un hash SHA-256 mientras se transfiere, y
 * copia delegada en el sistema operativo con {@link FileChannel#transferTo}.</p>
 */
public class EjemploStreamingBinarios {

    private static final int TAMANO_BUFFER = 8192; // 8 KB
    private static final long TAMANO_FICHERO_PRUEBA = 5L * 1024 * 1024; // 5 MB

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        Path directorio = Path.of("ejemplos-datos", "tema1-g");
        Files.createDirectories(directorio);

        Path origen = directorio.resolve("fichero-grande.bin");
        generarFicheroBinarioDePrueba(origen, TAMANO_FICHERO_PRUEBA);
        System.out.println("Fichero de prueba generado: " + origen.toAbsolutePath()
                + " (" + Files.size(origen) + " bytes)");

        Path copiaSimple = directorio.resolve("copia-simple.bin");
        copiarPorBloques(origen, copiaSimple);
        System.out.println("\nCopiado por bloques (buffer de " + TAMANO_BUFFER + " bytes) a: " + copiaSimple);

        Path copiaConHash = directorio.resolve("copia-con-hash.bin");
        String hash = copiarYCalcularHash(origen, copiaConHash);
        System.out.println("\nCopiado calculando SHA-256 al mismo tiempo. Hash: " + hash);

        Path copiaTransferTo = directorio.resolve("copia-transferto.bin");
        copiarConFileChannel(origen, copiaTransferTo);
        System.out.println("\nCopiado con FileChannel.transferTo (delegado en el SO) a: " + copiaTransferTo);
    }

    private static void generarFicheroBinarioDePrueba(Path destino, long tamano) throws IOException {
        Random random = new Random(42); // semilla fija: mismo contenido en cada ejecucion
        try (OutputStream salida = new BufferedOutputStream(Files.newOutputStream(destino))) {
            byte[] buffer = new byte[TAMANO_BUFFER];
            long escritos = 0;
            while (escritos < tamano) {
                random.nextBytes(buffer);
                int aEscribir = (int) Math.min(buffer.length, tamano - escritos);
                salida.write(buffer, 0, aEscribir);
                escritos += aEscribir;
            }
        }
    }

    private static void copiarPorBloques(Path origen, Path destino) throws IOException {
        try (InputStream entrada = Files.newInputStream(origen);
             OutputStream salida = Files.newOutputStream(destino)) {

            byte[] buffer = new byte[TAMANO_BUFFER];
            int leidos;
            while ((leidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, leidos);
            }
        }
    }

    private static String copiarYCalcularHash(Path origen, Path destino) throws IOException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");

        try (InputStream entrada = Files.newInputStream(origen);
             OutputStream salida = Files.newOutputStream(destino)) {

            byte[] buffer = new byte[TAMANO_BUFFER];
            int leidos;
            while ((leidos = entrada.read(buffer)) != -1) {
                salida.write(buffer, 0, leidos);
                digest.update(buffer, 0, leidos);
            }
        }

        return HexFormat.of().formatHex(digest.digest());
    }

    private static void copiarConFileChannel(Path origen, Path destino) throws IOException {
        try (FileChannel entrada = FileChannel.open(origen, StandardOpenOption.READ);
             FileChannel salida = FileChannel.open(destino, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {

            long tamano = entrada.size();
            long transferidos = 0;
            while (transferidos < tamano) {
                transferidos += entrada.transferTo(transferidos, tamano - transferidos, salida);
            }
        }
    }
}
