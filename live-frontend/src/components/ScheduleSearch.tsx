import React from 'react';
import { Box, TextField, FormControl, InputLabel, Select, MenuItem } from '@mui/material';
import { DatePicker } from '@mui/x-date-pickers/DatePicker';
import { LocalizationProvider } from '@mui/x-date-pickers';
import { AdapterDateFns } from '@mui/x-date-pickers/AdapterDateFns';
import { ScheduleVisibility } from '../types';

interface ScheduleSearchProps {
  searchQuery: string;
  setSearchQuery: (query: string) => void;
  startDate: Date | null;
  setStartDate: (date: Date | null) => void;
  endDate: Date | null;
  setEndDate: (date: Date | null) => void;
  visibility: ScheduleVisibility | 'ALL';
  setVisibility: (visibility: ScheduleVisibility | 'ALL') => void;
}

function ScheduleSearch({
  searchQuery,
  setSearchQuery,
  startDate,
  setStartDate,
  endDate,
  setEndDate,
  visibility,
  setVisibility,
}: ScheduleSearchProps) {
  return (
    <LocalizationProvider dateAdapter={AdapterDateFns}>
      <Box sx={{ display: 'flex', gap: 2, mb: 3, flexWrap: 'wrap' }}>
        <TextField
          label="Search schedules"
          value={searchQuery}
          onChange={(e) => setSearchQuery(e.target.value)}
          sx={{ flexGrow: 1, minWidth: '200px' }}
        />
        <DatePicker
          label="Start Date"
          value={startDate}
          onChange={setStartDate}
          sx={{ minWidth: '200px' }}
        />
        <DatePicker
          label="End Date"
          value={endDate}
          onChange={setEndDate}
          sx={{ minWidth: '200px' }}
        />
        <FormControl sx={{ minWidth: '200px' }}>
          <InputLabel>Visibility</InputLabel>
          <Select
            value={visibility}
            label="Visibility"
            onChange={(e) => setVisibility(e.target.value as ScheduleVisibility | 'ALL')}
          >
            <MenuItem value="ALL">All</MenuItem>
            <MenuItem value="PRIVATE">Private</MenuItem>
            <MenuItem value="PUBLIC">Public</MenuItem>
            <MenuItem value="FRIENDS_ONLY">Friends Only</MenuItem>
          </Select>
        </FormControl>
      </Box>
    </LocalizationProvider>
  );
}

export default ScheduleSearch; 