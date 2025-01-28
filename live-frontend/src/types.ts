export enum ScheduleVisibility {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE'
}

export interface CreateScheduleRequest {
  title: string;
  description: string;
  startDate: Date;
  endDate: Date;
  location?: string;
  visibility: ScheduleVisibility;
  activities: {
    title: string;
    description: string;
    location: string;
    startTime: string;
    endTime: string;
  }[];
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface User {
  id: string;
  username: string;
  email: string;
}

export interface AuthResponse {
  user: User;
  token: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface Schedule {
  id: string;
  title: string;
  description: string;
  startDate: string;
  endDate: string;
  visibility: string;
  activities: {
    id: string;
    title: string;
    description: string;
    startTime: string;
    endTime: string;
    location?: string;
  }[];
}   