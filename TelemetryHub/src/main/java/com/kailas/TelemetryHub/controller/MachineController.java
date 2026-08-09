package com.kailas.TelemetryHub.controller;


import com.kailas.TelemetryHub.model.ErrorResponse;
import com.kailas.TelemetryHub.service.MachineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/machine")
@PreAuthorize("hasAnyRole('ADMIN','OPERATOR')")
public class MachineController {

    private final MachineService machineService;

    public MachineController(MachineService machineService){
        this.machineService = machineService;

    }


    @Operation(summary = "Start the machine",
                description = """
                Start the transmission of ADC data over UART.
                Throws CONFLICT error if machine is already running.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine started successfully"
            ),
            @ApiResponse(responseCode = "409",
                            description = "Machine already running",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "503",
                        description = "Serial port disconnected.",
                        content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/start")
    public ResponseEntity<Void> startMachine(){
            machineService.startMachine();
            return ResponseEntity.noContent().build();
    }

    @Operation(summary = "stop the machine",
            description = """
                stops the transmission of ADC data over UART.
                Throws CONFLICT error if machine is already stopped.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine stopped successfully"
            ),
            @ApiResponse(responseCode = "409",
                    description = "Machine already stopped",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/stop")
    public ResponseEntity<Void> stopMachine(){
            machineService.stopMachine();
            return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Unlock the machine",
            description = """
                Unlock the transmission line.
                Throws CONFLICT error if machine is already unlocked.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine unlocked"
            ),
            @ApiResponse(responseCode = "409",
                    description = "Machine already unlocked",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/unlock")
    public ResponseEntity<Void> unlockMachine(){
            machineService.unlockMachine();
            return ResponseEntity.noContent().build();

        }


    @Operation(summary = "Lock the machine",
            description = """
                Lock the transmission line.
                Throws CONFLICT error if machine is already locked.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine locked"
            ),
            @ApiResponse(responseCode = "409",
                    description = "Machine already locked",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/lock")
    public ResponseEntity<Void> lockMachine(){

        machineService.lockMachine();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Stop the machine",
            description = """
                Stops and locks the transmission of ADC data over UART.
                Throws CONFLICT error if machine is already st.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine shutdown."
            ),
            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/shutdownHardware")
    public ResponseEntity<Void> shutdownHardware(){

        machineService.shutdownMachine();
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Increase the data acquisition speed",
            description = """
                Increase the speed of transmission of ADC data over UART.
                Throws CONFLICT error if machine is pushed beyond safe threshold speed.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine speed increased"
            ),
            @ApiResponse(responseCode = "409",
                    description = "Machine too fast",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            ),
            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/speed-increase")
    public ResponseEntity<Void> speedInc(){
        machineService.machineSpeedIncrease();
        return ResponseEntity.noContent().build();

    }

    @Operation(summary = "Decrease the data acquisition speed",
            description = """
                Slow the transmission of ADC data over UART.
                """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Machine speed decreased"
            ),

            @ApiResponse(responseCode = "503",
                    description = "Serial port disconnected.",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @PostMapping("/speed-decrease")
    public ResponseEntity<Void> speedDec(){
        machineService.machineSpeedDecrease();
        return ResponseEntity.noContent().build();

    }
}
