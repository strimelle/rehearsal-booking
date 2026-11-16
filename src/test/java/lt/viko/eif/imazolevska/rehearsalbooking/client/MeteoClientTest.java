package lt.viko.eif.imazolevska.rehearsalbooking.client;

import lt.viko.eif.imazolevska.rehearsalbooking.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class MeteoClientTest {

    private MockRestServiceServer meteoServer;
    private MeteoClient meteoClient;

    @BeforeEach
    void setUp() {
        // A fake Meteo.lt server: no real request leaves the computer
        RestClient.Builder builder = RestClient.builder().baseUrl("https://api.meteo.lt/v1");
        meteoServer = MockRestServiceServer.bindTo(builder).build();

        RestClient restClient = builder.build();
        meteoClient = new MeteoClient(restClient);
    }

    @Test
    void getLongTermForecastShouldReadPlaceAndTimestamps() {
        // Arrange: a short answer in the same format as Meteo.lt.
        // "windGust" and "cloudCover" are not used, they must be ignored.
        String json = """
                {
                  "place": {"code": "vilnius", "name": "Vilnius", "countryCode": "LT"},
                  "forecastType": "long-term",
                  "forecastTimestamps": [
                    {
                      "forecastTimeUtc": "2026-12-01 16:00:00",
                      "airTemperature": -2.5,
                      "feelsLikeTemperature": -6.0,
                      "windSpeed": 4,
                      "windGust": 9,
                      "cloudCover": 100,
                      "totalPrecipitation": 0.3,
                      "conditionCode": "light-snow"
                    },
                    {
                      "forecastTimeUtc": "2026-12-01 17:00:00",
                      "airTemperature": -3.0,
                      "feelsLikeTemperature": -7.0,
                      "windSpeed": 5,
                      "totalPrecipitation": 0,
                      "conditionCode": "cloudy"
                    }
                  ]
                }
                """;
        meteoServer.expect(requestTo("https://api.meteo.lt/v1/places/vilnius/forecasts/long-term"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(json, MediaType.APPLICATION_JSON));

        // Act
        MeteoForecast forecast = meteoClient.getLongTermForecast("vilnius");

        // Assert
        assertEquals("Vilnius", forecast.getPlace().getName());

        List<MeteoForecastTimestamp> timestamps = forecast.getForecastTimestamps();
        assertEquals(2, timestamps.size());

        MeteoForecastTimestamp first = timestamps.get(0);
        assertEquals("2026-12-01 16:00:00", first.getForecastTimeUtc());
        assertEquals(-2.5, first.getAirTemperature());
        assertEquals(4.0, first.getWindSpeed());
        assertEquals("light-snow", first.getConditionCode());

        meteoServer.verify();
    }

    @Test
    void getLongTermForecastShouldThrowExceptionWhenMeteoReturnsError() {
        // Arrange: Meteo.lt does not know this place
        meteoServer.expect(requestTo("https://api.meteo.lt/v1/places/nowhere/forecasts/long-term"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        // Act and Assert
        try {
            meteoClient.getLongTermForecast("nowhere");
            fail("Expected ExternalServiceException");
        } catch (ExternalServiceException exception) {
            assertEquals("Weather service is not available", exception.getMessage());
        }
    }
}
