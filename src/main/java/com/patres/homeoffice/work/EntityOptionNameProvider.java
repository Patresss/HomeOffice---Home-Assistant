package com.patres.homeoffice.work;

import com.patres.homeoffice.settings.HomeAssistantSettings;

@FunctionalInterface
public interface EntityOptionNameProvider {

    String getName(HomeAssistantSettings homeAssistantSettings);
}
