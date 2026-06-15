import { createContext, useContext, useState, useEffect, useCallback, ReactNode } from 'react';
import { apiClient } from '@/lib/api';
import type { UserResponse, UserWithProfileResponse } from '@/lib/api';

interface User {
  id: string;
  email: string;
  name: string;
  avatar?: string;
  university: string;
  universityId?: string;
  graduationYear?: number;
  major?: string;
  bio?: string;
  role?: 'alumni' | 'admin' | 'superadmin';
  isMentor?: boolean;
}

interface AuthContextType {
  user: User | null;
  isAdmin: boolean;
  isSuperAdmin: boolean;
  isLoading: boolean;
  login: (email: string, password: string) => Promise<void>;
  logout: () => void;
  updateProfile: (data: Partial<User>) => void;
  requestPasswordReset: (email: string) => Promise<{ success: boolean; universityId?: string; message: string }>;
  loading: boolean;
  refreshUser: () => Promise<void>;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within AuthProvider');
  return context;
};

// Convert backend UserResponse to frontend User format
const convertUserResponse = (userData: UserResponse | UserWithProfileResponse, universityName?: string): User => {
  return {
    id: userData.id,
    email: userData.email,
    name: userData.name,
    avatar: userData.avatar,
    university: universityName || 'Unknown University',
    universityId: userData.university_id,
    graduationYear: userData.graduation_year,
    major: userData.major,
    bio: 'profile' in userData ? userData.profile?.bio : undefined,
    role: userData.role as 'alumni' | 'admin' | 'superadmin',
    isMentor: userData.is_mentor,
  };
};

export const AuthProvider = ({ children }: { children: ReactNode }) => {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  // Check for existing token and fetch user on mount
  useEffect(() => {
    const initAuth = async () => {
      const token = localStorage.getItem('auth_token');
      if (token) {
        try {
          const userData = await apiClient.getCurrentUser();
          const convertedUser = convertUserResponse(userData, userData.university_name);
          setUser(convertedUser);
          localStorage.setItem('alumni_user', JSON.stringify(convertedUser));
        } catch (error) {
          console.error('Failed to fetch user:', error);
          // Token might be invalid, clear it
          apiClient.logout();
          localStorage.removeItem('alumni_user');
        }
      }
      setLoading(false);
    };

    initAuth();
  }, []);

  const login = async (email: string, password: string) => {
    try {
      const response = await apiClient.login({ email, password });
      const convertedUser = convertUserResponse(
        response.user,
        response.university?.name
      );
      setUser(convertedUser);
      localStorage.setItem('alumni_user', JSON.stringify(convertedUser));
    } catch (error: any) {
      console.error('Login failed:', error);
      throw new Error(error.message || 'Login failed. Please check your credentials.');
    }
  };

  const logout = () => {
    apiClient.logout();
    setUser(null);
    localStorage.removeItem('alumni_user');
  };

  const refreshUser = useCallback(async () => {
    const token = localStorage.getItem('auth_token');
    if (!token) return;

    try {
      const response = await apiClient.getCurrentUser();
      const transformedUser = convertUserResponse(response, response.university_name);
      setUser(transformedUser);
      localStorage.setItem('alumni_user', JSON.stringify(transformedUser));
    } catch (error) {
      // Token might be expired, clear everything
      console.error('Failed to refresh user:', error);
      setUser(null);
      localStorage.removeItem('alumni_user');
      localStorage.removeItem('auth_token');
    }
  }, []);

  const requestPasswordReset = async (email: string): Promise<{ success: boolean; universityId?: string; message: string }> => {
    try {
      const result = await apiClient.requestPasswordReset(email);
      return {
        success: result.success,
        message: result.message,
      };
    } catch (error: any) {
      return {
        success: false,
        message: error.message || 'Failed to request password reset',
      };
    }
  };

  const updateProfile = (data: Partial<User>) => {
    if (!user) return;
    const updated = { ...user, ...data };
    setUser(updated);
    localStorage.setItem('alumni_user', JSON.stringify(updated));
  };

  const isAdmin = user?.role === 'admin';
  const isSuperAdmin = user?.role === 'superadmin';

  return (
    <AuthContext.Provider value={{ user, isAdmin, isSuperAdmin, isLoading: loading, login, logout, updateProfile, requestPasswordReset, loading, refreshUser }}>
      {children}
    </AuthContext.Provider>
  );
};
