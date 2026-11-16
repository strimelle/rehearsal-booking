package lt.viko.eif.imazolevska.rehearsalbooking.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The weather forecast for one hour, as Meteo.lt returns it.
 * Only the fields we use are listed, all other fields are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoForecastTimestamp {

    private String forecastTimeUtc;
    private double airTemperature;
    private double feelsLikeTemperature;
    private double windSpeed;
    private double totalPrecipitation;
    private String conditionCode;

    /**
     * Empty constructor needed to create the object from JSON.
     */
    public MeteoForecastTimestamp() {
    }

    /**
     * Returns the time of the forecast in UTC, for example "2026-11-20 16:00:00".
     *
     * @return the forecast time as text
     */
    public String getForecastTimeUtc() {
        return forecastTimeUtc;
    }

    /**
     * Sets the time of the forecast in UTC.
     *
     * @param forecastTimeUtc the forecast time as text
     */
    public void setForecastTimeUtc(String forecastTimeUtc) {
        this.forecastTimeUtc = forecastTimeUtc;
    }

    /**
     * Returns the air temperature in degrees Celsius.
     *
     * @return the air temperature
     */
    public double getAirTemperature() {
        return airTemperature;
    }

    /**
     * Sets the air temperature in degrees Celsius.
     *
     * @param airTemperature the air temperature
     */
    public void setAirTemperature(double airTemperature) {
        this.airTemperature = airTemperature;
    }

    /**
     * Returns how warm or cold it feels, in degrees Celsius.
     *
     * @return the "feels like" temperature
     */
    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
    }

    /**
     * Sets the "feels like" temperature.
     *
     * @param feelsLikeTemperature the "feels like" temperature
     */
    public void setFeelsLikeTemperature(double feelsLikeTemperature) {
        this.feelsLikeTemperature = feelsLikeTemperature;
    }

    /**
     * Returns the wind speed in metres per second.
     *
     * @return the wind speed
     */
    public double getWindSpeed() {
        return windSpeed;
    }

    /**
     * Sets the wind speed in metres per second.
     *
     * @param windSpeed the wind speed
     */
    public void setWindSpeed(double windSpeed) {
        this.windSpeed = windSpeed;
    }

    /**
     * Returns the amount of rain or snow in millimetres.
     *
     * @return the precipitation
     */
    public double getTotalPrecipitation() {
        return totalPrecipitation;
    }

    /**
     * Sets the amount of rain or snow in millimetres.
     *
     * @param totalPrecipitation the precipitation
     */
    public void setTotalPrecipitation(double totalPrecipitation) {
        this.totalPrecipitation = totalPrecipitation;
    }

    /**
     * Returns the weather condition code, for example "light-rain".
     *
     * @return the condition code
     */
    public String getConditionCode() {
        return conditionCode;
    }

    /**
     * Sets the weather condition code.
     *
     * @param conditionCode the condition code
     */
    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }
}
