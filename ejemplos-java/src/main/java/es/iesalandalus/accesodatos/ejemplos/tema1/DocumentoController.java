package es.iesalandalus.accesodatos.ejemplos.tema1;

import es.iesalandalus.accesodatos.ejemplos.tema1.excepciones.RecursoNoEncontradoException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST de ejemplo para el apartado f.
 *
 * <p>Todo el acceso al sistema de ficheros vive en {@link AlmacenDocumentos};
 * este controlador solo delega y traduce la excepcion de dominio al codigo
 * HTTP correspondiente.</p>
 */
@RestController
@RequestMapping("/documentos")
public class DocumentoController {

    private final AlmacenDocumentos almacenDocumentos;

    public DocumentoController(AlmacenDocumentos almacenDocumentos) {
        this.almacenDocumentos = almacenDocumentos;
    }

    @PostMapping
    public ResponseEntity<Void> guardar(@RequestBody Documento documento) {
        almacenDocumentos.guardar(documento);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{nombre}")
    public ResponseEntity<Documento> leer(@PathVariable String nombre) {
        try {
            return ResponseEntity.ok(almacenDocumentos.leer(nombre));
        } catch (RecursoNoEncontradoException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
