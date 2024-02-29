package com.patres.homeoffice.device;

import com.patres.homeoffice.exception.ApplicationException;
import java.util.function.Consumer;

import static com.patres.homeoffice.device.DeviceState.calculateDeviceState;
import static com.patres.homeoffice.registry.RegistryManager.isDeviceWorking;
import static com.patres.homeoffice.registry.RegistryType.MICROPHONE;
import static com.patres.homeoffice.registry.RegistryType.WEBCAM;

public class WindowsDeviceManager implements DeviceManager {

    @Override
    public void handleDeviceState(Consumer<DeviceState> changeWorkState) {
        boolean cameraState = isDeviceWorking(WEBCAM);
        boolean microphoneState = isDeviceWorking(MICROPHONE);
        changeWorkState.accept(calculateDeviceState(cameraState, microphoneState));

        try {
            Thread.sleep((long) (1000.0));
        } catch (InterruptedException e) {
            throw new ApplicationException(e);
        }
    }

}