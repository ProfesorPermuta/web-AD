---
title: 'Chuleta de anotaciones: Lombok y Spring Framework'
description: Tabla de referencia rápida con las anotaciones de Lombok y de Spring Framework más usadas en el módulo, agrupadas por finalidad.
---

import NextPageButton from '@site/src/components/NextPageButton';

# Chuleta de anotaciones: Lombok y Spring Framework

Esta página no introduce conceptos nuevos: es una **tabla de referencia** para
consultar durante el desarrollo. Cada anotación enlaza, cuando procede, al
apartado donde se explica con detalle. Si necesitas entender el **porqué** de
alguna, ve primero a [Inversión de control e inyección de
dependencias](/tema-0/f-inyeccion-dependencias) o a [Anatomía de un proyecto
Spring Boot](/tema-0/e-anatomia-spring-boot).

:::warning
[Lombok no sustituye entender qué genera]

En la defensa oral se te puede pedir que expliques qué código genera una
anotación de Lombok en concreto. Usarla está bien; no saber qué hace, no.

:::

## Lombok

Lombok genera código repetitivo (getters, setters, constructores...) en tiempo
de compilación mediante un procesador de anotaciones. No aporta comportamiento
en tiempo de ejecución: si lo quitas del `pom.xml`, el código deja de
compilar, no de funcionar distinto.

### Generación de miembros

| Anotación | Genera |
|-----------|--------|
| `@Getter` | Getter público para cada campo (o para la clase si se pone a nivel de clase). |
| `@Setter` | Setter público para cada campo no `final`. |
| `@ToString` | `toString()` con el nombre de la clase y sus campos. |
| `@EqualsAndHashCode` | `equals()` y `hashCode()` basados en los campos. |
| `@NoArgsConstructor` | Constructor sin argumentos. |
| `@AllArgsConstructor` | Constructor con todos los campos como parámetros. |
| `@RequiredArgsConstructor` | Constructor con los campos `final` y los anotados `@NonNull`. Es la que usarás para [inyección por constructor](/tema-0/f-inyeccion-dependencias#con-lombok). |

### Anotaciones compuestas

| Anotación | Equivale a |
|-----------|------------|
| `@Data` | `@Getter` + `@Setter` + `@ToString` + `@EqualsAndHashCode` + `@RequiredArgsConstructor`. |
| `@Value` | Como `@Data` pero **inmutable**: campos `private final`, sin setters, clase `final`. |

:::warning
[`@Data` en entidades JPA]

`@Data` genera `equals()`/`hashCode()` sobre todos los campos, incluidas las
relaciones. En una entidad JPA con colecciones asociadas (por ejemplo
`Autor` con sus `Libro`) eso puede disparar cargas perezosas
inesperadas o bucles infinitos si dos entidades se referencian mutuamente.
Cuando llegues a la UD4, prefiere anotaciones sueltas (`@Getter`, `@Setter`) y
un `equals()`/`hashCode()` basado sólo en el identificador.

:::

### Otras

| Anotación | Uso |
|-----------|-----|
| `@Builder` | Genera un patrón *builder* para construir instancias paso a paso. |
| `@Slf4j` | Inyecta un campo estático `log` de SLF4J, sin declararlo a mano. |
| `@NonNull` | En un parámetro o campo, genera una comprobación de nulidad que lanza `NullPointerException` si se incumple. |

## Spring Framework

### Estereotipos y arranque

Ver también la tabla completa en [Los
estereotipos](/tema-0/f-inyeccion-dependencias#los-estereotipos).

| Anotación | Uso |
|-----------|-----|
| `@Component` | Marca una clase como bean genérico gestionado por Spring. |
| `@Service` | Estereotipo de `@Component` para la capa de negocio. |
| `@Repository` | Estereotipo de `@Component` para la capa de persistencia; traduce excepciones a `DataAccessException`. |
| `@Controller` | Estereotipo de `@Component` para la capa de presentación (vistas). |
| `@RestController` | `@Controller` + `@ResponseBody`: cada método serializa su valor de retorno directamente al cuerpo de la respuesta (JSON). |
| `@Configuration` | La clase declara beans mediante métodos `@Bean`. |
| `@SpringBootApplication` | Anotación compuesta: `@SpringBootConfiguration` + `@ComponentScan` + `@EnableAutoConfiguration`. Ver [Anatomía de un proyecto Spring Boot](/tema-0/e-anatomia-spring-boot#el-punto-de-entrada). |

### Inyección de dependencias

| Anotación | Uso |
|-----------|-----|
| `@Autowired` | Marca un punto de inyección. Innecesaria en un constructor único (ver [Formas de inyección](/tema-0/f-inyeccion-dependencias#formas-de-inyección)). Evita usarla sobre campos. |
| `@Qualifier("nombreBean")` | Desambigua qué implementación inyectar cuando hay varios beans candidatos. |
| `@Primary` | Marca una implementación como predeterminada entre varias candidatas. |
| `@Value("${propiedad}")` | Inyecta un valor suelto leído de `application.yml`. |
| `@ConditionalOnProperty` | Registra el bean sólo si una propiedad de configuración tiene un valor concreto. |
| `@Bean` | Dentro de una clase `@Configuration`, declara un bean a partir del valor de retorno del método. |

### Ciclo de vida y ámbito

| Anotación | Uso |
|-----------|-----|
| `@Scope("prototype")` | Cambia el ámbito de un bean (por defecto es `singleton`). |
| `@PostConstruct` | Método que se ejecuta una vez, justo después de la inyección de dependencias. |
| `@PreDestroy` | Método que se ejecuta al cerrar el contexto, antes de destruir el bean. |

### Configuración externalizada

| Anotación | Uso |
|-----------|-----|
| `@ConfigurationProperties(prefix = "...")` | Vincula un grupo de propiedades de `application.yml` a un `record` o clase tipada. |
| `@EnableConfigurationProperties` | Activa una clase concreta de `@ConfigurationProperties`. |
| `@ConfigurationPropertiesScan` | Activa el escaneo automático de todas las clases `@ConfigurationProperties` del paquete. |
| `@Validated` | Aplica validación de Bean Validation al construir el objeto de propiedades. |
| `@Profile("dev")` | Restringe un bean o una configuración a un perfil concreto. |

### Validación (Jakarta Bean Validation)

Ver [Controladores REST y validación de
entrada](/tema-0/g-controlador-rest-validacion#anotaciones-de-bean-validation-más-usadas)
para la tabla completa y ejemplos.

| Anotación | Uso |
|-----------|-----|
| `@NotNull` | El valor no puede ser `null`. |
| `@NotBlank` | El `String` no puede ser `null`, vacío ni sólo espacios. |
| `@NotEmpty` | La colección o `String` no puede ser `null` ni estar vacía. |
| `@Size(min=, max=)` | Longitud o tamaño dentro de un rango. |
| `@Min` / `@Max` / `@DecimalMin` / `@DecimalMax` | Valor numérico dentro de un rango. |
| `@Positive` / `@PositiveOrZero` | El número es (estrictamente) positivo. |
| `@Pattern(regexp=)` | El `String` coincide con una expresión regular. |
| `@Email` | El `String` tiene forma de correo electrónico. |
| `@Past` / `@Future` | La fecha es anterior / posterior al momento actual. |
| `@Valid` | En un parámetro de controlador o campo anidado, activa la validación en cascada. Sin ella, las anotaciones anteriores no se evalúan. |

### Web (Spring MVC)

Ver [Controladores REST y validación de
entrada](/tema-0/g-controlador-rest-validacion) para el ejemplo completo con
DTO, controlador y manejador de errores.

| Anotación | Uso |
|-----------|-----|
| `@RequestMapping` | Define la ruta base de un controlador o de un método. |
| `@GetMapping` / `@PostMapping` / `@PutMapping` / `@DeleteMapping` | Atajos de `@RequestMapping` para cada verbo HTTP. |
| `@PathVariable` | Vincula un fragmento de la ruta a un parámetro del método. |
| `@RequestParam` | Vincula un parámetro de la *query string* a un parámetro del método. |
| `@RequestBody` | Deserializa el cuerpo de la petición (JSON) al tipo del parámetro. |
| `@ResponseStatus` | Fija el código HTTP de la respuesta. |
| `@RestControllerAdvice` | Aplica sus `@ExceptionHandler` a todos los controladores de la aplicación. |
| `@ExceptionHandler(Tipo.class)` | Método que se ejecuta cuando ese tipo de excepción escapa de un controlador. |

:::note
[¿Dónde está `@Transactional`?]

No aparece en esta tabla porque su comportamiento — gestión de transacciones,
*rollback* automático — se explica en detalle en la UD3, cuando trabajemos
JDBC y JPA. Aquí sólo la nombramos para que la reconozcas cuando aparezca.

:::

<NextPageButton to="/tema-1/a-presentacion-tema">
Empezar la UD1
</NextPageButton>
