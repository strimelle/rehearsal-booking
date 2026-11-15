package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.spring.ScenarioScope;
import io.restassured.response.Response;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Data shared between step definition classes during one scenario.
 * A new object is created for every scenario.
 */
@Component
@ScenarioScope
public class TestContext {

    private Response lastResponse;
    private Long lastBookingId;
    private final Map<String, Long> roomIds = new HashMap<>();
    private final Map<String, Long> bandIds = new HashMap<>();
    private final List<Long> givenBookingIds = new ArrayList<>();

    /**
     * Returns the response of the last HTTP request.
     *
     * @return the last response
     */
    public Response getLastResponse() {
        return lastResponse;
    }

    /**
     * Remembers the response of the last HTTP request.
     *
     * @param lastResponse the response to remember
     */
    public void setLastResponse(Response lastResponse) {
        this.lastResponse = lastResponse;
    }

    /**
     * Returns the ID of the booking created in the last Given step.
     *
     * @return the booking ID
     */
    public Long getLastBookingId() {
        return lastBookingId;
    }

    /**
     * Remembers the ID of a booking created in a Given step.
     *
     * @param bookingId the booking ID
     */
    public void addGivenBooking(Long bookingId) {
        this.lastBookingId = bookingId;
        givenBookingIds.add(bookingId);
    }

    /**
     * Returns the IDs of all bookings created in Given steps.
     *
     * @return the list of booking IDs
     */
    public List<Long> getGivenBookingIds() {
        return givenBookingIds;
    }

    /**
     * Remembers the ID of a room by its name.
     *
     * @param name the room name
     * @param id   the room ID
     */
    public void putRoomId(String name, Long id) {
        roomIds.put(name, id);
    }

    /**
     * Returns the ID of a room by its name.
     *
     * @param name the room name
     * @return the room ID
     */
    public Long getRoomId(String name) {
        Long id = roomIds.get(name);
        return id;
    }

    /**
     * Remembers the ID of a band by its name.
     *
     * @param name the band name
     * @param id   the band ID
     */
    public void putBandId(String name, Long id) {
        bandIds.put(name, id);
    }

    /**
     * Returns the ID of a band by its name.
     *
     * @param name the band name
     * @return the band ID
     */
    public Long getBandId(String name) {
        Long id = bandIds.get(name);
        return id;
    }
}
