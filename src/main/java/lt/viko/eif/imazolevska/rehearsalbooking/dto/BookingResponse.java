package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Booking data that the API returns, together with HATEOAS links.
 */
@Relation(collectionRelation = "bookings", itemRelation = "booking")
public class BookingResponse extends RepresentationModel<BookingResponse> {

    private final Long id;
    private final Long bandId;
    private final String bandName;
    private final Long roomId;
    private final String roomName;
    private final LocalDateTime startTime;
    private final LocalDateTime endTime;
    private final String status;
    private final BigDecimal price;

    /**
     * Creates the response.
     *
     * @param id        the booking ID
     * @param bandId    the ID of the band
     * @param bandName  the name of the band
     * @param roomId    the ID of the room
     * @param roomName  the name of the room
     * @param startTime the start of the rehearsal
     * @param endTime   the end of the rehearsal
     * @param status    the booking status, ACTIVE or CANCELLED
     * @param price     the total price of the booking
     */
    public BookingResponse(Long id, Long bandId, String bandName, Long roomId, String roomName,
                           LocalDateTime startTime, LocalDateTime endTime, String status, BigDecimal price) {
        this.id = id;
        this.bandId = bandId;
        this.bandName = bandName;
        this.roomId = roomId;
        this.roomName = roomName;
        this.startTime = startTime;
        this.endTime = endTime;
        this.status = status;
        this.price = price;
    }

    public Long getId() {
        return id;
    }

    public Long getBandId() {
        return bandId;
    }

    public String getBandName() {
        return bandName;
    }

    public Long getRoomId() {
        return roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getPrice() {
        return price;
    }
}