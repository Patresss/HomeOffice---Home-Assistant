package com.patres.homeoffice.homeassistant;

import com.patres.homeoffice.exception.ApplicationException;
import com.patres.homeoffice.settings.HomeAssistantSettings;
import com.patres.homeoffice.work.WorkState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.http.HttpClient.Version.HTTP_1_1;

public class HomeAssistantRestClient {

    private static final Logger logger = LoggerFactory.getLogger(HomeAssistantRestClient.class);

    private final HomeAssistantSettings settings;
    private final URI homeAssistantUrl;
    private final HttpClient client;

    public HomeAssistantRestClient(final HomeAssistantSettings homeAssistantSettings) {
        this.settings = homeAssistantSettings;
        this.client = HttpClient.newBuilder()
                .version(HTTP_1_1)
                .build();
        try {
            this.homeAssistantUrl = new URI(settings.homeAssistantUrl() + "/api/services/input_select/select_option");
        } catch (URISyntaxException e) {
            throw new ApplicationException(e);
        }
    }

    public void changeOption(final WorkState workState) {
        try {
            final String entityOptionName = workState.getEntityOptionName(settings);
            final HttpRequest request = HttpRequest.newBuilder()
                    .uri(homeAssistantUrl)
                    .header("Authorization", "Bearer " + settings.token())
                    .POST(HttpRequest.BodyPublishers.ofString("""
                            {
                                "entity_id": "input_select.work_status",
                                "option": "$"
                            }
                            """.replace("$", entityOptionName)))
                    .build();
            final HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            logger.info("Request was successfully sent to Home Assistant: {}: {}", response.statusCode(), response.body());
        } catch (Exception e) {
            throw new ApplicationException("Cannot send request to Home Assistant", e);
        }
    }
}
