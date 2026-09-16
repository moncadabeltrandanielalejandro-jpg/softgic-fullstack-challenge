# Limitaciones y trabajo pendiente

- Helm chart incluido pero no desplegado a un clúster real (no se exige clúster público).
- Observabilidad limitada a `actuator/health` y logs estructurados; falta tracing distribuido (OpenTelemetry) — documentado como mejora.
- El publicador del Outbox se implementa como poller programado simple; una evolución natural es CDC (Debezium) para menor latencia.
- No se implementa un tercer proceso adicional (no es obligatorio según el reto).
- El microfrontend se valida en ejecución federada y standalone; no se cubre despliegue multi-región.
