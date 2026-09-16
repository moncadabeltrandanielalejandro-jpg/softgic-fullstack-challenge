function fn() {
  var env = karate.env || 'local';
  karate.log('karate.env =', env);

  var config = {
    baseUrl: karate.properties['solicitudes.baseUrl'] || 'http://localhost:8081/api/v1',
    // Tokens de prueba: en un entorno real se obtienen del flujo Client Credentials/Password
    // contra Keycloak con los usuarios de infra/keycloak/README.md. Se documentan como variables
    // para no incrustar tokens reales en el repositorio.
    tokenSolicitante: karate.properties['token.solicitante'] || '',
    tokenAnalista: karate.properties['token.analista'] || '',
    tokenSupervisor: karate.properties['token.supervisor'] || ''
  };

  return config;
}
