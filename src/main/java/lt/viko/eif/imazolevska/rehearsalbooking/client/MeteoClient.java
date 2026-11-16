package lt.viko.eif.imazolevska.rehearsalbooking.client;

import lt.viko.eif.imazolevska.rehearsalbooking.exception.ExternalServiceException;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

/**
 * Gets weather forecasts from the public Meteo.lt REST API (https://api.meteo.lt).
 * This is the only class that knows how to talk to Meteo.lt.
 */
@Component
public class MeteoClient implements WeatherClient {

    private final RestClient meteoRestClient;

    /**
     * Creates the client. Spring passes the configured RestClient automatically.
     *
     * @param meteoRestClient the RestClient with the Meteo.lt base address
     */
    public MeteoClient(RestClient meteoRestClient) {
        this.meteoRestClient = meteoRestClient;
    }

    /**
     * Gets the long-term forecast for a place.
     * The answer is cached, so Meteo.lt is called only once for the same place.
     *
     * @param placeCode the Meteo.lt place code, for example "vilnius"
     * @return the forecast
     * @throws ExternalServiceException if Meteo.lt cannot be reached or returns an error
     */
    @Override
    @Cacheable("forecasts")
    public MeteoForecast getLongTermForecast(String placeCode) {
        try {
            MeteoForecast forecast = meteoRestClient.get()
                    .uri("/places/{placeCode}/forecasts/long-term", placeCode)
                    .retrieve()
                    .body(MeteoForecast.class);
            return forecast;
        } catch (RestClientException exception) {
            throw new ExternalServiceException("Weather service is not available");
        }
    }
}
