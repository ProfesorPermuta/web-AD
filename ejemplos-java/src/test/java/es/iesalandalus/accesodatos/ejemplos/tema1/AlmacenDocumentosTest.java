package es.iesalandalus.accesodatos.ejemplos.tema1;

import es.iesalandalus.accesodatos.ejemplos.tema1.excepciones.RecursoNoEncontradoException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba de ejemplo sobre un directorio temporal, tal y como se pide en el
 * criterio de aceptación de las actividades de la UD1: nunca sobre rutas
 * reales del disco, y limpiando al terminar.
 */
class AlmacenDocumentosTest {

    private Path directorioTemporal;
    private AlmacenDocumentos almacen;

    @BeforeEach
    void prepararAlmacenTemporal() throws IOException {
        directorioTemporal = Files.createTempDirectory("almacen-documentos-test");

        AlmacenamientoProperties propiedades = new AlmacenamientoProperties();
        propiedades.setDirectorioRaiz(directorioTemporal.toString());
        almacen = new AlmacenDocumentos(propiedades);
    }

    @AfterEach
    void limpiarDirectorioTemporal() throws IOException {
        try (var rutas = Files.walk(directorioTemporal)) {
            rutas.sorted((a, b) -> b.compareTo(a)) // ficheros antes que sus directorios
                    .forEach(ruta -> ruta.toFile().delete());
        }
    }

    @Test
    void guardaYLeeUnDocumento() {
        Documento documento = new Documento("notas.txt", "contenido de prueba");

        almacen.guardar(documento);
        Documento leido = almacen.leer("notas.txt");

        assertThat(leido.contenido()).isEqualTo("contenido de prueba");
    }

    @Test
    void lanzaRecursoNoEncontradoSiElDocumentoNoExiste() {
        assertThatThrownBy(() -> almacen.leer("no-existe.txt"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
