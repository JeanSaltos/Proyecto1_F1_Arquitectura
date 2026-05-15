# 🎙️ Guion de Exposición Detallado: Proyecto LogiFlow (Evaluación 20/20)

Este guion está estructurado con un alto nivel de detalle técnico.
**Estudiante 1:** Encargado de Entregables 1 y 2.
**Estudiante 2 (Tú):** Encargado de Entregables 3 y 4.

> [!IMPORTANT]
> **Preparación previa:**
> 1. Levantar PostgreSQL.
> 2. Levantar `ms-flota-rest` (puerto 8081).
> 3. Levantar `ms-taller-rest` (puerto 8082).
> 4. Tener abierto el IDE con los controladores visibles.
> 5. Tener listas las pestañas del navegador: `DDD_ANALYSIS.md`, Swagger Flota, Swagger Taller, GitHub (pestaña Actions), SonarCloud y Telegram Web.

---

## 🗣️ Introducción (Estudiante 1)
**"Buenos días, ingeniero. Presentamos la Fase 1 de la Plataforma de Gestión Logística, LogiFlow. Para una evaluación ágil, nuestra exposición seguirá exactamente los 4 entregables de su rúbrica, demostrando en código y documentación el cumplimiento de cada punto."**

---

## 📦 ENTREGABLE 1: Documento de análisis DDD (20 puntos) - [Estudiante 1]
*(Abran el archivo `DDD_ANALYSIS.md` en su entorno visual o GitHub)*

1. **Event Storming – Claridad y completitud (5 pts):**
   - **Explicación:** *"En la sección 2.1 documentamos una tabla exhaustiva con 20 eventos de dominio. Aquí puede observar cómo identificamos claramente al **Actor**, el **Comando** y el **Evento de Dominio** en pasado (ej. `VehiculoRegistrado`). En la sección 2.2, modelamos un diagrama visual usando Mermaid que agrupa estos flujos por dominio."*

2. **Dominios – Correcta clasificación (4 pts):**
   - **Explicación:** *"Clasificamos el sistema en la sección 3. Definimos nuestro **Core Domain** como 'Ruteo y Despacho', ya que es el núcleo estratégico. Agrupamos los **Supporting Domains** como 'Gestión de Flota' y 'Mantenimiento/Taller' porque proveen recursos indispensables. Finalmente, los **Generic Domains** como Facturación."*

3. **Bounded Contexts – Definición completa (4 pts):**
   - **Explicación:** *"Definimos 10 Bounded Contexts. Por ejemplo, en el **BC-01: Gestión de Flota**, delimitamos su responsabilidad al CRUD y disponibilidad. Definimos que su Agregado Root es `Vehiculo`, y que se comunicará vía protocolo REST."*

4. **Lenguaje ubicuo – Coherencia y relevancia (3 pts):**
   - **Explicación:** *"Estructuramos el Lenguaje Ubicuo en la sección 5.1. Definimos términos de negocio que mapeamos directamente al código: el término 'Disponibilidad' se refleja en la variable `disponible` de nuestra clase `Conductor.java` y en el endpoint `/disponibles`."*

5. **Context Map – Identificación y justificación de patrones (4 pts):**
   - **Explicación:** *"En la sección 6 modelamos el Context Map. Usamos patrones estratégicos: entre Taller y Flota existe un patrón de **Anti-Corruption Layer (ACL)**, aislando nuestro dominio interno. También usamos **Upstream/Downstream** donde la Flota provee información al Ruteo."*

---

## 🚛 ENTREGABLE 2: Microservicio REST: ms-flota-rest (20 puntos) - [Estudiante 1]
*(Abran el Swagger de Flota y el IDE mostrando el `VehiculoController.java`)*

1. **Estructura del código y manejo de errores (3 pts):**
   - **Explicación:** *"Ingeniero, hemos diseñado el modelo utilizando **UUID** (`java.util.UUID`) como clave primaria en lugar de enteros autoincrementales, aplicando mejores prácticas para seguridad en microservicios distribuidos. Además, implementamos un `@ControllerAdvice` para capturar validaciones."*

2. **CRUD vehículos y API documentada (9 pts):**
   - **Explicación:** *"En `VehiculoController` usamos semántica HTTP estricta: `@PostMapping` para crear, `@GetMapping("/{id}")` para consultar, `@PutMapping` y `@DeleteMapping`. Todo documentado con Swagger UI."* *(Hagan un POST rápido).*

3. **CRUD conductores y Disponibilidad (8 pts):**
   - **Explicación:** *"Validamos en `ConductorDTO` que la cédula tenga 10 dígitos. Además, el endpoint `/disponibles` filtra a nivel de base de datos y retorna exclusivamente aquellos vehículos/conductores operativos para ruteo."*

---

## 🔧 ENTREGABLE 3: Microservicio REST: ms-taller-rest (20 puntos) - [Estudiante 2 - TU TURNO]
*(Toma la palabra, abre el IDE en `ms-taller-rest/pom.xml` y luego Swagger)*

1. **Transformación a REST, código limpio y validación (4 pts):**
   - **Acción:** *Muestra el `pom.xml` y explica la eliminación de SOAP.*
   - **Explicación:** *"Ingeniero, para este entregable tomamos la decisión arquitectónica de **refactorizar por completo** el servicio original SOAP. Eliminamos todas las dependencias de JAXB y archivos WSDL, e incluimos `spring-boot-starter-web`. Además, **eliminamos la clase `TallerTranslator.java`** que usábamos como Capa Anticorrupción para SOAP, ya que ahora trabajamos limpiamente con `MantenimientoDTO` validado mediante `@NotBlank`."*

2. **Endpoint GET /vehiculos/{matricula} funcional (6 pts):**
   - **Acción:** *Abre Swagger de Taller (`http://localhost:8082/swagger-ui.html`) y ejecuta el GET con `PBC1234`.*
   - **Explicación:** *"Aquí en Swagger expusimos el endpoint GET. Al pasar la matrícula por Path Variable, el controlador devuelve un JSON limpio mapeado a nuestro modelo de dominio `VehiculoTaller`. Como ve, nos retorna la marca, modelo y el estado actual del vehículo."*

3. **Endpoint POST /mantenimientos funcional (6 pts):**
   - **Acción:** *Ejecuta el POST en Swagger enviando el siguiente JSON:*
     ```json
     {
       "matricula": "PBC1234",
       "descripcion": "Cambio de aceite y filtros"
     }
     ```
   - **Explicación:** *"El endpoint POST registra la orden. Le enviamos este JSON, el servicio lo procesa, simula el registro generando un UUID para la orden y nos responde con un código HTTP `201 Created` y el objeto consolidado."*

4. **Documentación Swagger completa (4 pts):**
   - **Acción:** *Muestra los esquemas y las respuestas 200, 201, 400 y 404 en Swagger.*
   - **Explicación:** *"Añadimos `springdoc-openapi`. Todas las respuestas, tanto de éxito como de error (Bad Request, Not Found), están documentadas para garantizar un contrato API robusto con el frontend."*

---

## ☁️ ENTREGABLE 4: Infraestructura DevOps (20 puntos) - [Estudiante 2 - TU TURNO]
*(Abre GitHub en la pestaña Actions, SonarCloud y Telegram)*

1. **Repositorio y ramas correctamente configuradas (4 pts):**
   - **Acción:** *Muestra el desplegable de ramas en GitHub.*
   - **Explicación:** *"En cuanto a DevOps, manejamos un flujo Git profesional. Tenemos nuestras ramas aisladas: `main` para producción y `development` para desarrollo."*

2. **Pipeline funcional y ejecución de análisis estático (5 pts):**
   - **Acción:** *Muestra el archivo `.github/workflows/ci.yml`.*
   - **Explicación:** *"Diseñamos un pipeline en GitHub Actions que de forma automatizada compila el proyecto y ejecuta las pruebas de ambos microservicios usando Maven. Como puede ver en la configuración, definimos jobs paralelos para optimizar el tiempo de construcción antes de dar paso al análisis de calidad."*

3. **Integración con SonarCloud sin errores graves (4 pts):**
   - **Acción:** *Abre el dashboard de SonarCloud en la rama `main`.*
   - **Explicación:** *"Nuestro pipeline se conecta de forma directa y segura con SonarCloud mediante tokens inyectados como secretos. Como puede observar en el dashboard principal, ambos microservicios (`ms-flota-rest` y `ms-taller-rest`) pasaron el Quality Gate de forma exitosa, sin Bugs críticos ni Vulnerabilidades de seguridad."*

4. **Notificaciones a Telegram y Demostración en Vivo (4 pts):**
   - **Acción:** *Abre la terminal de tu IDE y Telegram Web a un lado.*
   - **Explicación:** *"Al finalizar, usamos un bot para notificar a Telegram el estado de los jobs. Para demostrarle que nuestro pipeline es 100% funcional en vivo, **voy a disparar una ejecución manualmente desde la terminal** mediante un commit vacío."*
   - **Acción en vivo:** *Copia, pega y ejecuta esto en la terminal frente al profesor:*
     ```bash
     git commit --allow-empty -m "Demo en vivo para presentacion: Trigger pipeline"
     git push origin main
     ```
   - *"Si vamos a GitHub Actions, verá que el workflow acaba de arrancar. Y en un par de minutos, recibiremos este mensaje en Telegram notificando el éxito de la compilación y del análisis de código."*

5. **README completo y claro (3 pts):**
   - **Acción:** *Muestra la portada del repositorio en GitHub (`README.md`).*
   - **Explicación:** *"Finalmente, documentamos todo en el README: diagramas de arquitectura, comandos para ejecución local y las instrucciones para inyectar los secretos de Sonar y Telegram. El proyecto está listo para ser replicado por cualquier desarrollador."*

**"Esto concluye nuestra presentación. Esperamos que el nivel de refactorización y automatización haya sido de su agrado. Muchas gracias."**
