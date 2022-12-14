package com.patres.homeoffice.ui;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXToggleButton;
import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.work.WorkManager;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainPane extends AnchorPane {

    public static final String BUTTON_PUSHED_SUFFIX_STYLE = "-button-pushed";
    public static final String BUTTON_SUFFIX_STYLE = "-button";
    public static final String MAIN_BUTTON_COLOR_STYLE = "main-button-color";
    @FXML
    private JFXButton availableButton;
    @FXML
    private JFXButton workingButton;
    @FXML
    private JFXButton meetingMicrophoneButton;
    @FXML
    private JFXButton meetingWebcamButton;
    @FXML
    private JFXButton automationButton;
    @FXML
    private JFXButton turnOffButton;
    @FXML
    private JFXButton exitButton;
    @FXML
    private JFXToggleButton pinToggleButton;

    private JFXButton selectedButton;


    private final Stage primaryStage;
    private final WorkManager workManager;

    public MainPane(final Stage primaryStage, final boolean pinned, WorkManager workManager) throws IOException {
        this.primaryStage = primaryStage;
        this.workManager = workManager;
        this.primaryStage.setAlwaysOnTop(pinned);

        final FXMLLoader fxmlLoader = new FXMLLoader(PrimaryWindow.class.getResource("/fxml/MainPane.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        fxmlLoader.load();
    }

    @FXML
    public void initialize() {
        pinToggleButton.setSelected(!primaryStage.isAlwaysOnTop());
        pinToggleButton.selectedProperty().addListener((obs, newValue, oldValue) -> primaryStage.setAlwaysOnTop(newValue));

        availableButton.setOnAction(event -> {
            selectButton(availableButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.AVAILABLE)).start();
        });
        workingButton.setOnAction(event -> {
            selectButton(workingButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.WORKING)).start();
        });
        meetingMicrophoneButton.setOnAction(event -> {
            selectButton(meetingMicrophoneButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.MEETING_MICROPHONE)).start();
        });
        meetingWebcamButton.setOnAction(event -> {
            selectButton(meetingWebcamButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.MEETING_WEBCAM)).start();
        });
        automationButton.setOnAction(event -> {
            selectButton(automationButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.AUTOMATION)).start();
        });
        turnOffButton.setOnAction(event -> {
            selectButton(turnOffButton);
            new Thread(() -> workManager.changeWorkMode(WorkMode.TURN_OFF)).start();
        });
        workManager.getCurrentHomeOfficeMode()
                .ifPresent(homeOfficeMode -> selectButton(getButtonByHomeOfficeMode(homeOfficeMode)));
    }

    private JFXButton getButtonByHomeOfficeMode(final WorkMode workMode) {
        return switch (workMode) {
            case AVAILABLE -> availableButton;
            case WORKING -> workingButton;
            case MEETING_MICROPHONE -> meetingMicrophoneButton;
            case MEETING_WEBCAM -> meetingWebcamButton;
            case TURN_OFF -> turnOffButton;
            case AUTOMATION -> automationButton;
        };
    }

    @FXML
    public void exitWindow() {
        primaryStage.close();
    }

    private void selectButton(final JFXButton buttonToSelect) {
        unselectButton(buttonToSelect);

        final String styleName = WorkMode.findByButtonsId(buttonToSelect.getId())
                .map(WorkMode::getName)
                .orElseThrow(() -> new ApplicationException("Cannot find style by id: " + buttonToSelect.getId()));
        if (!buttonToSelect.getStyleClass().contains(styleName + BUTTON_PUSHED_SUFFIX_STYLE)) {
            buttonToSelect.getStyleClass().add(styleName + BUTTON_PUSHED_SUFFIX_STYLE);
            buttonToSelect.getStyleClass().remove(styleName + BUTTON_SUFFIX_STYLE);
        }
        selectedButton = buttonToSelect;
    }

    private void unselectButton(final JFXButton newButtonToSelect) {
        if (selectedButton != null) {
            final String styleName = WorkMode.findByButtonsId(selectedButton.getId())
                    .map(WorkMode::getName)
                    .orElseThrow(() -> new ApplicationException("Cannot find style by id: " + selectedButton.getId()));
            if (!selectedButton.getStyleClass().contains(MAIN_BUTTON_COLOR_STYLE)) {
                selectedButton.getStyleClass().remove(styleName + BUTTON_PUSHED_SUFFIX_STYLE);
                selectedButton.getStyleClass().add(styleName + BUTTON_SUFFIX_STYLE);
            }
        }
        selectedButton = newButtonToSelect;
    }

}