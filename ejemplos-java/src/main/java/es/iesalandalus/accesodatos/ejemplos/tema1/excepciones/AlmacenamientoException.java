package es.iesalandalus.accesodatos.ejemplos.tema1.excepciones;

/**
 * Excepcion base del dominio de ejemplo. No comprobada: quien no necesite
 * tratarla caso por caso no esta obligado a declararla ni a capturarla.
 *
 * <p>Ver apartado f de la UD1: "Excepciones propias y gestion de rutas por
 * configuracion".</p>
 */
public class AlmacenamientoException extends RuntimeException {

    public AlmacenamientoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
