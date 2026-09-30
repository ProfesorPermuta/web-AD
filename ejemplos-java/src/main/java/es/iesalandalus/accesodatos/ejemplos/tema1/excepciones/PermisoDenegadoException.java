package es.iesalandalus.accesodatos.ejemplos.tema1.excepciones;

/** Se lanza cuando no hay permisos suficientes para acceder al documento. */
public class PermisoDenegadoException extends AlmacenamientoException {

    public PermisoDenegadoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
