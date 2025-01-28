import React from 'react';
import { useParams } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Box, Typography, Paper, Grid, Chip } from '@mui/material';
import client from '../api/client';
import { Schedule } from '../types';

function ScheduleDetail() {
  const { id } = useParams();
  const { data: schedule, isLoading } = useQuery<Schedule>({
    queryKey: ['schedule', id],
    queryFn: async () => {
      const response = await client.get(`/api/schedules/${id}`);
      return response.data;
    }
  });

  if (isLoading) {
    return <Typography>Loading...</Typography>;
  }

  if (!schedule) {
    return <Typography>Schedule not found</Typography>;
  }

  return (
    <Box>
      <Typography variant="h4" gutterBottom>
        {schedule.title}
      </Typography>
      <Paper sx={{ p: 3, mb: 3 }}>
        <Typography variant="body1" gutterBottom>
          {schedule.description}
        </Typography>
        <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
          <Chip 
            label={`${new Date(schedule.startDate).toLocaleDateString()} - ${new Date(schedule.endDate).toLocaleDateString()}`} 
            color="primary" 
          />
          <Chip 
            label={schedule.visibility.toLowerCase()} 
            variant="outlined" 
          />
        </Box>
      </Paper>

      <Typography variant="h5" gutterBottom>Activities</Typography>
      <Grid container spacing={2}>
        {schedule.activities.map((activity) => (
          <Grid item xs={12} key={activity.id}>
            <Paper sx={{ p: 2 }}>
              <Typography variant="h6">{activity.title}</Typography>
              <Typography variant="body2" color="text.secondary">
                {activity.description}
              </Typography>
              <Typography variant="caption" display="block">
                {new Date(activity.startTime).toLocaleString()} - 
                {new Date(activity.endTime).toLocaleString()}
              </Typography>
              {activity.location && (
                <Typography variant="caption" color="primary">
                  📍 {activity.location}
                </Typography>
              )}
            </Paper>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export default ScheduleDetail; 