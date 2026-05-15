# LogiFlow — Plataforma de Gestión Logística

> **Fase 1** — Fundación de Microservicios con DDD, REST, SOAP y CI/CD

---

## Descripción del Proyecto

**LogiFlow** es una plataforma de gestión logística diseñada bajo los principios de **Domain-Driven Design (DDD)** y **arquitectura de microservicios**. El proyecto nace como respuesta a la problemática de un monolito heredado que dificulta la escalabilidad, mantenibilidad y evolución independiente de los dominios de negocio.

### Arquitectura de la Fase 1

```
┌─────────────────────────────────────────────────┐
│                   LogiFlow                       │
│                                                  │
│  ┌──────────────────┐  ┌──────────────────────┐ │
│  │  ms-flota-rest   │  │   ms-taller-rest     │ │
│  │  (REST / JSON)   │  │   (REST / JSON)      │ │
│  │                  │  │                      │ │
│  │  • CRUD Vehículos│  │  • GET vehiculo      │ │
│  │  • CRUD Conduct. │  │  • POST mantenim.    │ │
│  │  • Disponibilidad│  │  • Capa Anticorrup.  │ │
│  │                  │  │                      │ │
│  │  Puerto: 8081    │  │  Puerto: 8082        │ │
│  └────────┬─────────┘  └──────────┬───────────┘ │
│           │                       │              │
│     ┌─────▼─────┐          ┌─────▼──────┐       │
│     │ PostgreSQL│          │ In-Memory  │       │
│     │ (logiflow │          │ Repository │       │
│     │  _flota)  │          │ (Simulado) │       │
│     └───────────┘          └────────────┘       │
└─────────────────────────────────────────────────┘
```

---

## Stack Tecnológico

| Componente          | Tecnología                        |
|---------------------|-----------------------------------|
| Lenguaje            | Java 17                           |
| Framework           | Spring Boot 3.2.5                 |
| Build Tool          | Apache Maven                      |
| REST API            | Spring Web + Bean Validation      |
| Documentación REST  | SpringDoc OpenAPI (Swagger UI)    |
| Base de Datos       | PostgreSQL (prod) / H2 (test)     |
| Documentación REST  | SpringDoc OpenAPI (Swagger UI)    |
| CI/CD               | GitHub Actions                    |
| Análisis de Calidad | SonarCloud                        |
| Notificaciones      | Telegram Bot API                  |
| ORM                 | Hibernate (JPA)                   |
| Boilerplate         | Lombok                            |

---

## Instrucciones de Ejecución Local

### Prerrequisitos

1. **JDK 17** — [Descargar Temurin](https://adoptium.net/)
2. **Maven 3.8+** — [Descargar](https://maven.apache.org/download.cgi)
3. **PostgreSQL 14+** — [Descargar](https://www.postgresql.org/download/)

### 1. Configurar Base de Datos (solo ms-flota-rest)

```sql
-- Conectarse a PostgreSQL y crear la base de datos
CREATE DATABASE logiflow_flota;
```

> **Nota:** El microservicio `ms-taller-rest` no requiere base de datos; usa un repositorio en memoria.

### 2. Ejecutar ms-flota-rest (REST)

```bash
cd ms-flota-rest
mvn clean compile
mvn spring-boot:run
```

**Endpoints disponibles:**
- API REST: `http://localhost:8081/api/vehiculos` y `http://localhost:8081/api/conductores`
- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/api-docs`

### 3. Ejecutar ms-taller-rest (REST)

```bash
cd ms-taller-rest
mvn clean compile
mvn spring-boot:run
```

**Endpoints disponibles:**
- API REST: `http://localhost:8082/api/taller/vehiculos/{matricula}` y `http://localhost:8082/api/taller/mantenimientos`
- Swagger UI: `http://localhost:8082/swagger-ui.html`

### 4. Ejecutar Tests

```bash
# ms-flota-rest (usa H2 automáticamente)
cd ms-flota-rest
mvn test

# ms-taller-rest
cd ms-taller-rest
mvn test
```

---

## Pipeline CI/CD (GitHub Actions)

El pipeline se ejecuta automáticamente en pushes y PRs a las ramas `main` y `development`.

### Jobs del Pipeline

| Job                     | Descripción                                          |
|-------------------------|------------------------------------------------------|
| `build-ms-flota-rest`   | Compila y ejecuta tests del microservicio REST       |
| `build-ms-taller-rest`  | Compila y ejecuta tests del microservicio REST       |
| `sonarcloud-analysis`   | Analiza calidad de código con SonarCloud             |
| `notify-telegram`       | Envía resultados del build a un grupo de Telegram    |

### Configuración de Secretos en GitHub

Para que el pipeline funcione correctamente, configura los siguientes secretos en:
**Settings → Secrets and variables → Actions → New repository secret**

| Secreto                  | Descripción                                                  | Ejemplo                    |
|--------------------------|--------------------------------------------------------------|----------------------------|
| `SONAR_TOKEN`            | Token de autenticación de SonarCloud                         | `sqa_xxxxxxxxxxxx`         |
| `SONAR_ORGANIZATION`     | Nombre de la organización en SonarCloud                      | `logiflow`                 |
| `TELEGRAM_BOT_TOKEN`     | Token del bot de Telegram (obtenido con @BotFather)          | `123456:ABC-DEF...`        |
| `TELEGRAM_CHAT_ID`       | ID del chat/grupo de Telegram para notificaciones            | `-1001234567890`           |

### Cómo obtener los secretos

#### SonarCloud
1. Ir a [sonarcloud.io](https://sonarcloud.io) → Iniciar sesión con GitHub
2. Crear organización y proyecto
3. Ir a **My Account → Security** → Generar token

#### Telegram
1. Buscar `@BotFather` en Telegram → `/newbot` → Obtener el token
2. Crear un grupo, agregar el bot
3. Enviar un mensaje al grupo
4. Visitar: `https://api.telegram.org/bot<TOKEN>/getUpdates` → Copiar el `chat.id`

---

## Estructura del Proyecto

```
ProyectoP1/
├── .github/
│   └── workflows/
│       └── ci.yml                    # Pipeline CI/CD
├── ms-flota-rest/                    # Bounded Context: Gestión de Flota
│   ├── pom.xml
│   └── src/main/java/ec/edu/espe/msflotarest/
│       ├── MsFlotaRestApplication.java
│       ├── config/
│       │   └── OpenApiConfig.java    # Swagger/OpenAPI
│       ├── controllers/
│       │   ├── VehiculoController.java
│       │   └── ConductorController.java
│       ├── dtos/
│       │   ├── VehiculoDTO.java
│       │   └── ConductorDTO.java
│       ├── exception/
│       │   ├── GlobalExceptionHandler.java
│       │   ├── ResourceNotFoundException.java
│       │   └── BusinessException.java
│       ├── models/
│       │   ├── Vehiculo.java
│       │   ├── Conductor.java
│       │   └── enums/
│       │       ├── EstadoVehiculo.java
│       │       └── TipoVehiculo.java
│       ├── repositories/
│       │   ├── VehiculoRepository.java
│       │   └── ConductorRepository.java
│       └── service/
│           ├── IVehiculoService.java
│           ├── IConductorService.java
│           └── impl/
│               ├── VehiculoServiceImpl.java
│               └── ConductorServiceImpl.java
├── ms-taller-rest/                   # Bounded Context: Taller Externo (ACL)
│   ├── pom.xml
│   └── src/main/java/ec/edu/espe/taller/
│       ├── MsTallerApplication.java
│       ├── controllers/
│       │   └── TallerController.java
│       ├── dtos/
│       │   └── MantenimientoDTO.java
│       ├── models/                   # ← Capa Anticorrupción / Modelo interno
│       │   ├── VehiculoTaller.java
│       │   └── OrdenMantenimiento.java
│       ├── repository/
│       │   └── TallerRepository.java
│       └── service/
│           ├── ITallerService.java
│           └── impl/
│               └── TallerServiceImpl.java
├── DDD_ANALYSIS.md                   # Documento de Análisis DDD
├── EXPLICACION_Y_PRUEBAS.md          # Explicación y escenarios de prueba
├── README.md                         # Este archivo
└── .gitignore
```

---

## Documentación Adicional

| Documento                                      | Contenido                                            |
|------------------------------------------------|------------------------------------------------------|
| [`DDD_ANALYSIS.md`](./DDD_ANALYSIS.md)         | Event Storming, Bounded Contexts, Context Map, Lenguaje Ubicuo |
| [`EXPLICACION_Y_PRUEBAS.md`](./EXPLICACION_Y_PRUEBAS.md) | Explicación de funcionamiento y escenarios de prueba |

---

## Equipo

- **Universidad:** Universidad de las Fuerzas Armadas — ESPE
- - **Integrantes:** Jean Saltos y Tupac Velasquez
- **Asignatura:** Arquitectura de Software
- **Fase:** 1 — Fundación de Microservicios

---

## Licencia

Proyecto de uso exclusivamente académico.
