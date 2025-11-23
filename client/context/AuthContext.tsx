"use client"

import React, { createContext, useContext, useState, useEffect, ReactNode, useCallback } from 'react';
import { AuthState, User } from '@/types/auth';

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
    refreshUser: () => Promise.resolve()
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
      setAuthState(prev => ({ ...prev, isLoading: true }));
      try {
        const response = await fetch("http://localhost:6600/api/v1/user/me", {credentials: "include"});

        if (response.ok) {
          const userData: User = await response.json();
          setAuthState({
            user: userData,
            isAuthenticated: true,
            isLoading: false,
          });

        } else {
          setAuthState({ user: null, isAuthenticated: false, isLoading: false });
        }
      } catch (error) {
        console.error("Failed to fetch user status:", error);
        setAuthState({ user: null, isAuthenticated: false, isLoading: false });
      }
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