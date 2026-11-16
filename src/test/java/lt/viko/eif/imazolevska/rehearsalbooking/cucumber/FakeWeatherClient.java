package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecast;
import lt.viko.eif.imazolevska.rehearsalbooking.client.WeatherClient;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ExternalServiceException;

/**
 * A fake weather service for the Cucumber tests.
 * The scenarios decide what it returns, so the tests do not depend on the real Meteo.lt.
 */
public class FakeWeatherClient implements WeatherClient {

    private MeteoForecast forecast;
    private boolean available = true;

    /**
     * Sets the forecast that will be returned.
     *
     * @param forecast the forecast
     */
    public void setForecast(MeteoForecast forecast) {
        this.forecast = forecast;
    }

    /**
     * Makes the fake service work or "break".
     *
     * @param available false to act as if Meteo.lt cannot be reached
     */
    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Forgets the settings of the previous scenario.
     */
    public void reset() {
        this.forecast = new MeteoForecast();
        this.available = true;
    }

    /**
     * Returns the forecast set by the scenario, or throws an error if the service is "broken".
     *
     * @param placeCode the place code, not used by the fake
     * @return the forecast
     */
    @Override
    public MeteoForecast getLongTermForecast(String placeCode) {
        if (!available) {
            throw new ExternalServiceException("Weather service is not available");
        }
        return forecast;
    }
}
