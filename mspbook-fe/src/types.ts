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

// --- Profile Types ---
export interface UserProfile {
    id: string;
    userId: string;
    username: string;
    firstName: string;
    lastName: string;
    avatar?: string;
}

// --- Chat Types ---

export interface ParticipantInfo {
    userId: string;
    username: string;
}

export interface ChatMessage {
    id: string;
    conversationId: string;
    me: boolean;
    message: string;
    sender: ParticipantInfo;
    createdDate: string; // ISO 8601 date string
}

export interface Conversation {
    id: string;
    name: string;
    participants: ParticipantInfo[];
    lastMessage: ChatMessage | null;
    conversationName?: string;
    conversationAvatar?: string;
    unread?: number;
    modifiedDate?: string;
}
