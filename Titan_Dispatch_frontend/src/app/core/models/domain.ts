// --- src/app/core/models/domain.ts ---

// --- Auth ---
export interface AuthRequest { 
  username: string; 
  password: string; 
}

export interface AuthResponse { 
  access_token: string; 
}

export interface ProblemDetail { 
  type: string; 
  title: string; 
  status: number; 
  detail: string; 
  instance: string; 
}

// --- Core Domain Types ---
export type EquipmentStatus = 'AVAILABLE' | 'DOWN' | 'MAINTENANCE' | 'DISPATCHED';
export type OperatorStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';
export type DispatchStatus = 'SCHEDULED' | 'ACTIVE' | 'COMPLETED' | 'CANCELLED' | 'PENDING';
export type ServiceType = 'PREVENTATIVE' | 'REPAIR' | 'INSPECTION';

// --- Equipment & UI Dropdowns ---
export interface EquipmentResponse {
  id: string;
  assetTag: string;
  status: EquipmentStatus;
  currentEngineHours: number;
  internalHourlyRate: number;
  insuranceExpiration: string; // ISO-8601 LocalDate
}

export interface EquipmentDropdown { 
  id: string; 
  assetTag: string; 
}

export interface OperatorDropdown { 
  id: string; 
  firstName: string; 
  lastName: string; 
}

export interface JobSiteDropdown { 
  id: string; 
  projectCode: string; 
}

// --- Dispatch Engine ---
export interface DispatchSummary { 
  id: string; 
  equipmentTag: string; 
  operatorName: string; 
  projectCode: string; 
  status: DispatchStatus; 
  startDate: string; // ISO-8601 LocalDateTime
  endDate?: string;  // ISO-8601 LocalDateTime
  startEngineHours: number;
}

export interface CreateDispatchCommand {
  equipmentId: string;
  operatorId: string;
  jobSiteId: string;
  startDate: string;
  expectedEndDate: string; 
  requiresHeavyTransport: boolean;
}

export interface CompleteDispatchCommand { 
  endHours: number; 
}

export interface DispatchCompletionPayload {
  dispatchId: string;
  endEngineHours: number;
  completionNotes?: string;
}

// --- Maintenance & Fuel ---

// FIX: Kept only ONE version of CreateMaintenanceLogCommand
export interface CreateMaintenanceLogCommand {
  equipmentId: string;
  serviceDate: string; 
  hoursAtService: number;
  serviceType: string;
  totalCost: number;
  notes: string;
}

export interface MaintenanceLogResponse {
  id: string;
  equipmentId: string;
  assetTag: string; // NEW
  serviceDate: string;
  hoursAtService: number;
  serviceType: string;
  totalCost: number;
  notes: string;
}

export interface ActiveMaintenanceResponse {
  equipmentId: string;
  assetTag: string;
  startDate: string; 
  expectedEndDate: string;
}

export interface CreateFuelLogCommand {
  equipmentId: string;
  operatorId: string;
  gallonsAdded: number;
  totalCost: number;
  engineHoursAtFillUp: number;
  fillDate: string; 
}

export interface FuelLogResponse {
  id: string;
  equipmentId: string;
  operatorId: string;
  gallonsAdded: number;
  totalCost: number;
  engineHoursAtFillUp: number;
  fillDate: string;
}