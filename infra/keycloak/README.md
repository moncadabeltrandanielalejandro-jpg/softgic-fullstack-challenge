# Keycloak — Realm `softgic-solicitudes`

Configuración importada automáticamente al levantar `docker compose up --build` desde [realm-export.json](realm-export.json).

## Usuarios de prueba (no reales, solo demo local)

| Usuario | Contraseña | Rol |
|---|---|---|
| `solicitante.demo` | `Demo_2026!` | SOLICITANTE |
| `analista.demo` | `Demo_2026!` | ANALISTA |
| `supervisor.demo` | `Demo_2026!` | SUPERVISOR |

## Clientes

- `solicitudes-shell`: público, Authorization Code + PKCE (S256), sin client secret. Usado por el frontend.
- `solicitudes-service`: bearer-only, usado conceptualmente como identificador del resource server (la validación real la hace cada microservicio contra el JWKS del realm).

## Notas de seguridad

- Ninguna credencial aquí es real ni corresponde a un sistema institucional; son únicamente para ejecución local del reto.
- En producción, las contraseñas de usuarios de prueba y `KEYCLOAK_ADMIN_PASSWORD` deben gestionarse vía secret manager, nunca en el repositorio.
- El token de acceso (JWT) incluye `realm_access.roles`; los backends lo mapean a `ROLE_<rol>` para `@PreAuthorize`.
