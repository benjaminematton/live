import React from 'react';
import { useForm, Controller } from 'react-hook-form';
import { 
  Box, 
  TextField, 
  Button, 
  FormControl, 
  InputLabel, 
  Select, 
  MenuItem 
} from '@mui/material';
import { DateTimePicker } from '@mui/x-date-pickers';
import { CreateScheduleRequest, ScheduleVisibility } from '../types';
import { useNavigate } from 'react-router-dom';

interface CreateScheduleFormProps {
  onSubmit: (data: CreateScheduleRequest) => Promise<void>;
  initialData?: Partial<CreateScheduleRequest>;
}

function CreateScheduleForm({ onSubmit, initialData }: CreateScheduleFormProps) {
  const { control, handleSubmit, watch, getValues, setValue } = useForm<CreateScheduleRequest>({
    defaultValues: {
      ...initialData,
      activities: initialData?.activities || []
    }
  });
  const navigate = useNavigate();
  const activities = watch('activities') || [];

  const addActivity = () => {
    const currentFormData = getValues();
    const searchContext = {
      formData: currentFormData,
      searchParams: {
        startDate: currentFormData.startDate,
        endDate: currentFormData.endDate,
        location: currentFormData.location
      }
    };
    sessionStorage.setItem('scheduleFormData', JSON.stringify(searchContext));
    navigate('/schedules/find-activity');
  };

  const removeActivity = (index: number) => {
    const newActivities = [...activities];
    newActivities.splice(index, 1);
    setValue('activities', newActivities);
  };

  return (
    <Box component="form" onSubmit={handleSubmit(onSubmit)} sx={{ mt: 2 }}>
      <Controller
        name="title"
        control={control}
        rules={{ required: 'Title your Event 😃' }}
        render={({ field, fieldState }) => (
          <TextField
            {...field}
            fullWidth
            label="Schedule Title"
            error={!!fieldState.error}
            helperText={fieldState.error?.message}
            sx={{ mb: 2 }}
          />
        )}
      />

      <Controller
        name="startDate"
        control={control}
        rules={{ required: 'Start date is required' }}
        render={({ field }) => (
          <DateTimePicker
            label="Start Date"
            value={field.value}
            onChange={field.onChange}
            sx={{ mb: 2, width: '100%' }}
          />
        )}
      />

      <Controller
        name="endDate"
        control={control}
        rules={{ required: 'End date is required' }}
        render={({ field }) => (
          <DateTimePicker
            label="End Date"
            value={field.value}
            onChange={field.onChange}
            sx={{ mb: 2, width: '100%' }}
          />
        )}
      />

      <Controller
        name="location"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            label="Location"
            sx={{ mb: 2 }}
          />
        )}
      />

      <Controller
        name="description"
        control={control}
        render={({ field }) => (
          <TextField
            {...field}
            fullWidth
            label="Tell our AI about your vision!"
            multiline
            rows={3}
            sx={{ mb: 2 }}
          />
        )}
      />

      {activities.map((activity, index) => (
        <Box key={index} sx={{ display: 'flex', gap: 1, mb: 2 }}>
          <Controller
            name={`activities.${index}`}
            control={control}
            render={({ field, fieldState }) => (
              <TextField
                {...field}
                fullWidth
                label="Activity Name"
                error={!!fieldState.error}
                helperText={fieldState.error?.message}
              />
            )}
          />
          <Button 
            variant="outlined" 
            color="error"
            onClick={() => removeActivity(index)}
          >
            Remove
          </Button>
        </Box>
      ))}

      <Button 
        variant="outlined" 
        fullWidth 
        onClick={addActivity}
        sx={{ mb: 2 }}
      >
        Add Activity
      </Button>

      <Controller
        name="visibility"
        control={control}
        defaultValue={ScheduleVisibility.PRIVATE}
        render={({ field }) => (
          <FormControl fullWidth sx={{ mb: 2 }}>
            <InputLabel>Visibility</InputLabel>
            <Select {...field} label="Visibility">
              <MenuItem value="PRIVATE">Private</MenuItem>
              <MenuItem value="PUBLIC">Public</MenuItem>
              <MenuItem value="FRIENDS_ONLY">Friends Only</MenuItem>
            </Select>
          </FormControl>
        )}
      />

      <Button type="submit" variant="contained" fullWidth>
        Create Schedule
      </Button>
    </Box>
  );
}

export default CreateScheduleForm; 