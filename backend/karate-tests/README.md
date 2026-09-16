# Suite Karate transversal

Cubre A1 (registro exitoso), A3 (403 sin rol) y el recorrido REGISTRADA → EN_ATENCION → RESUELTA (contrato en [`ciclo-vida-solicitud.feature`](src/test/java/features/solicitudes/ciclo-vida-solicitud.feature)).

## Estrategia

- Se ejecuta contra el stack levantado por `docker compose up --build` (`solicitudes-service` real + Keycloak real), no contra dobles/mocks, para validar el contrato end-to-end.
- Los tokens de los tres roles se obtienen fuera del repositorio (script local o variables de entorno `token.solicitante`, `token.analista`, `token.supervisor`) — nunca se commitean tokens reales.
- `categoriaId` debe reemplazarse por un UUID válido de la semilla de categorías (`SELECT id FROM categorias`).

## Ejecución

```powershell
cd backend/karate-tests
mvn test `
  "-Dsolicitudes.baseUrl=http://localhost:8081/api/v1" `
  "-Dtoken.solicitante=$env:TOKEN_SOLICITANTE" `
  "-Dtoken.analista=$env:TOKEN_ANALISTA" `
  "-Dtoken.supervisor=$env:TOKEN_SUPERVISOR"
```

## Estado

Escenario de estructura lista; pendiente automatizar la obtención de tokens (password grant contra Keycloak con los usuarios de prueba) en un script de soporte.
