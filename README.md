# Plataforma de Gestión de Solicitudes Operacionales

Prueba técnica Softgic — Desarrollador(a) Full Stack (arquitectura hexagonal, microservicios, eventos y microfrontends).

> **Alcance implementado en esta entrega**: ver sección [Alcance y decisiones](#alcance-y-decisiones). Este repo es un punto de partida en construcción activa; cada módulo documenta su estado (implementado / diseñado-no-implementado).

## Resumen

Sistema para registrar, asignar y hacer seguimiento a solicitudes operacionales internas de una entidad de gobierno. Compuesto por:

- **`backend/solicitudes-service`**: escritura y consulta operacional. Arquitectura hexagonal (dominio/casos de uso aislados de HTTP, JPA, Keycloak y broker). Publica eventos de dominio vía patrón *Outbox*.
- **`backend/indicadores-service`**: consumidor de eventos, mantiene un modelo de lectura (esquema estrella) y expone indicadores agregados.
- **`frontend/shell`**: aplicación contenedora (Module Federation host) — login/redirección, layout, enrutamiento.
- **`frontend/mfe-solicitudes`**: microfrontend remoto — bandeja, creación, detalle/línea de tiempo.
- **`infra/`**: Docker Compose, configuración de Keycloak, Helm chart, pipeline GitLab CI.

## Arquitectura

Ver [docs/c4.md](docs/c4.md) (contexto + contenedores) y [docs/secuencia-flujo-principal.md](docs/secuencia-flujo-principal.md).

Decisiones de diseño registradas como ADRs en [docs/adr](docs/adr).

## Seguridad

Keycloak como Identity Provider. Frontend usa Authorization Code + PKCE (sin client secret en navegador). Backend valida JWT como Resource Server y aplica autorización por rol (`SOLICITANTE`, `ANALISTA`, `SUPERVISOR`) en el servidor. Ver [infra/keycloak/README.md](infra/keycloak/README.md).

## Modelo de datos

- Operacional (normalizado): `solicitudes`, `categorias`, `observaciones`, `historial_estados`, `outbox_events`.
- Analítico (estrella): tabla de hechos `fact_transiciones` + dimensiones `dim_fecha`, `dim_categoria`, `dim_estado`, `dim_actor`.

Ver [docs/modelo-datos.md](docs/modelo-datos.md).

## Ejecución local

Prerrequisitos: Docker + Docker Compose, JDK 25, Node 20+, pnpm/npm.

```powershell
docker compose up --build
```

Esto levanta: SQL Server, Kafka (+ Zookeeper/KRaft), Keycloak, `solicitudes-service`, `indicadores-service`. El frontend se ejecuta aparte durante desarrollo (ver [frontend/README.md](frontend/README.md)).

Variables de entorno de ejemplo en [`.env.example`](.env.example). Usuarios de prueba documentados en [infra/keycloak/README.md](infra/keycloak/README.md).

Validación de salud:

```powershell
curl http://localhost:8081/actuator/health   # solicitudes-service
curl http://localhost:8082/actuator/health   # indicadores-service
```

## Pruebas

- Backend: JUnit 5 + Mockito, reporte JaCoCo (`mvn verify`).
- Frontend: Vitest + React Testing Library.
- Contrato/E2E: suite Karate en [`backend/karate-tests`](backend/karate-tests) cubriendo A1, A3 y el recorrido REGISTRADA → EN_ATENCION → RESUELTA.

## CI/CD

Pipeline GitLab CI ([.gitlab-ci.yml](.gitlab-ci.yml)) con etapas: lint/compilación, pruebas, cobertura, build de imágenes, validación de Helm. Despliegue como etapa manual.

## Alcance y decisiones

| Área | Estado |
|---|---|
| Camino feliz (registrar → tomar → resolver) | Implementado |
| A2 (concurrencia doble asignación) | Implementado (bloqueo optimista / condición atómica en UPDATE) |
| A3 (403 sin rol) | Implementado |
| A4 (transición inválida) | Implementado |
| A5 (idempotencia consumidor) | Implementado (tabla de eventos procesados por `eventId`) |
| Devolver/cerrar (Supervisor) | Implementado |
| Módulo de Indicadores completo (tendencia diaria) | Implementado |
| Helm / despliegue real a clúster | Diseñado, no desplegado (manifiestos incluidos, no aplicados) |
| Observabilidad (tracing distribuido) | Parcial / documentado como trabajo pendiente |

## Limitaciones y trabajo pendiente

Ver [docs/limitaciones.md](docs/limitaciones.md).

## Uso de IA

Ver [USO_DE_IA.md](USO_DE_IA.md).
