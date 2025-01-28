import React from 'react';
import { Box, Typography, Paper } from '@mui/material';
import { useAuthStore } from '../stores/authStore';

export default function Profile() {
  const user = useAuthStore(state => state.user);

  if (!user) {
    return <Typography>Loading...</Typography>;
  }

  return (
    <Box sx={{ p: 3 }}>
      <Paper sx={{ p: 3 }}>
        <Typography variant="h4" gutterBottom>Profile</Typography>
        <Typography>Username: {user.username}</Typography>
        <Typography>Email: {user.email}</Typography>
      </Paper>
    </Box>
  );
} 