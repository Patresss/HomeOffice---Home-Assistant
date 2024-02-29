package com.patres.homeoffice.work;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.homeassistant.HomeAssistantRestClient;
import com.patres.homeoffice.settings.SettingsManager;
import com.patres.homeoffice.ui.WorkMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;


public class WorkManager {

    private static final Logger logger = LoggerFactory.getLogger(WorkManager.class);

    private final HomeAssistantRestClient homeAssistantRestClient;
    private final Double automationFrequencySeconds;

    private final SettingsManager settingsManager;
    private final AutomationProcess automationProcess;

    private WorkMode currentWorkMode;
    private WorkState currentWorkState;

    public WorkManager(final SettingsManager settingsManager) {
        this.homeAssistantRestClient = new HomeAssistantRestClient(settingsManager.getSettings().homeAssistant());
        this.automationProcess = new AutomationProcess(settingsManager.getSettings().workingTime(), this);
        this.currentWorkMode = settingsManager.getSettings().currentMode();
        this.automationFrequencySeconds = settingsManager.getSettings().automationFrequencySeconds();
        this.settingsManager = settingsManager;
        changeWorkMode(currentWorkMode);
    }

    public void changeWorkMode(final WorkMode workMode) {
        logger.info("Changing work mode: {} -> {}", currentWorkMode, workMode);
        currentWorkMode = workMode;
        workMode.handle(this);
        settingsManager.updateHomeOfficeMode(workMode);
    }

    public void changeHomeOfficeState(final WorkState workState) {
        if (currentWorkState != workState) {
            currentWorkState = workState;
            homeAssistantRestClient.changeOption(workState);
            if (currentWorkMode != WorkMode.AUTOMATION) {
                automationProcess.disableAutomation();
            }
        }
    }

    public Optional<WorkMode> getCurrentHomeOfficeMode() {
        return Optional.ofNullable(currentWorkMode);
    }

    public void enableAutomationChanges() {
        automationProcess.enableAutomation();
    }

    public WorkState getCurrentWorkState() {
        return currentWorkState;
    }
}