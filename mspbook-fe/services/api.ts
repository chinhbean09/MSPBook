import { LoginPayload, RegisterPayload } from '../types';

// Tách base URL để dễ quản lý
const IDENTITY_API_URL = 'http://localhost:8888/api/v1/identity';
const CHAT_API_URL = 'http://localhost:8888/api/v1/chat';
const PROFILE_API_URL = 'http://localhost:8888/api/v1/profile';

// Hàm xử lý response chung, có "bóc vỏ" ApiResponse
const handleApiResponse = async (response: Response) => {
  const data = await response.json();
  if (!response.ok || data.code !== 1000) {
    throw new Error(data.message || 'An unknown error occurred');
  }
  return data.result;
};

// --- Identity Service ---
export const login = async (payload: LoginPayload) => {
  const response = await fetch(`${IDENTITY_API_URL}/auth/token`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  const data = await response.json();
  if (!response.ok || data.code !== 1000) {
    throw new Error(data.message || 'Login failed');
  }
  return data.result;
};

export const register = async (payload: RegisterPayload) => {
  const response = await fetch(`${IDENTITY_API_URL}/users/registration`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(payload),
  });
  return handleApiResponse(response);
};

// --- Helper lấy token ---
const getAuthHeaders = () => {
    const token = localStorage.getItem('msp_token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
    };
};

// --- Profile Service ---
export const searchUsers = async (keyword: string) => {
    const response = await fetch(`${PROFILE_API_URL}/users/search`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ keyword }),
    });
    return handleApiResponse(response);
};

// HÀM MỚI: Chỉ dành cho Admin
export const getAllUsers = async () => {
    const response = await fetch(`${PROFILE_API_URL}/users`, {
        headers: getAuthHeaders(),
    });
    return handleApiResponse(response);
};

// --- Chat Service ---
export const getMyConversations = async () => {
    const response = await fetch(`${CHAT_API_URL}/conversations/my-conversations`, {
        headers: getAuthHeaders(),
    });
    return handleApiResponse(response);
};

export const getMessagesForConversation = async (conversationId: string) => {
    const response = await fetch(`${CHAT_API_URL}/messages?conversationId=${conversationId}`, {
        headers: getAuthHeaders(),
    });
    return handleApiResponse(response);
};

export const createConversation = async (participantIds: string[]) => {
    const response = await fetch(`${CHAT_API_URL}/conversations/create`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify({ participantIds }),
    });
    return handleApiResponse(response);
};
