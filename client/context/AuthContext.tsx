"use client";

import React, {
  createContext,
  useContext,
  useState,
  useEffect,
  ReactNode,
  useCallback,
} from "react";
import { AuthState, User } from "@/types/auth";
import { isLoggedIn } from "@/components/user-components";

const initialAuthState: AuthState = {
  user: null,
  isAuthenticated: false,
  isLoading: true,
};

interface AuthContextTypeWithRefresh extends AuthState {
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextTypeWithRefresh>({
  ...initialAuthState,
  refreshUser: () => Promise.resolve(),
});

export const useAuth = () => {
  return useContext(AuthContext);
};

interface AuthProviderProps {
  children: ReactNode;
}

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  const [authState, setAuthState] = useState<AuthState>(initialAuthState);

  const fetchUser = useCallback(async () => {
    setAuthState((prev) => ({ ...prev, isLoading: true }));

    return new Promise<void>((resolve) => {
      isLoggedIn(
        (u: User | null) => {
          if (u == null) {
            setAuthState({
              user: null,
              isAuthenticated: false,
              isLoading: false,
            });
          } else {
            setAuthState({ user: u, isAuthenticated: true, isLoading: false });
          }
          resolve();
        },
        (e: Error) => {
          console.log(e.message);
          setAuthState({
            user: null,
            isAuthenticated: false,
            isLoading: false,
          });
          resolve();
        },
      );
    });
  }, []);

  const refreshUser = useCallback(() => {
    return fetchUser();
  }, [fetchUser]);

  useEffect(() => {
    fetchUser();
  }, [fetchUser]);

  const value: AuthContextTypeWithRefresh = { ...authState, refreshUser };

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
};
