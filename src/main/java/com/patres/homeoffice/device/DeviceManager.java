package com.patres.homeoffice.device;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public interface DeviceManager {


    void handleDeviceState(Consumer<DeviceState> changeWorkState);

}
