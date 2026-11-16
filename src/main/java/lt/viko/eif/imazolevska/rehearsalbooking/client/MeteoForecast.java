package lt.viko.eif.imazolevska.rehearsalbooking.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.ArrayList;
import java.util.List;

/**
 * The long-term forecast for one place, as Meteo.lt returns it.
 * Only the fields we use are listed, all other fields are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoForecast {

    private MeteoPlace place;
    private List<MeteoForecastTimestamp> forecastTimestamps = new ArrayList<>();

    /**
     * Empty constructor needed to create the object from JSON.
     */
    public MeteoForecast() {
    }

    /**
     * Returns the place of the forecast.
     *
     * @return the place
     */
    public MeteoPlace getPlace() {
        return place;
    }

    /**
     * Sets the place of the forecast.
     *
     * @param place the place
     */
    public void setPlace(MeteoPlace place) {
        this.place = place;
    }

    /**
     * Returns the forecast for every hour, in time order.
     *
     * @return the list of hourly forecasts
     */
    public List<MeteoForecastTimestamp> getForecastTimestamps() {
        return forecastTimestamps;
    }

    /**
     * Sets the list of hourly forecasts.
     *
     * @param forecastTimestamps the list of hourly forecasts
     */
    public void setForecastTimestamps(List<MeteoForecastTimestamp> forecastTimestamps) {
        this.forecastTimestamps = forecastTimestamps;
    }
}
