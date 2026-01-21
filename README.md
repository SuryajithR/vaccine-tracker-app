# Vaccine Tracker (Immunization Log)

A responsive full-stack application that helps users manage their immunization records by tracking vaccine doses and automatically calculating next due dates.

## Tech Stack
- Frontend: Vue.js 3 + Vue Router + Axios
- Backend: Java Spring Boot (Spring MVC) + JPA
- Database: MySQL

## Features
- User registration and login (simple token session auth)
- Add immunization records (dose tracking)
- Intelligent next due date calculation
- Prevent invalid dose order (Dose 2 only after Dose 1, etc.)
- Prevent duplicate vaccine entries on the same date
- Booster tracking + option to mark vaccine as finished
- Responsive UI (mobile + desktop) with a consistent custom theme

## 🔌 API Endpoints

### Authentication
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/auth/register` | Register new user |
| POST | `/auth/login` | Login user and return token |

### Vaccines
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/vaccines` | Fetch list of supported vaccines |

### Immunization Records
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/records` | Get all immunization records for logged-in user |
| POST | `/records` | Add vaccine dose record |
| DELETE | `/records/{id}` | Delete a record |
| PUT | `/records/{id}/finish` | Mark vaccine as finished (stops booster reminders) |

  

## Setup Instructions

### ✅ Prerequisites
Install:
- Java JDK 17+
- Maven
- Node.js 18+
- MySQL Server

### 1) Database
Create database:
```sql
CREATE DATABASE vaccine_tracker;
```

Add vaccines:
```sql
INSERT INTO vaccines (name, total_doses, default_gap_days, booster_gap_days) VALUES
('Hepatitis B', 3, 30, NULL),
('COVID-19', 2, 28, 180),
('Tetanus (Td/Tdap)', 1, 0, 3650),
('HPV', 3, 60, NULL);
```

### 2) Backend

Update DB password in:
backend/src/main/resources/application.properties

Run backend:
cd backend
```sql
mvn spring-boot:run
```

Backend runs at:
http://localhost:8080

### 3) Frontend

Run frontend:
cd frontend/frontend
```sql
npm install
npm run dev
```

Frontend runs at:
http://localhost:5173


## 📸 Screenshots

- Login Page
  (ScreenShots/Login Page.png)
  
- Registration Page
  (ScreenShots/Register Page.png)
  
- Dashboard Page
  (ScreenShots/Dashboard.png)
