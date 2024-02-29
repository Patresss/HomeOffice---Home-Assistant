package com.patres.homeoffice.device;

import com.patres.homeoffice.exception.ApplicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.patres.homeoffice.device.DeviceState.calculateDeviceState;

public class MacDeviceManager implements DeviceManager {

    private static final String startCameraEventMessage = "AppleH13CamIn::power_on_hardware";
    private static final String stopCameraEventMessage = "AppleH13CamIn::power_off_hardware";
    private static final String startAudioEventMessage = "- IOAudioDevice[<private>]::audioEngineStarting() - numRunningAudioEngines";
    private static final String stopAudioEventMessage = "- IOAudioDevice[<private>]::audioEngineStopped() - numRunningAudioEngines = 0";

    private static final List<String> events = List.of(startCameraEventMessage, stopCameraEventMessage, startAudioEventMessage, stopAudioEventMessage);
    private static final String command = "log stream --predicate 'process == \"kernel\" " + events.stream()
            .map(event -> "eventMessage contains \"" + event + "\"")
            .collect(Collectors.joining(" || ", " && (", ")'"));


    @Override
    public void handleDeviceState(Consumer<DeviceState> changeWorkState) {
        try {
            final ProcessBuilder processBuilder = new ProcessBuilder("/bin/sh", "-c", command);
            final Process process = processBuilder.start();

            final BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            boolean cameraState = false;
            boolean microphoneState = false;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(startCameraEventMessage)) {
                    cameraState = true;
                }
                if (line.contains(stopCameraEventMessage)) {
                    cameraState = false;
                }
                if (line.contains(startAudioEventMessage)) {
                    microphoneState = true;
                }
                if (line.contains(stopAudioEventMessage)) {
                    microphoneState = false;
                }
                changeWorkState.accept(calculateDeviceState(cameraState, microphoneState));
            }
        } catch (IOException e) {
            throw new ApplicationException(e);
        }
    }

}