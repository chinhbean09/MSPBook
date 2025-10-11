import { LoginPayload, RegisterPayload } from '../types';

// Tách base URL để dễ quản lý
const IDENTITY_API_URL = 'http://localhost:8888/api/v1/identity';
const CHAT_API_URL = 'http://localhost:8888/api/v1/chat';

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

// --- Chat Service ---
const getAuthHeaders = () => {
    const token = localStorage.getItem('msp_token');
    return {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`,
    };
};

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

// HÀM MỚI: Tạo cuộc hội thoại
export const createConversation = async (payload: { type: string; participantIds: string[] }) => {
    const response = await fetch(`${CHAT_API_URL}/conversations/create`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload),
    });
    return handleApiResponse(response);
};

// HÀM MỚI: Gửi tin nhắn (qua REST, không phải socket)
export const createMessage = async (payload: { conversationId: string; message: string }) => {
    const response = await fetch(`${CHAT_API_URL}/messages/create`, {
        method: 'POST',
        headers: getAuthHeaders(),
        body: JSON.stringify(payload),
    });
    return handleApiResponse(response);
};
