# Frontend — Shell + Microfrontend

## Estructura

- `shell/`: host de Module Federation. Login/redirección (Keycloak PKCE), layout, navegación, monta el remoto.
- `mfe-solicitudes/`: remoto federado. Bandeja, creación, detalle/línea de tiempo, resumen analítico. Ejecutable standalone.

## Ejecución en desarrollo

```powershell
cd frontend/mfe-solicitudes
npm install
npm run dev        # standalone en http://localhost:3001

cd ../shell
npm install
npm run dev         # host en http://localhost:3000, consume el remoto federado
```

## Variables de entorno

Ver [`.env.example`](../.env.example) en la raíz (`VITE_KEYCLOAK_URL`, `VITE_API_BASE_URL`).

## Estado del desarrollo

Este es el andamiaje inicial (bootstrap, configuración de Module Federation sobre Rspack, store de Redux Toolkit, integración base con Keycloak). Las vistas de negocio (bandeja, creación, detalle, analítica) están pendientes de implementación completa — ver [limitaciones](../docs/limitaciones.md).
