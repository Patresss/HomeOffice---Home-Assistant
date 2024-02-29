package com.patres.homeoffice.work;

import com.patres.homeoffice.settings.HomeAssistantSettings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public enum WorkState {

    AVAILABLE(HomeAssistantSettings::availableOptionName, 4,true),
    WORKING(HomeAssistantSettings::workingOptionName, 3,true),
    MEETING_MICROPHONE(HomeAssistantSettings::meetingMicrophoneOptionName, 2,false),
    MEETING_WEBCAM(HomeAssistantSettings::meetingCameraOptionName, 1,false),
    TURN_OFF(HomeAssistantSettings::turnOffOptionName);

    public static final List<WorkState> AUTOMATION_MODES = Arrays.stream(values())
            .filter(mode -> mode.getAutomationOrder() > 0)
            .sorted(Comparator.comparingInt(WorkState::getAutomationOrder))
            .collect(toList());
    private final EntityOptionNameProvider entityOptionNameProvider;
    private final int automationOrder;
    private final boolean timeRelatedState;


    WorkState(EntityOptionNameProvider entityOptionNameProvider, int automationOrder, final boolean timeRelatedState) {
        this.entityOptionNameProvider = entityOptionNameProvider;
        this.automationOrder = automationOrder;
        this.timeRelatedState = timeRelatedState;
    }

    WorkState(EntityOptionNameProvider entityOptionNameProvider) {
        this(entityOptionNameProvider, -1, false);
    }


    public int getAutomationOrder() {
        return automationOrder;
    }


    public String getEntityOptionName(final HomeAssistantSettings settings) {
        return entityOptionNameProvider.getName(settings);
    }

    public boolean isTimeRelatedState() {
        return timeRelatedState;
    }
}
