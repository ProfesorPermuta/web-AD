---
name: programacion-proens
description: Genera UNA única página HTML con las 8 secciones necesarias para volcar una programación didáctica en PROENS, en secuencia (procedimiento de evaluación inicial, secuencia de UD con sesiones/peso, matriz de criterios de evaluación × UD, matriz de bloques de contenido × UD, actividades por UD con sus CA/BC vinculados, diseño detallado de cada actividad con objetivos/resultados/recursos, criterios de cualificación con procedimiento de recuperación, y procedimiento de seguimiento de la programación y evaluación de la práctica docente). Usar cuando el usuario pida preparar, generar o pasar a PROENS la evaluación inicial, la secuenciación, los criterios, los contenidos, las actividades, el diseño de actividades, la cualificación/recuperación o el seguimiento/autoevaluación docente de un módulo, para cualquier ciclo formativo, no solo Acceso a Datos.
---

# Programación para PROENS

Genera **una sola página HTML** autocontenida (publicada como un único
Artifact, con botón de copiar en cada celda o bloque de texto) que reproduce
en secuencia, dentro de la misma página, los ocho bloques que PROENS pide
para dar de alta la programación de un módulo profesional:

0. **Procedemento de avaliación inicial** — campo de texto libre (no una
   matriz de checks) con el procedimiento, instrumentos y uso de resultados
   de la evaluación inicial del módulo (ver "Sección 0" más abajo).
1. **Secuencia de UD** — título, descripción, nº de sesiones y % de peso de
   cada unidad didáctica.
2. **Criterios de avaliación × UD** — matriz de checks (agrupada por RA)
   igual que la pantalla "Criterios de avaliación" de PROENS.
3. **Bloques de contidos × UD** — matriz de checks (agrupada por bloque BC
   oficial del currículo) igual que la pantalla de bloques de contenido.
4. **Actividades por UD** — título, descripción y sesiones de cada
   actividad, más una matriz de checks por UD (CA y contenidos, con las
   actividades en columnas) para cada una.
5. **Diseño de las actividades** — una tarjeta por actividad con
   descripción, objetivos específicos, resultados/productos y recursos
   específicos (ver "Sección 5" más abajo para el criterio de redacción de
   cada campo).
6. **Criterios de cualificación e procedemento de recuperación** — campo de
   texto libre (igual que la sección 0) con la ponderación de instrumentos de
   evaluación, condiciones de superación del módulo, y el procedimiento de
   recuperación (ver "Sección 6" más abajo — **siempre** se pregunta al
   usuario la base de cualificación antes de redactar, nunca se asume).
7. **Procedemento de seguimento da programación e avaliación da práctica
   docente** — campo de texto libre (igual que las secciones 0 y 6) con los
   indicadores de seguimiento, la periodicidad de revisión y el mecanismo de
   mejora continua (ver "Sección 7" más abajo).

Las ocho secciones van **una debajo de otra en la misma página**, con un
índice de anclas arriba para saltar entre ellas — no se generan artifacts
separados por sección.

Esta skill es agnóstica del módulo: sirve para cualquier ciclo formativo
siempre que el usuario aporte (o el proyecto ya tenga) la programación
didáctica de origen y, para las secciones 2-4, los bloques de contenido
oficiales del currículo tal como los expone PROENS.

## Cuándo usar esta skill

- "Necesito el procedimiento de evaluación inicial para PROENS" (sección 0).
- "Necesito la secuencia de UD para PROENS" / "dame título, descripción,
  sesiones y peso de cada UD".
- "Genérame la tabla de criterios de evaluación por UD, como en PROENS"
  (normalmente acompañado de una captura de pantalla del formulario PROENS).
- "Ahora lo mismo para los bloques de contenido".
- "Necesito las actividades de cada UD con sus CA y contenidos asociados".
- "Ahora necesito el diseño de cada actividad: descripción, objetivos
  específicos, resultados/productos y recursos" (sección 5).
- "Necesito los criterios de cualificación y el procedimiento de
  recuperación" (sección 6).
- "Necesito el procedimiento de seguimiento de la programación y de
  evaluación de la práctica docente" (sección 7).
- Cualquier variante de estas peticiones para un módulo de **otro** ciclo
  formativo, no solo Acceso a Datos: el proceso es el mismo, solo cambian los
  datos de origen.
- Si el usuario pide solo una de las ocho secciones (p.ej. "solo la
  secuencia de UD"), genera igualmente la página completa con esa única
  sección rellena y las demás vacías u omitidas del índice — no crees un
  artifact distinto por sección.

Si lo que se pide es contenido de clase (apuntes, presentación) en vez de
documentación administrativa para PROENS, esta skill no aplica: usa
`contenido-tema` o `presentacion-clase`.

## Entradas necesarias

Antes de generar nada, reúne:

1. **La programación didáctica de origen** del módulo (normalmente un `.md` en
   `src/pages/documentacion/` o donde indique el usuario) con, como mínimo:
   - Secuenciación de UD con horas/sesiones de cada una (sección tipo
     "Cuadro resumen" o "Secuenciación y temporalización").
   - Resultados de aprendizaje (RA) con sus criterios de evaluación (CA) y,
     si existe, su ponderación (% peso por RA).
   - Desarrollo de cada UD con sus actividades (título + descripción breve).
   - Fases/entregas del proyecto si el módulo se articula así.
2. **El número total de sesiones que exige PROENS**, que casi nunca coincide
   con las horas de la programación de origen (PROENS puede fijar sesiones de
   duración distinta a la hora completa, p.ej. 50 minutos). **Este dato no se
   puede modificar**: pregúntalo si no se ha dado y escala la secuenciación a
   ese total exacto (ver "Cálculo de sesiones").
3. **La numeración de UD que usa PROENS.** Puede no coincidir con la de la
   programación de origen (p.ej. la programación empieza en UD0 y PROENS
   empieza en UD1). Confírmalo con el usuario antes de generar nada — un
   desfase de numeración invalida toda la página.
4. **Para las secciones de criterios y contenidos**: si el usuario pega una
   captura o el texto tal cual de PROENS (filas `Bloque BCn. Nombre` con sus
   ítems `Cn.n - descripción`, o el bloque de RA/CA con checkboxes UD1..UDn),
   usa esos textos literalmente como fuente de verdad en vez de reconstruirlos
   tú mismo — PROENS pone nombres y numeraciones propias que no siempre
   coinciden exactamente con el currículo oficial o con la programación.
   Si no los tienes, pregunta antes de inventar la lista de bloques.

## Proceso

### 0. Redactar el procedimiento de evaluación inicial (sección 0)

A diferencia de las secciones 1-5, esta es **un campo de texto libre en
PROENS** (varios párrafos de descripción), no una matriz de checks ni una
tabla — no intentes convertirla en tabla.

Redacta 3-4 párrafos que cubran, en este orden:

1. **Cuándo se hace y con qué objetivo**: en las primeras sesiones del
   módulo (normalmente dentro de la UD de arranque), con carácter **no
   cualificador**, para comprobar el grado de adquisición de las competencias
   previas que el currículo presupone (identifícalas en la sección de
   contextualización de la programación de origen — típicamente proceden de
   los módulos de 1º curso relacionados).
2. **Instrumentos utilizados**: por defecto, combina un cuestionario
   individual breve (conceptual + pequeños ejercicios prácticos sobre las
   competencias previas concretas del módulo) con observación directa del
   profesorado durante las primeras actividades prácticas — esto último
   permite detectar dificultades de manejo de herramientas (IDE, control de
   versiones, línea de comandos) que un cuestionario escrito no siempre
   revela. Pregunta al usuario si prefiere atar el procedimiento a una
   actividad concreta ya diseñada en la sección 4/5 (p.ej. "la evaluación
   inicial se apoya en la actividad A0.1") en vez de un procedimiento
   genérico — ambas opciones son válidas, pero cambian la redacción.
3. **Qué se hace con los resultados**: a nivel individual (activar medidas
   de refuerzo/atención a la diversidad ya previstas en la programación para
   quien lo necesite) y a nivel agregado (ajustar el ritmo o el nivel de
   andamiaje de las primeras UD al punto de partida real del grupo), dejando
   claro que **no se alteran los resultados de aprendizaje ni los criterios
   de evaluación** del currículo.
4. **Constancia formal**: cómo y dónde queda registrada (registro interno
   del profesorado, comunicación individualizada al alumnado), remarcando
   que no genera calificación en actas ni expediente.

No inventes competencias previas que no estén en la programación de origen:
tómalas literalmente de su apartado de contextualización/perfil de entrada
del alumnado. Si esa programación no dice nada al respecto, pregunta antes
de asumirlas.

### 1. Leer la programación de origen completa

Lee el `.md`/documento de la programación entero (usa `offset`/`limit` si es
largo) antes de calcular nada. Identifica:
- La tabla resumen de UD con horas.
- Los RA con sus CA (numerados o con letras — convierte siempre a formato
  `RAn.m` numérico, p.ej. `RA1.1`, `RA1.2`, nunca `RA1.a`, salvo que el
  usuario pida explícitamente letras).
- El peso oficial de cada RA sobre la calificación final, si existe.
- Las actividades listadas dentro de cada UD.

### 2. Resolver la numeración de UD

Pregunta o confirma explícitamente la correspondencia entre la numeración de
la programación de origen y la de PROENS antes de escribir nada. Usa siempre
la numeración de PROENS en la página generada.

### 3. Calcular sesiones por UD (sección 1 y base de la sección 4)

1. Si el nº total de sesiones de PROENS coincide con las horas de la
   programación, usa las horas tal cual.
2. Si no coincide (caso habitual), **pregunta la duración de una sesión
   lectiva** (p.ej. 50 min, 55 min, 1h) y escala cada UD proporcionalmente:
   `sesiones_UD = round(horas_UD * 60 / duración_sesión_min)`, o
   `sesiones_UD = round(horas_UD / horas_totales * total_sesiones_PROENS)`
   si el usuario ya te da el total fijo directamente sin pasar por minutos.
   Comprueba siempre que la suma de las UD redondeadas cuadra exactamente
   con el total exigido; si el redondeo simple no cuadra, ajusta manualmente
   ±1 sesión en la UD de mayor peso hasta que la suma sea exacta — nunca
   entregues un total que no coincida.
3. Dentro de cada UD, para la sección 4 (actividades), reparte esas sesiones
   entre las actividades de la UD **ponderando por complejidad relativa**, no
   de forma uniforme, salvo que el usuario pida lo contrario explícitamente.
   Actividades de mayor peso (casos transaccionales, baterías de pruebas,
   integración final, defensas) llevan más sesiones que actividades simples
   (checklists, configuración). Asigna un peso relativo (p.ej. 0.8–1.4) a
   cada actividad, reparte proporcionalmente, redondea a enteros ≥1 y ajusta
   la diferencia en la actividad de mayor peso hasta que la suma cuadre
   exactamente con el total de sesiones de la UD. Un script rápido en Bash
   con `python3` es la forma más fiable de hacer este cálculo sin errores de
   redondeo — no lo hagas de cabeza.

### 4. Calcular el % de peso por UD (sección 1)

Pregunta al usuario cómo prefiere calcularlo si no lo ha indicado:
- **Proporcional a horas/sesiones de la UD** sobre el total (más simple).
- **Basado en el peso oficial de cada RA**, prorrateado por horas cuando una
  UD cubre un RA solo parcialmente o varias UD comparten un mismo RA (caso
  típico: un RA transversal como "programación de componentes" que se reparte
  entre varias UD tecnológicas). En este caso, para cada RA reparte su peso
  oficial entre las UD que lo trabajan proporcionalmente a las horas que cada
  una le dedica dentro del RA (si una UD trabaja dos RA a la vez, divide sus
  horas a partes iguales entre ambos RA antes de prorratear). Verifica siempre
  que la suma final de todas las UD da exactamente 100 %; si no, hay un error
  de reparto que hay que corregir antes de publicar.
- Las UD de arranque de entorno sin RA asociado (tipo "UD0/UD1 - arranque")
  llevan 0 % de peso.

### 5. Construir la matriz de Criterios × UD (sección 2)

Agrupa por RA (usa el nombre/enunciado oficial del RA). Cada CA es una fila
con su código `RAn.m` y su enunciado literal (usa el idioma y la redacción
exacta del currículo/PROENS, no lo traduzcas ni lo resumas). Marca en qué UD
se trabaja cada CA según la sección de contenidos/actividades de esa UD en la
programación de origen — normalmente un CA se marca en la UD que introduce esa
tecnología, y a veces también en la UD siguiente si allí se reutiliza o se
profundiza. Si un CA tiene un mínimo no exigible en el currículo, márcalo con
`minNo:true` (se muestra en cursiva) pero igual indica en qué UD se
trabajaría.

### 6. Construir la matriz de Bloques de contenido × UD (sección 3)

Misma estructura que la sección 2, pero agrupando por bloque BC en vez de por
RA. **Usa los nombres y numeración de contenidos exactamente como los da
PROENS** si el usuario los ha pegado — no los reconstruyas a partir del
currículo genérico, porque PROENS puede agrupar o numerar distinto. Si el
usuario no tiene ese texto a mano, dile que lo necesitas (captura o
copia/pega del formulario de PROENS) antes de inventar la lista de bloques.

El mapeo bloque→UD sigue la misma lógica que el de RA→UD: cada contenido se
marca en la UD donde se imparte según la sección "Contenidos" de esa UD en la
programación de origen. Los contenidos de bloques transversales (p.ej. un
bloque final de "programación de componentes") pueden aparecer repartidos en
varias UD tecnológicas si en cada una se construye el componente
correspondiente — pero avisa al usuario de este reparto, porque es el punto
más subjetivo del mapeo y conviene que lo confirme.

### 7. Construir la tabla de Actividades por UD (sección 4)

Estructura de datos por UD:

```js
{
  ud: "UD1",
  nombre: "Nombre de la UD",
  sesiones: 12,
  acts: [
    { cod:"A0.1", t:"Título corto", d:"Descripción de una frase", s:2,
      ca:["RA6.1"], bc:["C6.1"] },
    ...
  ]
}
```

- `s` de cada actividad sale del reparto ponderado del punto 3; la suma de
  `s` de todas las actividades de una UD debe coincidir exactamente con
  `sesiones` de esa UD.
- `ca` y `bc` son los códigos ya usados en las secciones 2 y 3 — mantenlos
  coherentes (si una actividad trabaja `RA2.10`, ese CA debe existir en la
  sección 2 marcado como trabajado en esa misma UD).
- La plantilla genera automáticamente, para cada UD, dos matrices con las
  actividades en **columnas** (código corto + tooltip con el título completo
  al pasar el ratón) y los CA/BC de esa UD en **filas**, con checks — no en
  la orientación contraria, salvo que el usuario pida lo contrario
  explícitamente (por defecto: actividades en horizontal, criterios/contenidos
  en vertical).

### 8. Redactar el diseño detallado de cada actividad (sección 5)

Para cada actividad ya definida en la sección 4, redacta cuatro campos:

- **Descripción**: la misma descripción breve de la sección 4 (o una versión
  ligeramente ampliada de una o dos frases) — no la dupliques con contenido
  distinto, debe ser coherente con lo ya mostrado en la sección 4.
- **Objetivos específicos**: 1-3 frases en infinitivo o forma de logro
  concreto ("Implementar...", "Diagnosticar...", "Comparar..."), derivadas de
  los CA que esa actividad ya tiene vinculados en la sección 4 — no inventes
  objetivos que no se correspondan con ningún CA de la actividad.
- **Resultados / productos**: lo que el alumnado entrega físicamente al
  terminar la actividad. Salvo que el usuario indique otra cosa, los
  resultados de las actividades de este tipo de módulos (desarrollo de
  software) son siempre alguna combinación de:
  - **Repositorio Git con el código** de la funcionalidad implementada
    (commit/rama/tag concreto si la actividad cierra un hito).
  - **Documento ADR** (Architecture Decision Record, formato simplificado)
    cuando la actividad implica una decisión técnica que justificar (elegir
    entre alternativas, una configuración, un patrón) — no todas las
    actividades generan un ADR, solo las que tienen una decisión de diseño
    real detrás; una actividad puramente mecánica (checklist de instalación,
    configuración de ramas) no necesita uno.
  - Pruebas automatizadas, documentación técnica (Javadoc, OpenAPI) o
    informes comparativos, cuando la actividad los pida explícitamente.
  Pregunta al usuario si el criterio de resultados difiere de este patrón
  (repo Git + ADR) antes de asumirlo para un módulo distinto.
- **Recursos específicos**: herramientas, datasets, plantillas o documentos
  que esa actividad concreta necesita más allá del entorno base del curso
  (que ya se cubre en la sección "Recursos y materiales" de la programación
  general) — p.ej. "Plantilla ADR", "Fichero XSD del catálogo de
  infracciones", "Cuenta de PostgreSQL con extensión PostGIS habilitada".
  No repitas aquí recursos genéricos ya cubiertos por el entorno del curso
  (IDE, JDK, Git) salvo que la actividad tenga un requisito no estándar.

Estructura de datos por UD para la sección 5:

```js
{
  ud: "UD1",
  nombre: "Nombre de la UD",
  acts: [
    {
      cod: "A0.1",
      t: "Título corto (igual que en la sección 4)",
      descripcion: "Descripción de una o dos frases.",
      objetivos: ["Objetivo específico 1.", "Objetivo específico 2."],
      resultados: ["Repositorio Git con ...", "ADR: decisión sobre ..."],
      recursos: ["Recurso específico 1.", "Recurso específico 2."]
    },
    ...
  ]
}
```

Cada campo (`objetivos`, `resultados`, `recursos`) es un array: si tiene un
solo elemento se muestra como párrafo, si tiene varios se muestra como lista
— no fuerces una lista de un solo ítem ni un párrafo con varias ideas
separadas por comas.

### 9. Preguntar la base de cualificación y redactar la sección 6

Esta sección, igual que la 0, es **un campo de texto libre en PROENS**, no
una tabla ni una matriz de checks.

**Nunca asumas el esquema de cualificación.** Antes de escribir una sola
frase, pregunta al usuario (con `AskUserQuestion` si la pregunta admite
opciones claras, o en texto si es más abierta) al menos:

1. **Instrumentos de evaluación y su ponderación**: qué se evalúa y con qué
   peso (p.ej. "40 % examen práctico de modificación del proyecto entregado
   + 60 % proyecto entregado"). No hay un esquema por defecto razonable para
   todos los módulos — varía mucho según si el módulo se articula por
   proyecto, por unidades independientes, con examen teórico, etc.
2. **Condiciones de superación**: si cada instrumento tiene un mínimo propio
   que superar independientemente de la media ponderada (p.ej. "5/10 en
   ambas partes obligatoriamente") o si basta la media ponderada global.
3. **Procedimiento de recuperación**: en qué consiste (tutorías, prueba
   extraordinaria, nueva entrega...), qué evalúa (todo el módulo o solo lo
   pendiente) y en qué formato/duración si aplica una prueba (p.ej. "examen
   de 4 horas desarrollando un proyecto distinto al proyecto marco del
   curso, para evitar que la solución ya conocida del proyecto marco
   contamine la prueba").

Si el usuario ya te ha dado estos datos en su mensaje (como ocurrió la
primera vez que se generó esta sección para Acceso a Datos: 40 % examen de
modificación + 60 % proyecto, mínimo 5/10 en ambas partes, recuperación con
tutorías + examen de 4h con un proyecto distinto al marco del curso), no
vuelvas a preguntar — solo pregunta cuando falte alguno de los tres puntos.

Con esos datos, redacta 3-4 párrafos:

1. **Ponderación de instrumentos** — qué pesa cada instrumento y por qué
   (coherencia con la metodología del módulo: si es un módulo por proyecto,
   explica por qué el proyecto entregado pesa lo que pesa frente a la prueba
   individual).
2. **Condiciones de superación** — el mínimo por instrumento y la fórmula de
   la calificación final, dejando explícito qué ocurre si un instrumento no
   llega al mínimo aunque la media sea suficiente (no se supera el módulo).
3. **Procedimiento de recuperación** — instrumentos y formato de la prueba
   de recuperación, y por qué se eligió ese diseño (p.ej. un proyecto
   distinto al proyecto marco del curso obliga a demostrar competencia
   transferible, no solo memorización de la solución ya trabajada en clase).
4. **Vinculación con el resto de la programación** — cómo esta cualificación
   conecta con lo ya definido en las secciones 1-5 (p.ej. qué UD/actividades
   corresponden al "proyecto entregado" si el usuario lo ha determinado así).

No inventes cifras ni condiciones que el usuario no haya dado. Si el usuario
menciona un instrumento (p.ej. "examen") sin más detalle sobre su contenido,
pregunta qué evalúa exactamente antes de redactar ese párrafo.

### 10. Redactar el seguimiento de la programación y la práctica docente (sección 7)

Igual que las secciones 0 y 6, es **texto libre**, no tabla ni matriz.

Busca en la programación de origen un apartado ya existente de tipo
"Seguimiento y evaluación de la programación" (suele estar cerca del final,
antes de la firma/cierre del documento) — muchas programaciones ya traen una
tabla de indicadores con su umbral de revisión. Si existe, tómala como base
literal en vez de inventar indicadores nuevos; si no existe, pregunta al
usuario si quiere que se propongan indicadores estándar o que él los
facilite.

Redacta 3-4 párrafos que cubran:

1. **Cuándo y quién revisa la programación**: periodicidad (habitualmente
   las reuniones ordinarias de departamento) y si además hay una revisión de
   cierre de curso.
2. **Indicadores de seguimiento y sus umbrales de alerta**: lista los
   indicadores tal como aparezcan en la programación de origen (grado de
   cumplimiento de la temporalización, % de alumnado que supera cada RA, %
   que supera el módulo, valoración de la carga de trabajo, vigencia de la
   pila tecnológica...) con su fuente de datos (diario de aula, registro de
   calificaciones, encuesta al alumnado...) y el umbral que dispara una
   revisión.
3. **Autoevaluación de la práctica docente**: cómo se recoge la valoración
   del propio desempeño docente — encuesta al alumnado, autoinforme, o
   ambos — y qué aspectos cubre (claridad de las explicaciones, ritmo,
   adecuación de los materiales, utilidad de la metodología por proyectos si
   el módulo la usa).
4. **Qué se hace con los resultados**: el mecanismo de mejora continua
   (memoria de fin de curso con propuestas concretas para el curso
   siguiente — p.ej. renovar el pliego de la prueba práctica final si el
   módulo tiene una, actualizar versiones de la pila tecnológica, ajustar la
   temporalización de las UD que superaron el umbral de desviación).

No inventes indicadores ni umbrales que no estén ni en la programación de
origen ni confirmados por el usuario — a diferencia de la sección 6 (donde
el usuario suele dar cifras concretas de cualificación), aquí es habitual
que la programación ya tenga el contenido casi listo y la tarea sea
principalmente convertirlo a prosa fluida para el campo de PROENS, no
inventarlo desde cero.

### 11. Rellenar la plantilla y publicar como un único Artifact

Copia `assets/pagina-proens.html` y sustituye únicamente los marcadores
`__..._DATA__` con los arrays JS correspondientes, los textos `__..._SUB__` /
`__..._HINT__` con las descripciones de esa sección para el módulo concreto
(nombre del módulo, ciclo, leyenda de qué es cada UD), y los tríos
`__AVAL_INICIAL_SUB__` / `__AVAL_INICIAL_PARRAFOS__` / `__AVAL_INICIAL_HINT__`
(sección 0), `__CUALIF_SUB__` / `__CUALIF_PARRAFOS__` / `__CUALIF_HINT__`
(sección 6) y `__SEGUIMENTO_SUB__` / `__SEGUIMENTO_PARRAFOS__` /
`__SEGUIMENTO_HINT__` (sección 7) con los textos redactados en los puntos 0,
9 y 10 respectivamente — los tres `__..._PARRAFOS__` son HTML plano, un
`<p>...</p>` por párrafo, no un array JS (el botón "Copiar texto completo" de
esas secciones lee directamente los `<p>` del DOM mediante `data-copy-block`,
que funciona igual para cualquier `id` de contenedor). No dupliques CSS ni
reestructures el HTML de la plantilla: está pensada para que las ocho
secciones convivan en una sola página con anclas de navegación
(`#avaliacion-inicial`, `#seq`, `#criterios`, `#contidos`, `#actividades`,
`#diseno`, `#cualificacion`, `#seguimento`).

Publica el resultado con la herramienta Artifact como **un único** artifact
(`action: "publish"` la primera vez sin `url`; con `url` en actualizaciones
posteriores dentro de la misma conversación). No crees varios artifacts para
las distintas secciones.

Antes de publicar, revisa mentalmente (o con un pequeño script) que:
- La suma de sesiones de la sección 1 coincide exactamente con el total
  exigido por PROENS.
- La suma de % de peso de la sección 1 da exactamente 100.
- Dentro de cada UD, la suma de sesiones de sus actividades (sección 4)
  coincide con las sesiones de esa UD en la sección 1.
- Todo código CA/BC usado en la sección 4 existe y está marcado igual en las
  secciones 2/3 para esa UD.
- Toda actividad de la sección 4 tiene su tarjeta correspondiente en la
  sección 5, con el mismo código y título.

## Notas de idioma y fidelidad

- Si el currículo/PROENS del ciclo está en gallego (caso habitual en centros
  de Galicia), respeta el idioma y la redacción literal de RA/CA/bloques de
  contenido; no traduzcas al castellano ni reformules.
- No inventes CA, bloques de contenido o actividades que no estén en la
  programación de origen o en el texto de PROENS aportado por el usuario. Si
  falta información para completar una sección, pregunta en vez de rellenar
  con contenido plausible.
- Esta skill es reutilizable para cualquier módulo/ciclo: no asumas el dominio
  concreto (proyecto marco, tecnologías) de ningún curso anterior — tómalo
  siempre de la programación que el usuario indique en cada caso.
