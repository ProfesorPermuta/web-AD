package es.iesalandalus.accesodatos.ejemplos.tema1.excepciones;

/** Se lanza cuando el documento solicitado no existe en el almacen. */
public class RecursoNoEncontradoException extends AlmacenamientoException {

    public RecursoNoEncontradoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
