package es.iesalandalus.accesodatos.ejemplos.tema1;

import es.iesalandalus.accesodatos.ejemplos.tema1.excepciones.AlmacenamientoException;
import es.iesalandalus.accesodatos.ejemplos.tema1.excepciones.PermisoDenegadoException;
import es.iesalandalus.accesodatos.ejemplos.tema1.excepciones.RecursoNoEncontradoException;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

/**
 * Apartado f: repositorio de ejemplo que encapsula el acceso al sistema de
 * ficheros y traduce las excepciones de {@code java.nio.file} a la
 * jerarquia de excepciones propia del dominio de ejemplo.
 *
 * <p>La ruta raiz del almacen se externaliza mediante
 * {@link AlmacenamientoProperties} en lugar de escribirse en el codigo.</p>
 */
@Repository
@EnableConfigurationProperties(AlmacenamientoProperties.class)
public class AlmacenDocumentos {

    private final Path directorioRaiz;

    public AlmacenDocumentos(AlmacenamientoProperties propiedades) {
        this.directorioRaiz = Path.of(propiedades.getDirectorioRaiz());
    }

    public void guardar(Documento documento) {
        try {
            Files.createDirectories(directorioRaiz);
            Files.writeString(rutaDe(documento.nombre()), documento.contenido(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new AlmacenamientoException("No se pudo guardar el documento: " + documento.nombre(), e);
        }
    }

    public Documento leer(String nombre) {
        try {
            String contenido = Files.readString(rutaDe(nombre), StandardCharsets.UTF_8);
            return new Documento(nombre, contenido);
        } catch (NoSuchFileException e) {
            throw new RecursoNoEncontradoException("No existe el documento: " + nombre, e);
        } catch (AccessDeniedException e) {
            throw new PermisoDenegadoException("Sin permisos para leer: " + nombre, e);
        } catch (IOException e) {
            throw new AlmacenamientoException("Error de E/S al leer: " + nombre, e);
        }
    }

    private Path rutaDe(String nombre) {
        return directorioRaiz.resolve(nombre);
    }
}
