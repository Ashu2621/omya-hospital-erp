CREATE TABLE roles (
    id BIGSERIAL PRIMARY KEY,
    role_name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    full_name VARCHAR(160),
    role_id BIGINT NOT NULL REFERENCES roles(id),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE patients (
    id BIGSERIAL PRIMARY KEY,
    uhid VARCHAR(32) NOT NULL UNIQUE,
    patient_name VARCHAR(160) NOT NULL,
    age INTEGER NOT NULL CHECK (age BETWEEN 0 AND 120),
    gender VARCHAR(30) NOT NULL,
    mobile VARCHAR(15) NOT NULL UNIQUE,
    address VARCHAR(500)
);

CREATE TABLE doctors (
    id BIGSERIAL PRIMARY KEY,
    doctor_name VARCHAR(160) NOT NULL,
    specialization VARCHAR(120) NOT NULL,
    mobile VARCHAR(15) UNIQUE,
    email VARCHAR(254) UNIQUE,
    consultation_fee NUMERIC(12,2) NOT NULL CHECK (consultation_fee >= 0)
);

CREATE TABLE appointments (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    appointment_date DATE NOT NULL,
    appointment_time TIME NOT NULL,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT uq_doctor_slot UNIQUE (doctor_id, appointment_date, appointment_time)
);

CREATE TABLE medicines (
    id BIGSERIAL PRIMARY KEY,
    medicine_name VARCHAR(180) NOT NULL,
    batch_no VARCHAR(80) NOT NULL,
    barcode VARCHAR(100) NOT NULL UNIQUE,
    mrp NUMERIC(12,2) NOT NULL CHECK (mrp >= 0),
    purchase_price NUMERIC(12,2) NOT NULL CHECK (purchase_price >= 0),
    selling_price NUMERIC(12,2) NOT NULL CHECK (selling_price >= 0),
    stock INTEGER NOT NULL CHECK (stock >= 0),
    minimum_stock INTEGER NOT NULL CHECK (minimum_stock >= 0),
    manufacturer VARCHAR(180),
    rack_location VARCHAR(80),
    expiry_date DATE NOT NULL,
    expired_locked BOOLEAN NOT NULL DEFAULT FALSE,
    low_stock BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE prescriptions (
    id BIGSERIAL PRIMARY KEY,
    appointment_id BIGINT NOT NULL REFERENCES appointments(id),
    medicine_barcode VARCHAR(100) NOT NULL REFERENCES medicines(barcode),
    dosage VARCHAR(120) NOT NULL,
    days INTEGER NOT NULL CHECK (days > 0)
);

CREATE TABLE lab_tests (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    appointment_id BIGINT REFERENCES appointments(id),
    test_name VARCHAR(180) NOT NULL,
    status VARCHAR(30) NOT NULL,
    result VARCHAR(1000),
    report_date DATE
);

CREATE TABLE admissions (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT NOT NULL REFERENCES doctors(id),
    bed_number VARCHAR(40) NOT NULL,
    ward VARCHAR(80) NOT NULL,
    admission_date DATE NOT NULL,
    discharge_date DATE,
    status VARCHAR(30) NOT NULL,
    CONSTRAINT ck_discharge_date CHECK (discharge_date IS NULL OR discharge_date >= admission_date)
);

CREATE TABLE bills (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    appointment_id BIGINT REFERENCES appointments(id),
    medicine_barcode VARCHAR(100) REFERENCES medicines(barcode),
    quantity INTEGER NOT NULL CHECK (quantity > 0),
    total_amount NUMERIC(14,2) NOT NULL CHECK (total_amount >= 0),
    bill_time TIMESTAMP NOT NULL,
    idempotency_key VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE subscriptions (
    id BIGSERIAL PRIMARY KEY,
    patient_id BIGINT NOT NULL REFERENCES patients(id),
    doctor_id BIGINT REFERENCES doctors(id),
    plan_type VARCHAR(80) NOT NULL,
    amount NUMERIC(12,2) NOT NULL CHECK (amount >= 0),
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(30) NOT NULL,
    auto_renew BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT ck_subscription_dates CHECK (end_date >= start_date)
);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL,
    action VARCHAR(80) NOT NULL,
    module_name VARCHAR(80) NOT NULL,
    details VARCHAR(1000),
    timestamp TIMESTAMP NOT NULL
);

CREATE INDEX idx_patients_name ON patients (LOWER(patient_name));
CREATE INDEX idx_appointments_date_status ON appointments (appointment_date, status);
CREATE INDEX idx_appointments_patient ON appointments (patient_id);
CREATE INDEX idx_admissions_patient_status ON admissions (patient_id, status);
CREATE INDEX idx_lab_tests_patient ON lab_tests (patient_id);
CREATE INDEX idx_prescriptions_appointment ON prescriptions (appointment_id);
CREATE INDEX idx_bills_patient_time ON bills (patient_id, bill_time DESC);
CREATE INDEX idx_medicines_expiry ON medicines (expiry_date);
CREATE INDEX idx_medicines_low_stock ON medicines (low_stock) WHERE low_stock = TRUE;
CREATE INDEX idx_subscriptions_patient_status ON subscriptions (patient_id, status);
CREATE INDEX idx_audit_timestamp ON audit_logs (timestamp DESC);
CREATE UNIQUE INDEX uq_active_bed
    ON admissions (bed_number) WHERE status = 'ADMITTED';
CREATE UNIQUE INDEX uq_active_subscription
    ON subscriptions (patient_id) WHERE status = 'ACTIVE';
CREATE UNIQUE INDEX uq_prescription_medicine
    ON prescriptions (appointment_id, medicine_barcode);
CREATE UNIQUE INDEX uq_lab_appointment_test
    ON lab_tests (appointment_id, test_name) WHERE appointment_id IS NOT NULL;
