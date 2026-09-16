const frontendEnv = (import.meta as ImportMeta & { env?: Record<string, string> }).env ?? {};
const API_BASE_URL = frontendEnv.VITE_API_BASE_URL ?? 'http://localhost:8081/api/v1';
const INDICADORES_API_BASE_URL = frontendEnv.VITE_INDICADORES_API_BASE_URL ?? 'http://localhost:8082/api/v1';

declare global {
  interface Window {
    __softgicAccessToken?: () => string | undefined;
  }
}

export async function apiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  return fetchFromApi<T>(API_BASE_URL, path, init);
}

export async function indicadoresApiFetch<T>(path: string, init?: RequestInit): Promise<T> {
  return fetchFromApi<T>(INDICADORES_API_BASE_URL, path, init);
}

async function fetchFromApi<T>(baseUrl: string, path: string, init?: RequestInit): Promise<T> {
  const token = typeof window !== 'undefined' ? window.__softgicAccessToken?.() : undefined;
  const response = await fetch(`${baseUrl}${path}`, {
    ...init,
    headers: {
      'Content-Type': 'application/json',
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
      ...init?.headers,
    },
  });

  if (response.status === 401 || response.status === 403) {
    throw new Error(`No autorizado (${response.status})`);
  }
  if (!response.ok) {
    throw new Error(`Error de API: ${response.status}`);
  }
  if (response.status === 204) return undefined as T;
  const contentType = response.headers.get('content-type') ?? '';
  if (!contentType.includes('application/json')) return undefined as T;
  return response.json() as Promise<T>;
}
