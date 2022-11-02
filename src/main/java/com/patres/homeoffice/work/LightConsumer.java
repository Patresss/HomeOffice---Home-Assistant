package com.patres.homeoffice.work;

@FunctionalInterface
public interface LightConsumer {

    void handleWorkMode(WorkManager workManager);
}
