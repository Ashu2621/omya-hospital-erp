export const apiBaseUrl = (process.env.NEXT_PUBLIC_API_BASE_URL ?? "").replace(/\/$/, "");
export const apiConfigured = apiBaseUrl.length > 0;

type LoginResponse = {
  message: string;
  role: string;
  token: string;
};

export type PatientPayload = {
  patientName: string;
  age: number;
  gender: string;
  mobile: string;
  address?: string;
};

export type PatientResponse = PatientPayload & {
  id: number;
  uhid: string;
};

export type DashboardResponse = {
  patients: number;
  doctors: number;
  appointments: number;
  todayAppointments: number;
  admittedPatients: number;
  occupiedBeds: number;
  availableBeds: number;
  medicines: number;
  lowStockMedicines: number;
  labTests: number;
  revenue: number;
};

export type AppointmentResponse = {
  id: number;
  patientId: number;
  doctorId: number;
  appointmentDate: string;
  appointmentTime: string;
  status: string;
};

export type MedicineResponse = {
  id: number;
  medicineName: string;
  stock: number;
  minimumStock: number;
  rackLocation?: string;
  lowStock: boolean;
};

export async function login(username: string, password: string) {
  return request<LoginResponse>("/auth/login", {
    method: "POST",
    body: JSON.stringify({ username, password }),
  });
}

export async function createPatient(token: string, payload: PatientPayload) {
  return request<PatientResponse>("/patient", {
    method: "POST",
    headers: { Authorization: `Bearer ${token}` },
    body: JSON.stringify(payload),
  });
}

export async function getDashboard(token: string) {
  return request<DashboardResponse>("/dashboard/stats", {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` },
  });
}

export async function getPatients(token: string) {
  return request<{ content: PatientResponse[] }>(
    "/patient/search?query=&page=0&size=100",
    {
      method: "GET",
      headers: { Authorization: `Bearer ${token}` },
    },
  );
}

export async function getAppointments(token: string) {
  return request<AppointmentResponse[]>("/appointment", {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` },
  });
}

export async function getMedicines(token: string) {
  return request<MedicineResponse[]>("/medicine", {
    method: "GET",
    headers: { Authorization: `Bearer ${token}` },
  });
}

async function request<T>(path: string, init: RequestInit): Promise<T> {
  const response = await fetch(`${apiBaseUrl}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      Accept: "application/json",
      ...init.headers,
    },
  });
  if (!response.ok) {
    const error = await response.json().catch(() => null) as
      | { message?: string }
      | null;
    throw new Error(error?.message ?? `Request failed with status ${response.status}`);
  }
  return response.json() as Promise<T>;
}
