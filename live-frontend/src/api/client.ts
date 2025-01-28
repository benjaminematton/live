/// <reference types="vite/client" />

import axios from 'axios';
import { useAuthStore } from '../stores/authStore';

const client = axios.create({
  baseURL: 'http://localhost:8080/api',  // Update this to use port 8080
  withCredentials: true
});

client.interceptors.request.use((config) => {
  // Get token from Zustand store instead of localStorage
  const token = useAuthStore.getState().token;
  console.log('Token being sent:', token); // Debug log
  console.log('Full headers:', config.headers); // Debug log
  
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  } else {
    console.warn('No token found in auth store'); // Debug warning
  }
  return config;
}, (error) => {
  console.error('Request interceptor error:', error);
  return Promise.reject(error);
});

// Add response interceptor to handle 403 errors
client.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 403) {
      // Clear auth if token is invalid/expired
      useAuthStore.getState().clearAuth();
    }
    return Promise.reject(error);
  }
);

export default client; 