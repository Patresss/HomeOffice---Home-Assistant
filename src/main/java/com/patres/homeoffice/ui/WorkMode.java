package com.patres.homeoffice.ui;

import com.patres.homeoffice.work.LightConsumer;
import com.patres.homeoffice.work.WorkManager;
import com.patres.homeoffice.work.WorkState;

import java.util.Arrays;
import java.util.Optional;

public enum WorkMode {

    AVAILABLE(workManager -> workManager.changeHomeOfficeState(WorkState.AVAILABLE)),
    WORKING(workManager -> workManager.changeHomeOfficeState(WorkState.WORKING)),
    MEETING_MICROPHONE(workManager -> workManager.changeHomeOfficeState(WorkState.MEETING_MICROPHONE)),
    MEETING_WEBCAM(workManager -> workManager.changeHomeOfficeState(WorkState.MEETING_WEBCAM)),
    TURN_OFF(workManager -> workManager.changeHomeOfficeState(WorkState.TURN_OFF)),
    AUTOMATION(WorkManager::enableAutomationChanges);

    private static final String BUTTON_SUFFIX = "Button";
    private static final String ENUM_SEPARATOR = "_";
    private static final String CSS_SEPARATOR = "-";
    private final LightConsumer lightConsumer;

    WorkMode(LightConsumer lightConsumer) {
        this.lightConsumer = lightConsumer;
    }

    public String getName() {
        return name().toLowerCase().replace(ENUM_SEPARATOR, CSS_SEPARATOR);
    }

    public static Optional<WorkMode> findByButtonsId(final String id) {
        final String modeName = id.replace(BUTTON_SUFFIX, "");
        return Arrays.stream(values())
                .filter(lightMode -> lightMode.getName().replace(CSS_SEPARATOR, "").equalsIgnoreCase(modeName))
                .findFirst();
    }

    public void handle(final WorkManager workManager) {
        lightConsumer.handleWorkMode(workManager);
    }

}