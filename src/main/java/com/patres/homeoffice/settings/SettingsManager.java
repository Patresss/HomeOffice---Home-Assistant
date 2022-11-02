package com.patres.homeoffice.settings;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.ui.WorkMode;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

public class SettingsManager {

    public static final String SETTING_PATH = "config/settings.yaml";
    private static final Logger logger = getLogger(SettingsManager.class);

    private final ObjectMapper mapper;
    private final String pathToFile;

    private SettingProperties settingProperties;

    public SettingsManager(final String pathToFile) {
        this.mapper = new ObjectMapper(new YAMLFactory());
        this.mapper.registerModule(new JavaTimeModule());
        this.pathToFile = pathToFile;
        try {
            this.settingProperties = loadSettings();
        } catch (Exception e) {
            logger.error("Unable to load settings. {}", e.getMessage(), e);
            throw new ApplicationException("Unable to load settings. Make sure you run the program with administrative privileges.", e);
        }
    }

    public SettingProperties getSettings() {
        return settingProperties;
    }

    public void updateWindowPosition(final Integer positionX, final Integer positionY) {
        final WindowSettings windowSettings = new WindowSettings(settingProperties.window().pinned(), settingProperties.window().enablePreviousPosition(), positionX, positionY);
        final SettingProperties newSettingProperties = new SettingProperties(
                settingProperties.homeAssistant(),
                windowSettings,
                settingProperties.workingTime(),
                settingProperties.currentMode(),
                settingProperties.automationFrequencySeconds()
        );
        saveSettings(newSettingProperties);
    }

    public void updateHomeOfficeMode(final WorkMode currentMode) {
        final SettingProperties newSettingProperties = new SettingProperties(
                settingProperties.homeAssistant(),
                settingProperties.window(),
                settingProperties.workingTime(),
                currentMode,
                settingProperties.automationFrequencySeconds());
        saveSettings(newSettingProperties);
    }


    private void saveSettings(final SettingProperties newSettingProperties) {
        try {
            final byte[] settingsAsByte = mapper.writeValueAsBytes(newSettingProperties);
            final Path path = Paths.get(pathToFile);
            Files.write(path, settingsAsByte);
            settingProperties = newSettingProperties;
        } catch (Exception e) {
            logger.error("Unable to save settings. {}", e.getMessage(), e);

            throw new ApplicationException("Unable to save settings. Make sure you run the program with administrative privileges.", e);
        }
    }

    private SettingProperties loadSettings() throws IOException {
        createSettingIfDoesntExist();
        final URL resource = Paths.get(pathToFile).toUri().toURL();
        return mapper.readValue(resource, SettingProperties.class);
    }

    private void createSettingIfDoesntExist() throws IOException {
        final FileManager fileManager = new FileManager(pathToFile);
        fileManager.createFileIfDoesntExist();
    }

    public void validateSettings() {
        final Map<Predicate<SettingProperties>, String> validationFields = Map.of(
                settings -> settings.homeAssistant().token() == null, "Home Assistant Token cannot be empty",
                settings -> settings.homeAssistant().homeAssistantUrl() == null, "Home Assistant URL cannot be empty",
                settings -> settings.homeAssistant().entityName() == null, "Home Assistant Entity Name cannot be empty"
        );
        final List<String> errors = validationFields.entrySet().stream()
                .filter(entry -> entry.getKey().test(settingProperties))
                .map(Map.Entry::getValue)
                .collect(Collectors.toList());

        if (!errors.isEmpty()) {
            throw new ApplicationException("Invalid setting file - " + SETTING_PATH + ": " + System.lineSeparator() + String.join(System.lineSeparator(), errors));
        }

    }

    public void calculateDefaultValues() {
        final HomeAssistantSettings homeAssistantSettings = new HomeAssistantSettings(
                settingProperties.homeAssistant().token(),
                settingProperties.homeAssistant().homeAssistantUrl(),
                settingProperties.homeAssistant().entityName(),
                Optional.ofNullable(settingProperties.homeAssistant().availableOptionName()).orElse("Available"),
                Optional.ofNullable(settingProperties.homeAssistant().workingOptionName()).orElse("Working"),
                Optional.ofNullable(settingProperties.homeAssistant().meetingMicrophoneOptionName()).orElse("Meeting - microphone"),
                Optional.ofNullable(settingProperties.homeAssistant().meetingCameraOptionName()).orElse("Meeting - camera"),
                Optional.ofNullable(settingProperties.homeAssistant().turnOffOptionName()).orElse("Turn off")
        );
        final WindowSettings windowSettings = new WindowSettings(
                Optional.ofNullable(settingProperties.window().pinned()).orElse(true),
                Optional.ofNullable(settingProperties.window().enablePreviousPosition()).orElse(false),
                Optional.ofNullable(settingProperties.window().positionX()).orElse(200),
                Optional.ofNullable(settingProperties.window().positionY()).orElse(200)
        );

        final WorkSettings workSettings = new WorkSettings(
                settingProperties.workingTime().days(),
                Optional.ofNullable(settingProperties.workingTime().start()).orElse(LocalTime.of(9, 0)),
                Optional.ofNullable(settingProperties.workingTime().end()).orElse(LocalTime.of(17, 0))
        );
        final WorkMode currentMode = Optional.ofNullable(settingProperties.currentMode()).orElse(WorkMode.AUTOMATION);
        final Double automationFrequencySeconds = Optional.ofNullable(settingProperties.automationFrequencySeconds()).orElse(1.0);
        final SettingProperties newSettingProperties = new SettingProperties(
                homeAssistantSettings,
                windowSettings,
                workSettings,
                currentMode,
                automationFrequencySeconds);
        saveSettings(newSettingProperties);
    }

}
