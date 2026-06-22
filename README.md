# 🏗️ LogiFlow — Plataforma Integral de Gestión Logística

> **Fase 2** — Backend Distribuido, Integración con RabbitMQ, Gateway GraphQL, WebSockets y Despliegue K8s.

---

## 📋 Descripción del Proyecto

**LogiFlow** es una plataforma moderna para la gestión logística de envíos urbanos, provinciales y nacionales. Diseñada bajo los principios de **Domain-Driven Design (DDD)** y desplegada como una arquitectura de **microservicios distribuidos**.

Esta Fase 2 introduce el bus de eventos asíncronos con **RabbitMQ**, una capa de agregación BFF con **GraphQL Gateway**, comunicación en tiempo real con **WebSockets** para el rastreo y empaquetado en manifiestos de **Kubernetes**.

---

## 🗺️ Mapa de Arquitectura (Fase 2)

```
                       ┌─────────────────────────────────┐
                       │        Cliente / Frontend       │
                       └─────┬──────────────┬────────────┘
                             │              │
                       (HTTP / REST)     (GraphQL / WS Subscriptions)
                             │              │
                    ┌────────▼───────┐  ┌───▼────────────────┐
                    │ ms-auth (:8083)│  │graphql-gateway:8080│
                    └────────────────┘  └──────┬──────┬──────┘
                                               │      │
                           ┌───────────────────┘      └───────────────────┐
                           │ (REST)                        (REST)         │
                    ┌──────▼─────────┐                 ┌───▼────────────┐ │
                    │ms-pedidos:8085 │                 │ ms-ruteo :8086 │ │
                    └──────┬─────────┘                 └───┬────────────┘ │
                           │                               │              │
                  (pedido.creado/cancelado)          (envio.asignado)     │
                           │                               │              │
              ┌────────────▼───────────────────────────────▼────────────┐ │
              │                     Broker RabbitMQ                     │ │
              └────────────┬───────────────────────────────┬────────────┘ │
                           │                               │              │
                 (posicion.actualizada)          (posicion.actualizada)   │
                           │                               │              │
                    ┌──────▼─────────┐             ┌──────▼─────────┐     │
                    │ms-seguim.:8087 │             │graphql-gateway │     │
                    │ (WebSockets)   │             │  (Subscription)│     │
                    └────────────────┘             └────────────────┘     │
                                                                          │
                           ┌──────────────────────────────────────────────┘
                           │ (REST)
                    ┌──────▼─────────┐                 ┌────────────────┐
                    │ms-clientes:8084│                 │ ms-flota-rest  │ (:8081)
                    └────────────────┘                 └────────────────┘
```

---

## ⚙️ Stack Tecnológico

| Componente | Tecnología |
| :--- | :--- |
| **Lenguaje Core** | Java 17 |
| **Framework Base** | Spring Boot 3.2.5 |
| **Gestor de Dependencias** | Apache Maven |
| **Buses de Eventos** | RabbitMQ (Topic Exchange) |
| **Capa de Entrada BFF** | Spring Boot Starter GraphQL |
| **Tiempo Real (Push)** | Spring WebSockets + STOMP Messaging |
| **Bases de Datos** | PostgreSQL (Local/Prod) / H2 (Entornos de Test) |
| **Documentación REST** | SpringDoc OpenAPI (Swagger UI) |
| **Análisis de Calidad** | SonarCloud |
| **Mensajería DevOps** | Telegram Bot API |

---

## 🔌 Asignación de Puertos y Servicios

| Microservicio | Puerto | Tipo de API | Base de Datos |
| :--- | :---: | :--- | :--- |
| `graphql-gateway` | **8080** | GraphQL (BFF) | *Ninguna* (In-Memory Cache / Reactor Sink) |
| `ms-flota-rest` | **8081** | REST API | PostgreSQL (`logiflow_flota`) |
| `ms-taller-rest` | **8082** | REST API (ACL) | *Ninguna* (In-Memory Repository) |
| `ms-auth` | **8083** | REST API | PostgreSQL (`logiflow_auth`) |
| `ms-clientes` | **8084** | REST API | PostgreSQL (`logiflow_clientes`) |
| `ms-pedidos` | **8085** | REST API + RabbitMQ Pub | PostgreSQL (`logiflow_pedidos`) |
| `ms-ruteo` | **8086** | REST API + RabbitMQ Sub/Pub | PostgreSQL (`logiflow_ruteo`) |
| `ms-seguimiento` | **8087** | WebSockets + RabbitMQ Sub | *Ninguna* |

---

## 🚀 Guía de Ejecución Local

### 1. Prerrequisitos
- **Java JDK 17** instalado y configurado en el `PATH`.
- **PostgreSQL** ejecutándose localmente en el puerto `5432` con usuario `postgres` y contraseña `1234`.
- **Docker Desktop** (o servicio de RabbitMQ) para arrancar el Message Broker.

### 2. Inicializar Bases de Datos
Si PostgreSQL se ejecuta localmente, las siguientes bases de datos deben existir (se crean automáticamente con el script `db-init/init-db.sql`):
- `logiflow_flota`
- `logiflow_auth`
- `logiflow_clientes`
- `logiflow_pedidos`
- `logiflow_ruteo`

### 3. Levantar RabbitMQ con Docker
Arranca el broker de RabbitMQ y el dashboard de administración ejecutando:
```bash
docker compose up -d rabbitmq
```
> [!NOTE]
> El panel de administración de RabbitMQ estará disponible en [http://localhost:15672](http://localhost:15672) (credenciales: `guest` / `guest`).

### 4. Arrancar los Microservicios
Ejecuta el siguiente comando en directorios independientes para iniciar cada microservicio:
```bash
# Cambia a la carpeta correspondiente y corre:
mvn spring-boot:run
```
Arráncalos en este orden recomendado:
1. `ms-auth` (:8083)
2. `ms-clientes` (:8084)
3. `ms-flota-rest` (:8081)
4. `ms-taller-rest` (:8082)
5. `ms-pedidos` (:8085)
6. `ms-ruteo` (:8086)
7. `ms-seguimiento` (:8087)
8. `graphql-gateway` (:8080)

---

## 🧪 Pruebas de Integración y Flujo End-to-End

### 1. Registrar Cliente y Vehículo
1. **Registrar Cliente**:
   Realiza una petición POST a `http://localhost:8084/api/clientes` con el JSON:
   ```json
   {
     "nombre": "Corporación Favorita",
     "email": "contacto@favorita.com",
     "telefono": "0999999999",
     "tipo": "CORPORATIVO",
     "identificacion": "1790000001001"
   }
   ```
2. **Registrar Cuenta Corporativa**:
   POST a `http://localhost:8084/api/clientes/cuentas` con:
   ```json
   {
     "clienteId": "<ID_DEL_CLIENTE_CREADO>",
     "saldo": 500.00,
     "limiteCredito": 200.00
   }
   ```
3. **Registrar Vehículo**:
   POST a `http://localhost:8081/api/vehiculos` con:
   ```json
   {
     "matricula": "PDF-9876",
     "tipo": "CAMION",
     "capacidad": 1500.0,
     "estado": "DISPONIBLE"
   }
   ```
4. **Registrar Conductor**:
   POST a `http://localhost:8081/api/conductores` con:
   ```json
   {
     "cedula": "1722222222",
     "nombre": "Carlos Pérez",
     "licencia": "Tipo E",
     "disponible": true
   }
   ```

### 2. Crear Pedido y Ver Asignación Asíncrona (RabbitMQ)
Usa la interfaz web **GraphiQL** en [http://localhost:8080/graphiql](http://localhost:8080/graphiql) para ejecutar la siguiente Mutation:

```graphql
mutation {
  crearPedido(input: {
    clienteId: "<ID_DEL_CLIENTE>"
    descripcion: "Carga consolidada de suministros"
    peso: 250.5
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

> [!IMPORTANT]
> **Efecto en Cadena**:
> 1. `graphql-gateway` llama a `ms-pedidos`.
> 2. `ms-pedidos` crea el pedido y publica `pedido.creado` en RabbitMQ.
> 3. `ms-ruteo` consume el evento, consulta disponibilidad de flota, asocia el vehículo `PDF-9876` y conductor `Carlos Pérez`, actualiza sus estados a `EN_SERVICIO`/`No disponible`, registra el `Envio` en base de datos y publica `envio.asignado`.

Para verificar la ruta del envío generado, ejecuta en GraphiQL:
```graphql
query {
  envio(id: "<ID_DEL_ENVIO_ASIGNADO>") {
    id
    ruta
    estado
    kms
    eta
  }
}
```

### 3. Simulación de GPS y Suscripción WebSocket en Tiempo Real
1. **Suscribirse al Rastreo (GraphQL Subscription)**:
   En GraphiQL, suscríbete al canal de tracking para recibir coordenadas:
   ```graphql
   subscription {
     tracking(envioId: "<ID_DEL_ENVIO>") {
       envioId
       lat
       lng
       velocidad
       timestamp
     }
   }
   ```
2. **Conexión WebSocket Nativa**:
   Alternativamente, puedes conectar un cliente WebSocket (como Hoppscotch o Postman) a `ws://localhost:8087/ws/seguimiento` y suscribirte al topic `/topic/seguimiento/<ID_DEL_ENVIO>`.
3. **Simular GPS**:
   Realiza una petición POST a `http://localhost:8086/api/ruteo/envios/<ID_DEL_ENVIO>/simular` (puedes enviar `lat`, `lng`, `velocidad` opcionales como query params). 
   *Verás cómo las coordenadas fluyen al instante a tu suscripción activa.*

---

## ⚓ Kubernetes Despliegue

Todos los manifiestos YAML necesarios para desplegar LogiFlow en local o en cloud se encuentran en la carpeta [/k8s](./k8s):
- `k8s/infrastructure.yaml`: Provisionamiento de Postgres y RabbitMQ.
- `k8s/microservices.yaml`: Despliegue de los 8 contenedores de microservicios con DNS interno.
- `k8s/ingress.yaml`: Reglas de enrutamiento basadas en rutas usando Nginx Ingress.

Para aplicar en tu clúster:
```bash
kubectl apply -f k8s/infrastructure.yaml
kubectl apply -f k8s/microservices.yaml
kubectl apply -f k8s/ingress.yaml
```
