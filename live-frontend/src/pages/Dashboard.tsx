import React, { useState } from 'react';
import { Box, Typography, Grid, Button, Chip, CircularProgress } from '@mui/material';
import { useQuery } from '@tanstack/react-query';
import ScheduleSearch from '../components/ScheduleSearch';
import client from '../api/client';
import { Schedule, ScheduleVisibility } from '../types';
import { useNavigate } from 'react-router-dom';
import AddIcon from '@mui/icons-material/Add';

function Dashboard() {
  const [searchQuery, setSearchQuery] = useState('');
  const [startDate, setStartDate] = useState<Date | null>(null);
  const [endDate, setEndDate] = useState<Date | null>(null);
  const [visibility, setVisibility] = useState<ScheduleVisibility | 'ALL'>('ALL');
  const navigate = useNavigate();

  const { data: schedules, isLoading } = useQuery({
    queryKey: ['schedules'],
    queryFn: async () => {
      const response = await client.get('/schedules');
      return response.data;
    }
  });

  const filteredSchedules = schedules?.filter((schedule: Schedule) => {
    const matchesSearch = schedule.title.toLowerCase().includes(searchQuery.toLowerCase()) ||
      schedule.description?.toLowerCase().includes(searchQuery.toLowerCase());

    const matchesDateRange = (!startDate || new Date(schedule.startDate) >= startDate) &&
      (!endDate || new Date(schedule.endDate) <= endDate);

    const matchesVisibility = visibility === 'ALL' || schedule.visibility === visibility;

    return matchesSearch && matchesDateRange && matchesVisibility;
  });

  if (isLoading) {
    return (
      <Box sx={{ display: 'flex', justifyContent: 'center', p: 4 }}>
        <CircularProgress />
      </Box>
    );
  }

  if (!filteredSchedules?.length) {
    return (
      <Box sx={{ textAlign: 'center', p: 4 }}>
        <Typography variant="h6" color="text.secondary" gutterBottom>
          No schedules found
        </Typography>
        <Button 
          variant="contained" 
          onClick={() => navigate('/schedules/new')}
          startIcon={<AddIcon />}
        >
          Create Your First Schedule
        </Button>
      </Box>
    );
  }

  return (
    <Box>
      <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
        <Typography variant="h4">My Schedules</Typography>
        <Button 
          variant="contained" 
          onClick={() => navigate('/schedules/new')}
          startIcon={<AddIcon />}
        >
          Create New Schedule
        </Button>
      </Box>
      
      <ScheduleSearch
        searchQuery={searchQuery}
        setSearchQuery={setSearchQuery}
        startDate={startDate}
        setStartDate={setStartDate}
        endDate={endDate}
        setEndDate={setEndDate}
        visibility={visibility}
        setVisibility={setVisibility}
      />

      <Grid container spacing={3}>
        {filteredSchedules?.map((schedule: Schedule) => (
          <Grid item xs={12} sm={6} md={4} key={schedule.id}>
            <Box
              onClick={() => navigate(`/schedules/${schedule.id}`)}
              sx={{
                p: 2,
                border: '1px solid',
                borderColor: 'divider',
                borderRadius: 1,
                cursor: 'pointer',
                transition: 'all 0.2s',
                '&:hover': {
                  boxShadow: 3,
                  transform: 'translateY(-2px)',
                },
              }}
            >
              <Typography variant="h6">{schedule.title}</Typography>
              <Typography variant="body2" color="text.secondary" noWrap>
                {schedule.description}
              </Typography>
              <Box sx={{ mt: 2, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                <Typography variant="caption" color="text.secondary">
                  {new Date(schedule.startDate).toLocaleDateString()} - 
                  {new Date(schedule.endDate).toLocaleDateString()}
                </Typography>
                <Chip 
                  label={schedule.visibility.toLowerCase()}
                  size="small"
                  color={schedule.visibility === 'PRIVATE' ? 'default' : 'primary'}
                />
              </Box>
            </Box>
          </Grid>
        ))}
      </Grid>
    </Box>
  );
}

export default Dashboard; 