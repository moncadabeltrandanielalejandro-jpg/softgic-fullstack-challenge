# ADR-0001: Arquitectura hexagonal en el Servicio de Solicitudes

## Estado
Aceptado

## Contexto
El reto exige separar el dominio y los casos de uso de HTTP, JPA, Keycloak y el broker de mensajería, permitiendo probar reglas de negocio sin infraestructura.

## Decisión
Se adopta arquitectura hexagonal (puertos y adaptadores) en `solicitudes-service`:

- `domain`: entidades y reglas (máquina de estados de `Solicitud`), sin dependencias de frameworks.
- `application`: casos de uso (orquestación), implementan puertos de entrada y dependen solo de puertos de salida (interfaces).
- `infrastructure`: adaptadores concretos — `adapter/in/rest` (controladores), `adapter/out/persistence` (JPA), `adapter/out/messaging` (outbox/Kafka), `adapter/out/security` (mapeo de roles Keycloak).

## Consecuencias
- Mayor cantidad de clases/interfaces frente a un enfoque en capas simple, a cambio de pruebas de dominio rápidas (sin Spring context) y bajo acoplamiento a JPA/Keycloak/Kafka.
- Las pruebas unitarias de dominio y casos de uso usan dobles (Mockito) para los puertos de salida.
