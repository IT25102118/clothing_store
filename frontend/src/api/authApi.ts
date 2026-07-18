import axiosInstance from './axiosInstance';
import type { AuthResponse, LoginRequest, RegisterRequest } from '../types/user';

export const authApi = {
  login: (data: LoginRequest) =>
    axiosInstance.post<AuthResponse>('/api/auth/login', data).then((r) => r.data),

  register: (data: RegisterRequest) =>
    axiosInstance.post<AuthResponse>('/api/auth/register', data).then((r) => r.data),
};
