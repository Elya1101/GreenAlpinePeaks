import axios from 'axios';

const API_BASE_URL = 'http://localhost:8080/api';

export interface User {
    id: number;
    name: string;
    email: string;
    role: 'admin' | 'user';
}

interface LoginResponse {
    token: string;
    user: User;
}

export const authApi = {
    login: async (email: string, password: string): Promise<LoginResponse> => {
        // Временное решение для теста
        if (email === 'admin@greenalpine.com' && password === 'admin123') {
            const mockResponse = {
                token: 'mock-jwt-token-' + Date.now(),
                user: {
                    id: 1,  // Важно: ID должен существовать в базе данных
                    name: 'Admin',
                    email: 'admin@greenalpine.com',
                    role: 'admin' as const
                }
            };
            localStorage.setItem('auth_token', mockResponse.token);
            localStorage.setItem('auth_user', JSON.stringify(mockResponse.user));
            return mockResponse;
        }

        throw new Error('Invalid credentials');
    },

    logout: (): void => {
        localStorage.removeItem('auth_token');
        localStorage.removeItem('auth_user');
    },

    getCurrentUser: (): User | null => {
        const data = localStorage.getItem('auth_user');
        if (!data) return null;
        try {
            return JSON.parse(data);
        } catch {
            return null;
        }
    },

    getToken: (): string | null => {
        return localStorage.getItem('auth_token');
    },

    isAuthenticated: (): boolean => {
        return !!localStorage.getItem('auth_token');
    },

    isAdmin: (): boolean => {
        const user = authApi.getCurrentUser();
        return user?.role === 'admin';
    }
};