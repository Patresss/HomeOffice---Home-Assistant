package com.patres.homeoffice.device;

public enum DeviceState {
    CAMERA_AND_MICROPHONE,
    CAMERA,
    MICROPHONE,
    NONE;

    public static DeviceState calculateDeviceState(boolean cameraState, boolean microphoneState) {
        if (cameraState && microphoneState) {
            return CAMERA_AND_MICROPHONE;
        }
        if (cameraState) {
            return CAMERA;
        }
        if (microphoneState) {
            return MICROPHONE;
        }
        return NONE;
    }
}
