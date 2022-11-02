package com.patres.homeoffice.settings;

public record HomeAssistantSettings(
        String token,
        String homeAssistantUrl,
        String entityName,
        String availableOptionName,
        String workingOptionName,
        String meetingMicrophoneOptionName,
        String meetingCameraOptionName,
        String turnOffOptionName
) {

}
