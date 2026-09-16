# Diagrama de secuencia — Flujo principal y eventos

```mermaid
sequenceDiagram
    actor Sol as Solicitante
    actor Ana as Analista
    participant Shell
    participant API as Servicio de Solicitudes
    participant DB as SQL Server (Operacional)
    participant OUT as Outbox
    participant Bus as Kafka
    participant IND as Servicio de Indicadores

    Sol->>Shell: Crear solicitud (asunto, descripción, categoría, prioridad)
    Shell->>API: POST /api/v1/solicitudes (JWT)
    API->>API: Validar rol SOLICITANTE + payload (Zod/Bean Validation)
    API->>DB: INSERT solicitud (REGISTRADA) + historial
    API->>OUT: INSERT evento SolicitudRegistrada (misma transacción)
    API-->>Shell: 201 Created
    OUT->>Bus: Publicar evento (poller/CDC, post-commit)
    Bus->>IND: Consumir SolicitudRegistrada
    IND->>IND: Verificar eventId no procesado (idempotencia)
    IND->>IND: Actualizar fact_transiciones / dimensiones

    Ana->>Shell: Tomar solicitud
    Shell->>API: POST /{id}/asignaciones (JWT rol ANALISTA)
    API->>DB: UPDATE ... WHERE estado='REGISTRADA' AND analista_id IS NULL
    alt Actualización afectó 1 fila
        API->>OUT: INSERT evento SolicitudTomada
        API-->>Shell: 200 OK
    else 0 filas afectadas (ya tomada)
        API-->>Shell: 409 Conflict
    end
    OUT->>Bus: Publicar SolicitudTomada
    Bus->>IND: Consumir (idempotente)

    Ana->>Shell: Resolver (con observación)
    Shell->>API: POST /{id}/transiciones (RESUELTA)
    API->>DB: UPDATE estado=RESUELTA + historial + observación
    API->>OUT: INSERT evento SolicitudResuelta
    API-->>Shell: 200 OK
    OUT->>Bus: Publicar SolicitudResuelta
    Bus->>IND: Consumir (idempotente)
```

## Notas de confiabilidad de eventos

- **Patrón Outbox**: el cambio de estado y el registro del evento se persisten en la misma transacción JDBC. Un publicador (poller programado o Debezium/CDC) lee la tabla `outbox_events` y publica a Kafka, marcando el evento como enviado. Esto evita publicar eventos de transacciones que luego hacen rollback, y evita perder eventos si el proceso cae antes de publicar (se reintenta desde la tabla).
- **Idempotencia en el consumidor**: `indicadores-service` registra `eventId` procesados (tabla `processed_events` con `UNIQUE (event_id)`) antes de aplicar la proyección; si el evento ya fue procesado, se descarta sin duplicar el conteo (cubre escenario A5).
