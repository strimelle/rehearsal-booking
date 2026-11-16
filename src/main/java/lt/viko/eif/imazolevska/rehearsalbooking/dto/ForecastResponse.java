package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import org.springframework.hateoas.RepresentationModel;

import java.time.LocalDateTime;

/**
 * The weather forecast for the start of a booking, together with HATEOAS links.
 */
public class ForecastResponse extends RepresentationModel<ForecastResponse> {

    private final Long bookingId;
    private final String place;
    private final LocalDateTime forecastTime;
    private final double airTemperature;
    private final double feelsLikeTemperature;
    private final double windSpeed;
    private final double totalPrecipitation;
    private final String conditionCode;
    private final String condition;

    /**
     * Creates the response.
     *
     * @param bookingId            the ID of the booking
     * @param place                the name of the place, for example "Vilnius"
     * @param forecastTime         the time of the forecast in Vilnius time
     * @param airTemperature       the air temperature in degrees Celsius
     * @param feelsLikeTemperature how warm or cold it feels, in degrees Celsius
     * @param windSpeed            the wind speed in metres per second
     * @param totalPrecipitation   the amount of rain or snow in millimetres
     * @param conditionCode        the Meteo.lt condition code, for example "light-rain"
     * @param condition            the condition in Lithuanian, for example "Nedidelis lietus"
     */
    public ForecastResponse(Long bookingId, String place, LocalDateTime forecastTime, double airTemperature,
                            double feelsLikeTemperature, double windSpeed, double totalPrecipitation,
                            String conditionCode, String condition) {
        this.bookingId = bookingId;
        this.place = place;
        this.forecastTime = forecastTime;
        this.airTemperature = airTemperature;
        this.feelsLikeTemperature = feelsLikeTemperature;
        this.windSpeed = windSpeed;
        this.totalPrecipitation = totalPrecipitation;
        this.conditionCode = conditionCode;
        this.condition = condition;
    }

    /**
     * Returns the ID of the booking.
     *
     * @return the booking ID
     */
    public Long getBookingId() {
        return bookingId;
    }

    /**
     * Returns the name of the place.
     *
     * @return the place name
     */
    public String getPlace() {
        return place;
    }

    /**
     * Returns the time of the forecast in Vilnius time.
     *
     * @return the forecast time
     */
    public LocalDateTime getForecastTime() {
        return forecastTime;
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
     * Returns how warm or cold it feels, in degrees Celsius.
     *
     * @return the "feels like" temperature
     */
    public double getFeelsLikeTemperature() {
        return feelsLikeTemperature;
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
     * Returns the amount of rain or snow in millimetres.
     *
     * @return the precipitation
     */
    public double getTotalPrecipitation() {
        return totalPrecipitation;
    }

    /**
     * Returns the Meteo.lt condition code.
     *
     * @return the condition code
     */
    public String getConditionCode() {
        return conditionCode;
    }

    /**
     * Returns the weather condition in Lithuanian.
     *
     * @return the condition
     */
    public String getCondition() {
        return condition;
    }
}
