import React from 'react';
import { useQuery } from '@tanstack/react-query';
import { Box, Typography, Button, Card, IconButton, Avatar } from '@mui/material';
import { Add as AddIcon } from '@mui/icons-material';
import { useNavigate } from 'react-router-dom';
import client from '../api/client';
import { Schedule, Activity } from '../types/index';

function Home() {
  const navigate = useNavigate();
  
  const { data: upcomingActivity } = useQuery<Activity>({
    queryKey: ['upcoming-activity'],
    queryFn: async () => {
      const response = await client.get('/api/activities/upcoming');
      return response.data;
    }
  });

  const { data: friendSchedules } = useQuery<Schedule[]>({
    queryKey: ['friend-schedules'],
    queryFn: async () => {
      const response = await client.get('/api/Users/friends');
      return response.data;
    }
  });

  return (
    <Box sx={{ maxWidth: 800, mx: 'auto', p: 3 }}>
      {/* Upcoming Activity Section */}
      <Card sx={{ mb: 4, p: 3, position: 'relative' }}>
        {upcomingActivity ? (
          <>
            <Typography variant="h6" gutterBottom>
              Your Next Adventure
            </Typography>
            <Typography variant="body1">
              {upcomingActivity.title}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              {new Date(upcomingActivity.startTime).toLocaleString()}
            </Typography>
          </>
        ) : (
          <Box sx={{ textAlign: 'center', py: 3 }}>
            <Typography variant="h6" gutterBottom>
              Press this button to live a little!
            </Typography>
            <IconButton 
              size="large" 
              color="primary" 
              onClick={() => navigate('/schedules/create')}
              sx={{ 
                backgroundColor: 'primary.light',
                '&:hover': { backgroundColor: 'primary.main' }
              }}
            >
              <AddIcon />
            </IconButton>
          </Box>
        )}
      </Card>

      {/* Friend Schedules Section */}
      <Typography variant="h6" gutterBottom>
        Friend Activities
      </Typography>
      <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2 }}>
        {friendSchedules?.map((schedule) => (
          <Card 
            key={schedule.id} 
            sx={{ 
              p: 2,
              cursor: 'pointer',
              '&:hover': { backgroundColor: 'action.hover' }
            }}
            onClick={() => navigate(`/schedules/${schedule.id}`)}
          >
            <Box sx={{ display: 'flex', alignItems: 'center', gap: 2 }}>
              <Avatar src={schedule.user?.avatarUrl} />
              <Box>
                <Typography variant="subtitle1">
                  {schedule.user?.name}
                </Typography>
                <Typography variant="body1">
                  {schedule.title}
                </Typography>
                <Typography variant="body2" color="text.secondary">
                  {new Date(schedule.startDate).toLocaleDateString()}
                </Typography>
              </Box>
            </Box>
            <Box 
              sx={{ 
                display: 'flex', 
                overflowX: 'auto', 
                gap: 2, 
                mt: 2,
                pb: 1,
                '::-webkit-scrollbar': {
                  height: 8,
                },
                '::-webkit-scrollbar-track': {
                  backgroundColor: 'background.paper',
                },
                '::-webkit-scrollbar-thumb': {
                  backgroundColor: 'primary.light',
                  borderRadius: 4,
                },
              }}
            >
              {schedule.activities?.map((activity) => (
                <Card 
                  key={activity.id} 
                  sx={{ 
                    minWidth: 200,
                    p: 2,
                    backgroundColor: 'background.paper'
                  }}
                >
                  <Typography variant="subtitle2">
                    {activity.title}
                  </Typography>
                  <Typography variant="body2" color="text.secondary">
                    {new Date(activity.startTime).toLocaleTimeString()}
                  </Typography>
                  {activity.location && (
                    <Typography variant="body2" color="text.secondary">
                      📍 {activity.location}
                    </Typography>
                  )}
                </Card>
              ))}
            </Box>
          </Card>
        ))}
      </Box>
    </Box>
  );
}

export default Home; 