---
title: 'Controladores REST y validación de entrada por anotaciones'
description: Exponer un servicio como endpoint REST, DTOs de entrada, validación con Bean Validation y el manejo del error 400.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Controladores REST y validación de entrada

En [Inversión de control e inyección de
dependencias](/tema-0/f-inyeccion-dependencias) construiste una vertical
completa hasta `service/`. Falta la última capa: **el controlador**, el punto
de entrada HTTP que expone ese servicio al exterior. Este apartado cierra el
ciclo y añade algo que no puede faltar en ningún endpoint que reciba datos de
fuera: **validar la entrada antes de que llegue a la lógica de negocio**.

:::info
[Por qué esto no es opcional]

Un controlador que confía ciegamente en el JSON que le llega traslada el
problema a la capa de servicio, o peor, a la base de datos. Validar en el
borde de la aplicación —en el DTO de entrada— es más barato que descubrir un
`NullPointerException` tres capas más abajo, y es el único sitio donde tiene
sentido devolver un error HTTP 400 con un mensaje útil para quien consume la
API.

:::

## El DTO de entrada

Un DTO (*Data Transfer Object*) es la forma que tienen los datos **al cruzar
la frontera de la API**. No es la entidad del dominio: es un tipo aparte, en
el paquete `dto`, pensado para lo que el cliente puede y debe enviar.

```java
// dto/NuevoExpedienteRequest.java
package es.edu.multagal.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record NuevoExpedienteRequest(

        @NotBlank(message = "El número de expediente es obligatorio")
        String numero,

        @NotNull(message = "El importe es obligatorio")
        @DecimalMin(value = "0.0", inclusive = false,
                message = "El importe debe ser mayor que 0")
        BigDecimal importe,

        @NotBlank
        @Pattern(regexp = "\\d{4}[A-Z]{3}", message = "Matrícula con formato inválido")
        String matricula
) { }
```

:::tip
[Por qué un `record` distinto de la entidad de dominio]

`Expediente` (el tipo de `domain/model`) puede tener campos que el cliente
nunca debe rellenar —un `id` generado, un `estado` que decide el servicio— o
puede necesitar forma distinta según la operación (crear no es lo mismo que
actualizar). Separar el DTO de la entidad evita anotar el dominio con
restricciones que sólo tienen sentido en un endpoint concreto, y evita que
cambios en la API obliguen a tocar el modelo de negocio.

:::

## El controlador

```java
// controller/ExpedientesController.java
package es.edu.multagal.controller;

import es.edu.multagal.dto.NuevoExpedienteRequest;
import es.edu.multagal.service.ServicioExpedientes;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/expedientes")
public class ExpedientesController {

    private final ServicioExpedientes servicio;

    public ExpedientesController(ServicioExpedientes servicio) {
        this.servicio = servicio;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExpedienteResponse crear(@Valid @RequestBody NuevoExpedienteRequest peticion) {
        var expediente = servicio.registrar(peticion.numero(), peticion.importe(),
                peticion.matricula());
        return ExpedienteResponse.desde(expediente);
    }

    @GetMapping("/{numero}")
    public ExpedienteResponse obtener(@PathVariable String numero) {
        return ExpedienteResponse.desde(servicio.obtener(numero));
    }
}
```

Fíjate en tres anotaciones que hacen todo el trabajo:

| Anotación | Qué hace |
|-----------|----------|
| `@RequestBody` | Deserializa el JSON del cuerpo de la petición al tipo `NuevoExpedienteRequest`. |
| `@Valid` | Le dice a Spring que, **antes** de ejecutar el método, valide el objeto según las anotaciones de Bean Validation de sus campos. |
| `@PathVariable` | Vincula el fragmento `{numero}` de la ruta al parámetro `numero`. |

Si `@Valid` detecta una violación, el método `crear` **no llega a ejecutarse**:
Spring lanza una `MethodArgumentNotValidException` antes de entrar en el
cuerpo del controlador.

:::danger
[Sin `@Valid`, las anotaciones del DTO no hacen nada]

`@NotBlank`, `@NotNull` o `@Pattern` en un `record` son sólo metadatos hasta
que algo las evalúa. Sin `@Valid` en la firma del método, Spring deserializa
el JSON igual y las restricciones se ignoran por completo. Es el error más
común al introducir validación: el código compila, el IDE no avisa, y el
`NuevoExpedienteRequest("", null, "")` pasa sin problema.

:::

## Qué devuelve Spring por defecto

Sin nada más, una petición que incumple una restricción produce un **400 Bad
Request** con un cuerpo generado automáticamente:

```json
{
  "timestamp": "2026-09-14T10:15:30.000+00:00",
  "status": 400,
  "error": "Bad Request",
  "path": "/api/expedientes"
}
```

Es correcto pero poco útil: no dice **qué** campo falló ni **por qué**. Para
eso se captura la excepción con un manejador global.

## Un manejador de errores centralizado

```java
// controller/ManejadorErrores.java
package es.edu.multagal.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> manejarValidacion(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errores.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
    }
}
```

Con esto, la misma petición inválida responde:

```json
{
  "numero": "El número de expediente es obligatorio",
  "importe": "El importe debe ser mayor que 0"
}
```

| Anotación | Uso |
|-----------|-----|
| `@RestControllerAdvice` | Aplica sus `@ExceptionHandler` a **todos** los controladores de la aplicación, no sólo a uno. |
| `@ExceptionHandler(Tipo.class)` | Marca el método que se ejecuta cuando ese tipo de excepción escapa de un controlador. |
| `ResponseEntity<T>` | Permite controlar explícitamente el código de estado y el cuerpo de la respuesta, en vez de dejar que Spring use uno por defecto. |

:::note
[¿Y las excepciones de negocio, como `ExpedienteNoEncontradoException`?]

El mismo `@RestControllerAdvice` es el sitio para traducirlas a un código
HTTP con sentido (`404` para «no encontrado», `409` para «número duplicado»).
Añade un `@ExceptionHandler` por cada una. Es una buena costumbre no dejar
escapar nunca una excepción de negocio como un `500` genérico.

:::

## Anotaciones de Bean Validation más usadas

| Anotación | Se aplica a | Comprueba |
|-----------|-------------|-----------|
| `@NotNull` | cualquier tipo | No es `null`. |
| `@NotBlank` | `String` | No es `null`, ni vacío, ni sólo espacios. |
| `@NotEmpty` | `String`, colección | No es `null` ni está vacío (sí admite espacios en un `String`). |
| `@Size(min=, max=)` | `String`, colección | Longitud o tamaño dentro de un rango. |
| `@Min` / `@Max` | numéricos | Valor mínimo/máximo (inclusive). |
| `@DecimalMin` / `@DecimalMax` | `BigDecimal`, `BigInteger` | Igual que `@Min`/`@Max` pero con precisión decimal y `inclusive` configurable. |
| `@Positive` / `@PositiveOrZero` | numéricos | Estrictamente positivo / positivo o cero. |
| `@Pattern(regexp=)` | `String` | Coincide con una expresión regular. |
| `@Email` | `String` | Tiene forma de correo electrónico. |
| `@Past` / `@Future` | fechas | Es anterior / posterior al momento actual. |

:::tip
[Mensajes de error explícitos]

El atributo `message` de cada anotación es el texto que verá quien consuma la
API cuando falle esa restricción en concreto. Sin él, Spring usa un mensaje
genérico en inglés. En un módulo donde la API la vas a defender oralmente,
escribe siempre el mensaje.

:::

## Validación anidada y en colecciones

Si un DTO contiene otro objeto o una lista, `@Valid` no baja de nivel por sí
solo: hay que pedirlo explícitamente.

```java
public record NuevaDenunciaRequest(
        @NotBlank String matricula,

        @Valid @NotNull                 // ← @Valid también en el campo anidado
        DireccionRequest direccion,

        @Valid @NotEmpty
        List<@Valid InfraccionRequest> infracciones
) { }
```

Sin el `@Valid` en `direccion`, Spring valida que el campo no sea `null`
(porque hay `@NotNull`) pero **no entra** a comprobar las restricciones de
`DireccionRequest`.

## Relación con `@ConfigurationProperties` y `@Validated`

Ya viste `@Validated` en [Configuración
externalizada](/tema-0/f-inyeccion-dependencias#configuración-externalizada)
aplicado a propiedades de `application.yml`. Es el mismo motor de Bean
Validation, pero aplicado en dos momentos distintos:

| | `@Valid` en un controlador | `@Validated` en `@ConfigurationProperties` |
|---|---|---|
| Se evalúa | En cada petición HTTP | Una vez, al arrancar la aplicación |
| Si falla | 400 Bad Request al cliente | La aplicación **no arranca** |
| Objetivo | Datos que vienen del exterior, en tiempo de ejecución | Configuración del propio sistema, antes de servir tráfico |

## Qué NO va en la validación del DTO

Bean Validation comprueba la **forma** del dato: que no esté vacío, que tenga
el formato correcto, que esté en un rango. No comprueba su **significado en
el dominio**.

```java
public record NuevoConductorRequest(
        @NotBlank
        @Pattern(regexp = "\\d{8}[A-Z]", message = "DNI con formato inválido")
        String dni,                          // ← forma: sí es validación del DTO

        @NotBlank String nombre
) { }
```

`@Pattern` comprueba que el DNI **tiene forma** de DNI. Comprobar que **ese
DNI no está ya registrado** es otra cosa completamente distinta: requiere
consultar el repositorio, y eso es lógica de negocio.

:::danger
[La validación del DTO no conoce el repositorio]

Un `@Valid` se resuelve **antes** de que el controlador reciba el objeto, sin
tocar la base de datos ni ningún otro bean. Bean Validation está diseñado
para restricciones que se comprueban **con el dato aislado**: no puede, y no
debe, decidir si un DNI ya existe, si un número de expediente está duplicado
o si un vehículo tiene ya una sanción pendiente. Esas comprobaciones viven en
el `service`, exactamente igual que la validación de negocio que ya
implementaste en la actividad
[A0.4](/tema-0/i-actividades#a04--primer-servicio-inyectado-y-su-prueba-unitaria)
para el número de expediente duplicado.

```java
@Service
public class ServicioConductores {

    private final RepositorioConductores repositorio;

    public Conductor registrar(String dni, String nombre) {
        if (repositorio.existePorDni(dni)) {
            throw new ConductorYaRegistradoException(dni);   // ← lógica de negocio
        }
        return repositorio.guardar(new Conductor(dni, nombre));
    }
}
```

:::

La regla práctica para decidir dónde va cada comprobación:

| Pregunta | Dónde se resuelve |
|----------|--------------------|
| ¿Tiene el campo el formato/tamaño/rango correcto? | DTO, con Bean Validation (`@Pattern`, `@Size`, `@Min`...) |
| ¿Existe ya ese DNI / número de expediente / matrícula? | `service`, consultando el repositorio |
| ¿Es coherente con otros datos del sistema (saldo, estado, plazos)? | `service` |
| ¿Depende de una regla del dominio (por ejemplo, la [regla R1 de bonificación](/tema-0/b-proyecto-multagal#r1--bonificación-por-pronto-pago))? | `service` |

Mezclar ambas cosas tiene un coste concreto: si un repositorio de
`@NotBlank` empezara a consultar la base de datos, cada validación de cada
DTO abriría una conexión, y una prueba de un DTO —que debería ser
instantánea y sin infraestructura— pasaría a necesitar una base de datos
levantada.

## Errores frecuentes

:::note
[«El campo llega vacío y aun así no salta ningún error»]

Comprueba que el método del controlador lleva `@Valid` delante de
`@RequestBody`. El orden de las anotaciones en el parámetro no importa, pero
la ausencia de `@Valid` sí: sin ella el `record` se deserializa igual y las
restricciones no se evalúan nunca.

:::

:::note
[«400 Bad Request pero sin ningún detalle en el cuerpo»]

Es el comportamiento por defecto de Spring sin un `@RestControllerAdvice`
propio. Añade el manejador de `MethodArgumentNotValidException` de este
apartado.

:::

:::note
[«Un campo anidado con datos inválidos no da error»]

Falta `@Valid` en el campo que contiene el objeto anidado, o en el tipo
genérico de la colección (`List<@Valid Tipo>`). `@NotNull` y `@Valid` no son
lo mismo: el primero comprueba que el campo exista, el segundo entra a
validar su contenido.

:::

<NextPageButton to="/tema-0/h-flujo-git">
Flujo de trabajo con Git
</NextPageButton>
