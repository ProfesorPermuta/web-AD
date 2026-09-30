package es.iesalandalus.accesodatos.ejemplos.tema1;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Apartado f: gestion de rutas por configuracion con {@code @ConfigurationProperties}.
 *
 * <p>Agrupa las rutas relacionadas con el almacen de documentos de ejemplo
 * en un unico objeto inyectable, en lugar de repetir {@code @Value} en
 * cada clase que necesite una ruta. Los valores por defecto viven en
 * {@code application.yml}.</p>
 */
@ConfigurationProperties(prefix = "app.almacen-documentos")
public class AlmacenamientoProperties {

    private String directorioRaiz;

    public String getDirectorioRaiz() {
        return directorioRaiz;
    }

    public void setDirectorioRaiz(String directorioRaiz) {
        this.directorioRaiz = directorioRaiz;
    }
}
