package lt.viko.eif.imazolevska.rehearsalbooking.service;

import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecast;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecastTimestamp;
import lt.viko.eif.imazolevska.rehearsalbooking.client.WeatherClient;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ForecastResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Finds the weather forecast for the start of a booking.
 * The forecast comes from Meteo.lt through WeatherClient.
 */
@Service
public class WeatherService {

    private static final ZoneId VILNIUS_ZONE = ZoneId.of("Europe/Vilnius");
    private static final DateTimeFormatter METEO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final long MAX_DISTANCE_MINUTES = 180;

    private final BookingService bookingService;
    private final WeatherClient weatherClient;
    private final String placeCode;

    /**
     * Creates the service. Spring passes the dependencies automatically.
     *
     * @param bookingService the service for bookings
     * @param weatherClient  the client that gives weather forecasts
     * @param placeCode      the Meteo.lt place code from application.properties
     */
    public WeatherService(BookingService bookingService, WeatherClient weatherClient,
                          @Value("${meteo.place-code}") String placeCode) {
        this.bookingService = bookingService;
        this.weatherClient = weatherClient;
        this.placeCode = placeCode;
    }

    /**
     * Returns the weather forecast for the start time of a booking.
     *
     * @param bookingId the booking ID
     * @return the forecast
     * @throws ResourceNotFoundException if the booking does not exist
     *                                   or there is no forecast for its time yet
     */
    public ForecastResponse getForecastForBooking(Long bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);

        // Meteo.lt uses UTC time, our bookings use Vilnius time
        LocalDateTime startUtc = toUtc(booking.getStartTime());

        MeteoForecast forecast = weatherClient.getLongTermForecast(placeCode);
        MeteoForecastTimestamp timestamp = findClosestTimestamp(forecast.getForecastTimestamps(), startUtc);
        if (timestamp == null) {
            throw new ResourceNotFoundException("Weather forecast for this booking time is not available");
        }

        LocalDateTime forecastTimeUtc = LocalDateTime.parse(timestamp.getForecastTimeUtc(), METEO_TIME_FORMAT);
        LocalDateTime forecastTime = toVilniusTime(forecastTimeUtc);
        String placeName = forecast.getPlace().getName();
        String condition = translateCondition(timestamp.getConditionCode());

        ForecastResponse response = new ForecastResponse(
                bookingId,
                placeName,
                forecastTime,
                timestamp.getAirTemperature(),
                timestamp.getFeelsLikeTemperature(),
                timestamp.getWindSpeed(),
                timestamp.getTotalPrecipitation(),
                timestamp.getConditionCode(),
                condition
        );
        return response;
    }

    /**
     * Finds the forecast closest to the given time.
     * Returns null if the closest forecast is more than 3 hours away,
     * for example when the booking is too far in the future.
     */
    private MeteoForecastTimestamp findClosestTimestamp(List<MeteoForecastTimestamp> timestamps,
                                                        LocalDateTime timeUtc) {
        MeteoForecastTimestamp closest = null;
        long closestDistance = Long.MAX_VALUE;

        for (MeteoForecastTimestamp timestamp : timestamps) {
            LocalDateTime forecastTime = LocalDateTime.parse(timestamp.getForecastTimeUtc(), METEO_TIME_FORMAT);
            Duration difference = Duration.between(timeUtc, forecastTime);
            long distance = Math.abs(difference.toMinutes());

            if (distance < closestDistance) {
                closest = timestamp;
                closestDistance = distance;
            }
        }

        if (closestDistance > MAX_DISTANCE_MINUTES) {
            return null;
        }
        return closest;
    }

    /**
     * Converts Vilnius time to UTC time.
     */
    private LocalDateTime toUtc(LocalDateTime vilniusTime) {
        ZonedDateTime inVilnius = vilniusTime.atZone(VILNIUS_ZONE);
        ZonedDateTime inUtc = inVilnius.withZoneSameInstant(ZoneOffset.UTC);
        return inUtc.toLocalDateTime();
    }

    /**
     * Converts UTC time to Vilnius time.
     */
    private LocalDateTime toVilniusTime(LocalDateTime utcTime) {
        ZonedDateTime inUtc = utcTime.atZone(ZoneOffset.UTC);
        ZonedDateTime inVilnius = inUtc.withZoneSameInstant(VILNIUS_ZONE);
        return inVilnius.toLocalDateTime();
    }

    /**
     * Translates a Meteo.lt condition code to Lithuanian.
     */
    private String translateCondition(String conditionCode) {
        if (conditionCode == null) {
            return "Nežinoma";
        }

        switch (conditionCode) {
            case "clear":
                return "Giedra";
            case "partly-cloudy":
                return "Mažai debesuota";
            case "cloudy-with-sunny-intervals":
                return "Debesuota su pragiedruliais";
            case "cloudy":
                return "Debesuota";
            case "light-rain":
                return "Nedidelis lietus";
            case "rain":
                return "Lietus";
            case "heavy-rain":
                return "Smarkus lietus";
            case "thunder":
            case "isolated-thunderstorms":
            case "thunderstorms":
            case "heavy-rain-with-thunderstorms":
                return "Perkūnija";
            case "light-sleet":
            case "sleet":
            case "freezing-rain":
                return "Šlapdriba";
            case "hail":
                return "Kruša";
            case "light-snow":
                return "Nedidelis sniegas";
            case "snow":
                return "Sniegas";
            case "heavy-snow":
                return "Smarkus sniegas";
            case "fog":
                return "Rūkas";
            default:
                return "Nežinoma";
        }
    }
}
