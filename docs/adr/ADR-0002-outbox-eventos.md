# ADR-0002: Publicación confiable de eventos (Outbox)

## Estado
Aceptado

## Contexto
Los eventos no pueden publicarse antes de confirmar la transacción de negocio (riesgo de publicar eventos "fantasma" si hay rollback), pero tampoco se puede perder un evento si el proceso falla justo después del commit.

## Decisión
Se implementa el patrón **Transactional Outbox**:

1. El caso de uso persiste el cambio de estado y una fila en `outbox_events` en la **misma transacción** JDBC.
2. Un publicador desacoplado (scheduled poller en esta entrega; CDC/Debezium como evolución) lee eventos no publicados (`published_at IS NULL`), los envía a Kafka y marca `published_at`.
3. El consumidor (`indicadores-service`) es **idempotente**: registra `event_id` procesados antes de proyectar, así que reintentos o duplicados del broker no duplican el conteo.

## Consecuencias
- Se evita pérdida de eventos (persisten en DB hasta confirmarse publicados) y duplicidad de efectos (idempotencia en consumo).
- Añade una tabla y un job adicional, y latencia de publicación ligada al intervalo del poller (configurable, ej. 500ms–1s).
