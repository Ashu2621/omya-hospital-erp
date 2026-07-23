"use client";

import {
  FormEvent,
  KeyboardEvent,
  ReactNode,
  useEffect,
  useMemo,
  useState,
} from "react";
import {
  apiConfigured,
  createPatient,
  getAppointments,
  getDashboard,
  getMedicines,
  getPatients,
  login,
} from "../lib/api";

type Section =
  | "Dashboard"
  | "Appointments"
  | "Admissions"
  | "Patients"
  | "Doctors"
  | "Billing"
  | "Pharmacy"
  | "Laboratory"
  | "Reports";

type Patient = {
  id: string;
  name: string;
  uhid: string;
  age: number;
  gender: string;
  mobile: string;
  lastVisit: string;
  status: "Active" | "Follow-up";
};

type Appointment = {
  time: string;
  patient: string;
  reference: string;
  initials: string;
  doctor: string;
  doctorInitials: string;
  department: string;
  type: string;
  status: "Confirmed" | "In progress" | "Scheduled";
};

type InventoryItem = {
  name: string;
  stock: string;
  minimum: string;
  department: string;
};

const navItems: Array<{ label: Section; icon: IconName }> = [
  { label: "Dashboard", icon: "home" },
  { label: "Appointments", icon: "calendar" },
  { label: "Admissions", icon: "bed" },
  { label: "Patients", icon: "users" },
  { label: "Doctors", icon: "stethoscope" },
  { label: "Billing", icon: "receipt" },
  { label: "Pharmacy", icon: "pill" },
  { label: "Laboratory", icon: "flask" },
  { label: "Reports", icon: "chart" },
];

const initialAppointments: Appointment[] = [
  {
    time: "09:00 AM",
    patient: "Rohit Sharma",
    reference: "#OPD-102131",
    initials: "RS",
    doctor: "Dr. Deepak Kumar",
    doctorInitials: "DK",
    department: "Cardiology",
    type: "Consultation",
    status: "Confirmed",
  },
  {
    time: "09:30 AM",
    patient: "Anita Pillai",
    reference: "#OPD-102132",
    initials: "AP",
    doctor: "Dr. Sneha Singh",
    doctorInitials: "SS",
    department: "Dermatology",
    type: "Consultation",
    status: "Confirmed",
  },
  {
    time: "10:00 AM",
    patient: "Vikram Kapoor",
    reference: "#OPD-102133",
    initials: "VK",
    doctor: "Dr. Amit Rana",
    doctorInitials: "AR",
    department: "Orthopedics",
    type: "Consultation",
    status: "In progress",
  },
  {
    time: "10:30 AM",
    patient: "Neha Patel",
    reference: "#OPD-102134",
    initials: "NP",
    doctor: "Dr. Pooja Mehta",
    doctorInitials: "PM",
    department: "Gynecology",
    type: "Video visit",
    status: "Scheduled",
  },
  {
    time: "11:00 AM",
    patient: "Sandeep Kumar",
    reference: "#OPD-102135",
    initials: "SK",
    doctor: "Dr. Deepak Kumar",
    doctorInitials: "DK",
    department: "Cardiology",
    type: "Consultation",
    status: "Scheduled",
  },
  {
    time: "11:30 AM",
    patient: "Meera Joshi",
    reference: "#OPD-102136",
    initials: "MJ",
    doctor: "Dr. Sneha Singh",
    doctorInitials: "SS",
    department: "Dermatology",
    type: "Consultation",
    status: "Scheduled",
  },
];

const initialPatients: Patient[] = [
  {
    id: "1",
    name: "Rohit Sharma",
    uhid: "UHID-8F24A912",
    age: 34,
    gender: "Male",
    mobile: "9876543210",
    lastVisit: "Today, 09:00",
    status: "Active",
  },
  {
    id: "2",
    name: "Anita Pillai",
    uhid: "UHID-129AC8F0",
    age: 29,
    gender: "Female",
    mobile: "9810023344",
    lastVisit: "Today, 09:30",
    status: "Follow-up",
  },
  {
    id: "3",
    name: "Vikram Kapoor",
    uhid: "UHID-A96D81C3",
    age: 47,
    gender: "Male",
    mobile: "9958841122",
    lastVisit: "Today, 10:00",
    status: "Active",
  },
  {
    id: "4",
    name: "Neha Patel",
    uhid: "UHID-49BE017A",
    age: 31,
    gender: "Female",
    mobile: "9899012255",
    lastVisit: "18 Jul 2026",
    status: "Follow-up",
  },
];

const initialInventory: InventoryItem[] = [
  {
    name: "Paracetamol 650mg",
    stock: "24 strips",
    minimum: "100 strips",
    department: "General Medicine",
  },
  {
    name: "Ceftriaxone 1g Injection",
    stock: "8 vials",
    minimum: "50 vials",
    department: "Emergency",
  },
  {
    name: "Salbutamol Inhaler",
    stock: "6 units",
    minimum: "25 units",
    department: "Respiratory",
  },
];

const summaryCards: Array<{
  label: string;
  value: string;
  trend: string;
  icon: IconName;
  tone: "green" | "blue" | "teal" | "amber";
}> = [
  {
    label: "Today's appointments",
    value: "38",
    trend: "12% vs yesterday",
    icon: "calendar",
    tone: "green",
  },
  {
    label: "Active admissions",
    value: "72",
    trend: "8% vs yesterday",
    icon: "bed",
    tone: "blue",
  },
  {
    label: "Bed occupancy",
    value: "84%",
    trend: "3% vs yesterday",
    icon: "donut",
    tone: "teal",
  },
  {
    label: "Low stock alerts",
    value: "12",
    trend: "4 items since yesterday",
    icon: "alert",
    tone: "amber",
  },
];

export function DashboardApp() {
  const [section, setSection] = useState<Section>("Dashboard");
  const [query, setQuery] = useState("");
  const [patients, setPatients] = useState(initialPatients);
  const [appointments, setAppointments] = useState(initialAppointments);
  const [inventory, setInventory] = useState(initialInventory);
  const [cards, setCards] = useState(summaryCards);
  const [showPatientModal, setShowPatientModal] = useState(false);
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const [toast, setToast] = useState<string | null>(null);
  const [token, setToken] = useState<string | null>(null);

  const normalizedQuery = query.trim().toLowerCase();
  const filteredAppointments = useMemo(
    () =>
      appointments.filter((item) =>
        [
          item.patient,
          item.reference,
          item.doctor,
          item.department,
          item.status,
        ]
          .join(" ")
          .toLowerCase()
          .includes(normalizedQuery),
      ),
    [appointments, normalizedQuery],
  );
  const filteredPatients = useMemo(
    () =>
      patients.filter((item) =>
        [item.name, item.uhid, item.mobile, item.status]
          .join(" ")
          .toLowerCase()
          .includes(normalizedQuery),
      ),
    [normalizedQuery, patients],
  );

  useEffect(() => {
    if (!token) {
      return;
    }
    let active = true;
    Promise.allSettled([
      getDashboard(token),
      getPatients(token),
      getAppointments(token),
      getMedicines(token),
    ]).then(([dashboardResult, patientsResult, appointmentsResult, medicinesResult]) => {
      if (!active) {
        return;
      }
      if (dashboardResult.status === "fulfilled") {
        const dashboard = dashboardResult.value;
        const totalBeds = dashboard.occupiedBeds + dashboard.availableBeds;
        setCards([
          { ...summaryCards[0], value: String(dashboard.todayAppointments) },
          { ...summaryCards[1], value: String(dashboard.admittedPatients) },
          {
            ...summaryCards[2],
            value: totalBeds
              ? `${Math.round((dashboard.occupiedBeds / totalBeds) * 100)}%`
              : "0%",
          },
          { ...summaryCards[3], value: String(dashboard.lowStockMedicines) },
        ]);
      }
      if (patientsResult.status === "fulfilled") {
        setPatients(
          patientsResult.value.content.map((patient) => ({
            id: String(patient.id),
            name: patient.patientName,
            uhid: patient.uhid,
            age: patient.age,
            gender: patient.gender,
            mobile: patient.mobile,
            lastVisit: "Live record",
            status: "Active",
          })),
        );
      }
      if (appointmentsResult.status === "fulfilled") {
        setAppointments(
          appointmentsResult.value.map((appointment) => ({
            time: appointment.appointmentTime.slice(0, 5),
            patient: `Patient #${appointment.patientId}`,
            reference: `#OPD-${appointment.id}`,
            initials: `P${appointment.patientId}`,
            doctor: `Doctor #${appointment.doctorId}`,
            doctorInitials: `D${appointment.doctorId}`,
            department: "Clinical",
            type: "Consultation",
            status:
              appointment.status === "IN_PROGRESS"
                ? "In progress"
                : appointment.status === "BOOKED"
                  ? "Confirmed"
                  : "Scheduled",
          })),
        );
      }
      if (medicinesResult.status === "fulfilled") {
        setInventory(
          medicinesResult.value
            .filter((medicine) => medicine.lowStock)
            .map((medicine) => ({
              name: medicine.medicineName,
              stock: `${medicine.stock} units`,
              minimum: `${medicine.minimumStock} units`,
              department: medicine.rackLocation || "Pharmacy",
            })),
        );
      }
      if (
        [dashboardResult, patientsResult, appointmentsResult, medicinesResult]
          .some((result) => result.status === "rejected")
      ) {
        setToast("Some live modules are temporarily unavailable");
      }
    });
    return () => {
      active = false;
    };
  }, [token]);

  function navigate(next: Section) {
    setSection(next);
    setSidebarOpen(false);
    setQuery("");
  }

  async function addPatient(patient: Omit<Patient, "id" | "uhid" | "lastVisit" | "status">) {
    const saved = apiConfigured && token
      ? await createPatient(token, {
          patientName: patient.name,
          age: patient.age,
          gender: patient.gender,
          mobile: patient.mobile,
        })
      : null;
    const nextNumber = saved ? String(saved.id) : String(patients.length + 1);
    const suffix = saved?.uhid.replace("UHID-", "")
      ?? createDemoUhidSuffix();
    setPatients((current) => [
      {
        ...patient,
        id: nextNumber,
        uhid: `UHID-${suffix}`,
        lastVisit: "Just now",
        status: "Active",
      },
      ...current,
    ]);
    setShowPatientModal(false);
    setSection("Patients");
    setToast(`${patient.name} registered successfully`);
    window.setTimeout(() => setToast(null), 3200);
  }

  if (apiConfigured && !token) {
    return <LoginView onLogin={setToken} />;
  }

  return (
    <main className="app-shell">
      <Sidebar
        active={section}
        open={sidebarOpen}
        onNavigate={navigate}
        onClose={() => setSidebarOpen(false)}
        onLogout={() => setToken(null)}
      />
      <section className="workspace">
        <Header
          query={query}
          onQueryChange={setQuery}
          onMenu={() => setSidebarOpen(true)}
        />
        <div className="page">
          {section === "Dashboard" ? (
            <DashboardView
              appointments={filteredAppointments}
              cards={cards}
              inventory={inventory}
              query={query}
              onRegister={() => setShowPatientModal(true)}
              onNavigate={navigate}
            />
          ) : section === "Patients" ? (
            <PatientsView
              patients={filteredPatients}
              onRegister={() => setShowPatientModal(true)}
            />
          ) : section === "Appointments" ? (
            <AppointmentsView appointments={filteredAppointments} />
          ) : section === "Pharmacy" ? (
            <PharmacyView inventory={inventory} />
          ) : (
            <ModuleView section={section} onRegister={() => setShowPatientModal(true)} />
          )}
        </div>
      </section>
      {showPatientModal ? (
        <PatientModal
          onClose={() => setShowPatientModal(false)}
          onSubmit={addPatient}
        />
      ) : null}
      {toast ? (
        <div className="toast" role="status">
          <Icon name="check" size={18} />
          {toast}
        </div>
      ) : null}
    </main>
  );
}

function createDemoUhidSuffix() {
  const bytes = new Uint8Array(4);
  if (globalThis.crypto?.getRandomValues) {
    globalThis.crypto.getRandomValues(bytes);
    return Array.from(bytes, (value) => value.toString(16).padStart(2, "0"))
      .join("")
      .toUpperCase();
  }
  return Date.now().toString(16).slice(-8).toUpperCase();
}

function Sidebar({
  active,
  open,
  onNavigate,
  onClose,
  onLogout,
}: {
  active: Section;
  open: boolean;
  onNavigate: (section: Section) => void;
  onClose: () => void;
  onLogout: () => void;
}) {
  return (
    <>
      <button
        className={`sidebar-scrim ${open ? "visible" : ""}`}
        aria-label="Close navigation"
        onClick={onClose}
      />
      <aside className={`sidebar ${open ? "open" : ""}`}>
        <div className="brand-mark" aria-label="Omya Hospital ERP">
          <span className="brand-symbol">
            <span />
            <span />
          </span>
          <span className="brand-mobile">Omya</span>
        </div>
        <nav aria-label="Primary navigation">
          {navItems.map((item) => (
            <button
              className={`nav-item ${active === item.label ? "active" : ""}`}
              key={item.label}
              onClick={() => onNavigate(item.label)}
            >
              <Icon name={item.icon} />
              <span>{item.label}</span>
            </button>
          ))}
        </nav>
        <div className="sidebar-footer">
          <button className="nav-item">
            <Icon name="settings" />
            <span>Settings</span>
          </button>
          <button className="nav-item">
            <Icon name="help" />
            <span>Support</span>
          </button>
          <button className="nav-item" onClick={onLogout}>
            <Icon name="logout" />
            <span>Logout</span>
          </button>
        </div>
      </aside>
    </>
  );
}

function Header({
  query,
  onQueryChange,
  onMenu,
}: {
  query: string;
  onQueryChange: (value: string) => void;
  onMenu: () => void;
}) {
  return (
    <header className="topbar">
      <button className="mobile-menu" aria-label="Open navigation" onClick={onMenu}>
        <Icon name="menu" />
      </button>
      <a className="wordmark" href="#" aria-label="Omya Hospital ERP home">
        Omya Hospital ERP
      </a>
      <label className="global-search">
        <span className="sr-only">Search patients, doctors, and records</span>
        <Icon name="search" size={18} />
        <input
          value={query}
          onChange={(event) => onQueryChange(event.target.value)}
          placeholder="Search patients, doctors, records..."
        />
        <kbd>⌘ K</kbd>
      </label>
      <button className="notification-button" aria-label="Five notifications">
        <Icon name="bell" />
        <span>5</span>
      </button>
      <div className="profile">
        <div className="avatar avatar-large">A</div>
        <div>
          <strong>Ashutosh</strong>
          <span>Admin</span>
        </div>
        <Icon name="chevron" size={16} />
      </div>
    </header>
  );
}

function DashboardView({
  appointments,
  cards,
  inventory,
  query,
  onRegister,
  onNavigate,
}: {
  appointments: Appointment[];
  cards: typeof summaryCards;
  inventory: InventoryItem[];
  query: string;
  onRegister: () => void;
  onNavigate: (section: Section) => void;
}) {
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Thursday · 23 July 2026</p>
          <h1>Good morning, Ashutosh</h1>
          <p>Here&apos;s what&apos;s happening in your hospital today.</p>
        </div>
        <button className="primary-button" onClick={onRegister}>
          <Icon name="userPlus" size={19} />
          Register patient
        </button>
      </div>

      <section className="summary-grid" aria-label="Hospital overview">
        {cards.map((card) => (
          <article className="summary-card" key={card.label}>
            <div className={`metric-icon ${card.tone}`}>
              <Icon name={card.icon} />
            </div>
            <div className="metric-copy">
              <span>{card.label}</span>
              <strong>{card.value}</strong>
              <small className={card.tone === "amber" ? "trend amber" : "trend"}>
                <Icon name="arrowUp" size={13} /> {card.trend}
              </small>
            </div>
            <MiniSparkline tone={card.tone} />
          </article>
        ))}
      </section>

      <section className="dashboard-grid">
        <article className="panel schedule-panel">
          <div className="panel-heading">
            <div>
              <span className="panel-kicker">Clinical flow</span>
              <h2>Appointment schedule</h2>
            </div>
            <button className="text-button" onClick={() => onNavigate("Appointments")}>
              View all <Icon name="arrowRight" size={15} />
            </button>
          </div>
          <AppointmentTable appointments={appointments.slice(0, 6)} query={query} />
          <p className="table-footnote">
            Showing {Math.min(appointments.length, 6)} of 38 appointments
          </p>
        </article>

        <div className="dashboard-stack">
          <article className="panel capacity-panel">
            <div className="panel-heading">
              <div>
                <span className="panel-kicker">Live capacity</span>
                <h2>Bed overview</h2>
              </div>
              <button className="icon-button" aria-label="More capacity options">
                <Icon name="dots" />
              </button>
            </div>
            <div className="capacity-content">
              <div className="occupancy-gauge">
                <svg viewBox="0 0 190 112" aria-label="84 percent bed occupancy">
                  <path
                    className="gauge-track"
                    d="M 18 98 A 77 77 0 0 1 172 98"
                    pathLength="100"
                  />
                  <path
                    className="gauge-value"
                    d="M 18 98 A 77 77 0 0 1 172 98"
                    pathLength="100"
                  />
                </svg>
                <div>
                  <strong>84%</strong>
                  <span>Bed occupancy</span>
                </div>
                <small><b>302</b> / 360 beds occupied</small>
              </div>
              <div className="ward-list">
                <WardRow name="ICU" occupied="51 / 60 beds" value={85} tone="green" />
                <WardRow name="General" occupied="164 / 200 beds" value={82} tone="blue" />
                <WardRow name="Private" occupied="87 / 100 beds" value={87} tone="teal" />
              </div>
            </div>
          </article>

          <article className="panel inventory-panel">
            <div className="panel-heading inventory-heading">
              <div>
                <span className="panel-kicker amber-text">Needs attention</span>
                <h2><Icon name="alert" size={19} /> Pharmacy alerts</h2>
              </div>
              <div className="alert-count"><strong>12</strong><span>Low stock</span></div>
            </div>
            <InventoryTable compact items={inventory} />
            <button className="inventory-action" onClick={() => onNavigate("Pharmacy")}>
              Review inventory <Icon name="arrowRight" size={16} />
            </button>
          </article>
        </div>
      </section>
    </>
  );
}

function PatientsView({
  patients,
  onRegister,
}: {
  patients: Patient[];
  onRegister: () => void;
}) {
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Patient registry</p>
          <h1>Patients</h1>
          <p>Manage demographics, visits and clinical records.</p>
        </div>
        <button className="primary-button" onClick={onRegister}>
          <Icon name="userPlus" size={19} /> Register patient
        </button>
      </div>
      <section className="module-stats">
        <Stat label="Total patients" value="12,438" note="+124 this month" />
        <Stat label="New registrations" value="18" note="Today" />
        <Stat label="Follow-ups due" value="43" note="Next 7 days" />
      </section>
      <article className="panel data-panel">
        <div className="panel-heading">
          <div>
            <span className="panel-kicker">Master record</span>
            <h2>Patient directory</h2>
          </div>
          <button className="secondary-button"><Icon name="download" size={17} /> Export</button>
        </div>
        <div className="responsive-table">
          <table>
            <thead>
              <tr>
                <th>Patient</th>
                <th>UHID</th>
                <th>Age / Gender</th>
                <th>Mobile</th>
                <th>Last visit</th>
                <th>Status</th>
              </tr>
            </thead>
            <tbody>
              {patients.map((patient) => (
                <tr key={patient.id}>
                  <td><Person name={patient.name} initials={initials(patient.name)} /></td>
                  <td className="mono">{patient.uhid}</td>
                  <td>{patient.age} / {patient.gender}</td>
                  <td>{patient.mobile}</td>
                  <td>{patient.lastVisit}</td>
                  <td><span className={`status ${patient.status === "Active" ? "confirmed" : "scheduled"}`}>{patient.status}</span></td>
                </tr>
              ))}
            </tbody>
          </table>
          {patients.length === 0 ? <EmptyState /> : null}
        </div>
      </article>
    </>
  );
}

function AppointmentsView({ appointments }: { appointments: Appointment[] }) {
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Clinical flow</p>
          <h1>Appointments</h1>
          <p>Coordinate consultations across departments and channels.</p>
        </div>
        <button className="primary-button"><Icon name="calendarPlus" size={19} /> New appointment</button>
      </div>
      <section className="module-stats">
        <Stat label="Today" value="38" note="6 in progress" />
        <Stat label="Waiting" value="9" note="Average 14 min" />
        <Stat label="Video visits" value="7" note="2 upcoming" />
      </section>
      <article className="panel data-panel">
        <div className="panel-heading">
          <div><span className="panel-kicker">23 July 2026</span><h2>Today&apos;s schedule</h2></div>
          <button className="secondary-button"><Icon name="filter" size={17} /> Filter</button>
        </div>
        <AppointmentTable appointments={appointments} query="" />
      </article>
    </>
  );
}

function PharmacyView({ inventory }: { inventory: InventoryItem[] }) {
  return (
    <>
      <div className="page-heading">
        <div>
          <p className="eyebrow">Inventory control</p>
          <h1>Pharmacy</h1>
          <p>Monitor stock, expiries and dispensing operations.</p>
        </div>
        <button className="primary-button"><Icon name="plus" size={19} /> Add medicine</button>
      </div>
      <section className="module-stats">
        <Stat label="SKUs in stock" value="2,186" note="Across 4 stores" />
        <Stat label="Low stock" value="12" note="Needs reorder" danger />
        <Stat label="Expiring soon" value="7" note="Within 30 days" danger />
      </section>
      <article className="panel data-panel">
        <div className="panel-heading">
          <div><span className="panel-kicker amber-text">Action queue</span><h2>Low-stock inventory</h2></div>
          <button className="secondary-button"><Icon name="download" size={17} /> Export</button>
        </div>
        <InventoryTable items={inventory} />
      </article>
    </>
  );
}

function ModuleView({
  section,
  onRegister,
}: {
  section: Exclude<Section, "Dashboard" | "Patients" | "Appointments" | "Pharmacy">;
  onRegister: () => void;
}) {
  const copy: Record<typeof section, { eyebrow: string; description: string; action: string }> = {
    Admissions: { eyebrow: "Inpatient care", description: "Track beds, wards, transfers and discharge readiness.", action: "New admission" },
    Doctors: { eyebrow: "Care team", description: "Manage clinicians, availability and department rosters.", action: "Add doctor" },
    Billing: { eyebrow: "Revenue operations", description: "Generate bills, reconcile payments and review claims.", action: "Create bill" },
    Laboratory: { eyebrow: "Diagnostics", description: "Track test orders, samples, results and turnaround time.", action: "New test order" },
    Reports: { eyebrow: "Operations intelligence", description: "Review clinical, financial and capacity performance.", action: "Create report" },
  };
  const item = copy[section];
  return (
    <>
      <div className="page-heading">
        <div><p className="eyebrow">{item.eyebrow}</p><h1>{section}</h1><p>{item.description}</p></div>
        <button className="primary-button" onClick={section === "Admissions" ? onRegister : undefined}>
          <Icon name="plus" size={19} /> {item.action}
        </button>
      </div>
      <section className="module-stats">
        <Stat label="Open today" value={section === "Admissions" ? "72" : "24"} note="Live operational count" />
        <Stat label="Completed" value="186" note="This month" />
        <Stat label="Needs review" value="8" note="Action required" danger />
      </section>
      <section className="module-grid">
        <article className="panel module-card">
          <div className="module-card-icon"><Icon name={navItems.find((nav) => nav.label === section)?.icon ?? "chart"} /></div>
          <span className="panel-kicker">Live workspace</span>
          <h2>{section} workflow</h2>
          <p>The interface is connected to the versioned Spring Boot API contract and ready for hospital-specific data.</p>
          <button className="text-button">Open work queue <Icon name="arrowRight" size={15} /></button>
        </article>
        <article className="panel module-card">
          <div className="module-card-icon blue"><Icon name="shield" /></div>
          <span className="panel-kicker">Controls</span>
          <h2>Secure by default</h2>
          <p>Role-based access, audit history and validation protect sensitive operational workflows.</p>
          <button className="text-button">View audit trail <Icon name="arrowRight" size={15} /></button>
        </article>
        <article className="panel module-card">
          <div className="module-card-icon amber"><Icon name="chart" /></div>
          <span className="panel-kicker">Performance</span>
          <h2>Operational signals</h2>
          <p>Health metrics, structured logs and dashboards make production behavior observable.</p>
          <button className="text-button">View metrics <Icon name="arrowRight" size={15} /></button>
        </article>
      </section>
    </>
  );
}

function AppointmentTable({
  appointments,
  query,
}: {
  appointments: Appointment[];
  query: string;
}) {
  return (
    <div className="responsive-table schedule-table">
      <table>
        <thead>
          <tr>
            <th>Time</th>
            <th>Patient</th>
            <th>Doctor</th>
            <th>Department</th>
            <th>Type</th>
            <th>Status</th>
          </tr>
        </thead>
        <tbody>
          {appointments.map((item) => (
            <tr className={item.status === "In progress" ? "selected" : ""} key={item.reference}>
              <td className="time-cell">{item.time}</td>
              <td><Person name={item.patient} subtitle={item.reference} initials={item.initials} /></td>
              <td><Person name={item.doctor} initials={item.doctorInitials} small /></td>
              <td>{item.department}</td>
              <td><span className="type-cell"><Icon name={item.type === "Video visit" ? "video" : "stethoscope"} size={15} />{item.type}</span></td>
              <td><span className={`status ${slug(item.status)}`}>{item.status}</span></td>
            </tr>
          ))}
        </tbody>
      </table>
      {appointments.length === 0 ? <EmptyState query={query} /> : null}
    </div>
  );
}

function InventoryTable({
  compact = false,
  items,
}: {
  compact?: boolean;
  items: InventoryItem[];
}) {
  return (
    <div className={`responsive-table inventory-table ${compact ? "compact" : ""}`}>
      <table>
        <thead>
          <tr><th>Medicine</th><th>Current stock</th><th>Min. required</th>{compact ? null : <th>Department</th>}</tr>
        </thead>
        <tbody>
          {items.map((item) => (
            <tr key={item.name}>
              <td>{item.name}</td>
              <td className="danger-text">{item.stock}</td>
              <td>{item.minimum}</td>
              {compact ? null : <td>{item.department}</td>}
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function PatientModal({
  onClose,
  onSubmit,
}: {
  onClose: () => void;
  onSubmit: (patient: Omit<Patient, "id" | "uhid" | "lastVisit" | "status">) => Promise<void>;
}) {
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    const name = String(data.get("name") ?? "").trim();
    const mobile = String(data.get("mobile") ?? "").trim();
    const age = Number(data.get("age"));
    const gender = String(data.get("gender") ?? "");
    if (name.length < 2 || !/^\d{10}$/.test(mobile) || age < 0 || age > 120) {
      setError("Enter a valid name, age and 10-digit mobile number.");
      return;
    }
    try {
      setSaving(true);
      setError("");
      await onSubmit({ name, mobile, age, gender });
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Patient registration failed.");
    } finally {
      setSaving(false);
    }
  }

  function handleKeyDown(event: KeyboardEvent<HTMLDivElement>) {
    if (event.key === "Escape") onClose();
  }

  return (
    <div className="modal-backdrop" role="presentation" onMouseDown={onClose}>
      <div
        className="modal"
        role="dialog"
        aria-modal="true"
        aria-labelledby="patient-modal-title"
        onMouseDown={(event) => event.stopPropagation()}
        onKeyDown={handleKeyDown}
      >
        <div className="modal-heading">
          <div>
            <span className="panel-kicker">Patient registry</span>
            <h2 id="patient-modal-title">Register a new patient</h2>
            <p>A UHID will be generated automatically.</p>
          </div>
          <button className="icon-button" aria-label="Close" onClick={onClose}><Icon name="close" /></button>
        </div>
        <form onSubmit={submit}>
          <label className="field full"><span>Full name</span><input name="name" autoFocus placeholder="e.g. Aditi Sharma" required /></label>
          <label className="field"><span>Age</span><input name="age" type="number" min="0" max="120" placeholder="32" required /></label>
          <label className="field"><span>Gender</span><select name="gender" defaultValue="Female"><option>Female</option><option>Male</option><option>Other</option></select></label>
          <label className="field full"><span>Mobile number</span><input name="mobile" inputMode="numeric" maxLength={10} placeholder="10-digit mobile number" required /></label>
          {error ? <p className="form-error" role="alert">{error}</p> : null}
          <div className="modal-actions">
            <button type="button" className="secondary-button" onClick={onClose}>Cancel</button>
            <button className="primary-button" type="submit" disabled={saving}><Icon name="check" size={17} /> {saving ? "Registering..." : "Register patient"}</button>
          </div>
        </form>
      </div>
    </div>
  );
}

function LoginView({ onLogin }: { onLogin: (token: string) => void }) {
  const [error, setError] = useState("");
  const [busy, setBusy] = useState(false);

  async function submit(event: FormEvent<HTMLFormElement>) {
    event.preventDefault();
    const data = new FormData(event.currentTarget);
    try {
      setBusy(true);
      setError("");
      const response = await login(
        String(data.get("username") ?? ""),
        String(data.get("password") ?? ""),
      );
      onLogin(response.token);
    } catch (exception) {
      setError(exception instanceof Error ? exception.message : "Sign in failed.");
    } finally {
      setBusy(false);
    }
  }

  return (
    <main className="login-page">
      <section className="login-brand">
        <div className="brand-symbol login-symbol"><span /><span /></div>
        <p className="eyebrow">Clinical operations platform</p>
        <h1>Omya Hospital ERP</h1>
        <p>One secure workspace for patients, care teams, pharmacy, diagnostics and revenue operations.</p>
        <div className="login-proof">
          <span><Icon name="shield" /> Role-based access</span>
          <span><Icon name="chart" /> Production observability</span>
        </div>
      </section>
      <section className="login-card">
        <div>
          <span className="panel-kicker">Authorized staff only</span>
          <h2>Welcome back</h2>
          <p>Sign in with your hospital account.</p>
        </div>
        <form onSubmit={submit}>
          <label className="field"><span>Username</span><input name="username" autoComplete="username" required /></label>
          <label className="field"><span>Password</span><input name="password" type="password" autoComplete="current-password" required /></label>
          {error ? <p className="form-error" role="alert">{error}</p> : null}
          <button className="primary-button login-button" disabled={busy}>{busy ? "Signing in..." : "Sign in securely"}</button>
        </form>
        <small>Access is audited. Contact your administrator if you need an account.</small>
      </section>
    </main>
  );
}

function Person({
  name,
  subtitle,
  initials: personInitials,
  small,
}: {
  name: string;
  subtitle?: string;
  initials: string;
  small?: boolean;
}) {
  return (
    <span className="person">
      <span className={`avatar ${small ? "avatar-small" : ""}`}>{personInitials}</span>
      <span><strong>{name}</strong>{subtitle ? <small>{subtitle}</small> : null}</span>
    </span>
  );
}

function WardRow({
  name,
  occupied,
  value,
  tone,
}: {
  name: string;
  occupied: string;
  value: number;
  tone: string;
}) {
  return (
    <div className="ward-row">
      <div><span className={`ward-dot ${tone}`} /><strong>{name}</strong><b>{value}%</b></div>
      <small>{occupied}</small>
      <span className="progress"><i className={tone} style={{ width: `${value}%` }} /></span>
    </div>
  );
}

function Stat({
  label,
  value,
  note,
  danger,
}: {
  label: string;
  value: string;
  note: string;
  danger?: boolean;
}) {
  return (
    <article className="stat-card">
      <span>{label}</span>
      <strong className={danger ? "danger-text" : ""}>{value}</strong>
      <small>{note}</small>
    </article>
  );
}

function MiniSparkline({ tone }: { tone: string }) {
  return (
    <svg className={`sparkline ${tone}`} viewBox="0 0 92 42" aria-hidden="true">
      <path className="spark-fill" d="M2,38 C12,35 15,30 23,32 C33,34 34,17 43,20 C52,24 54,8 62,13 C70,20 75,9 90,5 L90,42 L2,42 Z" />
      <path className="spark-line" d="M2,38 C12,35 15,30 23,32 C33,34 34,17 43,20 C52,24 54,8 62,13 C70,20 75,9 90,5" />
    </svg>
  );
}

function EmptyState({ query }: { query?: string }) {
  return (
    <div className="empty-state">
      <Icon name="search" />
      <strong>No matching records</strong>
      <span>{query ? `No results for “${query}”.` : "Try adjusting the current filters."}</span>
    </div>
  );
}

type IconName =
  | "alert" | "arrowRight" | "arrowUp" | "bed" | "bell" | "calendar"
  | "calendarPlus" | "chart" | "check" | "chevron" | "close" | "donut"
  | "dots" | "download" | "filter" | "flask" | "help" | "home" | "logout"
  | "menu" | "pill" | "plus" | "receipt" | "search" | "settings" | "shield"
  | "stethoscope" | "userPlus" | "users" | "video";

function Icon({
  name,
  size = 20,
}: {
  name: IconName;
  size?: number;
}) {
  const paths: Record<IconName, ReactNode> = {
    home: <><path d="m3 10 9-7 9 7" /><path d="M5 9v11h14V9" /><path d="M9 20v-6h6v6" /></>,
    calendar: <><rect x="3" y="5" width="18" height="16" rx="2" /><path d="M16 3v4M8 3v4M3 10h18" /><path d="M8 14h.01M12 14h.01M16 14h.01M8 18h.01M12 18h.01" /></>,
    calendarPlus: <><rect x="3" y="5" width="18" height="16" rx="2" /><path d="M16 3v4M8 3v4M3 10h18M12 14v4M10 16h4" /></>,
    bed: <><path d="M3 5v16M21 21v-8a2 2 0 0 0-2-2H8a2 2 0 0 0-2 2v8M3 17h18" /><path d="M7 11V7h5a2 2 0 0 1 2 2v2" /></>,
    users: <><path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" /><circle cx="9" cy="7" r="4" /><path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75" /></>,
    stethoscope: <><path d="M6 3v6a4 4 0 0 0 8 0V3M4 3h4M12 3h4" /><path d="M10 13v2a5 5 0 0 0 10 0v-1" /><circle cx="20" cy="11" r="2" /></>,
    receipt: <><path d="M5 3v18l3-2 4 2 4-2 3 2V3l-3 2-4-2-4 2-3-2Z" /><path d="M9 9h6M9 13h6M9 17h3" /></>,
    pill: <><path d="m10.5 20.5-7-7a4.95 4.95 0 0 1 7-7l7 7a4.95 4.95 0 0 1-7 7Z" /><path d="m8.5 8.5 7 7" /></>,
    flask: <><path d="M9 3h6M10 3v6l-5 9a2 2 0 0 0 2 3h10a2 2 0 0 0 2-3l-5-9V3" /><path d="M7.5 15h9" /></>,
    chart: <><path d="M4 20V10M10 20V4M16 20v-7M22 20H2" /></>,
    settings: <><circle cx="12" cy="12" r="3" /><path d="M19.4 15a1.7 1.7 0 0 0 .34 1.88l.06.06-2.83 2.83-.06-.06a1.7 1.7 0 0 0-1.88-.34 1.7 1.7 0 0 0-1.03 1.56V21h-4v-.09A1.7 1.7 0 0 0 9 19.36a1.7 1.7 0 0 0-1.88.34l-.06.06-2.83-2.83.06-.06A1.7 1.7 0 0 0 4.63 15 1.7 1.7 0 0 0 3.08 14H3v-4h.09A1.7 1.7 0 0 0 4.64 9a1.7 1.7 0 0 0-.34-1.88l-.06-.06 2.83-2.83.06.06A1.7 1.7 0 0 0 9 4.63h.01A1.7 1.7 0 0 0 10 3.08V3h4v.09A1.7 1.7 0 0 0 15 4.64a1.7 1.7 0 0 0 1.88-.34l.06-.06 2.83 2.83-.06.06A1.7 1.7 0 0 0 19.37 9v.01A1.7 1.7 0 0 0 20.92 10H21v4h-.09A1.7 1.7 0 0 0 19.4 15Z" /></>,
    help: <><circle cx="12" cy="12" r="10" /><path d="M9.1 9a3 3 0 1 1 5.83 1c0 2-3 2-3 4M12 18h.01" /></>,
    logout: <><path d="M10 17l5-5-5-5M15 12H3" /><path d="M14 3h5a2 2 0 0 1 2 2v14a2 2 0 0 1-2 2h-5" /></>,
    search: <><circle cx="11" cy="11" r="7" /><path d="m20 20-4-4" /></>,
    bell: <><path d="M18 8a6 6 0 1 0-12 0c0 7-3 7-3 9h18c0-2-3-2-3-9M10 21h4" /></>,
    chevron: <path d="m9 18 6-6-6-6" />,
    menu: <path d="M4 6h16M4 12h16M4 18h16" />,
    userPlus: <><path d="M15 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2" /><circle cx="8" cy="7" r="4" /><path d="M19 8v6M16 11h6" /></>,
    donut: <><path d="M12 2v10h10A10 10 0 1 1 12 2Z" /><path d="M16 2.8A10 10 0 0 1 21.2 8H16Z" /></>,
    alert: <><path d="M10.3 3.6 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.6a2 2 0 0 0-3.4 0Z" /><path d="M12 9v4M12 17h.01" /></>,
    arrowUp: <path d="m5 11 7-7 7 7M12 4v16" />,
    arrowRight: <path d="M5 12h14M13 6l6 6-6 6" />,
    dots: <><circle cx="5" cy="12" r="1" /><circle cx="12" cy="12" r="1" /><circle cx="19" cy="12" r="1" /></>,
    video: <><rect x="3" y="6" width="13" height="12" rx="2" /><path d="m16 10 5-3v10l-5-3" /></>,
    check: <path d="m5 12 4 4L19 6" />,
    plus: <path d="M12 5v14M5 12h14" />,
    download: <><path d="M12 3v12M7 10l5 5 5-5M5 21h14" /></>,
    filter: <path d="M4 5h16l-6 7v5l-4 2v-7L4 5Z" />,
    shield: <><path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10Z" /><path d="m9 12 2 2 4-4" /></>,
    close: <path d="m6 6 12 12M18 6 6 18" />,
  };
  return (
    <svg
      className="icon"
      width={size}
      height={size}
      viewBox="0 0 24 24"
      fill="none"
      stroke="currentColor"
      strokeWidth="1.8"
      strokeLinecap="round"
      strokeLinejoin="round"
      aria-hidden="true"
    >
      {paths[name]}
    </svg>
  );
}

function initials(name: string) {
  return name
    .split(" ")
    .slice(0, 2)
    .map((part) => part[0])
    .join("")
    .toUpperCase();
}

function slug(value: string) {
  return value.toLowerCase().replaceAll(" ", "-");
}
