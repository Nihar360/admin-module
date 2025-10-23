import React, { createContext, useContext, useState, useEffect } from 'react';
import { adminApi } from '@/api/adminApi';

interface Admin {
  id: string;
  email: string;
  name: string;
  role: string;
}

interface AdminAuthContextType {
  admin: Admin | null;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  isAuthenticated: boolean;
}

const AdminAuthContext = createContext<AdminAuthContextType | undefined>(undefined);

export const AdminAuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [admin, setAdmin] = useState<Admin | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('adminToken');
      const storedAdmin = localStorage.getItem('adminUser');
      
      if (token && storedAdmin) {
        try {
          const user = await adminApi.getCurrentUser();
          const adminData: Admin = {
            id: user.data.id.toString(),
            email: user.data.email,
            name: user.data.fullName,
            role: user.data.role,
          };
          setAdmin(adminData);
          localStorage.setItem('adminUser', JSON.stringify(adminData));
        } catch (error) {
          console.error('Failed to verify token:', error);
          localStorage.removeItem('adminToken');
          localStorage.removeItem('adminUser');
        }
      }
      setIsLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await adminApi.login(email, password);
      
      if (response.success && response.data) {
        const { token, user } = response.data;
        
        const adminData: Admin = {
          id: user.id.toString(),
          email: user.email,
          name: user.fullName,
          role: user.role,
        };
        
        localStorage.setItem('adminToken', token);
        localStorage.setItem('adminUser', JSON.stringify(adminData));
        setAdmin(adminData);
      } else {
        throw new Error(response.message || 'Login failed');
      }
    } catch (error: any) {
      const errorMessage = error.response?.data?.message || error.message || 'Invalid credentials';
      throw new Error(errorMessage);
    }
  };

  const logout = () => {
    setAdmin(null);
    localStorage.removeItem('adminToken');
    localStorage.removeItem('adminUser');
  };

  return (
    <AdminAuthContext.Provider
      value={{
        admin,
        isLoading,
        login,
        logout,
        isAuthenticated: !!admin,
      }}
    >
      {children}
    </AdminAuthContext.Provider>
  );
};

export const useAdminAuth = () => {
  const context = useContext(AdminAuthContext);
  if (context === undefined) {
    throw new Error('useAdminAuth must be used within an AdminAuthProvider');
  }
  return context;
};
