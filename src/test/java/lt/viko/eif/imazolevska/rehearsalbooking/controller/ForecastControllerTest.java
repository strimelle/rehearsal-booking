package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import lt.viko.eif.imazolevska.rehearsalbooking.dto.ForecastResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ExternalServiceException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.GlobalExceptionHandler;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.service.WeatherService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ForecastControllerTest {

    @Mock
    private WeatherService weatherService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        ForecastController controller = new ForecastController(weatherService);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void getForecastShouldReturnForecast() throws Exception {
        // Arrange
        LocalDateTime time = LocalDateTime.of(2026, 12, 1, 18, 0);
        ForecastResponse forecast = new ForecastResponse(10L, "Vilnius", time, -2.5, -6.0, 4.0, 0.3,
                "light-snow", "Nedidelis sniegas");
        when(weatherService.getForecastForBooking(10L)).thenReturn(forecast);

        // Act
        ResultActions result = mockMvc.perform(get("/bookings/10/forecast"));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.bookingId").value(10));
        result.andExpect(jsonPath("$.place").value("Vilnius"));
        result.andExpect(jsonPath("$.airTemperature").value(-2.5));
        result.andExpect(jsonPath("$.condition").value("Nedidelis sniegas"));
    }

    @Test
    void getForecastShouldReturn404WhenThereIsNoForecast() throws Exception {
        // Arrange
        String message = "Weather forecast for this booking time is not available";
        when(weatherService.getForecastForBooking(10L)).thenThrow(new ResourceNotFoundException(message));

        // Act
        ResultActions result = mockMvc.perform(get("/bookings/10/forecast"));

        // Assert
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value(message));
    }

    @Test
    void getForecastShouldReturn502WhenMeteoIsNotAvailable() throws Exception {
        // Arrange
        String message = "Weather service is not available";
        when(weatherService.getForecastForBooking(10L)).thenThrow(new ExternalServiceException(message));

        // Act
        ResultActions result = mockMvc.perform(get("/bookings/10/forecast"));

        // Assert
        result.andExpect(status().isBadGateway());
        result.andExpect(jsonPath("$.status").value(502));
        result.andExpect(jsonPath("$.message").value(message));
    }
}
