import React, { useState, useEffect, useRef, useCallback } from 'react';
import { getMyConversations, getMessagesForConversation } from '../services/api';
import { Conversation, ChatMessage, ParticipantInfo } from '../types';
import { io, Socket } from 'socket.io-client';
import { 
    Box, Card, TextField, Typography, Paper, IconButton, Avatar, List, ListItem, 
    ListItemAvatar, ListItemText, Divider, Badge, CircularProgress, Alert, Stack 
} from '@mui/material';
import SendIcon from '@mui/icons-material/Send';

// --- CONFIGS (SỬA LỖI Ở ĐÂY) ---
// Kết nối trực tiếp đến cổng Socket.IO của chat-service khi chạy local
const SOCKET_URL = "http://localhost:8099"; 

const SEND_MESSAGE_EVENT = "send_message";

// --- MOCK COMPONENTS (để code có thể chạy) ---
const Scene: React.FC<{ children: React.ReactNode }> = ({ children }) => <Box sx={{ p: 2 }}>{children}</Box>;

// --- Main HomePage Component ---
const HomePage: React.FC<{ token: string; onLogout: () => void }> = ({ token, onLogout }) => {
    const [conversations, setConversations] = useState<Conversation[]>([]);
    const [selectedConversation, setSelectedConversation] = useState<Conversation | null>(null);
    const [messagesMap, setMessagesMap] = useState<Record<string, ChatMessage[]>>({});
    const [loading, setLoading] = useState({ convos: false, messages: false });
    const [error, setError] = useState<string | null>(null);
    const [message, setMessage] = useState("");
    const socketRef = useRef<Socket | null>(null);
    const messageContainerRef = useRef<HTMLDivElement>(null);

    const scrollToBottom = useCallback(() => {
        if (messageContainerRef.current) {
            messageContainerRef.current.scrollTop = messageContainerRef.current.scrollHeight;
        }
    }, []);

    // --- API & Socket Logic ---
    useEffect(() => {
        const fetchConversations = async () => {
            setLoading(prev => ({ ...prev, convos: true }));
            setError(null);
            try {
                const convos = await getMyConversations();
                setConversations(convos || []);
                if (convos && convos.length > 0) {
                    setSelectedConversation(convos[0]);
                }
            } catch (err: any) {
                setError("Failed to load conversations.");
                console.error("Error fetching conversations:", err);
            } finally {
                setLoading(prev => ({ ...prev, convos: false }));
            }
        };
        fetchConversations();
    }, []);

    useEffect(() => {
        const fetchMessages = async (conversationId: string) => {
            if (messagesMap[conversationId]) return; // Don't refetch if already loaded
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
        // SỬA LỖI Ở ĐÂY: Kết nối đến đúng URL và không cần `path`
        const socket = io(SOCKET_URL, {
            transports: ['websocket'],
            query: { token },
        });
        socketRef.current = socket;

        socket.on('connect', () => console.log("Socket connected to port 8099!"));
        socket.on('disconnect', () => console.log("Socket disconnected"));

        socket.on(SEND_MESSAGE_EVENT, (newMessage: ChatMessage) => {
            console.log("New message received:", newMessage);
            setMessagesMap(prev => ({
                ...prev,
                [newMessage.conversationId]: [...(prev[newMessage.conversationId] || []), newMessage],
            }));
        });

        return () => {
            socket.disconnect();
        };
    }, [token]);

    useEffect(() => {
        scrollToBottom();
    }, [messagesMap, selectedConversation, scrollToBottom]);

    const handleSendMessage = () => {
        if (!message.trim() || !selectedConversation || !socketRef.current) return;

        const messagePayload = {
            conversationId: selectedConversation.id,
            message: message,
        };

        socketRef.current.emit(SEND_MESSAGE_EVENT, messagePayload);
        setMessage("");
    };

    const currentMessages = selectedConversation ? messagesMap[selectedConversation.id] || [] : [];

    return (
        <Scene>
            <Card sx={{ width: '100%', height: 'calc(100vh - 100px)', display: 'flex' }}>
                {/* Conversations List */}
                <Box sx={{ width: 320, borderRight: 1, borderColor: 'divider', display: 'flex', flexDirection: 'column' }}>
                    <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider', display: 'flex', justifyContent: 'space-between' }}>
                        <Typography variant="h6">Chats</Typography>
                        <button onClick={onLogout} className="text-sm text-red-500 hover:underline">Đăng xuất</button>
                    </Box>
                    <Box sx={{ flexGrow: 1, overflowY: 'auto' }}>
                        {loading.convos ? <CircularProgress /> : 
                            <List>
                                {conversations.map(convo => (
                                    <ListItem button key={convo.id} onClick={() => setSelectedConversation(convo)} selected={selectedConversation?.id === convo.id}>
                                        <ListItemAvatar><Avatar /></ListItemAvatar>
                                        <ListItemText primary={convo.name || convo.conversationName} secondary={convo.lastMessage?.message} />
                                    </ListItem>
                                ))}
                            </List>
                        }
                    </Box>
                </Box>

                {/* Chat Area */}
                <Box sx={{ flexGrow: 1, display: 'flex', flexDirection: 'column' }}>
                    {selectedConversation ? (
                        <>
                            <Box sx={{ p: 2, borderBottom: 1, borderColor: 'divider' }}><Typography variant="h6">{selectedConversation.name || selectedConversation.conversationName}</Typography></Box>
                            <Box ref={messageContainerRef} sx={{ flexGrow: 1, p: 2, overflowY: 'auto', bgcolor: '#f5f5f5' }}>
                                {loading.messages ? <CircularProgress /> : 
                                    currentMessages.map(msg => (
                                        <Box key={msg.id} sx={{ display: 'flex', justifyContent: msg.me ? 'flex-end' : 'flex-start', mb: 2 }}>
                                            <Paper elevation={1} sx={{ p: 1.5, bgcolor: msg.me ? '#e3f2fd' : 'white' }}>
                                                <Typography variant="body1">{msg.message}</Typography>
                                                <Typography variant="caption" sx={{ display: 'block', textAlign: 'right', mt: 0.5 }}>{new Date(msg.createdDate).toLocaleTimeString()}</Typography>
                                            </Paper>
                                        </Box>
                                    ))
                                }
                            </Box>
                            <Box component="form" sx={{ p: 2, borderTop: 1, borderColor: 'divider', display: 'flex' }} onSubmit={e => { e.preventDefault(); handleSendMessage(); }}>
                                <TextField fullWidth value={message} onChange={e => setMessage(e.target.value)} placeholder="Type a message" size="small" />
                                <IconButton color="primary" type="submit" disabled={!message.trim()}><SendIcon /></IconButton>
                            </Box>
                        </>
                    ) : (
                        <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100%' }}>
                            <Typography variant="h6" color="text.secondary">Select a conversation</Typography>
                        </Box>
                    )}
                </Box>
            </Card>
        </Scene>
    );
};

export default HomePage;
