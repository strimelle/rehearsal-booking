package lt.viko.eif.imazolevska.rehearsalbooking.mapper;

import lt.viko.eif.imazolevska.rehearsalbooking.dto.BookingResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BookingResponseMapperTest {

    private final BookingResponseMapper mapper = new BookingResponseMapper();

    @BeforeEach
    void setUp() {
        // A fake request, so that linkTo() knows the server address
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void toResponseShouldCopyBookingBandAndRoomData() {
        // Arrange
        Booking booking = createBooking();

        // Act
        BookingResponse response = mapper.toResponse(booking);

        // Assert
        Long expectedId = 10L;
        assertEquals(expectedId, response.getId());
        assertEquals("Vakaro Aidas", response.getBandName());
        assertEquals("Studija A", response.getRoomName());
        assertEquals("ACTIVE", response.getStatus());
        assertEquals(new BigDecimal("30.00"), response.getPrice());
    }

    @Test
    void activeBookingShouldHaveCancelAndForecastLinks() {
        // Arrange
        Booking booking = createBooking();

        // Act
        BookingResponse response = mapper.toResponse(booking);

        // Assert
        Link selfLink = response.getRequiredLink("self");
        assertEquals("http://localhost/bookings/10", selfLink.getHref());

        Link bandLink = response.getRequiredLink("band");
        assertEquals("http://localhost/bands/2", bandLink.getHref());

        Link roomLink = response.getRequiredLink("room");
        assertEquals("http://localhost/rooms/1", roomLink.getHref());

        boolean hasCancelLink = response.hasLink("cancel");
        assertTrue(hasCancelLink);

        Link forecastLink = response.getRequiredLink("forecast");
        assertEquals("http://localhost/bookings/10/forecast", forecastLink.getHref());
    }

    @Test
    void cancelledBookingShouldNotHaveCancelAndForecastLinks() {
        // Arrange
        Booking booking = createBooking();
        booking.cancel();

        // Act
        BookingResponse response = mapper.toResponse(booking);

        // Assert
        assertEquals("CANCELLED", response.getStatus());

        boolean hasCancelLink = response.hasLink("cancel");
        assertFalse(hasCancelLink);

        boolean hasForecastLink = response.hasLink("forecast");
        assertFalse(hasForecastLink);
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
