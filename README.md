# Web AD — Acceso a Datos

Apuntes del módulo 0486 (CS DAM), construidos con [Docusaurus](https://docusaurus.io/).

## Instalación

```bash
npm install
```

## Desarrollo local

```bash
npm start
```

Arranca un servidor local y abre el navegador. La mayoría de los cambios se reflejan en vivo.

## Build

```bash
npm run build
```

Genera el contenido estático en el directorio `build`.

## Estructura del contenido

El contenido **no** vive en `docs/` ni en `blog/` (ambos plugins están desactivados).
Cada tema es una carpeta de páginas Markdown bajo `src/pages/`:

```
src/pages/
  tema-0/
    a-introduccion.md
    b-....md
  tema-1/
    a-....md
```

Al añadir un tema hay que actualizar dos sitios:

1. El array `TEMAS` en `src/pages/index.tsx` (índice de la portada).
2. El dropdown "Temas" en `docusaurus.config.ts` (barra de navegación).

## Changelogs

1. Crea `static/changelogs/X.Y.Z.md` con secciones `### Nuevo`, `### Mejora`, `### Corrección`.
2. Añade la versión al array de `src/components/WhatsNewModal/changelogs/index.ts`.

El modal de novedades muestra la versión más alta; la página `/changelogs` lista el historial completo.

## Componentes disponibles

- `src/components/WipBanner` — aviso modal de "proyecto en construcción".
- `src/components/WhatsNewModal` — modal de novedades de la última versión.

Ninguno está montado todavía; para usarlos hay que hacer swizzle del `Layout` o incluirlos en las páginas.
