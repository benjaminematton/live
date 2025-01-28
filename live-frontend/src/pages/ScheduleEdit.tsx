import React from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query';
import { Box, Typography } from '@mui/material';
import CreateScheduleForm from '../components/CreateScheduleForm';
import client from '../api/client';
import { CreateScheduleRequest } from '../types';
import { Schedule, ScheduleVisibility } from '../types';

function ScheduleEdit() {
  const { id } = useParams();
  const navigate = useNavigate();
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

  const handleSubmit = async (data: CreateScheduleRequest) => {
    try {
      await client.put(`/api/schedules/${id}`, data);
      navigate(`/schedules/${id}`);
    } catch (error) {
      console.error('Failed to update schedule:', error);
    }
  };

  return (
    <Box sx={{ maxWidth: 600, mx: 'auto', mt: 4 }}>
      <Typography variant="h4" gutterBottom>Edit Schedule</Typography>
      <CreateScheduleForm 
        onSubmit={handleSubmit}
        initialData={schedule && {
          title: schedule.title,
          description: schedule.description,
          startDate: new Date(schedule.startDate),
          endDate: new Date(schedule.endDate),
          visibility: schedule.visibility as ScheduleVisibility,
          activities: schedule.activities?.map(activity => ({
            title: activity.title,
            description: activity.description,
            location: activity.location ?? '',
            startTime: activity.startTime,
            endTime: activity.endTime
          })),
        }}
      />
    </Box>
  );
}

export default ScheduleEdit; 