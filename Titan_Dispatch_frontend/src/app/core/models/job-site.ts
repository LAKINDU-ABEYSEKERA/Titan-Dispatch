export interface JobSiteResponse {
  id: string;
  projectCode: string;
  siteName: string;
  latitude: number;
  longitude: number;
  geofenceRadiusMeters: number;
  accumulatedCost: number;
}

export interface CreateJobSiteCommand {
  projectCode: string;
  siteName: string;
  latitude: number;
  longitude: number;
  geofenceRadiusMeters: number;
}

export interface UpdateJobSiteCommand {
  siteName: string;
  latitude: number;
  longitude: number;
  geofenceRadiusMeters: number;
}