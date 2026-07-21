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

| Method | Endpoint |
|---------|----------|
| GET | `/telemetry/latest/data` |
| GET | `/telemetry/latest/status` |
| GET | `/telemetry/history` |

### Machine

| Method | Endpoint |
|---------|----------|
| POST | `/machine/start` |
| POST | `/machine/stop` |
| POST | `/machine/unlock` |
| POST | `/machine/lock` |
| POST | `/machine/shutdownHardware` |

---

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

## Current Status

Implemented

- UART communication
- Serial parser
- Telemetry parsing
- REST API
- Machine control endpoints
- Layered architecture (Controller → Service → Serial)

Currently Working On

- Proper HTTP response codes
- Logging
- Exception handling
- WebSocket telemetry streaming

Planned

- Global exception handling(need to fix exception swallowing)
- Database persistence
- Authentication
- Frontend dashboard
- Docker deployment
- Machine state management
- API documentation (OpenAPI / Swagger)

---

## Learning Goals

This project is being built to gain practical experience with:

- Spring Boot
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
