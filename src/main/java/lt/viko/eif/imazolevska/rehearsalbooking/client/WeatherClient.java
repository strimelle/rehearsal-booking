package lt.viko.eif.imazolevska.rehearsalbooking.client;

/**
 * Gives weather forecasts. WeatherService depends on this interface,
 * not on a concrete class, so tests can use a fake implementation.
 */
public interface WeatherClient {

    /**
     * Gets the long-term forecast for a place.
     *
     * @param placeCode the place code, for example "vilnius"
     * @return the forecast
     */
    MeteoForecast getLongTermForecast(String placeCode);
}
