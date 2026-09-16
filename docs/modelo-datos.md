# Modelo de datos

## Operacional (normalizado) — `solicitudes-service`

```mermaid
erDiagram
    CATEGORIA ||--o{ SOLICITUD : clasifica
    SOLICITUD ||--o{ HISTORIAL_ESTADO : registra
    SOLICITUD ||--o{ OBSERVACION : contiene
    SOLICITUD ||--o{ OUTBOX_EVENT : origina

    CATEGORIA {
        uuid id PK
        string nombre
        boolean activa
    }
    SOLICITUD {
        uuid id PK
        string codigo "identificador legible, ej. SOL-2026-000123"
        string asunto
        string descripcion
        uuid categoria_id FK
        string prioridad "BAJA|MEDIA|ALTA"
        string estado "REGISTRADA|EN_ATENCION|RESUELTA|CERRADA"
        uuid solicitante_id
        uuid analista_id "nullable"
        timestamp creado_en
        timestamp actualizado_en
        long version "optimistic locking"
    }
    HISTORIAL_ESTADO {
        uuid id PK
        uuid solicitud_id FK
        string estado_origen
        string estado_destino
        uuid actor_id
        string rol_actor
        string motivo
        timestamp ocurrido_en
    }
    OBSERVACION {
        uuid id PK
        uuid solicitud_id FK
        uuid autor_id
        string texto
        timestamp creado_en
    }
    OUTBOX_EVENT {
        uuid event_id PK
        string aggregate_id
        string type
        int version
        string correlation_id
        string payload_json
        timestamp occurred_at
        timestamp published_at "nullable"
    }
```

## Analítico (estrella) — `indicadores-service`

```mermaid
erDiagram
    FACT_TRANSICIONES }o--|| DIM_FECHA : ocurre_en
    FACT_TRANSICIONES }o--|| DIM_CATEGORIA : clasifica
    FACT_TRANSICIONES }o--|| DIM_ESTADO : estado
    FACT_TRANSICIONES }o--|| DIM_ACTOR : actor

    FACT_TRANSICIONES {
        uuid id PK
        string event_id "idempotencia, unique"
        uuid solicitud_id
        int fecha_key FK
        int categoria_key FK
        int estado_key FK
        int actor_key FK
        timestamp ocurrido_en
    }
    DIM_FECHA {
        int fecha_key PK
        date fecha
        int anio
        int mes
        int dia
    }
    DIM_CATEGORIA {
        int categoria_key PK
        uuid categoria_id
        string nombre
    }
    DIM_ESTADO {
        int estado_key PK
        string codigo
    }
    DIM_ACTOR {
        int actor_key PK
        uuid actor_id
        string rol "sin datos personales, solo rol"
    }
```

Se evita replicar datos personales: `dim_actor` guarda únicamente `actor_id` (identificador técnico) y `rol`, no nombre/correo.
