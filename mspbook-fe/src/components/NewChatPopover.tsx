import React, { useState, useEffect } from 'react';
import { Popover, TextField, List, ListItem, ListItemText, CircularProgress, Typography, Box } from '@mui/material';
import { searchUsers, getAllUsers } from '../../services/api';
import { UserProfile } from '../types';
import { useAuth } from '../hooks/useAuth'; // Import hook mới

interface NewChatPopoverProps {
  anchorEl: HTMLElement | null;
  open: boolean;
  onClose: () => void;
  onSelectUser: (user: UserProfile) => void;
}

// Custom hook for debouncing
const useDebounce = (value: string, delay: number) => {
  const [debouncedValue, setDebouncedValue] = useState(value);
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedValue(value);
    }, delay);
    return () => {
      clearTimeout(handler);
    };
  }, [value, delay]);
  return debouncedValue;
};

const NewChatPopover: React.FC<NewChatPopoverProps> = ({ anchorEl, open, onClose, onSelectUser }) => {
  const [searchTerm, setSearchTerm] = useState('');
  const [results, setResults] = useState<UserProfile[]>([]);
  const [loading, setLoading] = useState(false);
  const debouncedSearchTerm = useDebounce(searchTerm, 300);
  const { isAdmin } = useAuth(); // Lấy vai trò của người dùng

  useEffect(() => {
    if (!open) {
      setResults([]);
      return;
    }

    const fetchUsers = async () => {
      setLoading(true);
      try {
        let users;
        // LOGIC PHÂN QUYỀN:
        // Nếu là admin VÀ không có từ khóa tìm kiếm, thì lấy tất cả user.
        if (isAdmin && !debouncedSearchTerm) {
          users = await getAllUsers();
        } else {
          // Ngược lại, hoặc nếu là user thường, hoặc nếu admin đang tìm kiếm,
          // thì dùng API search.
          users = await searchUsers(debouncedSearchTerm);
        }
        setResults(users || []);
      } catch (error) {
        console.error("Failed to fetch users:", error);
        setResults([]);
      } finally {
        setLoading(false);
      }
    };

    fetchUsers();
  }, [debouncedSearchTerm, open, isAdmin]); // Thêm isAdmin vào dependency array

  const handleSelect = (user: UserProfile) => {
    onSelectUser(user);
    onClose();
  };

  return (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{ vertical: 'bottom', horizontal: 'left' }}
      transformOrigin={{ vertical: 'top', horizontal: 'left' }}
    >
      <Box sx={{ p: 2, width: 320 }}>
        <Typography variant="h6" gutterBottom>Tạo cuộc trò chuyện mới</Typography>
        <TextField
          fullWidth
          label="Tìm kiếm người dùng..."
          variant="outlined"
          size="small"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          autoFocus
        />
        <Box sx={{ height: 300, overflowY: 'auto', mt: 2 }}>
          {loading && <Box sx={{ display: 'flex', justifyContent: 'center', p: 2 }}><CircularProgress size={24} /></Box>}
          {!loading && results.length === 0 && (
            <Typography sx={{ p: 2, color: 'text.secondary' }}>
              {debouncedSearchTerm ? 'Không tìm thấy người dùng.' : 'Nhập để tìm kiếm hoặc admin sẽ thấy tất cả.'}
            </Typography>
          )}
          <List>
            {results.map(user => (
              <ListItem button key={user.id} onClick={() => handleSelect(user)}>
                <ListItemText primary={`${user.firstName} ${user.lastName}`} secondary={`@${user.username}`} />
              </ListItem>
            ))}
          </List>
        </Box>
      </Box>
    </Popover>
  );
};

export default NewChatPopover;
