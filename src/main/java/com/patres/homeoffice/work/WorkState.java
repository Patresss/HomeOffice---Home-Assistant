package com.patres.homeoffice.work;

import com.patres.homeoffice.settings.HomeAssistantSettings;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public enum WorkState {

    AVAILABLE(HomeAssistantSettings::availableOptionName, 4, automationProcess -> true),
    WORKING(HomeAssistantSettings::workingOptionName, 3, AutomationProcess::isWorkingTime),
    MEETING_MICROPHONE(HomeAssistantSettings::meetingMicrophoneOptionName, 2, AutomationProcess::isMicrophoneWorking),
    MEETING_WEBCAM(HomeAssistantSettings::meetingCameraOptionName, 1, AutomationProcess::isWebcamWorking),
    TURN_OFF(HomeAssistantSettings::turnOffOptionName);

    public static final List<WorkState> AUTOMATION_MODES = Arrays.stream(values())
            .filter(mode -> mode.getAutomationOrder() > 0)
            .sorted(Comparator.comparingInt(WorkState::getAutomationOrder))
            .collect(toList());
    private final EntityOptionNameProvider entityOptionNameProvider;
    private final int automationOrder;
    private final Predicate<AutomationProcess> automationProcessPredicate;


    WorkState(EntityOptionNameProvider entityOptionNameProvider, int automationOrder, Predicate<AutomationProcess> automationProcessPredicate) {
        this.entityOptionNameProvider = entityOptionNameProvider;
        this.automationOrder = automationOrder;
        this.automationProcessPredicate = automationProcessPredicate;
    }

    WorkState(EntityOptionNameProvider entityOptionNameProvider) {
        this(entityOptionNameProvider, -1, null);
    }


    public int getAutomationOrder() {
        return automationOrder;
    }

    public boolean isAutomationProcessRunning(final AutomationProcess automationProcess) {
        if (automationProcessPredicate == null) {
            return false;
        }
        return automationProcessPredicate.test(automationProcess);
    }

    public static WorkState getAutomationAction(final AutomationProcess automationProcess) {
        return AUTOMATION_MODES.stream()
                .filter(mode -> mode.isAutomationProcessRunning(automationProcess))
                .findFirst()
                .orElse(AVAILABLE);
    }

    public String getEntityOptionName(final HomeAssistantSettings settings) {
        return entityOptionNameProvider.getName(settings);
    }
}
