# 🎙️ Guion de Exposición Detallado: LogiFlow - Fase 2 (Evaluación 20/20)

Este guion está estructurado detalladamente para **dos personas**, enfocándose exclusivamente en la **Fase 2**: Backend Distribuido, Integración con RabbitMQ, Gateway GraphQL, WebSockets, Contenedores Docker, Despliegue en Kubernetes y Calidad con SonarCloud.

---

### 📋 Preparación Previa a la Defensa:
1. **Entorno Activo**: Asegúrate de tener el clúster de Kubernetes corriendo (`kubectl get pods` debe mostrar los 10 pods: 8 microservicios + postgres + rabbitmq en estado `Running`).
2. **Herramientas Abiertas**:
   - IDE (IntelliJ / VS Code) con las pestañas de los microservicios clave abiertos (`ms-pedidos`, `ms-ruteo`, `graphql-gateway`).
   - Navegador web con:
     - **GraphiQL**: `http://localhost:8080/graphiql` (GraphQL Gateway)
     - **RabbitMQ Management**: `http://localhost:15672` (guest/guest)
     - **SonarCloud Dashboard** de tu cuenta.
     - **Archivo `README.md`** y **`k8s/microservices.yaml`** listos.

---

## 🗣️ INTRODUCCIÓN Y ARQUITECTURA GENERAL

### 👤 [Estudiante 1]
**"Buenos días, ingeniero. Presentamos la Fase 2 de la plataforma LogiFlow. En la fase anterior teníamos un monolito dividido en dos microservicios REST/SOAP básicos. Para esta Fase 2, hemos evolucionado el sistema a una arquitectura de microservicios distribuida y reactiva, orientada a eventos, con una capa de agregación unificada por un GraphQL Gateway, comunicación en tiempo real y despliegue automatizado en Kubernetes."**

**"Para facilitar la defensa, dividiremos la presentación en dos bloques de 4 microservicios cada uno, seguidos del despliegue de infraestructura y análisis de calidad."**

---

## 🔑 BLOQUE 1: SEGURIDAD, CLIENTES Y PEDIDOS (Estudiante 1)

*(Muestra en el IDE las carpetas de `ms-auth`, `ms-clientes` y `ms-pedidos`)*

### 1. Autenticación Centralizada (`ms-auth` - Puerto 8083)
**"El primer componente crítico es la seguridad. Diseñamos `ms-auth`, un microservicio REST que gestiona las identidades del sistema. En lugar de usar sesiones tradicionales de servidor, implementamos autenticación stateless mediante JWT (JSON Web Tokens). Las contraseñas se almacenan de forma segura utilizando hashing con BCrypt, y el microservicio expone endpoints para registrar usuarios, iniciar sesión y verificar tokens en tiempo real."**

### 2. Clientes y Cuentas Corporativas (`ms-clientes` - Puerto 8084)
**"Para soportar el modelo de negocio multinivel, implementamos `ms-clientes`. Este servicio maneja el ciclo de vida de los clientes (particulares y corporativos). Para los clientes corporativos, implementamos una regla de negocio crítica: el control de saldo financiero mediante una entidad `CuentaCorporativa` con un límite de crédito. Cada vez que se crea un pedido, se valida si el cliente tiene fondos disponibles o crédito suficiente, garantizando la solvencia del servicio."**

### 3. Creación de Pedidos (`ms-pedidos` - Puerto 8085)
**"El corazón transaccional de este bloque es `ms-pedidos`. Cuando recibe una solicitud para crear un pedido, realiza una consulta REST interna a `ms-clientes` para validar la existencia del cliente y el estado financiero de su cuenta corporativa. Si el saldo es suficiente, el pedido se guarda en estado `REGISTRADO` y, de manera asíncrona, se publica el evento `pedido.creado` en el bus de mensajes RabbitMQ, desacoplando este proceso de la asignación de transporte."**

---

## 🧪 DEMO EN VIVO - PARTE 1: REGISTRO Y EVENTO (Estudiante 1)

*(Abre GraphiQL en el navegador: `http://localhost:8080/graphiql`)*

**"Para demostrar la integración de este primer bloque, utilizaremos la interfaz de GraphiQL expuesta por el Gateway. Voy a ejecutar la creación de un nuevo pedido."**

#### Acción 1: Crear Pedido mediante GraphQL Mutation
*Ejecuta la siguiente mutation en GraphiQL:*
```graphql
mutation {
  crearPedido(input: {
    clienteId: "bf813017-0049-4217-bb19-481bf80c0abc"
    descripcion: "Carga de suministros médicos urgentes"
    peso: 350.0
    origen: "Quito"
    destino: "Guayaquil"
    prioridad: "ALTA"
  }) {
    id
    estado
    prioridad
  }
}
```

*Al recibir el JSON de respuesta exitosa:*
**"Como observa, el Gateway nos devuelve el ID y el estado inicial `REGISTRADO`. En este mismo instante, `ms-pedidos` ha enviado de forma no bloqueante un mensaje a RabbitMQ. Doy el paso a mi compañero para explicar cómo se procesa esta información de forma asíncrona."**

---

## 🗺️ BLOQUE 2: RUTEO, SEGUIMIENTO Y TIEMPO REAL (Estudiante 2)

*(Toma la palabra. Muestra en el IDE las carpetas de `ms-ruteo`, `ms-seguimiento` y `graphql-gateway`)*

### 👤 [Estudiante 2]
**"Gracias. Continuamos con el procesamiento de los eventos de negocio."**

### 1. Consumo y Asignación de Rutas (`ms-ruteo` - Puerto 8086)
**"El microservicio `ms-ruteo` está escuchando la cola de RabbitMQ. Al capturar el evento `pedido.creado`, inicia la orquestación: realiza llamadas REST a `ms-flota-rest` para buscar vehículos y conductores aptos y disponibles. Cuando los encuentra, asocia el viaje, calcula la ruta óptima y crea un registro `Envio`. Además, a través de peticiones PATCH de `RestTemplate` actualiza el estado de la flota a `EN_SERVICIO` y el del pedido a `ASIGNADO`. Finalmente, publica el evento `envio.asignado` en RabbitMQ."**
*(Nota técnica si pregunta sobre PATCH: Corregimos la fábrica del `RestTemplate` usando `JdkClientHttpRequestFactory` para permitir peticiones PATCH nativas en Java 17).*

### 2. Transmisión WebSocket y GraphQL Gateway (`ms-seguimiento` y `graphql-gateway`)
**"Para la última milla y el monitoreo por parte del cliente, necesitamos tiempo real. Implementamos `ms-seguimiento` (Puerto 8087), que levanta un broker WebSocket STOMP. Este servicio consume las coordenadas del simulador GPS de `ms-ruteo` desde RabbitMQ y las inyecta en el canal WebSocket `/topic/seguimiento/{envioId}`."**

**"Por otro lado, nuestro GraphQL Gateway (Puerto 8080) expone una suscripción reactiva (`Subscription tracking`). Internamente, lee los eventos GPS desde RabbitMQ y, usando la programación reactiva de Spring WebFlux con un `Sinks.Many` multicast, transmite el flujo de coordenadas directamente a los clientes conectados a GraphQL sin necesidad de WebSockets tradicionales adicionales."**

---

## 🧪 DEMO EN VIVO - PARTE 2: TIEMPO REAL GPS (Estudiante 2)

*(Abre GraphiQL, limpia la pantalla anterior y prepara la suscripción y la simulación)*

**"Vamos a demostrar el rastreo en vivo. Primero, me suscribiré al canal de rastreo utilizando la Subscription de GraphQL en el Gateway."**

#### Acción 2: Activar Suscripción en GraphiQL
*Ejecuta en GraphiQL:*
```graphql
subscription {
  tracking(envioId: "62e95be9-a6db-460f-a675-84aa8be82187") {
    envioId
    lat
    lng
    velocidad
    timestamp
  }
}
```
*(El panel de GraphiQL quedará a la espera de datos en tiempo real).*

#### Acción 3: Disparar Simulación GPS
*Abre otra pestaña o terminal y ejecuta un POST simulado a `ms-ruteo`:*
```powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8086/api/ruteo/envios/62e95be9-a6db-460f-a675-84aa8be82187/simular"
```

*Vuelve a la pantalla de GraphiQL para que el docente vea las coordenadas fluyendo dinámicamente:*
**"Aquí puede ver cómo las coordenadas GPS simuladas fluyen instantáneamente en formato JSON al cliente de GraphQL en tiempo real. No hay peticiones HTTP continuas (polling), es una conexión persistente reactiva mediante WebSockets estructurados en GraphQL."**

---

## ⚓ INFRAESTRUCTURA DEVOPS Y KUBERNETES (Estudiante 2)

*(Muestra la carpeta `k8s/` en el IDE o abre `microservices.yaml`)*

**"Para desplegar y escalar este ecosistema de 8 microservicios de manera profesional, implementamos manifiestos de Kubernetes:"**

1. **Infraestructura (`k8s/infrastructure.yaml`)**:
   **"Provisiona una base de datos PostgreSQL multi-esquema con un script de inicialización automática (`postgres-init-sql`) y la instancia del broker RabbitMQ."**
2. **Microservicios (`k8s/microservices.yaml`)**:
   **"Define los Deployments y Services de los 8 contenedores. Se configuró `imagePullPolicy: IfNotPresent` para utilizar las imágenes compiladas localmente en Docker, y se inyectaron variables de entorno DNS internas para interconectar los servicios dentro del clúster (ej. `http://ms-clientes:8084`)."**
3. **Ingress (`k8s/ingress.yaml`)**:
   **"Utilizamos Nginx Ingress para exponer un único punto de entrada en el puerto 80 del host, enrutando dinámicamente el tráfico a `/api/auth`, `/api/pedidos`, `/graphql` y `/ws/seguimiento`."**

---

## 📊 INTEGRACIÓN CONTINUA Y CALIDAD CON SONARCLOUD (Estudiante 2)

*(Muestra el archivo `.github/workflows/ci.yml` y luego abre tu panel de SonarCloud)*

**"Finalmente, el aseguramiento de la calidad se automatizó en el pipeline CI/CD en GitHub Actions (`ci.yml`):"**
* **"Al hacer un push, el pipeline compila y corre tests automáticos en los 8 microservicios."**
* **"Posteriormente, el job `sonarcloud-analysis` ejecuta un análisis estático detallado para cada módulo."**
* **"Para comprobar el análisis local, hemos configurado un comando Maven en PowerShell usando el operador de stop-parsing `--%` para evitar que la shell altere los flags `-D`. Esto permite verificar bugs, code smells y vulnerabilidades de seguridad a nivel local antes de subir el código."**

---

## 🗣️ CIERRE DE LA PRESENTACIÓN

### 👤 [Estudiante 1]
**"Con esto demostramos que LogiFlow es una plataforma desacoplada, segura, tolerante a fallos, construida bajo prácticas de Domain-Driven Design y lista para producción en contenedores y orquestación K8s."**

### 👤 [Estudiante 2]
**"Muchas gracias por su atención, ingeniero. Quedamos abiertos a sus preguntas."**
