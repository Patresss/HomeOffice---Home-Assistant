package com.patres.homeoffice.work;

import com.patres.homeoffice.device.*;
import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.settings.WorkSettings;
import com.patres.homeoffice.ui.WorkMode;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

import static com.patres.homeoffice.device.OSValidator.isMac;
import static com.patres.homeoffice.device.OSValidator.isWindows;


public class AutomationProcess {

    private final WorkSettings workSettings;
    private final DeviceManager deviceManager;
    private final WorkManager workManager;
    private final Thread workingTimeThread;
    private final Thread deviceManagerThread;

    public AutomationProcess(final WorkSettings workSettings, WorkManager workManager) {
        this.workSettings = workSettings;
        this.deviceManager = findDeviceManager();
        this.workManager = workManager;
        this.workingTimeThread = createWorkinTimeThread();
        this.deviceManagerThread = createDeviceManagerThread();
    }

    private DeviceManager findDeviceManager() {
        if (isWindows()) {
            return new WindowsDeviceManager();
        } else if (isMac()) {
            return new MacDeviceManager();
        } else {
            return new DefaultDeviceManager();
        }
    }

    public void enableAutomation() {
        Thread.startVirtualThread(deviceManagerThread);
        Thread.startVirtualThread(workingTimeThread);
    }

    public void disableAutomation() {
        deviceManagerThread.interrupt();
        workingTimeThread.interrupt();
    }

    private Thread createWorkinTimeThread() {
        return new Thread(() -> {
                if (workManager.getCurrentWorkState().isTimeRelatedState()) {
                    if (isWorkingTime()) {
                        workManager.changeHomeOfficeState(WorkState.WORKING);
                    } else {
                        workManager.changeHomeOfficeState(WorkState.AVAILABLE);
                    }
                }
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(5));
                } catch (InterruptedException e) {
                    throw new ApplicationException(e);
                }
        });
    }
    private Thread createDeviceManagerThread() {
        return new Thread(() -> deviceManager.handleDeviceState(this::handleAutomationByDeviceState));
    }

    public void handleAutomationByDeviceState(final DeviceState deviceState) {
        if (deviceState == DeviceState.CAMERA_AND_MICROPHONE || deviceState == DeviceState.CAMERA) {
            workManager.changeHomeOfficeState(WorkState.MEETING_WEBCAM);
        } else if (deviceState == DeviceState.MICROPHONE) {
            workManager.changeHomeOfficeState(WorkState.MEETING_MICROPHONE);
        } else if (isWorkingTime()) {
            workManager.changeHomeOfficeState(WorkState.WORKING);
        } else {
            workManager.changeHomeOfficeState(WorkState.AVAILABLE);
        }
    }

    public boolean isWorkingTime() {
        final LocalDateTime now = LocalDateTime.now();
        return workSettings.days().contains(now.getDayOfWeek()) && now.toLocalTime().isAfter(workSettings.start()) && now.toLocalTime().isBefore(workSettings.end());
    }

}
