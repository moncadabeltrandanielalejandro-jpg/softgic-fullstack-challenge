import Keycloak from 'keycloak-js';

// Authorization Code + PKCE: sin client secret ni contraseña almacenados en el navegador.
export const keycloak = new Keycloak({
  url: import.meta.env.VITE_KEYCLOAK_URL ?? 'http://localhost:8080',
  realm: 'softgic-solicitudes',
  clientId: 'solicitudes-shell',
});

export async function initKeycloak(): Promise<boolean> {
  return keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  });
}
