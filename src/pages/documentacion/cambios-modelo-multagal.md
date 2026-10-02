---
title: 'Cambios en el modelo MULTAGAL'
description: Resumen de los cambios estructurales introducidos en el modelo de dominio de MULTAGAL y guía para aplicarlos a tu proyecto actual.
---

# Cambios en el modelo MULTAGAL

Este documento resume los cambios **estructurales** que se han hecho al modelo de
dominio descrito en [El proyecto MULTAGAL](/tema-0/b-proyecto-multagal) y explica
cómo aplicarlos a las entidades que ya tienes creadas. La versión de referencia es
siempre la de esa página; aquí tienes sólo la diferencia entre el modelo anterior y
el nuevo.

## Resumen

| # | Cambio | Entidades afectadas | Esfuerzo |
|---|--------|---------------------|----------|
| 1 | Un permiso por clase | `PermisoConduccion`, regla R3 | Alto |
| 2 | El denunciante se modela mejor | `Denuncia`, nueva regla R6 | Medio |
| 3 | La fecha de notificación se deduce | `Expediente` | Bajo |
| 4 | El pronto pago pasa por `RESUELTO` | `Expediente`, `Resolucion`, regla R1 | Medio |
| 5 | Los pagos son totales | `Pago` | Bajo |
| 6 | Tipos enumerados cerrados | Varias | Medio |
| 7 | Restricciones de unicidad | Varias | Bajo |
| 8 | Se elimina la regla de identificación del conductor | — | Bajo |

## 1. Un permiso por cada clase

**Antes:** `PermisoConduccion` tenía un atributo `clases` con una lista de clases
(A, B, C…), y compartía fechas y estado para todas ellas.

**Ahora:** cada permiso tiene **una única clase**. Un conductor con las clases B y
C tiene **dos** registros de `PermisoConduccion`, cada uno con sus propias fechas
de expedición y caducidad y su propio estado.

**Por qué:** cada clase caduca en su fecha y puede suspenderse por separado. Una
columna con varios valores además rompe la primera forma normal y complica el
mapeo a tablas.

**Qué hacer:**

- Sustituye `List<ClasePermiso> clases` por `ClasePermiso clase`.
- La relación `Conductor` → `PermisoConduccion` sigue siendo `1 : 0..N`.
- `numeroPermiso` **no es único**: se repite en todos los permisos del mismo
  conductor.
- Si ya tenías datos de prueba, desdóblalos: una fila por cada clase.

### Efecto sobre la regla R3

Un conductor con saldo cero queda con **todos sus permisos suspendidos**. La
transacción ya no es un `UPDATE` del saldo más un `UPDATE` del permiso: ahora
actualiza el saldo y **cada uno** de los permisos del conductor, y todo debe
confirmarse o deshacerse junto.

## 2. El denunciante en `Denuncia`

**Antes:** un único texto, `agenteODispositivo`, mezclaba al agente y al
dispositivo.

**Ahora:**

| Atributo | Descripción |
|----------|-------------|
| `tipoDenunciante` | Enumerado `TipoDenunciante`: `AGENTE` o `RADAR`. |
| `identificadorAgente` | Placa o identificador del agente. Nulo si la capta un radar. |
| `velocidadMedida` | **Nuevo.** Velocidad registrada, en km/h. Nula si no es radar. |
| `radarId` | Sin cambios. Nulo si no es radar. |

**Qué hacer:**

- Renombra `agenteODispositivo` a `identificadorAgente` y hazlo opcional.
- Añade `int velocidadMedida`, también opcional (usa `Integer` si lo mapeas como
  objeto, para poder representar el nulo).
- Aplica la regla R6 (más abajo).

### Nueva regla R6 — Coherencia del denunciante

| `tipoDenunciante` | Obligatorios | Deben ser nulos |
|-------------------|--------------|-----------------|
| `AGENTE` | `identificadorAgente` | `radarId`, `velocidadMedida` |
| `RADAR` | `radarId`, `velocidadMedida` | `identificadorAgente` |

Es una validación que **cruza varios campos** de la misma entidad. Impleméntala en
la capa de servicio (o con una validación a nivel de clase) y, cuando llegues a la
base de datos, refuérzala con una restricción `CHECK`.

## 3. `Expediente` ya no guarda `fechaNotificacion`

**Antes:** `Expediente` tenía un atributo `fechaNotificacion`.

**Ahora:** se ha eliminado. Esa fecha ya existe en `Notificacion` y guardarla dos
veces permite que se contradigan.

**Qué hacer:**

- Elimina el atributo.
- Donde necesites la fecha (por ejemplo, para el plazo de 20 días de R1), obténla
  de la notificación con acuse del expediente.

El resto de fechas del expediente (`fechaIncoacion`, `fechaLimitePrescripcion`,
`fechaResolucion`, `fechaFirmeza`) **se mantienen**: cada una registra cuándo
entró el expediente en ese estado.

## 4. El pronto pago pasa por `RESUELTO`

**Antes:** el pronto pago llevaba el expediente directamente de
`EN_PLAZO_ALEGACIONES` a `FIRME`, sin ninguna `Resolucion`. Eso dejaba sin
resolver cómo detraer los puntos, porque la regla R2 exige una resolución firme.

**Ahora:** el pronto pago lleva el expediente a `RESUELTO`, con una `Resolucion`
de tipo `SANCION`, que después adquiere firmeza como cualquier otra. Todo
expediente que llega a `FIRME` ha pasado antes por `RESUELTO`.

**Qué hacer:**

- Cambia la transición en tu máquina de estados.
- Al registrar un pago con pronto pago, crea también la `Resolucion` de tipo
  `SANCION` en la misma transacción.
- Revisa la regla R1: pagar cierra la vía de alegación **y** genera la resolución.

## 5. Los pagos son totales

**Antes:** la descripción de `Pago` hablaba de abonos totales o parciales.

**Ahora:** cada expediente se liquida con un único pago. `TipoPago` sólo admite
`TOTAL` y `PRONTO_PAGO`.

**Qué hacer:**

- No implementes pagos parciales.
- **Mantén** la relación `Expediente` → `Pago` como `1 : 0..N` y `TipoPago` como
  enumerado. El sistema queda preparado para añadir `PARCIAL` más adelante sin
  cambiar el esquema.
- `porcentajeBonificacion` se mantiene: guarda el porcentaje realmente aplicado,
  porque el valor de R1 (50 %) podría cambiar con la ley.

## 6. Tipos enumerados cerrados

Los atributos con un conjunto cerrado de valores se modelan ahora como `enum` con
estos valores:

| Tipo | Se usa en | Valores |
|------|-----------|---------|
| `ClasePermiso` | `PermisoConduccion.clase` | `AM`, `A1`, `A2`, `A`, `B`, `BE`, `C1`, `C`, `D1`, `D` |
| `EstadoPermiso` | `PermisoConduccion.estado` | `VIGENTE`, `SUSPENDIDO`, `CADUCADO`, `REVOCADO` |
| `TipoVehiculo` | `Vehiculo.tipo` | `TURISMO`, `MOTOCICLETA` |
| `EstadoItv` | `Vehiculo.estadoItv` | `VIGENTE`, `CADUCADA`, `DESFAVORABLE` |
| `TipoRadar` | `Radar.tipo` | `FIJO`, `MOVIL` |
| `Gravedad` | `Infraccion.gravedad` | `LEVE`, `GRAVE`, `MUY_GRAVE` |
| `TipoDenunciante` | `Denuncia.tipoDenunciante` | `AGENTE`, `RADAR` |
| `EstadoExpediente` | `Expediente.estado` | `INCOADO`, `NOTIFICADO`, `EN_PLAZO_ALEGACIONES`, `ALEGADO`, `RESUELTO`, `FIRME`, `SOBRESEIDO`, `PRESCRITO` |
| `ResultadoNotificacion` | `Notificacion.resultado` | `ENTREGADA`, `AUSENTE`, `REHUSADA`, `DIRECCION_DESCONOCIDA` |
| `EstadoAlegacion` | `Alegacion.estado` | `PENDIENTE`, `ESTIMADA`, `DESESTIMADA` |
| `TipoResolucion` | `Resolucion.tipo` | `SANCION`, `SOBRESEIMIENTO` |
| `TipoPago` | `Pago.tipo` | `TOTAL`, `PRONTO_PAGO` |

**Qué hacer:** sustituye los `String` por el `enum` correspondiente.

Ten en cuenta que `TipoResolucion` ya **no** incluye una estimación de
alegaciones como tipo propio: estimar las alegaciones lleva al sobreseimiento, así
que es un `SOBRESEIMIENTO`.

## 7. Restricciones de unicidad

Los `id` son claves sustitutas, pero los campos de negocio siguen siendo únicos:

| Entidad | Campo único |
|---------|-------------|
| `Conductor` | `documentoIdentidad` |
| `Vehiculo` | `matricula` |
| `Infraccion` | `codigo` |
| `Radar` | `identificador` |
| `Expediente` | `numeroExpediente` y `denunciaId` (relación 1:1 con `Denuncia`) |
| `Resolucion` | `expedienteId` (como mucho una por expediente) |

`PermisoConduccion.numeroPermiso` **no** está en la lista: no es único.

## 8. Se elimina la regla de identificación del conductor

La antigua regla R6 (obligación de identificar al conductor, cuya omisión generaba
una infracción autónoma muy grave) **se ha eliminado**: el modelo no tenía forma
de representar un expediente que genera otro. No la implementes. El número R6
corresponde ahora a la regla de coherencia del denunciante (apartado 2).

## Otros cambios menores

- El diagrama de relaciones indica la cardinalidad en cada etiqueta
  (por ejemplo, `posee (1 : 0..N)`) y se puede descargar como PNG.
- La actividad A5.4 usa ahora **arrays** para `documentosAdjuntos` de la
  alegación, en lugar de las clases de permiso.

## Lista de comprobación

Marca cada punto cuando lo hayas aplicado:

- [ ] `PermisoConduccion` tiene una sola `clase` y `numeroPermiso` no es único.
- [ ] La suspensión por saldo cero actualiza todos los permisos del conductor en una sola transacción.
- [ ] `Denuncia` tiene `tipoDenunciante`, `identificadorAgente` y `velocidadMedida`.
- [ ] Se valida la coherencia del denunciante (R6).
- [ ] `Expediente` ya no tiene `fechaNotificacion`.
- [ ] El pronto pago deja el expediente en `RESUELTO` y crea una `Resolucion` de tipo `SANCION`.
- [ ] Sólo existen los tipos de pago `TOTAL` y `PRONTO_PAGO`.
- [ ] Todos los atributos de la tabla del apartado 6 son `enum`.
- [ ] Los campos de la tabla del apartado 7 son únicos.
- [ ] No queda código de la antigua regla de identificación del conductor.
