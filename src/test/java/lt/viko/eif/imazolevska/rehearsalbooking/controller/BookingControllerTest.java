package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ConflictException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.GlobalExceptionHandler;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BookingResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookingControllerTest {

    @Mock
    private BookingService bookingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BookingResponseMapper bookingResponseMapper = new BookingResponseMapper();
        BookingController controller = new BookingController(bookingService, bookingResponseMapper);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void createBookingShouldReturn201AndActiveBooking() throws Exception {
        // Arrange
        Booking booking = createBooking();
        LocalDateTime start = LocalDateTime.of(2030, 12, 1, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 12, 1, 20, 0);
        when(bookingService.createBooking(2L, 1L, start, end)).thenReturn(booking);

        String json = """
                {
                  "bandId": 2,
                  "roomId": 1,
                  "startTime": "2030-12-01T18:00:00",
                  "endTime": "2030-12-01T20:00:00"
                }
                """;

        // Act
        ResultActions result = mockMvc.perform(post("/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(header().string("Location", "http://localhost/bookings/10"));
        result.andExpect(jsonPath("$.status").value("ACTIVE"));
        result.andExpect(jsonPath("$.price").value(30.0));
    }

    @Test
    void cancelBookingShouldReturnCancelledBooking() throws Exception {
        // Arrange
        Booking booking = createBooking();
        booking.cancel();
        when(bookingService.cancelBooking(10L)).thenReturn(booking);

        // Act
        ResultActions result = mockMvc.perform(post("/bookings/10/cancel"));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.status").value("CANCELLED"));
    }

    @Test
    void cancelBookingShouldReturn409WhenTooLate() throws Exception {
        // Arrange
        String message = "Booking can be cancelled no later than 24 hours before it starts";
        ConflictException exception = new ConflictException(message);
        when(bookingService.cancelBooking(10L)).thenThrow(exception);

        // Act
        ResultActions result = mockMvc.perform(post("/bookings/10/cancel"));

        // Assert
        result.andExpect(status().isConflict());
        result.andExpect(jsonPath("$.message").value(message));
    }

    /**
     * Creates an active booking with ID 10 for band 2 in room 1.
     */
    private Booking createBooking() {
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        ReflectionTestUtils.setField(room, "id", 1L);

        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        ReflectionTestUtils.setField(band, "id", 2L);

        LocalDateTime start = LocalDateTime.of(2030, 12, 1, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 12, 1, 20, 0);
        Booking booking = new Booking(band, room, start, end, new BigDecimal("30.00"));
        ReflectionTestUtils.setField(booking, "id", 10L);
        return booking;
    }
}
