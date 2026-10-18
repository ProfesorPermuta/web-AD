---
name: presentacion-clase
description: Genera una presentación de diapositivas HTML autocontenida (navegable con flechas/espacio/swipe) para dar una clase del curso, basada en los apuntes de un tema. Usar cuando el usuario pida crear, generar o actualizar una presentación, unas diapositivas o unos "slides" para una clase. NO publica un Artifact: el resultado se guarda como fichero en el proyecto.
---

# Presentación de clase

Genera diapositivas HTML de una sola página (sin dependencias de build, sin
frameworks) para proyectar en clase. Se navega con flechas de teclado,
barra espaciadora, los botones on-screen o swipe táctil. El resultado es un
fichero `.html` autocontenido que se guarda en el proyecto — **nunca se
publica como Artifact** aunque la skill `artifact-design`/`design` esté
disponible; esta skill sustituye a esas para este tipo de encargo.

## Cuándo usar esta skill

- "Crea una presentación sobre el tema X"
- "Genera las diapositivas para dar la clase de..."
- "Actualiza la presentación del tema Y añadiendo..."
- Cualquier petición de material de clase en formato de slides navegables.

Si en vez de slides piden un documento largo tipo apuntes (para leer con
scroll, no para proyectar), esta skill no aplica: usa un artifact HTML normal
con la skill `artifact-design`.

## Proceso

1. **Reunir el contenido fuente.** Busca los apuntes del tema en el
   proyecto (`src/pages/tema-N/**`, `docs/**`, o donde el usuario indique) y
   léelos por completo antes de diseñar nada. La presentación debe ser fiel
   al contenido real de los apuntes: mismos conceptos, mismos ejemplos de
   código, misma terminología. No inventes contenido que no esté en la
   fuente; si faltan apuntes de una parte, pregunta o avisa.

2. **Resolver la paleta de color y el tono**, si no están ya fijados por
   una presentación anterior del curso (mira si existe
   `static/presentaciones/*.html` con una paleta ya establecida y reutilízala
   para mantener coherencia visual entre temas). Si es la primera
   presentación del curso o el usuario pide un color distinto, pregunta o
   deriva 4-6 tokens hex con `oklch`/análogos como se explica en
   "Paleta y tokens" más abajo.

3. **Planificar las diapositivas.** Antes de escribir HTML, esboza en una
   lista breve el índice de diapositivas: portada, mapa del tema, una o
   varias diapositivas por sección de los apuntes, y cierre. Como referencia
   de granularidad: los apuntes de un tema con 6-7 secciones markdown suelen
   producir 30-40 diapositivas (cada tabla, cada bloque de código relevante,
   cada concepto con su propia diapositiva en vez de amontonar texto).

4. **Construir el HTML** copiando `assets/template.html` como punto de
   partida (contiene el motor de navegación JS, el sistema de temas
   claro/oscuro y todas las clases CSS reutilizables) y rellenando los
   tokens de color y las diapositivas. Usa el catálogo de bloques de
   "Componentes disponibles" y añade diagramas SVG (ver "Diagramas") para
   apoyar visualmente cada concepto que lo permita — evita diapositivas que
   sean solo párrafos de texto cuando el concepto tiene una estructura
   visual natural (flujo, comparación, jerarquía, línea temporal, arquitectura).

5. **Guardar el resultado directamente como fichero del proyecto**, sin
   publicar ningún Artifact:
   - Ruta servida por el sitio (Docusaurus): `static/presentaciones/<slug-tema>.html`
   - Si el proyecto no es Docusaurus o no existe `static/`, pregunta al
     usuario dónde prefiere guardarla, o usa `presentaciones/<slug-tema>.html`
     en la raíz del proyecto.
   - Si ya existe una carpeta `presentaciones/` de trabajo (copia editable)
     además de la servida en `static/`, sincroniza ambas.
   - Si el sitio tiene una página índice de presentaciones (busca
     `src/pages/presentaciones.*`), añade la nueva entrada ahí siguiendo el
     mismo patrón que las existentes.

6. **No abrir un navegador en bucle para verificar.** Una única
   comprobación estática basta: balanceo de etiquetas (`<section>`,
   `<svg>`, `<div>`) y que el título/currículo mencionado en portada
   coincida con el tema. No repitas capturas de pantalla ni scripts de
   verificación — el usuario revisa el resultado en su navegador.

## Paleta y tokens

El motor usa variables CSS con tres estados de tema (oscuro por defecto,
claro explícito, y auto vía `prefers-color-scheme`) ya resueltos en la
plantilla. Sustituye estos marcadores de `assets/template.html`:

| Marcador | Rol | Ejemplo (esmeralda, oscuro) | Ejemplo (esmeralda, claro) |
|---|---|---|---|
| `__BG_DARK__` / `__BG_LIGHT__` | Fondo de página | `#0a1410` | `#f4f8f5` |
| `__SURFACE_DARK__` / `_LIGHT__` | Fondo de tarjetas/terminal | `#0f1d17` | `#ffffff` |
| `__SURFACE2_DARK__` / `_LIGHT__` | Fondo secundario (barras, código) | `#142821` | `#eaf3ec` |
| `__BORDER_DARK__` / `_LIGHT__` | Bordes | `#1e3b2f` | `#d3e5da` |
| `__TEXT_DARK__` / `_LIGHT__` | Texto principal | `#eef6f1` | `#0e1f17` |
| `__TEXTDIM_DARK__` / `_LIGHT__` | Texto secundario | `#9fbcac` | `#3f5f4f` |
| `__TEXTFAINT_DARK__` / `_LIGHT__` | Texto terciario/etiquetas | `#6b8a79` | `#6f8f7c` |
| `__ACCENT_DARK__` / `_LIGHT__` | Color de marca (kicker, énfasis) | `#34d399` | `#0f9d6b` |
| `__ACCENTSTRONG_DARK__` / `_LIGHT__` | Acento más saturado (prompt, progreso) | `#10b981` | `#0c8a5c` |
| `__ACCENTDEEP_DARK__` / `_LIGHT__` | Acento oscuro (líneas foot-note) | `#065f46` | `#d7f0e2` |
| `__AMBER_DARK__` / `_LIGHT__` | Advertencia / segundo color de rama | `#f5c76e` | `#a5730a` |
| `__ROSE_DARK__` / `_LIGHT__` | Error / conflicto | `#e88b8b` | `#b3423f` |

Reglas al elegir la paleta:

- El color pedido por el usuario (p.ej. "verde esmeralda") es siempre
  `--accent`/`--accent-strong`. Deriva `--accent-deep` oscureciendo el mismo
  hue, nunca cambiando de matiz.
- Fondo y superficies deben tener un ligero sesgo de matiz hacia el acento
  (un verde muy oscuro casi negro, no un gris/negro neutro) para que el
  tema se sienta cohesivo, no "acento sobre plantilla genérica".
- Mantén siempre `amber` (advertencia) y `rose` (error/conflicto) como
  colores semánticos separados del acento de marca, aunque el acento
  cambie — son necesarios para badges de error/warning y para diferenciar
  ramas en los diagramas de Git u otras bifurcaciones.
- Define ambos temas (oscuro y claro) siempre, incluso si el usuario no lo
  pide explícitamente — es requisito del motor de plantilla.

Tipografía fija de la plantilla (no cambiar salvo petición explícita):
`Instrument Serif` (títulos, cursiva para énfasis), `Manrope` (cuerpo),
`JetBrains Mono` (código, kickers, etiquetas, diagramas). Están cargadas
vía Google Fonts en la cabecera del `<style>`.

## Componentes disponibles

Todos usan las clases ya definidas en `assets/template.html`. Copia y
adapta estos patrones en vez de inventar nuevas clases:

- **`.kicker`** — etiqueta superior de sección (p.ej. "02 · Proveedores web").
- **`h1.big` / `h2.title`** — titular de portada/cierre y título de slide
  normal. Usa `<em>` para la palabra clave en acento.
- **`.two-col`** — layout de dos columnas para texto+terminal, texto+diagrama, etc.
- **`.term` / `.term-bar` / `.term-body`** — tarjeta estilo terminal para
  bloques de código o comandos; spans `.prompt`, `.c` (comentario), `.a`
  (ámbar/valor), `.g` (acento), `.rose` (error) para colorear líneas.
  `.cursor` añade un cursor parpadeante al final de la última línea activa.
- **`.grid.cols-2` / `.cols-3` + `.card`** — tarjetas para comparativas o
  listados de conceptos paralelos (usa `.card .num` como etiqueta corta).
- **`table.tbl`** — tablas de comparación o referencia de comandos.
- **`ul.list` con `<span class="bul">`** — listas con viñeta o numeración
  monoespaciada; usa `<b>` para el término y `<span class="d">` para la
  descripción atenuada.
- **`.badge` / `.badge.warn` / `.badge.err`** — etiquetas cortas de estado
  (✓ ventaja, ✗ inconveniente, "error N", "cuidado").
  - **`.foot-note`** — aclaración al pie de una diapositiva con barra lateral de acento.
- **`.svg-card`** — contenedor para diagramas (ver siguiente sección).

No es necesario usar todos en cada diapositiva; elige el que mejor
represente la estructura del contenido de esa sección concreta de los
apuntes.

## Diagramas (siempre en SVG, nunca solo texto/ASCII)

Cada concepto con estructura visual (flujo de pasos, ramas divergiendo,
comparación antes/después, arquitectura de cajas, jerarquía, línea de
tiempo) debe llevar un diagrama SVG dentro de `.svg-card`, no solo una
tabla o un bloque de texto. Usa las clases ya definidas:

- `.lbl` / `.lbl-strong` / `.lane-label` — texto dentro del SVG (usan
  `fill`, no `color`).
- `.edge` / `.edge-a` / `.edge-b` — líneas de conexión neutra / acento /
  segundo color (ámbar), para grafos de commits, líneas de tiempo o flujos
  con ramas paralelas.
- `.commit` / `.commit-a` / `.commit-b` / `.commit-m` / `.commit-ghost` —
  nodos circulares (neutro / rama acento / rama secundaria / hito
  destacado / placeholder punteado).
- `.box` / `.box-accent` — rectángulos para diagramas de cajas y flechas
  (arquitecturas, pipelines, etapas).
- `.arrow` / `.arrow-accent` — flechas con marcador (requieren definir los
  `<marker>` `arrowhead` / `arrowhead-accent` una vez por diagrama, como en
  el ejemplo).

Patrón mínimo de un diagrama de cajas con flecha:

```html
<div class="svg-card">
  <svg viewBox="0 0 620 140" xmlns="http://www.w3.org/2000/svg">
    <defs>
      <marker id="arrowhead" markerWidth="10" markerHeight="8" refX="8" refY="4" orient="auto">
        <path d="M0,0 L10,4 L0,8 Z" fill="var(--text-faint)"/>
      </marker>
    </defs>
    <rect class="box" x="20" y="40" width="220" height="60" rx="10"/>
    <text x="130" y="75" text-anchor="middle" class="lbl-strong">Paso 1</text>
    <path class="arrow" d="M245,70 H310"/>
    <rect class="box-accent" x="315" y="40" width="220" height="60" rx="10"/>
    <text x="425" y="75" text-anchor="middle" class="lbl-strong">Paso 2</text>
  </svg>
</div>
```

Patrón mínimo de grafo de commits/ramas (dos ramas que divergen y confluyen):

```html
<div class="svg-card">
  <svg viewBox="0 0 460 200" xmlns="http://www.w3.org/2000/svg">
    <text x="10" y="45" class="lane-label">main</text>
    <text x="10" y="145" class="lane-label">feature</text>
    <path class="edge" d="M75,40 H155"/>
    <path class="edge" d="M155,40 L215,140"/>
    <path class="edge" d="M215,140 H305"/>
    <path class="edge" d="M305,140 L365,40"/>
    <circle class="commit" cx="75" cy="40" r="9"/>
    <circle class="commit" cx="155" cy="40" r="9"/>
    <circle class="commit-a" cx="215" cy="140" r="9"/>
    <circle class="commit-a" cx="305" cy="140" r="9"/>
    <circle class="commit" cx="365" cy="40" r="9"/>
  </svg>
</div>
```

Ajusta el `viewBox` y las coordenadas a mano según el número de nodos —
no hace falta una librería de gráficos; son pocas primitivas SVG por
diagrama y el resultado se integra perfectamente con el tema claro/oscuro
porque todo el color viene de `var(--...)`.

## Fotografías reales (cuando no compensa un SVG)

Algunos conceptos se explican mejor con una fotografía o captura de pantalla real
(dispositivos históricos, capturas de un IDE, personas, productos) que con un
diagrama esquemático. En esos casos **no inventes ni descargues la imagen**: deja
un hueco marcado con la clase `.img-placeholder` (ya definida en
`assets/template.html`) para que el usuario la añada a mano después.

```html
<div class="img-placeholder">
  <svg class="ph-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="9" cy="9" r="2"/><path d="M21 15l-5-5L5 21"/></svg>
  <span class="ph-file">img/nombre-descriptivo.jpg</span>
  <span class="ph-desc">Descripción de qué debe mostrar la imagen, con detalle suficiente para buscarla o hacerla.</span>
</div>
```

Reglas:

- Usa `.img-placeholder` en vez de `.svg-card` solo cuando la imagen aporte algo
  que un diagrama no puede (un objeto real, una UI real, una persona, un momento
  histórico) — para flujos, jerarquías o comparaciones estructurales sigue
  usando SVG.
- El nombre de fichero en `.ph-file` debe ser el que se usará al guardar la
  imagen real en `static/img/` (o la carpeta de imágenes del proyecto).
- La `.ph-desc` debe ser lo bastante concreta como para que quien añada la
  imagen sepa exactamente qué buscar o fotografiar.
- **Registra cada hueco en un fichero Markdown** `static/presentaciones/<slug-tema>-imagenes-pendientes.md`
  con una tabla `Diapositiva | Fichero | Qué debe mostrar`, generada a la vez
  que la presentación. Así el usuario tiene un checklist de qué imágenes faltan
  por añadir y en qué diapositiva va cada una.

## Estructura de diapositivas recomendada

1. **Portada** — kicker con "Tema N", `h1.big` con el título (parte en
   `<em>`), subtítulo de una frase, opcionalmente un `.term` decorativo.
2. **Mapa del tema** — grid de tarjetas, una por sección de los apuntes,
   numeradas.
3. **Una o varias diapositivas por sección** de los apuntes fuente, en el
   mismo orden que el documento original. Divide una sección larga en
   varias diapositivas en vez de amontonar contenido.
4. **Errores comunes / advertencias** (si los apuntes los tienen) — grid de
   `.card` con `.badge.err`, mostrando el error y la corrección.
5. **Ejercicios** (si el tema los tiene) — lista numerada con
   `.bul.exnum`.
6. **Cierre** — frase memorable relacionada con el tema, estilo portada.

## Multi-tema / coherencia entre presentaciones del curso

Si el curso ya tiene una presentación publicada (revisa
`static/presentaciones/`), reutiliza exactamente su paleta y estructura de
componentes al crear la del siguiente tema, salvo que el usuario pida
explícitamente cambiar el estilo. La identidad visual del curso debe ser
consistente diapositiva a diapositiva y tema a tema.
