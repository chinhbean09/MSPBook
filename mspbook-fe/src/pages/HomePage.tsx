import React, { useState, useEffect, useRef, useCallback } from 'react';
import { getMyConversations, getMessagesForConversation, createConversation } from '../services/api';
import { Conversation, ChatMessage, UserProfile } from '../types';
import { io, Socket } from 'socket.io-client';
import { 
    Box, Card, TextField, Typography, Paper, IconButton, Avatar, List, ListItem, 
    ListItemAvatar, ListItemText, Divider, Badge, CircularProgress, Alert, Stack 
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';
import AddIcon from '@mui/icons-material/Add';
import NewChatPopover from '../components/NewChatPopover';

// --- CONFIGS ---
const SOCKET_URL = "http://localhost:8099";
const SEND_MESSAGE_EVENT = "send_message";

// --- MOCK COMPONENT ---
const Scene: React.FC<{ children: React.ReactNode }> = ({ children }) => <Box sx={{ p: 2 }}>{children}</Box>;

// --- Main HomePage Component ---
const HomePage: React.FC<{ token: string; onLogout: () => void }> = ({ token, onLogout }) => {
    const [conversations, setConversations] = useState<Conversation[]>([]);
    const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
    const [messagesMap, setMessagesMap] = useState<Record<string, ChatMessage[]>>({});
    const [loading, setLoading] = useState({ convos: false, messages: false });
    const [error, setError] = useState<string | null>(null);
    const [message, setMessage] = useState("");
    const [newChatAnchorEl, setNewChatAnchorEl] = useState<HTMLElement | null>(null);
    const socketRef = useRef<Socket | null>(null);
    const messageContainerRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = useCallback(() => {
        if (messageContainerRef.current) {
            messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
        }
    }, []);

    // --- API & Socket Logic ---
    const fetchConversations = useCallback(async () => {
        setLoading(prev => ({ ...prev, convos: true }));
        setError(null);
        try {
            const convos = await getMyConversations();
            setConversations(convos || []);
            if (convos && convos.length > 0 && !selectedConversation) {
                setSelectedConversation(convos[0]);
            }
        } catch (err: any) {
            setError("Failed to load conversations.");
            console.error("Error fetching conversations:", err);
        } finally {
            setLoading(prev => ({ ...prev, convos: false }));
        }
    }, [selectedConversation]);

    useEffect(() => {
        fetchConversations();
    }, [fetchConversations]);

    useEffect(() => {
        const fetchMessages = async (conversationId: string) => {
            if (messagesMap[conversationId]) return;
            setLoading(prev => ({ ...prev, messages: true }));
            try {
                const messageHistory = await getMessagesForConversation(conversationId);
                setMessagesMap(prev => ({ ...prev, [conversationId]: messageHistory || [] }));
            } catch (err) {
                console.error(`Error fetching messages for ${conversationId}:`, err);
            } finally {
                setLoading(prev => ({ ...prev, messages: false }));
            }
        };

        if (selectedConversation?.id) {
            fetchMessages(selectedConversation.id);
        }
    }, [selectedConversation, messagesMap]);

    useEffect(() => {
        const socket = io(SOCKET_URL, { query: { token } });
        socketRef.current = socket;

        socket.on('connect', () => console.log("Socket connected!"));
        socket.on(SEND_MESSAGE_EVENT, (newMessage: ChatMessage) => {
            setMessagesMap(prev => ({
                ...prev,
                [newMessage.conversationId]: [...(prev[newMessage.conversationId] || []), newMessage],
            }));
        });

        return () => { socket.disconnect(); };
    }, [token]);

    useEffect(() => { scrollToBottom(); }, [messagesMap, selectedConversation, scrollToBottom]);

    // --- Handlers ---
    const handleSendMessage = () => {
        if (!message.trim() || !selectedConversation || !socketRef.current) return;
        const messagePayload = { conversationId: selectedConversation.id, message: message };
        socketRef.current.emit(SEND_MESSAGE_EVENT, messagePayload);
        setMessage("");
    };

    const handleSelectNewChatUser = async (user: UserProfile) => {
        try {
            const newConversation = await createConversation([user.userId]);
            const existingConvo = conversations.find(c => c.id === newConversation.id);
            if (!existingConvo) {
                setConversations(prev => [newConversation, ...prev]);
            }
            setSelectedConversation(newConversation);
        } catch (error) {
            console.error("Failed to create conversation:", error);
        }
    };

    return (
        <Scene>
            <Card sx={{ width: '100%', height: 'calc(100vh - 100px)', display: 'flex' }}>
                <ConversationList 
                    conversations={conversations} 
                    onSelect={setSelectedConversation} 
                    selectedConversationId={selectedConversation?.id}
                    loading={loading.convos}
                    onLogout={onLogout}
                    onNewChatClick={(e) => setNewChatAnchorEl(e.currentTarget)}
                />
                <ChatWindow 
                    conversation={selectedConversation}
                    messages={selectedConversation ? messagesMap[selectedConversation.id] || [] : []}
                    onSendMessage={handleSendMessage}
                    loading={loading.messages}
                />
                <NewChatPopover
                    anchorEl={newChatAnchorEl}
                    open={Boolean(newChatAnchorEl)}
                    onClose={() => setNewChatAnchorEl(null)}
                    onSelectUser={handleSelectNewChatUser}
                />
            </Card>
        </Scene>
    );
};

// --- Sub-components ---
const ConversationList: React.FC<{conversations: Conversation[], onSelect: (c: Conversation) => void, selectedConversationId?: string, loading: boolean, onLogout: () => void, onNewChatClick: (e: React.MouseEvent<HTMLElement>) => void}> = 
({ conversations, onSelect, selectedConversationId, loading, onLogout, onNewChatClick }) => (
    <div className="w-1/4 bg-white border-r border-gray-200 flex flex-col">
        <div className="p-4 border-b border-gray-200 flex justify-between items-center">
            <Typography variant="h6">Chats</Typography>
            <IconButton color="primary" size="small" onClick={onNewChatClick}><AddIcon /></IconButton>
            <button onClick={onLogout} className="text-sm text-red-500 hover:underline">Đăng xuất</button>
        </div>
        <div className="flex-grow overflow-y-auto">
            {loading ? <div className="p-4 text-center"><CircularProgress size={24} /></div> : 
                <List>{conversations.map(convo => (
                    <ListItem button key={convo.id} onClick={() => onSelect(convo)} selected={selectedConversationId === convo.id}>
                        <ListItemText primary={convo.name || convo.conversationName} secondary={convo.lastMessage?.message || "..."} />
                    </ListItem>
                ))}</List>
            }
        </div>
    </div>
);

const ChatWindow: React.FC<{conversation: Conversation | null, messages: ChatMessage[], onSendMessage: (c: string) => void, loading: boolean}> = 
({ conversation, messages, onSendMessage, loading }) => {
    if (!conversation) return <div className="w-3/4 flex items-center justify-center bg-gray-50"><p>Chọn một cuộc hội thoại</p></div>;
    return (
        <div className="w-3/4 flex flex-col">
            <div className="p-4 bg-white border-b"><Typography variant="h6">{conversation.name || conversation.conversationName}</Typography></div>
            <div className="flex-grow p-4 overflow-y-auto bg-gray-50">
                {loading ? <div className="text-center"><CircularProgress size={24} /></div> : 
                    messages.map(msg => (
                        <div key={msg.id} className={`flex ${msg.me ? 'justify-end' : 'justify-start'} mb-2`}>
                            <Paper elevation={1} sx={{ p: 1.5, bgcolor: msg.me ? '#e3f2fd' : 'white' }}>
                                <Typography variant="body1">{msg.message}</Typography>
                                <Typography variant="caption" sx={{ display: 'block', textAlign: 'right', mt: 0.5 }}>{new Date(msg.createdDate).toLocaleTimeString()}</Typography>
                            </Paper>
                        </div>
                    ))
                }
            </div>
            <MessageInput onSendMessage={onSendMessage} />
        </div>
    );
};

const MessageInput: React.FC<{onSendMessage: (c: string) => void}> = ({ onSendMessage }) => {
    const [content, setContent] = useState('');
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (content.trim()) { onSendMessage(content); setContent(''); }
    };
    return (
        <form onSubmit={handleSubmit} className="p-4 bg-white border-t">
            <TextField fullWidth value={content} onChange={e => setContent(e.target.value)} placeholder="Nhập tin nhắn..." size="small" InputProps={{ endAdornment: <IconButton color="primary" type="submit" disabled={!content.trim()}><SendIcon /></IconButton> }} />
        </form>
    );
};

export default HomePage;
