package es.iesalandalus.accesodatos.ejemplos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de arranque de la aplicacion de ejemplos.
 *
 * <p>Este proyecto no es una aplicacion "de negocio": es una coleccion de
 * ejemplos ejecutables organizados por tema y apartado de los apuntes.
 * Arrancarlo levanta un servidor Spring Boot mínimo, pero el interes
 * didactico esta en cada clase concreta de {@code tema1}, que se puede
 * ejecutar sola desde el IDE (metodo {@code main} o botón "Run") sin
 * necesidad de tocar esta clase.</p>
 */
@SpringBootApplication
public class EjemplosJavaApplication {

    public static void main(String[] args) {
        SpringApplication.run(EjemplosJavaApplication.class, args);
    }
}
