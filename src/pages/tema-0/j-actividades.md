---
title: 'Actividades de la UD0'
description: Actividades A0.1 a A0.5 del arranque del proyecto MULTAGAL, con sus criterios de aceptación.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Actividades de la UD0

Seis actividades, 7 horas. Al terminarlas debes tener el entorno verificado, el
proyecto arrancando, el dominio esbozado, una vertical mínima probada y
expuesta por HTTP, y el repositorio publicado con su primera etiqueta.

:::info[Entrega]

El conjunto de la UD0 forma parte de la **ENTREGA 1** y se materializa como una
rama `hito/ud0-arranque` fusionada en `main`. No evalúa ningún RA directamente,
pero sin ella no se puede abordar ninguna unidad posterior.

:::

---

## A0.1 — Instalación y verificación del entorno

**Objetivo:** disponer de un entorno de desarrollo completo y reproducible.

### Tareas

1. Instala JDK 25, Maven, Git, Docker y tu IDE, siguiendo el
   [apartado c](/tema-0/c-entorno-desarrollo).
2. Configura `user.name` y `user.email` en Git con tu nombre real.
3. Crea la cuenta en GitHub, GitLab o Bitbucket y registra tu clave SSH.
4. Ejecuta la checklist de arranque completa y **guarda la salida**.

### Entregable

Un fichero `docs/entorno.md` en el repositorio con la salida literal de:

```bash
java -version
mvn -version
git --version
docker --version
docker compose version
```

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `java -version` responde 25.x |
| 2 | `mvn -version` confirma que usa el JDK 25 |
| 3 | todos los comandos han encontrado versiones validas |

---

## A0.2 — Generación del esqueleto y primer arranque

**Objetivo:** comprender la estructura de un proyecto Spring Boot.

### Tareas

1. Genera el proyecto con Spring Initializr según la configuración del
   [apartado e](/tema-0/e-anatomia-spring-boot#generar-el-proyecto-spring-initializr).
2. Crea la estructura de paquetes de la
   [arquitectura en capas](/tema-0/f-inyeccion-dependencias#arquitectura-en-capas-del-proyecto):
   `config`, `domain.model`, `domain.exception`, `repository`, `service`,
   `controller`, `dto`.
3. Crea el `compose.yaml` con los tres servicios de datos, más `.env` y
   `.env.example`.
4. Configura `application.yml` y los perfiles `dev` y `prod`.
5. Arranca la aplicación y comprueba que Tomcat responde en el 8080.
6. Levanta los tres contenedores y verifica que están `healthy`.
7. Ejecuta `./mvnw spring-boot:run -Dspring-boot.run.arguments=--debug` y
   localiza en el *Condition Evaluation Report* **tres** autoconfiguraciones que
   se hayan activado y **una** que no, con su motivo.

### Entregable

- Proyecto que arranca con `./mvnw spring-boot:run`.
- `compose.yaml` funcional.
- En `docs/adr/0000-autoconfiguracion.md`: las cuatro autoconfiguraciones
  localizadas, con la explicación de por qué se activaron o no.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | `./mvnw clean package` termina sin errores |
| 2 | La aplicación arranca y registra el perfil `dev` activo |
| 3 | `docker compose ps` muestra los tres servicios `healthy` |
| 4 | No hay ninguna contraseña escrita en `compose.yaml` ni en el código |
| 5 | El informe de autoconfiguración está correctamente interpretado |

---

## A0.3 — Modelos de dominio de MULTAGAL

**Objetivo:** traducir el modelo conceptual a tipos Java.

### Tareas

Crea en `domain/model` los tipos que representan el dominio descrito en el
[apartado b](/tema-0/b-proyecto-multagal). Todavía **sin persistencia**: son
POJOs o `record`, sin anotaciones de JPA ni de Jackson.

Como mínimo:

- `Conductor`, `PermisoConduccion`, `Vehiculo`, `Titularidad`
- `Infraccion`, `Denuncia`, `Expediente`
- `Notificacion`, `Alegacion`, `Resolucion`, `Pago`, `Radar`
- Los enumerados: `Gravedad`, `EstadoExpediente`, `EstadoPermiso`,
  `ResultadoNotificacion`
- Los objetos valor: `Coordenadas`, `DireccionPostal`, `Matricula`
- Todas las entidades identificadas por un `UUID id`, no por su clave natural
  (`numeroExpediente`, `matricula` o `documentoIdentidad` son atributos
  ordinarios)

### Decisiones que debes tomar y justificar

1. **`record` o clase.** ¿Cuáles de estos tipos son inmutables por naturaleza y
   cuáles necesitarán mutabilidad más adelante? Ten en cuenta que en la UD4 JPA
   exige constructor sin argumentos y campos mutables: ¿afecta eso a la decisión
   de ahora?
2. **Tipos de los importes.** `BigDecimal`, nunca `double`. Explica por qué en el
   ADR.
3. **Tipos de las fechas.** `LocalDate` frente a `LocalDateTime`: ¿cuál para la
   fecha de comisión de la infracción? ¿Y para el plazo de pronto pago?
4. **Objetos valor frente a `String`.** ¿`Matricula` debe ser un tipo propio o
   basta con un `String`? Argumenta.
5. **Nulos.** ¿Qué campos pueden ser `null` legítimamente? Recuerda que
   `Denuncia.conductor` lo es cuando la capta un radar.

### Entregable

- Los tipos del dominio en `domain/model`.
- `docs/adr/0001-modelado-del-dominio.md` con las cinco decisiones anteriores,
  sus alternativas descartadas y el porqué.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | Están modeladas las doce entidades del dominio |
| 2 | Los importes usan `BigDecimal` y las fechas la API `java.time` |
| 3 | Los enumerados sustituyen a los `String` mágicos |
| 4 | Ningún tipo del paquete `domain` importa nada de Spring |
| 5 | El ADR justifica las decisiones, no sólo las describe |

:::tip
[Sobre el criterio 4]

Es la regla de dependencia del [apartado
f](/tema-0/f-inyeccion-dependencias#la-regla-de-dependencia). Si tu
`Expediente` importa `org.springframework.*`, el dominio ha dejado de ser el
núcleo estable. Compruébalo mirando los `import`.

:::

---

## A0.4 — Primer servicio inyectado y su prueba unitaria

**Objetivo:** aplicar inyección de dependencias y probar sin infraestructura.

### Tareas

1. Define la interfaz `RepositorioExpedientes` en `repository`, con al menos:
   `crear`, `actualizar`, `buscarPorId(UUID id)`, `buscarPorNumeroExpediente`,
   `buscarTodos` y `buscarPorEstado`.
2. Implementa `RepositorioExpedientesMemoria` con un `ConcurrentHashMap`,
   anotada con `@Repository`. Es una implementación provisional: en la UD3 la
   sustituirá una JDBC **sin tocar la capa de servicio**.
3. Crea `ServicioExpedientes` en `service`, con **inyección por constructor**,
   que ofrezca:
   - `obtener(String numeroExpediente)` — lanza `NoEncontradoException` si no
     existe.
   - `registrar(ExpedienteRequest e)` — asigna un `UUID` nuevo y rechaza un
     `numeroExpediente` duplicado.
   - `calcularImporteConBonificacion(UUID expediente, LocalDate fechaPago)` —
     aplica la [regla R1](/tema-0/b-proyecto-multagal#r1--bonificación-por-pronto-pago):
     50 % si el pago se produce dentro de los 20 días naturales siguientes a la
     notificación.
4. Crea la jerarquía inicial de excepciones en `domain/exception`:
   `MultagalException` como raíz, y `NoEncontradoException` y `YaExistenteException` colgando
   de ella.
5. Escribe las pruebas unitarias de `ServicioExpedientes` con **Mockito**, sin
   contexto de Spring y sin base de datos. Cubre:
   - El caso favorable de cada método.
   - El expediente inexistente.
   - El número duplicado.
   - La bonificación **en el día 20** y **en el día 21** (los límites).

### Entregable

Código y pruebas, con `./mvnw test` en verde.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | La inyección es por constructor y los campos son `final` |
| 2 | El servicio depende de la **interfaz**, no de la implementación |
| 3 | Las pruebas se ejecutan sin Docker y sin `@SpringBootTest` |
| 4 | Están cubiertos los casos límite del plazo de bonificación |
| 5 | El servicio no tiene estado mutable |

:::warning
[Los límites importan]

«Dentro de los 20 días naturales siguientes» — ¿el día 20 entra o no entra? Esa
ambigüedad del enunciado la tienes que resolver tú, documentarla y probarla. Los
errores por uno son la fuente más común de defectos en cálculos de plazos, y en
un procedimiento sancionador real tienen consecuencias jurídicas.

:::

---

## A0.5 — Controladores REST para el servicio de expedientes

**Objetivo:** exponer por HTTP el servicio construido en A0.4, con validación
de entrada por anotaciones.

### Tareas

1. Crea en `dto` los DTOs de entrada y salida necesarios para el recurso
   `expedientes`: como mínimo `NuevoExpedienteRequest` (número de expediente, `denunciaId`
   e instructor, o equivalente) y
   `ExpedienteResponse`. Sigue el ejemplo del [apartado
   g](/tema-0/g-controlador-rest-validacion#el-dto-de-entrada).
2. Anota los campos del DTO de entrada con las restricciones de [Bean
   Validation](/tema-0/g-controlador-rest-validacion#anotaciones-de-bean-validation-más-usadas)
   que correspondan a cada campo (número de expediente obligatorio y con
   formato válido, denuncia de origen obligatoria...).
3. Crea `ExpedientesController` en `controller`, con inyección por
   constructor de `ServicioExpedientes`, y expón como mínimo:
   - `POST /api/expedientes` — crea un expediente a partir del DTO validado
     con `@Valid`. Responde `201 Created`.
   - `GET /api/expediente/{numero}` (con `numero` de tipo `string`) — devuelve el expediente o dispara la
     excepción de «no encontrado».
4. Crea un `@RestControllerAdvice` que traduzca:
   - los errores de validación del DTO a `400 Bad Request` con el detalle de
     cada campo, siguiendo el [ejemplo del apartado
     g](/tema-0/g-controlador-rest-validacion#un-manejador-de-errores-centralizado);
   - `ExpedienteNoEncontradoException` a `404 Not Found`.
5. Comprueba manualmente con `curl` o el cliente REST de tu IDE:
   - una petición válida (`201`);
   - una petición con el número de expediente en blanco y sin `denunciaId`
     (`400`, con los dos campos en el cuerpo del error);
   - una consulta de un expediente inexistente (`404`).

### Entregable

Código del controlador, los DTOs y el manejador de errores, con la
aplicación arrancando y respondiendo a las tres comprobaciones del punto 5.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El controlador depende del servicio por **interfaz de `service`**, nunca del repositorio directamente |
| 2 | El DTO de entrada usa Bean Validation y el método del controlador lleva `@Valid` |
| 3 | Una petición inválida responde `400` con el detalle de qué campo falló y por qué |
| 4 | Un expediente inexistente responde `404`, no `500` ni `200` con cuerpo vacío |
| 5 | El DTO de entrada **no valida nada que dependa del repositorio** (duplicados, existencia) |

:::warning
[Sobre el criterio 5]

El número de expediente duplicado ya lo rechaza `ServicioExpedientes` desde
la A0.4: esa comprobación **no se repite** en el DTO. Un DTO sólo valida la
forma del dato; que ya exista es lógica de negocio y vive en el `service`.
Repasa la distinción en el [apartado
g](/tema-0/g-controlador-rest-validacion#qué-no-va-en-la-validación-del-dto)
si tienes dudas.

:::

---

## A0.6 — Publicación del repositorio y configuración de ramas

**Objetivo:** establecer el flujo de trabajo profesional del curso.

### Tareas

1. Crea el `.gitignore` y el `.gitattributes` del
   [apartado h](/tema-0/h-flujo-git) **antes** del primer commit.
2. Inicializa el repositorio, crea el remoto **privado** y añade al profesorado
   como colaborador.
3. Redacta un `README.md` con: descripción del proyecto, requisitos previos,
   cómo levantar los servicios, cómo arrancar la aplicación y cómo ejecutar las
   pruebas.
4. Reorganiza el trabajo de A0.2–A0.5 en **commits con sentido**, siguiendo
   Conventional Commits. No vale un único commit con todo.
5. Trabaja en la rama `hito/ud0-arranque`, abre un pull request hacia `main`,
   autorrevísalo leyendo el diff completo y fusiónalo con `--no-ff`.
6. Etiqueta el resultado como `ud0-completado` y súbela.

### Entregable

Repositorio publicado, con historial, PR fusionado y etiqueta.

### Criterios de aceptación

| # | Criterio |
|---|----------|
| 1 | El repositorio no contiene `target/`, `.idea/` ni `.env` |
| 2 | El historial tiene **al menos 8 commits** con mensajes convencionales |
| 3 | Cada commit compila y las pruebas pasan |
| 4 | El PR está fusionado con `--no-ff` y la etiqueta `ud0-completado` apunta a `main` |
| 5 | El `README.md` permite arrancar el proyecto a alguien que lo clona por primera vez |
| 6 | `docs/adr/` contiene al menos dos decisiones registradas |

:::tip

[Prueba el criterio 5 de verdad]

Clona tu propio repositorio en otra carpeta y arráncalo siguiendo **sólo** lo que
dice tu README, sin usar nada que sepas de memoria. Si te falta un paso, tu
README está incompleto — y es exactamente lo que le pasará a quien lo corrija.

:::

---

## Autoevaluación de la unidad

Antes de dar la UD0 por cerrada, comprueba que sabes responder a esto **sin
consultar los apuntes**. Son el tipo de preguntas de la defensa oral.

1. ¿Qué diferencia hay entre una imagen y un contenedor? ¿Y entre un contenedor y
   una máquina virtual?
2. ¿Por qué `docker compose down -v` es peligroso?
3. ¿Qué hace exactamente `@SpringBootApplication`? Nombra sus tres anotaciones.
4. Tienes un `@Service` en el paquete `es.otro.servicio` y la aplicación en
   `es.edu.multagal`. ¿Qué ocurre al arrancar y por qué?
5. ¿Por qué se prefiere la inyección por constructor a la inyección por campo?
   Da dos razones.
6. ¿Qué añade `@Repository` que no añada `@Component`?
7. ¿Por qué un `@Service` no debe tener campos mutables?
8. Explica el orden de precedencia de la configuración en Spring Boot.
9. ¿Qué es un *starter* y qué relación tiene con la autoconfiguración?
10. Enumera las reglas de dependencia entre las capas del proyecto. ¿Puede
    `controller` llamar a `repository`?
11. ¿Por qué el paquete `domain` no debe importar nada de Spring?
12. ¿Por qué los importes van en `BigDecimal` y no en `double`?
13. ¿Qué evidencia aporta el historial de commits a la evaluación?
14. ¿Qué diferencia hay entre una etiqueta y una rama?
15. ¿Qué diferencia hay entre validar un DTO con `@Valid` y validar una regla
    de negocio como «el NIF/NIE ya está registrado»? ¿Por qué la segunda no puede
    resolverse en el DTO?
16. ¿Qué ocurre si un método de controlador recibe `@RequestBody` sin
    `@Valid`?

## Ampliación (opcional)

Para quien avance más rápido. No es exigible, pero se valora en el nivel de
sobresaliente:

- **Actuator.** Añade `spring-boot-starter-actuator` y explora `/actuator/health`,
  `/actuator/beans` y `/actuator/configprops`. `/actuator/beans` te muestra el
  contenido real del `ApplicationContext`: es la mejor forma de ver la inyección
  de dependencias.
- **Integración continua.** Configura un workflow de GitHub Actions que ejecute
  `./mvnw test` en cada push. Que el PR no se pueda fusionar con las pruebas en
  rojo.
- **Generador de datos sintéticos.** Un componente que produzca conductores,
  vehículos y denuncias verosímiles y **seudonimizados**. Lo agradecerás en la
  UD3, cuando necesites volumen para que las consultas sean interesantes.
- **Diagrama del dominio.** Elabora el diagrama entidad-relación completo de
  MULTAGAL e inclúyelo en el README. Te servirá de guía durante todo el curso.

<NextPageButton to="/tema-0/k-chuleta-anotaciones">
Chuleta de anotaciones: Lombok y Spring Framework
</NextPageButton>
