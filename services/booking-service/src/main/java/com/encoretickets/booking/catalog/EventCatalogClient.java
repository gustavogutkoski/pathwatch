package com.encoretickets.booking.catalog;

import java.time.Duration;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Component
public class EventCatalogClient {

    private final RestClient restClient;

    public EventCatalogClient(RestClient.Builder builder,
                              @Value("${catalog.base-url}") String baseUrl,
                              @Value("${catalog.timeout}") Duration timeout) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeout);
        requestFactory.setReadTimeout(timeout);

        this.restClient = builder
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    public Optional<CatalogEvent> findEvent(String eventId) {
        try {
            CatalogEvent event = restClient.get()
                    .uri("/events/{id}", eventId)
                    .retrieve()
                    .body(CatalogEvent.class);
            return Optional.ofNullable(event);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (RestClientException e) {
            throw new CatalogUnavailableException(e);
        }
    }
}