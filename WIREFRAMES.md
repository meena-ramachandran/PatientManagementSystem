# Patient Management System - UI Wireframes & Navigation Flows

**API Gateway Base URL**: `http://localhost:4007`

---

## Authentication Flow (All Roles)

### Login Screen
```
┌─────────────────────────────────────┐
│   PATIENT MANAGEMENT SYSTEM         │
│   Login                             │
├─────────────────────────────────────┤
│                                     │
│  Email:      [________________]    │
│  Password:   [________________]    │
│                                     │
│             [  LOGIN BUTTON  ]      │
│                                     │
│  Remember me  □                     │
└─────────────────────────────────────┘

API CALL:
POST /auth/login
Body: { "email": "user@example.com", "password": "password123" }
Response: { "token": "eyJhbGc..." }
Store token in localStorage for subsequent requests
All following requests include: Authorization: Bearer <token>
```

---

# ADMIN WIREFRAMES

## Admin Dashboard
```
┌──────────────────────────────────────────────────────────┐
│  ADMIN DASHBOARD                          [User ▼] [Logout]│
├──────────────────────────────────────────────────────────┤
│                                                            │
│  [Patients]  [Appointments]  [Billing]  [Analytics]       │
│  [Users]     [Reports]       [Settings]                   │
│                                                            │
├──────────────────────────────────────────────────────────┤
│  QUICK STATS                                              │
│  ┌────────────┐  ┌────────────┐  ┌────────────┐          │
│  │Total       │  │Appointments│  │Revenue     │          │
│  │Patients: 42│  │This Week:15│  │This Month: │          │
│  │            │  │            │  │$3,200      │          │
│  └────────────┘  └────────────┘  └────────────┘          │
│                                                            │
├──────────────────────────────────────────────────────────┤
│  RECENT ACTIVITIES                                        │
│  - John Doe registered (10 mins ago)                      │
│  - Appointment scheduled: Jane Smith (25 mins ago)        │
│  - Billing payment received: $150 (1 hour ago)            │
└──────────────────────────────────────────────────────────┘

API CALLS on Page Load:
1. GET /api/analytics/summary
2. GET /api/analytics?eventType=PATIENT_CREATED (limit 5)
3. GET /api/analytics?eventType=APPOINTMENT_CREATED (limit 5)
```

---

## Admin - Patient Management

### Patient List Page
```
┌─────────────────────────────────────────────────────┐
│ PATIENT MANAGEMENT                  [+ ADD PATIENT] │
├─────────────────────────────────────────────────────┤
│                                                      │
│ Search: [Search by name or email____] [Search]     │
│ Filter: [All Statuses ▼] [Date Range ▼]           │
│                                                      │
├─────────────────────────────────────────────────────┤
│ ID    │ NAME         │ EMAIL          │ REG DATE   │
├───────┼──────────────┼────────────────┼────────────┤
│ uuid1 │ John Doe     │ john@ex.com    │ 2026-05-01│ [Edit][Delete]
│ uuid2 │ Jane Smith   │ jane@ex.com    │ 2026-05-02│ [Edit][Delete]
│ uuid3 │ Bob Johnson  │ bob@ex.com     │ 2026-05-03│ [Edit][Delete]
│       │              │                │            │
└─────────────────────────────────────────────────────┘

API CALLS:
1. GET /api/patients (initial load, paginated)
2. GET /api/patients?name=search_term (on search)
3. GET /api/patients?email=search_term (on search)
Click [Edit] → GET /api/patients/{patientId}
Click [Delete] → DELETE /api/patients/{patientId}
```

### Patient Registration/Edit Form
```
┌────────────────────────────────────────────────────┐
│ NEW PATIENT REGISTRATION                   [CLOSE] │
├────────────────────────────────────────────────────┤
│                                                    │
│ NAME:                                             │
│ [___________________________________]             │
│ (validation: required, max 100 chars)             │
│                                                    │
│ EMAIL:                                            │
│ [___________________________________]             │
│ (validation: required, valid email, unique)       │
│                                                    │
│ ADDRESS:                                          │
│ [___________________________________]             │
│ (validation: required)                            │
│                                                    │
│ DATE OF BIRTH:                                    │
│ [_______________] (YYYY-MM-DD)                   │
│ (validation: required)                            │
│                                                    │
│ REGISTRATION DATE:                                │
│ [_______________] (YYYY-MM-DD)                   │
│ (validation: required, auto-set to today)         │
│                                                    │
│     [REGISTER]  [CANCEL]                          │
│                                                    │
│ STATUS: Patient record created successfully       │
│ Billing account auto-created via gRPC             │
│ Analytics event: PATIENT_CREATED                  │
│                                                    │
└────────────────────────────────────────────────────┘

API CALLS:
Create:
POST /api/patients
Body: {
  "name": "John Doe",
  "email": "john@example.com",
  "address": "123 Main St",
  "dateOfBirth": "1990-01-15",
  "registeredDate": "2026-05-11"
}
Response: { "id": "uuid", "name": "John Doe", ... }

Backend chain:
- patient-service saves patient
- patient-service calls billing-service gRPC → creates billing account
- patient-service publishes Kafka PATIENT_CREATED
- analytics-service consumes event

Update:
PUT /api/patients/{patientId}
Body: { "name": "...", "email": "...", ... }
Backend publishes PATIENT_UPDATED event
```

---

## Admin - Appointment Management

### Appointment List & Calendar
```
┌────────────────────────────────────────────────────┐
│ APPOINTMENT MANAGEMENT          [+ SCHEDULE APPT.] │
├────────────────────────────────────────────────────┤
│                                                    │
│ Patient Filter: [All Patients ▼]                 │
│ Date Range:    [Start] ______  [End] ______      │
│                [FILTER]                           │
│                                                    │
├────────────────────────────────────────────────────┤
│ APPOINTMENT ID │ PATIENT    │ DATE/TIME      │ STATUS
├────────────────┼────────────┼────────────────┼─────────
│ apt-uuid1      │ John Doe   │ 2026-05-15 10:00 SCHEDULED
│ apt-uuid2      │ Jane Smith │ 2026-05-16 14:30 COMPLETED
│ apt-uuid3      │ Bob Johnson│ 2026-05-17 09:00 SCHEDULED
│                │            │                │
│                                                    │
│ [Edit] [Cancel] [Mark Complete] [View Billing]    │
│                                                    │
└────────────────────────────────────────────────────┘

API CALLS:
1. GET /api/appointments (initial load)
2. GET /api/appointments?patientId=uuid (on patient filter)
Click [Edit] → GET /api/appointments/{appointmentId}
Click [Cancel] → DELETE /api/appointments/{appointmentId}
```

### Appointment Scheduling Form
```
┌────────────────────────────────────────────────────┐
│ SCHEDULE APPOINTMENT                       [CLOSE] │
├────────────────────────────────────────────────────┤
│                                                    │
│ PATIENT: [Search & Select Dropdown ▼]            │
│ (Auto-loads or searches: GET /api/patients)       │
│                                                    │
│ STAFF/USER: [Search & Select Dropdown ▼]         │
│ (GET /api/users)                                  │
│                                                    │
│ APPOINTMENT DATE & TIME:                          │
│ [_______________] (YYYY-MM-DD HH:MM)             │
│ ⚠ System checks conflict: no duplicate patient   │
│   or staff appointments at same time              │
│                                                    │
│ STATUS:                                           │
│ [SCHEDULED ▼]                                    │
│                                                    │
│ APPOINTMENT FEE:                                  │
│ $ [______] (default: $100.00)                    │
│                                                    │
│ NOTES:                                            │
│ [__________________________________]              │
│                                                    │
│      [SCHEDULE]  [CANCEL]                         │
│                                                    │
│ INFO: Billing account will be charged             │
│       Analytics event will be logged               │
│                                                    │
└────────────────────────────────────────────────────┘

API CALLS:
POST /api/appointments
Body: {
  "patientId": "patient-uuid",
  "userId": "user-uuid",
  "appointmentDateTime": "2026-05-20T10:00:00",
  "status": "SCHEDULED",
  "appointmentFee": 100.00,
  "notes": "Regular checkup"
}

Backend chain:
1. appointment-service validates patient (REST call to patient-service)
2. appointment-service validates user via gRPC
3. billing-service gRPC creates/returns account, charges fee
4. appointment-service saves appointment
5. appointment-service publishes APPOINTMENT_CREATED
6. analytics-service consumes event

Update:
PUT /api/appointments/{appointmentId}
(Fee changes trigger automatic credit/charge via billing-service)

Delete:
DELETE /api/appointments/{appointmentId}
(Fee is automatically credited back to billing account)
```

---

## Admin - Billing Management

### Billing Accounts Page
```
┌────────────────────────────────────────────────────┐
│ BILLING ACCOUNTS                                   │
├────────────────────────────────────────────────────┤
│                                                    │
│ Patient Search: [Search by name/email___] [Search]│
│ Status Filter: [All Statuses ▼]                  │
│                                                    │
├────────────────────────────────────────────────────┤
│ ACCOUNT ID │ PATIENT    │ STATUS  │ BALANCE      │
├────────────┼────────────┼─────────┼──────────────┤
│ bill-uuid1 │ John Doe   │ ACTIVE  │ -$50.00      │
│ bill-uuid2 │ Jane Smith │ ACTIVE  │ $0.00        │
│ bill-uuid3 │ Bob Johnson│ ACTIVE  │ -$100.00     │
│            │            │         │              │
│ [View] [Charge] [Credit] [Adjust]                │
│                                                    │
└────────────────────────────────────────────────────┘

API CALLS:
1. GET /api/billing (initial load)
2. GET /api/billing/{accountId} (on view)
```

### Billing Account Detail with Transactions
```
┌────────────────────────────────────────────────────┐
│ BILLING ACCOUNT DETAIL                     [CLOSE] │
├────────────────────────────────────────────────────┤
│                                                    │
│ PATIENT:      John Doe                           │
│ EMAIL:        john@example.com                   │
│ ACCOUNT ID:   bill-uuid1                         │
│ STATUS:       ACTIVE                             │
│ BALANCE:      -$50.00 (credit owed to patient)  │
│                                                    │
├────────────────────────────────────────────────────┤
│ ACTIONS:                                           │
│ [CHARGE]  [CREDIT]  [ADJUST]                     │
│                                                    │
├────────────────────────────────────────────────────┤
│ TRANSACTION HISTORY                              │
│ DATE      │ TYPE    │ AMOUNT  │ BALANCE        │
├───────────┼─────────┼─────────┼────────────────┤
│ 2026-05-15│ CHARGE  │ $100.00 │ -$100.00       │
│ 2026-05-16│ CREDIT  │ $50.00  │ -$50.00        │
│ 2026-05-17│ CHARGE  │ $150.00 │ -$200.00       │
│           │         │         │                │
└────────────────────────────────────────────────────┘

API CALLS:
GET /api/billing/{accountId}

Charge action:
POST /api/billing/{accountId}/charge
Body: { "amount": 100.00 }

Credit action:
POST /api/billing/{accountId}/credit
Body: { "amount": 50.00 }

Both trigger BILLING_ACCOUNT_CHARGED or BILLING_ACCOUNT_CREDITED events
```

---

## Admin - User Management

### User List Page
```
┌────────────────────────────────────────────────────┐
│ USER MANAGEMENT                        [+ ADD USER]│
├────────────────────────────────────────────────────┤
│                                                    │
│ Search: [Search by email___________] [Search]    │
│ Role Filter: [All Roles ▼]                       │
│                                                    │
├────────────────────────────────────────────────────┤
│ ID    │ EMAIL          │ ROLE           │ STATUS   │
├───────┼────────────────┼────────────────┼──────────┤
│ usr-1 │ admin@ex.com   │ ADMIN          │ ACTIVE   │ [Edit][Delete]
│ usr-2 │ recep@ex.com   │ RECEPTIONIST   │ ACTIVE   │ [Edit][Delete]
│ usr-3 │ doc@ex.com     │ PHYSICIAN      │ ACTIVE   │ [Edit][Delete]
│ usr-4 │ analy@ex.com   │ ANALYST        │ ACTIVE   │ [Edit][Delete]
│       │                │                │          │
└────────────────────────────────────────────────────┘

API CALLS:
1. GET /api/users
2. Click [Edit] → GET /api/users/{userId}
```

### Create/Edit User Form
```
┌────────────────────────────────────────────────────┐
│ CREATE USER                                [CLOSE] │
├────────────────────────────────────────────────────┤
│                                                    │
│ EMAIL:                                            │
│ [___________________________________]             │
│                                                    │
│ PASSWORD:                                         │
│ [___________________________________]             │
│                                                    │
│ ROLE:                                             │
│ [ADMIN ▼]  (ADMIN / RECEPTIONIST / PHYSICIAN)    │
│            (ANALYST / PATIENT)                    │
│                                                    │
│      [CREATE]  [CANCEL]                           │
│                                                    │
│ NOTE: User credentials will be sent to email     │
│                                                    │
└────────────────────────────────────────────────────┘

API CALLS:
POST /api/users
Body: {
  "email": "newuser@example.com",
  "password": "securepass123",
  "role": "RECEPTIONIST"
}
```

---

## Admin - Analytics Dashboard

### Analytics Overview
```
┌─────────────────────────────────────────────────────┐
│ ANALYTICS & REPORTING                               │
├─────────────────────────────────────────────────────┤
│                                                     │
│ Date Range: [Start] ______ [End] ______ [Apply]   │
│                                                     │
├─────────────────────────────────────────────────────┤
│ SUMMARY METRICS                                     │
│ ┌──────────────┐ ┌──────────────┐ ┌──────────────┐│
│ │Patients      │ │Appointments  │ │Billing       ││
│ │Created: 42   │ │Scheduled: 78 │ │Charged: $5.2k││
│ └──────────────┘ └──────────────┘ └──────────────┘│
│                                                     │
├─────────────────────────────────────────────────────┤
│ EVENT BREAKDOWN                                     │
│                                                     │
│ Event Type Filter: [All Events ▼]                 │
│ - PATIENT_CREATED: 42                             │
│ - PATIENT_UPDATED: 8                              │
│ - APPOINTMENT_CREATED: 78                         │
│ - APPOINTMENT_UPDATED: 22                         │
│ - APPOINTMENT_DELETED: 5                          │
│ - BILLING_ACCOUNT_CREATED: 42                     │
│ - BILLING_ACCOUNT_CHARGED: 112                    │
│ - BILLING_ACCOUNT_CREDITED: 18                    │
│                                                     │
├─────────────────────────────────────────────────────┤
│ RECENT EVENTS (DETAILED VIEW)                      │
│                                                     │
│ [LOAD MORE]                                         │
└─────────────────────────────────────────────────────┘

API CALLS:
1. GET /api/analytics/summary
2. GET /api/analytics/summary?eventType=PATIENT_CREATED
3. GET /api/analytics?eventType=PATIENT_CREATED (paginated)
4. GET /api/analytics (all events, paginated)
```

---

# RECEPTIONIST WIREFRAMES

## Receptionist Dashboard
```
┌──────────────────────────────────────────────────┐
│  RECEPTIONIST DASHBOARD                [Logout]  │
├──────────────────────────────────────────────────┤
│                                                  │
│  [Patient Search]  [Register Patient]           │
│  [Schedule Appt.]  [View Schedule]  [Billing]   │
│                                                  │
├──────────────────────────────────────────────────┤
│  TODAY'S SCHEDULE (Read-Only)                    │
│  09:00 - John Doe (Dr. Smith)                    │
│  10:30 - Jane Smith (Dr. Johnson)                │
│  14:00 - Bob Johnson (Dr. Smith)                 │
│                                                  │
├──────────────────────────────────────────────────┤
│  QUICK ACTIONS                                   │
│  [Register New Patient]  [Schedule Appointment]  │
│  [View Patient Info]     [Check Billing]         │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS on Load:
1. GET /api/appointments (today's appointments)
```

---

## Receptionist - Patient Search & Registration

### Patient Search
```
┌────────────────────────────────────────────────┐
│ FIND PATIENT                                   │
├────────────────────────────────────────────────┤
│                                                │
│ Search by Name:                               │
│ [_____________________________]               │
│                          [SEARCH]             │
│                                                │
│ - OR -                                         │
│                                                │
│ Search by Email:                              │
│ [_____________________________]               │
│                          [SEARCH]             │
│                                                │
├────────────────────────────────────────────────┤
│ RESULTS:                                       │
│                                                │
│ John Doe (john@example.com) [SELECT]          │
│ John Doyle (johnd@example.com) [SELECT]       │
│                                                │
│ [NEW PATIENT (Register)] [CLEAR]              │
│                                                │
└────────────────────────────────────────────────┘

API CALLS:
1. GET /api/patients?name=search_term (as user types)
2. GET /api/patients?email=search_term (as user types)
```

### Patient Registration Form
```
┌────────────────────────────────────────────────┐
│ REGISTER NEW PATIENT                           │
├────────────────────────────────────────────────┤
│                                                │
│ FULL NAME:                                    │
│ [_____________________________]               │
│                                                │
│ EMAIL:                                        │
│ [_____________________________]               │
│ ⚠ Must be unique in the system                │
│                                                │
│ ADDRESS:                                      │
│ [_____________________________]               │
│                                                │
│ DATE OF BIRTH:                                │
│ [___________] (YYYY-MM-DD)                   │
│                                                │
│     [REGISTER]   [CANCEL]                     │
│                                                │
│ SUCCESS: Patient registered successfully      │
│ Billing account created                       │
│ Redirecting to appointment scheduling...     │
│                                                │
└────────────────────────────────────────────────┘

API CALLS:
POST /api/patients
Body: {
  "name": "New Patient",
  "email": "new@example.com",
  "address": "456 Oak Ave",
  "dateOfBirth": "1985-05-05",
  "registeredDate": "2026-05-11"
}

Backend triggers:
- Billing account creation via gRPC
- PATIENT_CREATED event published to Kafka
- Analytics event stored
```

---

## Receptionist - Schedule Appointment

### Schedule Appointment Form
```
┌────────────────────────────────────────────────┐
│ SCHEDULE APPOINTMENT                           │
├────────────────────────────────────────────────┤
│                                                │
│ PATIENT: John Doe                             │
│          (john@example.com)                   │
│          [CHANGE PATIENT]                     │
│                                                │
│ STAFF/PROVIDER:                               │
│ [Select Provider ▼]                           │
│ - Dr. Smith                                   │
│ - Dr. Johnson                                 │
│ - Nurse Williams                              │
│                                                │
│ APPOINTMENT DATE & TIME:                      │
│ Date: [__________] (YYYY-MM-DD)              │
│ Time: [__:__] (HH:MM)                        │
│ ⚠ System validates: no conflicts              │
│                                                │
│ REASON FOR VISIT:                             │
│ [_______________________________]              │
│                                                │
│ NOTES:                                        │
│ [_______________________________]              │
│                                                │
│     [SCHEDULE]   [CANCEL]                     │
│                                                │
│ FEES WILL BE CHARGED AUTOMATICALLY            │
│ Standard fee: $100.00                         │
│                                                │
└────────────────────────────────────────────────┘

API CALLS:
1. GET /api/users (or filtered to staff only)
2. POST /api/appointments (on schedule)

Backend chain:
- Validates patient and provider
- Creates/returns billing account
- Charges appointment fee
- Publishes APPOINTMENT_CREATED
- Stores in analytics
```

---

## Receptionist - Patient Billing View

### Billing Quick View
```
┌────────────────────────────────────────────┐
│ PATIENT BILLING                     [CLOSE]│
├────────────────────────────────────────────┤
│                                            │
│ PATIENT: John Doe                         │
│                                            │
│ ACCOUNT BALANCE:                          │
│ $ -$150.00 (Patient credit/owed)          │
│                                            │
│ STATUS: ACTIVE                            │
│                                            │
├────────────────────────────────────────────┤
│ RECENT CHARGES:                            │
│ • Appointment 2026-05-15: $100.00 CHARGED │
│ • Appointment 2026-05-16: $50.00 CHARGED  │
│ • Credit applied: $200.00 CREDITED         │
│                                            │
│ [PRINT STATEMENT]  [SEND INVOICE]        │
│                                            │
└────────────────────────────────────────────┘

API CALLS:
GET /api/billing/{accountId}
```

---

# PHYSICIAN WIREFRAMES

## Physician Dashboard
```
┌──────────────────────────────────────────────────┐
│  PHYSICIAN DASHBOARD                   [Logout]  │
├──────────────────────────────────────────────────┤
│                                                  │
│ [My Schedule]  [My Patients]  [Update Notes]    │
│                                                  │
├──────────────────────────────────────────────────┤
│  TODAY'S APPOINTMENTS                            │
│                                                  │
│  09:00 - John Doe (Consultation)                │
│  10:30 - Jane Smith (Follow-up)                 │
│  14:00 - Bob Johnson (Initial)                  │
│                                                  │
│ [VIEW DETAILS] [UPDATE STATUS]                  │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS on Load:
1. GET /api/appointments (filtered by physician's userId)
```

---

## Physician - View Schedule & Appointments

### Physician Schedule
```
┌─────────────────────────────────────────────────┐
│ MY SCHEDULE                                     │
├─────────────────────────────────────────────────┤
│                                                 │
│ Week of: [Select Week ▼]                      │
│                                                 │
│ MON 2026-05-12  TUE 2026-05-13  WED 2026-05-14│
│ No appointments                                 │
│                                                 │
│ THU 2026-05-15  FRI 2026-05-16  SAT 2026-05-17│
│ 09:00 John Doe       09:00 Jane Smith          │
│ 14:00 Bob Johnson    15:30 Mike Wilson         │
│                                                 │
│ [VIEW] [UPDATE] [COMPLETE] [CANCEL]            │
│                                                 │
└─────────────────────────────────────────────────┘

API CALLS:
GET /api/appointments
(filtered locally by physician's schedule)
```

---

## Physician - Appointment Details & Update

### View & Update Appointment
```
┌─────────────────────────────────────────────────┐
│ APPOINTMENT DETAIL & NOTES                      │
├─────────────────────────────────────────────────┤
│                                                 │
│ PATIENT: John Doe                              │
│ DATE/TIME: 2026-05-15 09:00                    │
│ APPOINTMENT ID: apt-uuid1                      │
│                                                 │
│ STATUS: [SCHEDULED ▼]                          │
│         (SCHEDULED / IN-PROGRESS / COMPLETED)  │
│                                                 │
│ APPOINTMENT NOTES:                             │
│ [_________________________________]            │
│ [_________________________________]            │
│ [_________________________________]            │
│ Patient reports: headache, fatigue...          │
│                                                 │
│ CLINICAL NOTES (Physician Input):              │
│ [_________________________________]            │
│ [_________________________________]            │
│ [_________________________________]            │
│ Diagnosis: Common cold, recommended rest...    │
│                                                 │
│ MEDICATIONS PRESCRIBED:                        │
│ [_________________________________]            │
│                                                 │
│       [SAVE]  [MARK COMPLETE]  [CANCEL]        │
│                                                 │
│ INFO: Changes will be synced to analytics      │
│       (APPOINTMENT_UPDATED event)               │
│                                                 │
└─────────────────────────────────────────────────┘

API CALLS:
1. GET /api/appointments/{appointmentId}
2. PUT /api/appointments/{appointmentId}
   Body: {
     "status": "COMPLETED",
     "notes": "Patient examined, prescribed...",
     ... other fields
   }

Backend response:
- Publishes APPOINTMENT_UPDATED event
- Analytics logs the update
```

---

## Physician - Patient Records

### View Patient Details
```
┌─────────────────────────────────────────────────┐
│ PATIENT RECORD: John Doe                        │
├─────────────────────────────────────────────────┤
│                                                 │
│ NAME: John Doe                                 │
│ DOB: 1990-01-15 (Age: 36)                      │
│ EMAIL: john@example.com                        │
│ ADDRESS: 123 Main St, City, State              │
│ REGISTERED: 2026-05-01                         │
│                                                 │
├─────────────────────────────────────────────────┤
│ MEDICAL HISTORY                                 │
│ 2026-05-15: Consultation - Cold symptoms       │
│ 2026-05-10: Consultation - Annual checkup      │
│ 2026-04-20: Follow-up - Hypertension           │
│                                                 │
│ [UPDATE RECORD]  [ADD NOTE]  [BACK]             │
│                                                 │
└─────────────────────────────────────────────────┘

API CALLS:
GET /api/patients/{patientId}
```

---

# PATIENT (SELF-SERVICE) WIREFRAMES

## Patient Dashboard
```
┌──────────────────────────────────────────────────┐
│  MY HEALTH PORTAL                      [Logout]  │
├──────────────────────────────────────────────────┤
│                                                  │
│  [My Profile]  [My Appointments]               │
│  [My Billing]  [Health Records]                │
│                                                  │
├──────────────────────────────────────────────────┤
│  QUICK SUMMARY                                   │
│  Next Appointment: 2026-05-20 10:00 AM          │
│  (with Dr. Smith)                               │
│                                                  │
│  Account Balance: $0.00 (No balance)             │
│                                                  │
│  [SCHEDULE APPOINTMENT] [VIEW RECORDS]          │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS on Load:
1. GET /api/patients/{patientId} (own record)
2. GET /api/billing/{accountId} (own billing)
3. GET /api/appointments (filtered by own patientId)
```

---

## Patient - View Profile

### Patient Profile
```
┌────────────────────────────────────────────┐
│ MY PROFILE                                 │
├────────────────────────────────────────────┤
│                                            │
│ NAME: John Doe                            │
│ EMAIL: john@example.com                   │
│ ADDRESS: 123 Main St, City, State         │
│ DATE OF BIRTH: 1990-01-15                 │
│ JOINED: 2026-05-01                        │
│                                            │
│ [EDIT PROFILE]  [BACK]                    │
│                                            │
│ NOTE: Contact support to change email     │
│       or date of birth                    │
│                                            │
└────────────────────────────────────────────┘

API CALLS:
GET /api/patients/{patientId}
```

---

## Patient - View Appointments

### Patient Appointment List
```
┌─────────────────────────────────────────────────┐
│ MY APPOINTMENTS                                 │
├─────────────────────────────────────────────────┤
│                                                 │
│ UPCOMING APPOINTMENTS:                          │
│                                                 │
│ 2026-05-20 10:00 AM - Dr. Smith                │
│ Consultation                                    │
│ [VIEW DETAILS]                                  │
│                                                 │
│ 2026-06-05 02:00 PM - Dr. Johnson              │
│ Follow-up                                       │
│ [VIEW DETAILS]  [CANCEL REQUEST]               │
│                                                 │
├─────────────────────────────────────────────────┤
│ PAST APPOINTMENTS:                              │
│                                                 │
│ 2026-05-15 09:00 AM - Dr. Smith                │
│ Consultation - Completed                        │
│ [VIEW DETAILS]                                  │
│                                                 │
│ [SCHEDULE NEW APPOINTMENT] [BACK]               │
│                                                 │
└─────────────────────────────────────────────────┘

API CALLS:
GET /api/appointments (filtered by own patientId)
```

---

## Patient - View Billing

### Patient Account Statement
```
┌─────────────────────────────────────────────────┐
│ MY BILLING                                      │
├─────────────────────────────────────────────────┤
│                                                 │
│ ACCOUNT BALANCE:                                │
│ $ 0.00                                          │
│                                                 │
│ STATUS: ACTIVE                                  │
│                                                 │
├─────────────────────────────────────────────────┤
│ RECENT TRANSACTIONS:                            │
│                                                 │
│ 2026-05-15  Appointment Charge   $100.00       │
│ 2026-05-16  Appointment Charge   $50.00        │
│ 2026-05-17  Credit Applied       -$150.00      │
│                                                 │
│ [PRINT STATEMENT]  [BACK]                       │
│                                                 │
│ NOTE: Payments can be arranged with our        │
│       billing department. Contact us for       │
│       payment options.                         │
│                                                 │
└─────────────────────────────────────────────────┘

API CALLS:
GET /api/billing/{accountId} (own billing)
```

---

# ANALYTICS MANAGER WIREFRAMES

## Analytics Manager Dashboard
```
┌──────────────────────────────────────────────────┐
│  ANALYTICS & REPORTS                   [Logout]  │
├──────────────────────────────────────────────────┤
│                                                  │
│  [Event Dashboard]  [Patient Analytics]         │
│  [Appointment Trends]  [Revenue Reports]        │
│                                                  │
├──────────────────────────────────────────────────┤
│  KEY METRICS (Current Month)                     │
│  ┌──────────────┐  ┌──────────────┐            │
│  │New Patients: │  │Appointments: │            │
│  │42            │  │112           │            │
│  └──────────────┘  └──────────────┘            │
│                                                  │
│  ┌──────────────┐  ┌──────────────┐            │
│  │Revenue:      │  │Avg. Per Appt:│            │
│  │$11,200       │  │$100          │            │
│  └──────────────┘  └──────────────┘            │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS on Load:
1. GET /api/analytics/summary
2. GET /api/analytics/summary?eventType=PATIENT_CREATED
3. GET /api/analytics/summary?eventType=APPOINTMENT_CREATED
```

---

## Analytics Manager - Event Explorer

### Event List & Filters
```
┌──────────────────────────────────────────────────┐
│ EVENT EXPLORER                                   │
├──────────────────────────────────────────────────┤
│                                                  │
│ Filters:                                         │
│ Event Type: [All Events ▼]                      │
│             - PATIENT_CREATED                   │
│             - PATIENT_UPDATED                   │
│             - APPOINTMENT_CREATED               │
│             - APPOINTMENT_UPDATED               │
│             - APPOINTMENT_DELETED               │
│             - BILLING_ACCOUNT_CREATED           │
│             - BILLING_ACCOUNT_CHARGED           │
│             - BILLING_ACCOUNT_CREDITED          │
│                                                  │
│ Patient ID: [__________________________] (opt)  │
│ Date Range: [From] ____ [To] ____              │
│                              [APPLY]            │
│                                                  │
├──────────────────────────────────────────────────┤
│ RESULTS (showing 1-50 of 1,245 events)          │
│                                                  │
│ TIMESTAMP          │ EVENT               │ DETAILS
├────────────────────┼─────────────────────┼────────
│ 2026-05-15 09:15   │ APPOINTMENT_CREATED │ [view]
│ 2026-05-15 08:30   │ PATIENT_CREATED     │ [view]
│ 2026-05-14 16:45   │ BILLING_CHARGED     │ [view]
│ 2026-05-14 14:20   │ APPOINTMENT_UPDATED │ [view]
│                                                  │
│ [PREV] 1 2 3 4 5 [NEXT]                        │
│ [EXPORT TO CSV]  [DOWNLOAD REPORT]             │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS:
1. GET /api/analytics?eventType=PATIENT_CREATED
2. GET /api/analytics?patientId=uuid
3. GET /api/analytics?eventType=APPOINTMENT_CREATED&patientId=uuid
```

---

## Analytics Manager - Summary Reports

### Analytics Summary View
```
┌──────────────────────────────────────────────────┐
│ ANALYTICS SUMMARY REPORTS                        │
├──────────────────────────────────────────────────┤
│                                                  │
│ Report Type: [Summary by Event Type ▼]          │
│                                                  │
│ Date Range: [From] _________ [To] _________     │
│             [Generate Report]                   │
│                                                  │
├──────────────────────────────────────────────────┤
│ EVENT SUMMARY (May 2026)                         │
│                                                  │
│ Event Type                │ Count │ Unique Patients
├──────────────────────────────┼───────┼────────────────
│ PATIENT_CREATED             │  42   │ 42
│ PATIENT_UPDATED             │   8   │ 6
│ APPOINTMENT_CREATED         │  78   │ 40
│ APPOINTMENT_UPDATED         │  22   │ 18
│ APPOINTMENT_DELETED         │   5   │ 5
│ BILLING_ACCOUNT_CREATED     │  42   │ 42
│ BILLING_ACCOUNT_CHARGED     │ 112   │ 65
│ BILLING_ACCOUNT_CREDITED    │  18   │ 15
│                                                  │
│ Total Events: 327                               │
│ Total Unique Patients: 89                       │
│                                                  │
│ [PRINT]  [EXPORT]  [EMAIL REPORT]               │
│                                                  │
└──────────────────────────────────────────────────┘

API CALLS:
1. GET /api/analytics/summary
2. GET /api/analytics/summary?eventType=PATIENT_CREATED
3. GET /api/analytics/summary?eventType=APPOINTMENT_CREATED
... (for each event type)
```

---

# API GATEWAY ROUTING SUMMARY

All routes go through: `http://localhost:4007`

| Endpoint | Service | Port | JWT Required |
|----------|---------|------|--------------|
| `/auth/**` | auth-service | 4005 | No (login) |
| `/api/users/**` | auth-service | 4005 | Yes |
| `/api/patients/**` | patient-service | 4000 | Yes |
| `/api/appointments/**` | appointment-service | 4006 | Yes |
| `/api/billing/**` | billing-service | 4002 | Yes |
| `/api/analytics/**` | analytics-service | 4004 | Yes |

---

# Key Features & Data Flow

## Patient Registration Flow
1. Receptionist fills out patient form
2. `POST /api/patients` → patient-service
3. Patient-service saves patient
4. Patient-service calls billing-service gRPC → creates billing account
5. Patient-service publishes `PATIENT_CREATED` to Kafka
6. Analytics-service consumes event → stores in DB
7. UI shows success message

## Appointment Scheduling Flow
1. Receptionist selects patient and provider
2. `POST /api/appointments` → appointment-service
3. Appointment-service validates patient (REST) and provider (gRPC)
4. Billing-service gRPC charges appointment fee
5. Appointment-service saves appointment
6. Appointment-service publishes `APPOINTMENT_CREATED` to Kafka
7. Analytics-service consumes event → stores in DB
8. UI shows success + booking confirmation

## Analytics Event Chain
- Every major action (patient create/update/delete, appointment create/update/delete, billing charge/credit) publishes an event to Kafka
- analytics-service consumes from three topics: `patient`, `appointment`, `billing`
- Events are stored in analytics DB for:
  - Admin to review dashboards
  - Analytics Manager to generate reports
  - Compliance/audit trails

---

# Technology Stack for UI

**Frontend Framework Recommendation**: React.js
- Axios/Fetch for API calls
- React Router for navigation
- Material-UI or Tailwind CSS for UI
- JWT token storage in localStorage
- Role-based conditional rendering

**State Management**: Redux or Context API
- Store user role, token, patient/appointment/billing data

**Real-time Updates**: WebSocket (optional enhancement)
- SignalR or Socket.io for live appointment updates

---

This wireframe documentation provides all screens, flows, and API calls needed to build the complete UI for the Patient Management System across all 5 roles.
