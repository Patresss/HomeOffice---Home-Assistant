package com.patres.homeoffice.settings;

import com.patres.homeoffice.ui.WorkMode;

public record SettingProperties(
        HomeAssistantSettings homeAssistant,
        WindowSettings window,
        WorkSettings workingTime,
        WorkMode currentMode,
        Double automationFrequencySeconds
) {
}
