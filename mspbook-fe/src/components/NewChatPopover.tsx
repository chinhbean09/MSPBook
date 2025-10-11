import React, { useState, useEffect } from 'react';
import { Popover, TextField, List, ListItem, ListItemText, CircularProgress, Typography } from '@mui/material';
import { searchUsers } from '../services/api';
import { UserProfile } from '../types';

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
  const debouncedSearchTerm = useDebounce(searchTerm, 500); // 500ms delay

  useEffect(() => {
    if (debouncedSearchTerm) {
      const fetchUsers = async () => {
        setLoading(true);
        try {
          const users = await searchUsers(debouncedSearchTerm);
          setResults(users || []);
        } catch (error) {
          console.error("Failed to search users:", error);
          setResults([]);
        }
        setLoading(false);
      };
      fetchUsers();
    } else {
      setResults([]);
    }
  }, [debouncedSearchTerm]);

  const handleSelect = (user: UserProfile) => {
    onSelectUser(user);
    onClose(); // Close popover after selection
  };

  return (
    <Popover
      open={open}
      anchorEl={anchorEl}
      onClose={onClose}
      anchorOrigin={{
        vertical: 'bottom',
        horizontal: 'left',
      }}
    >
      <div style={{ padding: '16px', width: '300px' }}>
        <Typography variant="h6" gutterBottom>Tạo cuộc trò chuyện mới</Typography>
        <TextField
          fullWidth
          label="Tìm kiếm người dùng..."
          variant="outlined"
          size="small"
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
        />
        {loading && <div style={{ display: 'flex', justifyContent: 'center', padding: '16px' }}><CircularProgress size={24} /></div>}
        <List>
          {results.map(user => (
            <ListItem button key={user.id} onClick={() => handleSelect(user)}>
              <ListItemText primary={`${user.firstName} ${user.lastName}`} secondary={`@${user.username}`} />
            </ListItem>
          ))}
        </List>
      </div>
    </Popover>
  );
};

export default NewChatPopover;
