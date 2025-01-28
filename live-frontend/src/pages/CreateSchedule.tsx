import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Box, Typography, Alert, Snackbar } from '@mui/material';
import CreateScheduleForm from '../components/CreateScheduleForm';
import client from '../api/client';
import { CreateScheduleRequest } from '../types';

function CreateSchedulePage() {
  const navigate = useNavigate();
  const [error, setError] = useState<string | null>(null);
  
  // Load saved form data if it exists
  const savedFormData = JSON.parse(sessionStorage.getItem('scheduleFormData') || '{}');
  
  const handleSubmit = async (data: CreateScheduleRequest) => {
    try {
      await client.post('/api/schedules', data);
      // Clear saved form data after successful submission
      sessionStorage.removeItem('scheduleFormData');
      navigate('/dashboard');
    } catch (error) {
      console.error('Failed to create schedule:', error);
      setError('Failed to create schedule. Please try again.');
    }
  };

  return (
    <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
      <Typography variant="h4" gutterBottom>Create New Schedule</Typography>
      <CreateScheduleForm 
        onSubmit={handleSubmit} 
        initialData={savedFormData}  // Pass saved data as initial data
      />
      <Snackbar 
        open={!!error}
        autoHideDuration={6000}
        onClose={() => setError(null)}
      >
        <Alert severity="error" onClose={() => setError(null)}>
          {error}
        </Alert>
      </Snackbar>
    </Box>
  );
}

export default CreateSchedulePage; 