# TelemetryHub

A full-stack, real-time IoT monitoring and control platform for embedded systems over UART.

TelemetryHub communicates with a dsPIC microcontroller through a serial connection, parses live telemetry packets, stores telemetry history in PostgreSQL, broadcasts updates over STOMP WebSockets, and exposes secure REST APIs protected by JWT authentication and Role-Based Access Control (RBAC).

---

## Architecture Overview

```
                      Browser Client (HTTP / WS)
                                  │
                                  ▼
                    Nginx Reverse Proxy (:80)
                   ┌──────────────┴──────────────┐
                   │                             │
                   ▼                             ▼
         React SPA Static Files        Spring Boot Backend (:8080)
                                                 │
                                     ┌───────────┴───────────┐
                                     ▼                       ▼
                           PostgreSQL DB (:5432)     UART Serial Port
                                                             │
                                                             ▼
                                                        dsPIC33 MCU
```

### Production Docker Container Stack

- **`frontend`** (Nginx + React SPA on port `80`)
- **`backend`** (Spring Boot 3 REST & STOMP WebSocket service on port `8080`)
- **`postgres`** (PostgreSQL 17 persistent database on port `5432`)

---

## Key Features

### 1. Real-Time Telemetry & STOMP WebSockets
- **Live Packet Stream:** Live reception of ADC values, sample counts, and sample period metrics broadcasted over STOMP WebSockets (`/ws -> /topic/telemetry`).
- **Live Machine Status:** Real-time state change broadcasts (`/topic/status`).
- **Real-Time Threshold Alerts:** Automatic alert creation when ADC readings exceed configured voltage thresholds (`> 900`).

### 2. Telemetry Analytics & Historical Data
- **Paginated History:** Browse past telemetry logs with page navigation (`Next` / `Previous`).
- **Range Filtering:** Filter telemetry records by `adcMin` and `adcMax`.
- **Aggregate Statistics:** Query average ADC, min ADC, max ADC, and total sample counts with custom timestamp (`from` / `to`) and ADC range filters (`/telemetry/stats`).

### 3. Machine Control & State Guarding
- **Hardware Control Endpoints:** Start, Stop, Lock, Unlock, Shutdown, Speed +, and Speed -.
- **State Validation & Error Handling:** Backend prevents invalid transitions (e.g. starting an already running machine) and returns clean `ErrorResponse` DTOs via a `@ControllerAdvice` global exception handler.

### 4. Serial Port Management
- **Safe Connection Control:** Connect, disconnect, and reconnect UART serial interfaces without restarting the Spring Boot application.
- **Port Status Monitoring:** Inspect active port status (`connected`, `portName`).

### 5. Security & Role-Based Access Control (RBAC)
- **Stateless JWT Authentication:** Secure `/auth/login`, `/auth/me`, `/auth/refresh`, and `/auth/logout` endpoints.
- **Granular Roles (`ADMIN`, `OPERATOR`, `VIEWER`):**
  - `ADMIN`: Full access (Machine Control, Serial Control, Telemetry).
  - `OPERATOR`: Machine Control and Telemetry.
  - `VIEWER`: Read-only access to Telemetry and Status.
- **Graceful Frontend Enforcement:** Captures HTTP `403 Forbidden` responses and displays clear authorization notices.

### 6. React + Vite Frontend Dashboard
- **Modern Dashboard UI:** Built with React 18, Vite 5, JavaScript, and Vanilla CSS.
- **Tabbed Authentication:** Login and user registration interface.
- **Interactive Controls & Status Badges:** Visual machine control buttons, serial status, and live WebSocket connection badges (`Connected` / `Connecting` / `Disconnected`).

### 7. Dockerization & Nginx Reverse Proxy
- **Single-Origin Deployment:** Nginx proxies REST requests (`/auth`, `/machine`, `/serial`, `/telemetry`) and WebSocket upgrades (`/ws`) to the backend container.
- **Single Command Launch:** Complete environment orchestrator via `docker compose up`.

---

## Tech Stack

### Backend
- **Java 17 & Spring Boot 3**
- **Spring Security** (JWT Authentication & RBAC)
- **Spring Data JPA & Flyway** (Database Migrations)
- **Spring WebSocket / STOMP**
- **PostgreSQL 17**
- **jSerialComm** (UART Serial Communication)
- **OpenAPI / Swagger UI**

### Frontend
- **React 18 & Vite 5**
- **JavaScript & Vanilla CSS**
- **@stomp/stompjs** (STOMP Client over WebSockets)
- **Nginx** (Reverse Proxy & Static Asset Server)

### Embedded Hardware & Firmware
- **Microchip dsPIC33 MCU**
- **MPLAB X IDE & XC16 Compiler**
- **Firmware Repository:** [dsPIC33E ADC DMA UART FIRMWARE](https://github.com/Kailasnath-RR/Embedded_Eng/tree/main/ADC_DMA_UART.X)

---

## REST API Summary

### Authentication (`/auth`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Public | Register new user account |
| `POST` | `/auth/login` | Public | Authenticate user; returns JWT access & refresh tokens |
| `GET` | `/auth/me` | Authenticated | Retrieve current user profile (`username`, `role`) |
| `POST` | `/auth/refresh` | Public | Obtain new access token via refresh token |
| `POST` | `/auth/logout` | Authenticated | Revoke refresh token |

### Machine Control (`/machine`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/machine/start` | `ADMIN`, `OPERATOR` | Start ADC transmission over UART |
| `POST` | `/machine/stop` | `ADMIN`, `OPERATOR` | Stop transmission |
| `POST` | `/machine/lock` | `ADMIN`, `OPERATOR` | Lock transmission line |
| `POST` | `/machine/unlock` | `ADMIN`, `OPERATOR` | Unlock transmission line |
| `POST` | `/machine/shutdownHardware` | `ADMIN`, `OPERATOR` | Gracefully shut down hardware |
| `POST` | `/machine/speed-increase` | `ADMIN`, `OPERATOR` | Increase sampling rate |
| `POST` | `/machine/speed-decrease` | `ADMIN`, `OPERATOR` | Decrease sampling rate |

### Serial Management (`/serial`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/serial/status` | `ADMIN` | Query current serial port status |
| `POST` | `/serial/reconnect` | `ADMIN` | Reconnect serial port and reset state |
| `POST` | `/serial/disconnect` | `ADMIN` | Safely disconnect serial communication |

### Telemetry (`/telemetry`)

| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/telemetry/latest/data` | Authenticated | Retrieve latest telemetry record |
| `GET` | `/telemetry/latest/status` | Authenticated | Retrieve latest machine status |
| `GET` | `/telemetry/history` | Authenticated | Paginated telemetry history (`page`, `size`, `adcMin`, `adcMax`) |
| `GET` | `/telemetry/stats` | Authenticated | Query aggregate stats (`from`, `to`, `minAdc`, `maxAdc`) |

### WebSockets (`/ws`)

| Protocol | Destination | Description |
| :--- | :--- | :--- |
| STOMP | `/topic/telemetry` | Real-time `TelemetryData` broadcast (`Count`, `AdcValue`, `SamplePeriod`, `receivedAt`) |
| STOMP | `/topic/status` | Real-time `TelemetryStatus` broadcast (`State`, `receivedAt`) |

---

## How to Run the Project

### Option 1: Docker Compose (Recommended Production Run)

1. Create a `.env` file in the root directory:
   ```env
   DB_USERNAME=telemetry_app
   DB_PASSWORD=your_db_password
   JWT_SECRET=your_jwt_secret_key_here
   ```

2. Launch all services:
   ```bash
   docker compose up --build
   ```

3. Access the applications:
   - **Frontend Dashboard:** [http://localhost](http://localhost)
   - **Backend API / Swagger UI:** [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

### Option 2: Local Development Mode

#### 1. Backend
```bash
cd TelemetryHub
./mvnw spring-boot:run
```

#### 2. Frontend
```bash
cd frontend
npm install
npm run dev
```
The Vite development server will start at `http://localhost:3000`.

---

## Author

**Kailasnath R**
