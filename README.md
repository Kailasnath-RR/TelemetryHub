# TelemetryHub

A Spring Boot backend for monitoring and controlling an embedded system over UART.

TelemetryHub communicates with a dsPIC microcontroller through a serial connection, receives live telemetry packets, parses them into structured objects, and exposes REST APIs for monitoring and machine control.

This project is part of my embedded systems and backend engineering learning journey.

---

## Features

### Telemetry

- Receive UART telemetry packets
- Parse incoming STATUS and DATA packets
- Store the latest telemetry values
- Maintain telemetry history
- Expose telemetry through REST APIs

### Machine Control

Control the embedded device through REST endpoints:

- Start Machine
- Stop Machine
- Lock Machine
- Unlock Machine
- Shutdown Hardware

Commands are transmitted to the microcontroller over UART.

### Serial Port Management
- Connect to the configured serial port.
- Disconnect safely by stopping the reader thread and closing the port.
- Reconnect to the device without restarting the application.
- Query the current serial connection status.

### REST API
- REST endpoints for telemetry, machine control, and serial management.
- Clean layered architecture separating controllers, services, and models.

### Reliability
- Dedicated UART reader thread.
- Graceful shutdown of serial communication.
- Safe reconnection support.
---

## Tech Stack

- Java 17
- Spring Boot 3
- Maven
- jSerialComm
- REST API
- UART Serial Communication

Embedded Hardware

- Microchip dsPIC33
- MPLAB X IDE
- XC16 Compiler

---

## Architecture

```
                   HTTP

                    │
                    ▼
        +----------------------+
        | TelemetryController  |
        | MachineController    |        
        | ServiceController    |
        +----------------------+
                    │
                    ▼
        +----------------------+
        |  TelemetryService    |
        |  MachineService      |
        +----------------------+
                    │
                    ▼
        +----------------------+
        |    SerialService     |
        +----------------------+
                    │
                    ▼
             UART Serial Port
                    │
                    ▼
                dsPIC33 MCU
```

---

## Project Structure

```
src
 ├── controller
 │     ├── MachineController
 │     └── TelemetryController
 │ 
 ├── service
 │     ├── MachineService
 │     └── TelemetryService
 │
 ├── serial
 │     ├── SerialService
 │     └── SerialParser
 │
 ├── model
 │     ├── TelemetryData
 │     └── TelemetryStatus
 │
 └── TelemetryHubApplication
```

---

## REST API

### Telemetry

| Method | Endpoint | Description |
|---------|----------|-------------|
| GET | `/telemetry/latest/data` | Latest telemetry values |
| GET | `/telemetry/latest/status` | Latest machine status |
| GET | `/telemetry/history` | Telemetry history |

### Machine Control

| Method | Endpoint                  | Description |
|--------|---------------------------|-------------|
| POST   | `/machine/start`          | Start machine |
| POST   | `/machine/stop`           | Stop machine |
| POST   | `/machine/lock`           | Lock machine |
| POST   | `/machine/unlock`         | Unlock machine |
| POST   | `/machine/shutdownHardware` | Gracefully shut down hardware | 
| POST   | `/machine/speed-increase` |Increase sampling rate|
| POST   | `/machine/speed-decrease` |Decrease sampling rate|
### Serial Communication

| Method | Endpoint | Description |
|---------|----------|-------------|
| POST | `/serial/connect` | Open serial connection |
| POST | `/serial/disconnect` | Safely disconnect serial communication |
| POST | `/serial/reconnect` | Reconnect to the configured serial port |
| GET | `/serial/status` | Retrieve current serial connection status |

## Example Requests

Start Machine

```http
POST /machine/start
```

Retrieve Latest Telemetry

```http
GET /telemetry/latest/data
```

---

## Current Capabilities

✔ Connect to dsPIC over UART

✔ Send machine control commands

✔ Adjust machine sampling speed

✔ Receive and parse telemetry packets

✔ Broadcast live telemetry over WebSockets

✔ Persist telemetry history to a database

✔ Retrieve telemetry history through REST endpoints

### Machine State
`(Global Exception Handler used here)`

The backend maintains machine state to prevent invalid commands such as:

- Starting an already running machine
- Unlocking an already unlocked machine
- Locking an already locked machine
- Stopping an already stopped machine

The state is reset after a reconnect since the disconnect sequence always stops and locks the hardware.

## Database

Telemetry packets are automatically persisted using Spring Data JPA.

Current database:
- H2 (In-Memory)

Future migration:
- PostgreSQL
### Planned

- Database persistence(PostgreSQL otw)
- Authentication
- Frontend dashboard
- Docker deployment
- Machine state management
- API documentation (OpenAPI / Swagger)
- better state persistence and sync with the MCU 
---

## Learning Goals

This project is being built to gain practical experience with:

- Spring Boot
- Spring Data JPA
- REST APIs
- Backend Architecture
- Embedded Systems
- UART Communication
- Java
- Layered Application Design
- Software Engineering Best Practices

---
## Related Projects

### Embedded Firmware

The firmware running on the dsPIC33 microcontroller is available here:

- [dsPIC33E ADC DMA UART FIRMWARE](https://github.com/Kailasnath-RR/Embedded_Eng/tree/main/ADC_DMA_UART.X)

The firmware is responsible for:

- UART communication
- Sensor acquisition
- Machine control
- Telemetry packet generation
## Author

Kailasnath R
