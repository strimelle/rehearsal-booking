package lt.viko.eif.imazolevska.rehearsalbooking.mapper;

import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomResponse;
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

import static org.junit.jupiter.api.Assertions.assertEquals;

class RoomResponseMapperTest {

    private final RoomResponseMapper mapper = new RoomResponseMapper();

    @BeforeEach
    void setUp() {
        // linkTo() needs the current request to know the server address.
        // A unit test has no real request, so we create a fake one.
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        // Remove the fake request, so it does not affect other tests
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void toResponseShouldCopyAllFields() {
        // Arrange
        Room room = createRoom(1L);

        // Act
        RoomResponse response = mapper.toResponse(room);

        // Assert
        Long expectedId = 1L;
        assertEquals(expectedId, response.getId());
        assertEquals("Studija A", response.getName());
        assertEquals(5, response.getCapacity());
        assertEquals(new BigDecimal("15.00"), response.getHourlyPrice());
    }

    @Test
    void toResponseShouldAddLinks() {
        // Arrange
        Room room = createRoom(1L);

        // Act
        RoomResponse response = mapper.toResponse(room);

        // Assert: the fake request uses the address http://localhost
        Link selfLink = response.getRequiredLink("self");
        assertEquals("http://localhost/rooms/1", selfLink.getHref());

        Link allRoomsLink = response.getRequiredLink("rooms");
        assertEquals("http://localhost/rooms", allRoomsLink.getHref());

        Link bookingsLink = response.getRequiredLink("bookings");
        assertEquals("http://localhost/rooms/1/bookings", bookingsLink.getHref());
    }

    /**
     * Creates a room with the given ID.
     * Room has no setId(), because the ID is set by the database,
     * so the test writes the private field directly.
     */
    private Room createRoom(Long id) {
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }
}
