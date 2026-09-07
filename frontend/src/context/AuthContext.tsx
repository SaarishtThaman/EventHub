import { createContext, useContext, useState, useCallback, type ReactNode } from "react";
import { authApi, clearToken, getToken, setToken } from "../api/client";
import type { AuthResponse } from "../api/types";

interface DecodedToken {
  userId: string;
  role: string;
}

function decodeToken(token: string): DecodedToken {
  const payload = token.split(".")[1];
  const json = JSON.parse(atob(payload.replace(/-/g, "+").replace(/_/g, "/")));
  return { userId: json.sub, role: json.role };
}

interface AuthContextValue {
  userId: string | null;
  role: string | null;
  isAuthenticated: boolean;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const existingToken = getToken();
  const decoded = existingToken ? decodeToken(existingToken) : null;
  const [userId, setUserId] = useState<string | null>(decoded?.userId ?? null);
  const [role, setRole] = useState<string | null>(decoded?.role ?? null);

  const applyToken = (token: string) => {
    setToken(token);
    const d = decodeToken(token);
    setUserId(d.userId);
    setRole(d.role);
  };

  const login = useCallback(async (email: string, password: string) => {
    const res = await authApi.request<AuthResponse>("/auth/login", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    applyToken(res.token);
  }, []);

  const signup = useCallback(async (email: string, password: string) => {
    const res = await authApi.request<AuthResponse>("/auth/signup", {
      method: "POST",
      body: JSON.stringify({ email, password }),
    });
    applyToken(res.token);
  }, []);

  const logout = useCallback(() => {
    clearToken();
    setUserId(null);
    setRole(null);
  }, []);

  return (
    <AuthContext.Provider value={{ userId, role, isAuthenticated: !!userId, login, signup, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error("useAuth must be used within AuthProvider");
  return ctx;
}
