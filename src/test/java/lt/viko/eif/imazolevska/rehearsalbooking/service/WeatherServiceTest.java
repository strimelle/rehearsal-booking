package lt.viko.eif.imazolevska.rehearsalbooking.service;

import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecast;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecastTimestamp;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoPlace;
import lt.viko.eif.imazolevska.rehearsalbooking.client.WeatherClient;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ForecastResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WeatherServiceTest {

    @Mock
    private BookingService bookingService;

    @Mock
    private WeatherClient weatherClient;

    private WeatherService weatherService;

    @BeforeEach
    void setUp() {
        // @InjectMocks is not used, because the place code is a String, not a mock
        weatherService = new WeatherService(bookingService, weatherClient, "vilnius");
    }

    @Test
    void getForecastShouldReturnForecastForBookingStartInWinter() {
        // Arrange: in winter Vilnius is UTC+2, so 18:00 in Vilnius is 16:00 UTC
        prepareBooking(LocalDateTime.of(2026, 12, 1, 18, 0));

        List<MeteoForecastTimestamp> timestamps = new ArrayList<>();
        timestamps.add(createTimestamp("2026-12-01 15:00:00", -1.0, "cloudy"));
        timestamps.add(createTimestamp("2026-12-01 16:00:00", -2.5, "light-snow"));
        timestamps.add(createTimestamp("2026-12-01 17:00:00", -3.0, "cloudy"));
        prepareForecast(timestamps);

        // Act
        ForecastResponse response = weatherService.getForecastForBooking(10L);

        // Assert: the 16:00 UTC forecast is chosen and shown in Vilnius time
        assertEquals(-2.5, response.getAirTemperature());
        assertEquals("light-snow", response.getConditionCode());
        assertEquals("Nedidelis sniegas", response.getCondition());
        assertEquals(LocalDateTime.of(2026, 12, 1, 18, 0), response.getForecastTime());
        assertEquals("Vilnius", response.getPlace());
    }

    @Test
    void getForecastShouldUseSummerTime() {
        // Arrange: in summer Vilnius is UTC+3, so 18:00 in Vilnius is 15:00 UTC
        prepareBooking(LocalDateTime.of(2026, 7, 1, 18, 0));

        List<MeteoForecastTimestamp> timestamps = new ArrayList<>();
        timestamps.add(createTimestamp("2026-07-01 15:00:00", 24.0, "clear"));
        timestamps.add(createTimestamp("2026-07-01 16:00:00", 23.0, "partly-cloudy"));
        prepareForecast(timestamps);

        // Act
        ForecastResponse response = weatherService.getForecastForBooking(10L);

        // Assert
        assertEquals(24.0, response.getAirTemperature());
        assertEquals("Giedra", response.getCondition());
    }

    @Test
    void getForecastShouldThrowExceptionWhenBookingIsTooFarAway() {
        // Arrange: the forecast ends many days before the booking
        prepareBooking(LocalDateTime.of(2026, 12, 20, 18, 0));

        List<MeteoForecastTimestamp> timestamps = new ArrayList<>();
        timestamps.add(createTimestamp("2026-12-01 16:00:00", -2.5, "light-snow"));
        prepareForecast(timestamps);

        // Act and Assert
        try {
            weatherService.getForecastForBooking(10L);
            fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException exception) {
            assertEquals("Weather forecast for this booking time is not available", exception.getMessage());
        }
    }

    /**
     * Makes the booking service return booking 10 with the given start time.
     */
    private void prepareBooking(LocalDateTime start) {
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        Booking booking = new Booking(band, room, start, start.plusHours(2), new BigDecimal("30.00"));
        when(bookingService.getBookingById(10L)).thenReturn(booking);
    }

    /**
     * Makes the weather client return a forecast for Vilnius with the given hours.
     */
    private void prepareForecast(List<MeteoForecastTimestamp> timestamps) {
        MeteoPlace place = new MeteoPlace();
        place.setCode("vilnius");
        place.setName("Vilnius");

        MeteoForecast forecast = new MeteoForecast();
        forecast.setPlace(place);
        forecast.setForecastTimestamps(timestamps);

        when(weatherClient.getLongTermForecast("vilnius")).thenReturn(forecast);
    }

    /**
     * Creates the forecast for one hour.
     */
    private MeteoForecastTimestamp createTimestamp(String timeUtc, double temperature, String conditionCode) {
        MeteoForecastTimestamp timestamp = new MeteoForecastTimestamp();
        timestamp.setForecastTimeUtc(timeUtc);
        timestamp.setAirTemperature(temperature);
        timestamp.setConditionCode(conditionCode);
        return timestamp;
    }
}
