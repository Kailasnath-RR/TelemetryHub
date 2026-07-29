package com.kailas.TelemetryHub.serial;

import com.kailas.TelemetryHub.model.SerialStatus;

public interface SerialInterface {

    void connect();
    void disconnect();
    void reconnect();

    SerialStatus status();

    void startMachine();
    void stopMachine();
    void lockMachine();
    void unlockMachine();

    void machineSpeedIncrease();
    void machineSpeedDecrease();

    void shutdownHardware();
}
