# 🎙️ Guion de Exposición Detallado: Proyecto LogiFlow (Evaluación 20/20)

Este guion está estructurado con un alto nivel de detalle técnico para cumplir con la rigurosidad del docente. Indica exactamente qué archivo abrir, a qué línea apuntar y qué palabras usar para sustentar cada punto de la rúbrica.

> [!IMPORTANT]
> **Preparación previa:**
> 1. Levantar PostgreSQL.
> 2. Levantar `ms-flota-rest` (puerto 8081).
> 3. Levantar `ms-taller-rest` (puerto 8082).
> 4. Tener abierto el IDE con los controladores visibles.
> 5. Tener abierto el navegador con: `DDD_ANALYSIS.md` renderizado (ej. en GitHub), Swagger Flota (`http://localhost:8081/swagger-ui.html`), Swagger Taller (`http://localhost:8082/swagger-ui.html`), GitHub (pestaña Actions), SonarCloud y Telegram.

---

## 🗣️ Introducción
**"Buenos días, ingeniero. Presentamos la Fase 1 de la Plataforma de Gestión Logística, LogiFlow. Para una evaluación ágil, nuestra exposición seguirá exactamente los 4 entregables de su rúbrica, demostrando en código y documentación el cumplimiento de cada punto."**

---

## 📦 ENTREGABLE 1: Documento de análisis DDD (20 puntos)
*(Abran el archivo `DDD_ANALYSIS.md` en su entorno visual o GitHub)*

**"Empezamos con la propuesta de arquitectura basada en Domain-Driven Design detallada en nuestro `DDD_ANALYSIS.md`."**

1. **Event Storming – Claridad y completitud (5 pts):**
   - **Acción:** Diríjanse a la **Sección 2** del documento.
   - **Explicación:** *"En la sección 2.1 documentamos una tabla exhaustiva con 20 eventos de dominio. Aquí puede observar cómo identificamos claramente al **Actor** (ej. Administrador de Flota), el **Comando** que ejecuta (ej. Registrar Vehículo) y el **Evento de Dominio** en pasado que se dispara (ej. `VehiculoRegistrado`). Inmediatamente abajo, en la sección 2.2, modelamos un diagrama visual usando Mermaid que agrupa estos flujos por dominio."*

2. **Dominios – Correcta clasificación (4 pts):**
   - **Acción:** Diríjanse a la **Sección 3**.
   - **Explicación:** *"Clasificamos el sistema en tres tipos de dominios. En la sección 3.1 definimos nuestro **Core Domain** como 'Ruteo y Despacho', ya que es el núcleo estratégico de la logística. En la 3.2 agrupamos los **Supporting Domains** como 'Gestión de Flota' y 'Mantenimiento/Taller' (los microservicios desarrollados hoy) porque proveen recursos indispensables. Finalmente, en la 3.3, los **Generic Domains** como Facturación o Usuarios, que podrían incluso ser delegados a software de terceros."*

3. **Bounded Contexts – Definición completa (4 pts):**
   - **Acción:** Diríjanse a la **Sección 4**.
   - **Explicación:** *"Definimos 10 Bounded Contexts. Por ejemplo, en el **BC-01: Gestión de Flota**, delimitamos su responsabilidad exclusivamente al CRUD y disponibilidad. Definimos que su Agregado Root es `Vehiculo`, y que se comunicará vía protocolo REST (JSON). Cada BC cuenta con esta ficha técnica."*

4. **Lenguaje ubicuo – Coherencia y relevancia (3 pts):**
   - **Acción:** Diríjanse a la **Sección 5**.
   - **Explicación:** *"Para evitar ambigüedades, estructuramos el Lenguaje Ubicuo en la sección 5.1. Definimos términos de negocio; por ejemplo, la 'Capacidad' se mide estrictamente en kilogramos. Lo más importante es que mapeamos esto al código: el término 'Disponibilidad' se refleja directamente en la variable `disponible` de nuestra clase `Conductor.java` y en el endpoint `/disponibles`."*

5. **Context Map – Identificación y justificación de patrones (4 pts):**
   - **Acción:** Diríjanse a la **Sección 6**.
   - **Explicación:** *"En la sección 6.1 modelamos el Context Map. Usamos patrones estratégicos descritos en la sección 6.2: Por ejemplo, entre Taller y Flota existe un patrón de **Anti-Corruption Layer (ACL)**, aislando nuestro dominio interno de contratos externos. También usamos **Upstream/Downstream** donde la Flota es proveedora de información para el Ruteo."*

---

## 🚛 ENTREGABLE 2: Microservicio REST: ms-flota-rest (20 puntos)
*(Abran el Swagger de Flota y el IDE mostrando el `VehiculoController.java`)*

1. **Estructura del código y manejo de errores (3 pts):**
   - **Acción:** Muestren en el IDE `Vehiculo.java` y `GlobalExceptionHandler.java`.
   - **Explicación:** *"Ingeniero, a nivel de estructura, hemos diseñado el modelo utilizando **UUID** (`java.util.UUID`) como clave primaria en lugar de enteros autoincrementales, aplicando las mejores prácticas para seguridad y desacoplamiento en microservicios distribuidos. Para el manejo de errores, implementamos un `@ControllerAdvice` que captura excepciones de negocio y de validación (`@Valid`), devolviendo respuestas limpias estructuradas."*

2. **CRUD vehículos – todas las operaciones (5 pts) & API documentada (4 pts):**
   - **Acción:** Muestren `VehiculoController.java` y luego vayan al Swagger.
   - **Explicación:** *"Definimos todas las operaciones en `VehiculoController`. Usamos semántica HTTP estricta: `@PostMapping` para crear, `@GetMapping("/{id}")` para consultar, `@PutMapping("/{id}")` para actualizar toda la entidad, `@DeleteMapping` para bajas, e incluso un `@PatchMapping("/{id}/estado")` exclusivo para transiciones de estado operativo. Todo está completamente documentado con `@Operation` y `@ApiResponses`, tal como se refleja aquí en nuestro Swagger UI donde podemos probar la creación inmediata."* *(Hagan un POST rápido)*

3. **CRUD conductores – todas las operaciones (4 pts):**
   - **Acción:** Muestren la sección de Conductores en Swagger.
   - **Explicación:** *"Para el conductor tenemos la misma robustez. Soportamos creación, lectura, actualización y eliminación. Garantizamos la integridad validando en `ConductorDTO` que la cédula cumpla la expresión regular de 10 dígitos obligatorios."*

4. **Endpoint de disponibilidad funcional (4 pts):**
   - **Acción:** Ejecuten el endpoint `GET /api/vehiculos/disponibles` en Swagger.
   - **Explicación:** *"Cumpliendo con la rúbrica, implementamos consultas de disponibilidad para el ruteo. El endpoint `/disponibles` filtra a nivel de base de datos y retorna exclusivamente aquellos vehículos cuyo estado (`EstadoVehiculo`) es exactamente `DISPONIBLE`, excluyendo los que están en mantenimiento o en servicio."*

---

## 🔧 ENTREGABLE 3: Microservicio REST: ms-taller-rest (20 puntos)
*(Abran el IDE en el paquete `ec.edu.espe.taller` de `ms-taller-rest` y el Swagger de este microservicio)*

1. **Transformación a REST, código limpio y validación (4 pts):**
   - **Acción:** Muestren el `pom.xml` y la clase `MantenimientoDTO.java`.
   - **Explicación:** *"Este microservicio fue concebido puramente bajo la arquitectura REST. Utilizamos `spring-boot-starter-web`. La recepción de datos es limpia: usamos el objeto `MantenimientoDTO` validado con anotaciones `@NotBlank` para asegurar que el formato JSON entrante siempre contenga una matrícula y una descripción válidas."*

2. **Endpoint GET /vehiculos/{matricula} funcional (6 pts):**
   - **Acción:** Muestren el endpoint en Swagger y ejecútenlo con la matrícula `PBC1234`.
   - **Explicación:** *"Expusimos el endpoint GET solicitado. Al pasar la matrícula en el Path Variable, el controlador consulta nuestra Capa Anticorrupción y devuelve un JSON con los datos mapeados a nuestro modelo `VehiculoTaller`. Como ve en la respuesta, devuelve la marca, modelo y estado."*

3. **Endpoint POST /mantenimientos funcional (6 pts):**
   - **Acción:** Ejecuten el POST en Swagger enviando `{"matricula": "PBC1234", "descripcion": "Cambio de aceite"}`.
   - **Explicación:** *"El endpoint POST registra órdenes de mantenimiento. Recibe el payload JSON, el servicio procesa la solicitud, simula el registro generando un ID único (UUID) para la orden y retorna el objeto `OrdenMantenimiento` consolidado, confirmando la creación con un código HTTP 201 Created."*

4. **Documentación Swagger completa (4 pts):**
   - **Acción:** Muestren el panel superior de Swagger.
   - **Explicación:** *"Todo el servicio cuenta con Swagger. Implementamos las dependencias de `springdoc-openapi` y documentamos con descripciones técnicas los retornos 200 (OK), 201 (Created), 404 (Not Found) y 400 (Bad Request) garantizando un contrato API claro."*

---

## ☁️ ENTREGABLE 4: Infraestructura DevOps (20 puntos)
*(Muestren su navegador)*

1. **Repositorio y ramas correctamente configuradas (4 pts):**
   - **Acción:** Abran GitHub y muestren el desplegable de ramas.
   - **Explicación:** *"Nuestro repositorio cumple la directiva de poseer las ramas aisladas `main` (producción) y `development` (desarrollo)."*

2. **Pipeline funcional - ejecuta análisis estático (5 pts):**
   - **Acción:** Abran la pestaña Actions y abran el último Workflow exitoso. Muestren el archivo `.github/workflows/ci.yml` si es posible.
   - **Explicación:** *"Diseñamos un pipeline en GitHub Actions (`ci.yml`). Al realizar un push, el pipeline compila el proyecto y ejecuta las pruebas automatizadas de ambos microservicios usando Maven (`mvn clean verify`). Si compila con éxito, da paso a la fase de análisis de calidad."*

3. **Integración con SonarCloud sin errores graves (4 pts):**
   - **Acción:** Abran el dashboard de SonarCloud.
   - **Explicación:** *"El pipeline dispara el escáner de SonarCloud enviando métricas mediante nuestro token seguro. Como puede observar en el dashboard de la organización, ambos microservicios (`ms-flota-rest` y `ms-taller-rest`) pasaron el Quality Gate sin Bugs críticos, Vulnerabilidades ni Code Smells severos."*

4. **Notificaciones a Telegram automáticas (4 pts):**
   - **Acción:** Abran la aplicación de Telegram y muestren el historial del bot.
   - **Explicación:** *"Al finalizar todo el flujo, usamos `appleboy/telegram-action`. Nuestro bot notifica instantáneamente al canal de desarrollo el autor, el ID del commit, la rama afectada y si cada job (Build de Flota, Build de Taller y SonarCloud) resultó en un '✅ Éxito' o '❌ Fallo', junto al enlace directo al pipeline."*

5. **README completo y claro (3 pts):**
   - **Acción:** Muestren la portada del repositorio en GitHub (`README.md`).
   - **Explicación:** *"Finalmente, centralizamos toda la información en un README detallado. Incluye diagramas de arquitectura ASCII actualizados, el stack tecnológico, los puertos designados (8081 y 8082), un listado de endpoints disponibles y las instrucciones precisas para inyectar los secretos de DevOps. El proyecto es 100% reproducible."*

**"Esto concluye nuestra presentación. Hemos evidenciado técnicamente el cumplimiento absoluto de su rúbrica."**
