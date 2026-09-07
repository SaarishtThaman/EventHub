const AUTH_BASE_URL = "http://localhost:8081";
const EVENT_BASE_URL = "http://localhost:8082";
const BOOKING_BASE_URL = "http://localhost:8083";

const TOKEN_KEY = "eventhub_token";

export function getToken(): string | null {
  return localStorage.getItem(TOKEN_KEY);
}

export function setToken(token: string): void {
  localStorage.setItem(TOKEN_KEY, token);
}

export function clearToken(): void {
  localStorage.removeItem(TOKEN_KEY);
}

class ApiError extends Error {
  status: number;
  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

async function request<T>(baseUrl: string, path: string, options: RequestInit = {}): Promise<T> {
  const token = getToken();
  const headers: Record<string, string> = {
    "Content-Type": "application/json",
    ...(options.headers as Record<string, string>),
  };
  if (token) {
    headers["Authorization"] = `Bearer ${token}`;
  }

  const response = await fetch(`${baseUrl}${path}`, { ...options, headers });

  if (!response.ok) {
    const body = await response.text();
    throw new ApiError(response.status, body || response.statusText);
  }

  if (response.status === 204 || response.headers.get("content-length") === "0") {
    return undefined as T;
  }

  return response.json() as Promise<T>;
}

export const authApi = {
  base: AUTH_BASE_URL,
  request: <T>(path: string, options?: RequestInit) => request<T>(AUTH_BASE_URL, path, options),
};

export const eventApi = {
  base: EVENT_BASE_URL,
  request: <T>(path: string, options?: RequestInit) => request<T>(EVENT_BASE_URL, path, options),
};

export const bookingApi = {
  base: BOOKING_BASE_URL,
  request: <T>(path: string, options?: RequestInit) => request<T>(BOOKING_BASE_URL, path, options),
};

export { ApiError };
