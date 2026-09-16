import Keycloak from 'keycloak-js';

// Authorization Code + PKCE: sin client secret ni contraseña almacenados en el navegador.
const frontendEnv = (import.meta as ImportMeta & { env?: Record<string, string> }).env ?? {};

export const keycloak = new Keycloak({
  url: frontendEnv.VITE_KEYCLOAK_URL ?? 'http://localhost:8080',
  realm: 'softgic-solicitudes',
  clientId: 'solicitudes-shell',
});

declare global {
  interface Window {
    __softgicAccessToken?: () => string | undefined;
    __softgicRoles?: () => string[];
  }
}

window.__softgicAccessToken = () => keycloak.token;
window.__softgicRoles = () => (keycloak.tokenParsed?.realm_access?.roles as string[] | undefined) ?? [];

export async function initKeycloak(): Promise<boolean> {
  return keycloak.init({
    onLoad: 'login-required',
    pkceMethod: 'S256',
    checkLoginIframe: false,
  });
}
