---
name: contenido-tema
description: Crea o edita páginas de contenido del curso (apartados de un tema, página de actividades) en el formato Docusaurus usado en src/pages/tema-N/. Úsala cuando el usuario pida escribir, añadir o revisar un apartado, unidad o actividades del módulo Acceso a Datos.
---

# Contenido de tema (formato Docusaurus del curso)

Genera páginas `.md` para `src/pages/tema-N/` replicando exactamente las
convenciones ya usadas en `tema-0`. Antes de escribir, si existen apartados
previos del mismo tema o de tema-0, léelos para mantener tono, nivel de
detalle y estilo.

## Estructura de ficheros

- Ruta: `src/pages/tema-N/<letra>-<slug-kebab-case>.md` (p. ej.
  `a-presentacion-modulo.md`, `b-proyecto-multagal.md`...). Los apartados de
  un tema se ordenan por letra consecutiva `a, b, c, ...`.
- La última letra del tema es siempre `actividades` (p. ej.
  `h-actividades.md` en tema-0): recoge las actividades prácticas de la
  unidad con sus criterios de aceptación.
- Los enlaces internos a otros apartados usan rutas absolutas del sitio:
  `/tema-N/<slug>` y, si apuntan a una sección concreta, un ancla en
  minúsculas y con guiones: `/tema-0/a-presentacion-modulo#el-diario-técnico`.

## Frontmatter obligatorio

```yaml
---
title: 'Título de la página (entre comillas simples si lleva : o similar)'
description: Una frase que resume el contenido, en tono neutro y específico.
---
```

Solo la primera página de un tema (la de presentación) importa además el
botón de siguiente página:

```mdx
import NextPageButton from '@site/src/components/NextPageButton';
```

Ese `import` va justo debajo del frontmatter, antes del `#` título. Todas las
páginas del tema lo incluyen (no solo la primera) si terminan con
`<NextPageButton>`.

## Cuerpo

- Empieza con un `# Título` (h1) que repite o resume el `title` del
  frontmatter.
- Usa `##` y `###` para subsecciones. No saltes niveles.
- Tono: directo, en segunda persona cuando se dirige al alumnado ("verás",
  "crea", "debes tener"), en tercera/impersonal cuando describe el sistema.
  Frases cortas. Nada de relleno motivacional.
- Negrita (`**...**`) para destacar términos clave la primera vez que
  aparecen o para remarcar la palabra que lleva el peso de la frase (p. ej.
  **individual**, **no una entrega única**).
- Las tablas Markdown se usan mucho para pesos, comparativas, criterios de
  aceptación y calendarios. Cabecera siempre en negrita implícita por ser
  cabecera de tabla, sin negrita extra en las celdas salvo excepción.

## Admonitions de Docusaurus

Sintaxis exacta, con línea en blanco antes y después del bloque y el cierre
`:::` en su propia línea:

```
:::info[Título opcional entre corchetes]
Contenido.
:::
```

Tipos usados, por orden de frecuencia y su propósito real en este curso:

| Tipo | Uso |
|------|-----|
| `:::info` | Contexto o principio rector que conviene destacar sin que sea una advertencia. |
| `:::tip` | Consejo práctico, atajo o aclaración que facilita la tarea. |
| `:::warning` | Exigencia del módulo o error frecuente con consecuencia moderada. |
| `:::danger` | Condición formal de evaluación, riesgo grave (credenciales, pérdida de datos) o algo que puede suponer un 0. |
| `:::note` | Errores frecuentes en formato pregunta/síntoma entre comillas angulares («...»), o matices menores. Muy usado en secciones "Errores frecuentes" al final de un apartado, con un `:::note[...]` por cada síntoma. |

El título entre corchetes es opcional; cuando el bloque es largo o el
contenido ya deja claro el tema, se omite (`:::warning` a secas, con un
párrafo `[Título]` en la primera línea del cuerpo si se quiere destacar,
como en `a-presentacion-modulo.md`).

## Bloques de código

- Siempre con el lenguaje indicado tras los backticks: `bash`, `java`,
  `yaml`, `gitignore`, `gitattributes`, `markdown`, `mermaid`, sin lenguaje
  solo para salidas de consola sin resaltado si aplica.
- Los ejemplos de comandos `bash` incluyen comentarios `#` explicando qué
  hace cada línea cuando no es obvio.
- Los diagramas usan mermaid embebido en un bloque ```mermaid (por ejemplo
  `gitGraph` para el flujo de ramas).
- Las plantillas reutilizables (ADR, descripción de PR) van en un bloque
  ` ```markdown ` con el formato completo relleno de un ejemplo realista del
  dominio del curso (MULTAGAL), no con placeholders genéricos tipo "texto
  aquí".

## Página de actividades (`*-actividades.md`)

Estructura fija, replicando `h-actividades.md`:

1. `# Actividades de la UD<N>` y un párrafo breve con el número de
   actividades, horas y qué debe quedar terminado.
2. Un `:::info[Entrega]` indicando a qué entrega pertenece la unidad y qué
   rama/etiqueta la materializa.
3. Separador `---` entre actividades.
4. Por cada actividad, `## A<N>.<n> — Título`:
   - **Objetivo:** una frase.
   - `### Tareas` — lista numerada, imperativo, con enlaces a los apartados
     teóricos correspondientes cuando aplica.
   - `### Entregable` — qué debe existir en el repo al terminar.
   - `### Criterios de aceptación` — tabla `# | Criterio`.
   - Admonitions `:::tip` o `:::warning` opcionales para matices o trampas
     conocidas del enunciado.
5. Al final, `## Autoevaluación de la unidad`: lista numerada de preguntas
   tipo examen oral, sin respuesta, que cubren todo lo visto.
6. Opcional, `## Ampliación (opcional)`: lista de propuestas para quien
   avance más rápido, no exigibles.

## Navegación al final de página

Toda página (excepto la última del tema, que puede enlazar al siguiente
tema o no llevar botón) termina con:

```mdx
<NextPageButton to="/tema-N/<siguiente-slug>">
Texto corto del siguiente apartado
</NextPageButton>
```

## Checklist antes de entregar la página

- [ ] Frontmatter con `title` y `description` coherentes con el contenido.
- [ ] `import NextPageButton` si la página lo usa.
- [ ] Jerarquía de encabezados sin saltos.
- [ ] Admonitions con sintaxis `:::tipo[Título opcional]` ... `:::` y líneas
      en blanco alrededor.
- [ ] Bloques de código con lenguaje explícito.
- [ ] Enlaces internos con ruta absoluta `/tema-N/slug`.
- [ ] Tono y nivel de exigencia coherentes con el resto de `tema-0` (rigor
      técnico, referencias explícitas a RA y criterios de evaluación cuando
      aplica).
- [ ] Si es la página de actividades: criterios de aceptación en tabla y
      autoevaluación final.
