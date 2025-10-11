
export interface User {
  id: string;
  name: string;
  avatarUrl: string;
}

export interface Comment {
  id: string;
  author: User;
  text: string;
  timestamp: string;
}

export interface Post {
  id: string;
  author: User;
  text: string;
  imageUrl?: string;
  timestamp: string;
  likes: number;
  comments: Comment[];
}

export interface LoginPayload {
  username?: string;
  password?: string;
}

export interface RegisterPayload {
  username?: string;
  password?: string;
  firstName?: string;
  lastName?: string;
  email?: string;
  dob?: string;
}

// Thêm kiểu dữ liệu cho tin nhắn chat
export interface ChatMessage {
    id: string;
    sender: string; // Hoặc một object User đầy đủ
    content: string;
    timestamp: string;
}
