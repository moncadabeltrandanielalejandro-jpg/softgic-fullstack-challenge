const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8081/api/v1';

/** Cliente HTTP mínimo; el token se inyecta desde el shell vía interceptor (pendiente de integrar). */
export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...init?.headers,
    },
  });

  if (response.status === 401 || response.status === 403) {
    throw new Error(`No autorizado (${response.status})`);
  }
  if (!response.ok) {
    throw new Error(`Error de API: ${response.status}`);
  }
  return response.json() as Promise<T>;
}
