export interface User {
  avatarUrl?: string;
  name: string;
  id: number;
  username: string;
  email: string;
  profilePicture?: string;
  bio?: string;
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export type ScheduleVisibility = 'PRIVATE' | 'PUBLIC' | 'FRIENDS_ONLY';

export interface Schedule {
  id: number;
  title: string;
  description?: string;
  startDate: string;
  endDate: string;
  visibility: ScheduleVisibility;
  activities: Activity[];
  createdAt: string;
  updatedAt: string;
  user: User;
}

export interface Activity {
  id: number;
  title: string;
  description?: string;
  location?: string;
  startTime: string;
  endTime: string;
}

export interface CreateScheduleRequest {
  title: string;
  description?: string;
  location?: string;
  startDate: Date;
  endDate: Date;
  visibility: ScheduleVisibility;
  activities?: ActivityDto[];
}

export interface ActivityDto {
  title: string;
  description?: string;
  location?: string;
  startTime: Date;
  endTime: Date;
} 