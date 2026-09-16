# Uso de IA en esta entrega

## Herramientas utilizadas
- GitHub Copilot (Claude Sonnet) como asistente de codificación dentro de VS Code.

## Actividades apoyadas por IA
- Generación del andamiaje inicial del repositorio (estructura de carpetas, esqueletos de módulos backend/frontend, Docker Compose, pipeline CI).
- Redacción inicial de documentación (README, ADRs, diagramas en Mermaid) a partir de las decisiones tomadas por el candidato.
- Sugerencias de implementación para el patrón Outbox, control de concurrencia optimista y validaciones de máquina de estados.

## Verificaciones realizadas
- Revisión manual de cada archivo generado antes de aceptarlo.
- Ejecución local de `mvn verify` / `npm test` para validar que el código compila y las pruebas pasan.
- Validación de que no se incluyen datos reales, credenciales ni información institucional del cliente.

## Decisiones propias del candidato
- Alcance de la entrega (qué se implementa completo vs. qué se documenta como pendiente).
- Elección de patrón Outbox sobre publicación directa para garantizar entrega confiable de eventos.
- Estrategia de control de concurrencia en la asignación de solicitudes (UPDATE condicional / optimistic locking).
- Modelo de datos operacional y analítico, y separación de responsabilidades entre servicios.

_(Actualizar este documento a medida que se avance en la implementación.)_
