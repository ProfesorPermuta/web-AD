---
title: Presentación del módulo y criterios de calificación
description: Qué es Acceso a Datos, cómo se organiza el curso por proyecto único incremental y cómo se califica.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Presentación del módulo y criterios de calificación

El módulo **0486 — Acceso a Datos** ocupa 80 horas del segundo curso de DAM,
aproximadamente 4 horas semanales. Su objeto es uno solo: **la capa de
persistencia** de una aplicación. Todo lo que vamos a ver responde a la misma
pregunta formulada de seis maneras distintas — *¿dónde y cómo guardo los datos
para poder recuperarlos después?*

## De dónde venimos

El módulo da por adquiridas competencias de primer curso. Si alguna de estas
está floja, conviene repasarla ahora y no en noviembre:

| Módulo previo | Lo que se presupone |
|---------------|---------------------|
| Programación (0485) | POO en Java: clases, herencia, interfaces, colecciones, genéricos, excepciones. |
| Bases de Datos (0484) | Diseño relacional, SQL (DDL, DML, DQL), transacciones, procedimientos almacenados. |
| Entornos de Desarrollo (0487) | Uso de un IDE profesional, Git, gestión de dependencias. |

Y a dónde va: lo que produzcas aquí se integra directamente en *Programación de
Servicios y Procesos* (0490), *Desarrollo de Interfaces* (0489), *Programación
Multimedia* (0488) y, sobre todo, en el módulo de **Proyecto** (0493) y en la
FCT.

## Los seis resultados de aprendizaje

El curso está estructurado alrededor de seis resultados de aprendizaje (RA). No
son temas independientes: son seis tecnologías que resuelven **el mismo
problema** con compromisos distintos.

| RA | Denominación | Tecnología | Peso |
|----|--------------|------------|------|
| RA1 | Ficheros | `java.nio.file`, CSV, XML, JSON | 15 % |
| RA2 | Conectores y BD relacionales | JDBC, `JdbcTemplate` | 20 % |
| RA3 | Herramientas ORM | JPA / Hibernate | 22 % |
| RA4 | BD objeto-relacionales y OO | PostgreSQL, ObjectDB | 13 % |
| RA5 | BD documentales nativas | MongoDB | 15 % |
| RA6 | Componentes de acceso a datos | Maven multimódulo, patrones | 15 % |

RA6 es transversal: se trabaja **en cada** unidad, no al final.

## Metodología: un único proyecto, común e incremental

No hay ejercicios sueltos ni proyectos a elegir. Todo el grupo desarrolla el
**mismo** proyecto — [MULTAGAL](/tema-0/b-proyecto-multagal) — durante los tres
trimestres, ampliándolo unidad a unidad. Las razones:

1. **Es lo que pasa en la empresa.** Un desarrollador no elige el dominio sobre
   el que trabaja: recibe requisitos y se adapta.
2. **Evaluación comparable.** Mismo enunciado para todos ⇒ las rúbricas se
   aplican sobre entregables homogéneos, sin sesgo por dominios de dificultad
   desigual.
3. **Eficiencia en el aula.** El tiempo se dedica a resolver problemas técnicos
   comunes. Una depuración colectiva le sirve a todo el grupo.
4. **Progresión visible.** Verás la misma necesidad de negocio resuelta con
   ficheros, luego con JDBC, luego con un ORM, luego con tipos
   objeto-relacionales y luego con una base documental. Eso te enseña el
   **porqué** de cada tecnología, no sólo su sintaxis.

:::info[Principio rector]
Cada contenido se introduce **cuando el proyecto lo demanda**. Pasamos a JDBC
cuando los ficheros no soportan la concurrencia; pasamos a JPA cuando el código
JDBC se vuelve repetitivo; pasamos a MongoDB cuando el esquema rígido no admite
la variabilidad del atestado. Primero se descubre la limitación, después llega
la herramienta que la supera.
:::

### Andamiaje decreciente

| Evaluación | Qué se te entrega |
|------------|-------------------|
| 1ª | Estructura del proyecto y buena parte del código guiado. |
| 2ª | Sólo los requisitos y las pruebas que deben pasar. |
| 3ª | Una especificación funcional; el diseño es tuyo. |

### El error como contenido

Se introducen **defectos deliberados** (fugas de conexiones, problema N+1,
inyección SQL, condiciones de carrera) para que los diagnostiques y corrijas.
La depuración es una actividad programada, no un imprevisto.

## Modalidad de trabajo

El proyecto es **individual**. Cada alumno o alumna debe alcanzar personalmente
los seis RA. Sí hay actividades cooperativas: revisión de código entre iguales
y resolución colectiva de incidencias.

Tipología de sesiones:

| Formato | Peso |
|---------|------|
| Exposición y codificación en directo | 25 % |
| Taller guiado | 35 % |
| Trabajo autónomo sobre el proyecto | 30 % |
| Revisión, depuración colectiva y defensa | 10 % |

## Calendario de entregas

| Entrega | Unidades | RA evaluados | Momento |
|---------|----------|--------------|---------|
| **ENTREGA 1** | UD0, UD1, UD2, UD3 | RA1, RA2, RA6 parcial | Fin 1ª evaluación |
| **ENTREGA 2** | UD4, UD5 | RA3, RA4, RA6 parcial | Fin 2ª evaluación |
| **ENTREGA 3** | UD6, UD7 | RA5, RA6 completo | Mediados 3ª evaluación |
| **PRUEBA FINAL** | Todas | Todos | Fin 3ª evaluación |

Cada entrega se materializa con una **etiqueta (*tag*) en Git** en la fecha y
hora límite publicadas. No se admiten modificaciones posteriores a esa marca.

## Cómo se califica

**Peso de cada instrumento dentro de cada RA:**

| Instrumento | Peso |
|-------------|------|
| Entregable del proyecto (código, pruebas, documentación) | 45 % |
| Prueba práctica final de modificación | 30 % |
| Defensa oral y diario técnico | 15 % |
| Actividades de aula y pruebas escritas breves | 10 % |

**Condiciones para superar el módulo:**

1. La nota final es la media ponderada de los seis RA según la tabla de pesos.
2. Hay que sacar **≥ 4,0 sobre 10 en cada uno de los seis RA**. Uno por debajo
   impide aprobar, aunque la media dé.
3. Hay que sacar **≥ 4,0 en la prueba práctica final**.
4. Se supera con **≥ 5,0**, expresado de 1 a 10 sin decimales.

:::danger

[Condiciones formales de los entregables]

Un entregable que **no compile o no arranque** se califica con 0 en los
criterios procedimentales del hito. Además se exige:

- Ninguna credencial ni ruta absoluta escrita en el código fuente.
- Ninguna concatenación de parámetros en sentencias SQL.
- Cierre correcto de todos los recursos.
- Historial de commits que evidencie trabajo progresivo, no una entrega única
  el último día.
:::

## La prueba práctica final

Sustituye al examen teórico. Se te entrega un **pliego de nuevos requisitos**
que obliga a modificar sustancialmente **tu propio proyecto**, tal como quedó en
la Entrega 3. Individual y presencial, **4 horas** en dos sesiones consecutivas.

- **Permitido:** documentación oficial de Java, Spring, PostgreSQL y MongoDB, y
  tu propio código.
- **No permitido:** asistentes de IA, repositorios de terceros, comunicación con
  otras personas.

El pliego incluye una modificación por bloque tecnológico, de modo que cubre
necesariamente los seis RA.

## El diario técnico

Mantendrás en el repositorio un documento de **decisiones técnicas** (formato
ADR simplificado). Para cada hito registras: qué alternativa elegiste, qué otras
descartaste y por qué.

No es burocracia. Es el instrumento con el que se evalúan los criterios de tipo
valorativo (RA1.b, RA2.a, RA4.a, RA5.a, RA6.a), que no pueden acreditarse
mirando sólo el código. Formato mínimo por decisión:

```markdown
## ADR-003 — Uso de JdbcTemplate en lugar de JDBC puro

**Fecha:** 2025-11-14
**Estado:** aceptada

### Contexto
El CRUD de Expediente con JDBC puro requiere 40 líneas por método, la mayoría
de gestión de recursos y de mapeo de ResultSet.

### Decisión
Usar JdbcTemplate con RowMapper propios.

### Alternativas descartadas
- JDBC puro: control total, pero repetición insostenible.
- Salto directo a JPA: oculta el SQL, que en este hito queremos ver.

### Consecuencias
Se pierde control fino sobre el ResultSet; se gana en legibilidad y en
liberación automática de recursos.
```

## Asistentes de IA

No se prohíbe su uso en el trabajo autónomo, porque es práctica habitual en el
sector. Pero:

- Todo código incorporado al proyecto debe poder ser **explicado y justificado**
  por su autor. La defensa oral y el examen final se hacen **sin asistentes**.
- El uso de asistentes se declara en el diario técnico.
- Habrá una actividad específica de análisis crítico de código generado
  automáticamente, buscando errores sutiles: fugas de recursos, transacciones
  mal delimitadas, consultas ineficientes.

:::warning[Autoría]
El plagio entre entregas o el código ajeno no justificado ni comprendido supone
0 en el hito y obliga a acreditar los RA afectados **exclusivamente** mediante
la prueba práctica final.
:::

## Qué hay en esta unidad

La UD0 son 6 horas y no evalúa ningún RA: es el arranque. Al terminarla debes
tener el entorno funcionando, el proyecto Spring Boot arrancando, el modelo de
dominio esbozado y el repositorio publicado.

| Apartado | Contenido |
|----------|-----------|
| [a](/tema-0/a-presentacion-modulo) | Presentación del módulo y criterios de calificación |
| [b](/tema-0/b-proyecto-multagal) | El dominio MULTAGAL: entidades y reglas de negocio |
| [c](/tema-0/c-entorno-desarrollo) | JDK 25, Maven, IDE y Git |
| [d](/tema-0/d-docker-servicios-datos) | Docker y Docker Compose: los servicios de datos |
| [e](/tema-0/e-anatomia-spring-boot) | Anatomía de un proyecto Spring Boot |
| [f](/tema-0/f-inyeccion-dependencias) | IoC, inyección de dependencias y arquitectura en capas |
| [g](/tema-0/g-controlador-rest-validacion) | Controladores REST y validación de entrada |
| [h](/tema-0/h-flujo-git) | Flujo de trabajo con Git |
| [i](/tema-0/i-flujo-mvc) | Flujo MVC de Spring Boot |
| [j](/tema-0/j-actividades) | Actividades A0.1 – A0.6 |
| [k](/tema-0/k-chuleta-anotaciones) | Chuleta de anotaciones: Lombok y Spring Framework |
| [l](/tema-0/l-mappers-mapstruct) | Mappers con MapStruct |

<NextPageButton to="/tema-0/b-proyecto-multagal">
El proyecto MULTAGAL
</NextPageButton>
