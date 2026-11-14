package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Data that the client sends to create a booking.
 */
public class BookingRequest {

    @NotNull(message = "Band ID is required")
    private Long bandId;

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Start time is required")
    private LocalDateTime startTime;

    @NotNull(message = "End time is required")
    private LocalDateTime endTime;

    /**
     * Empty constructor needed to create the object from JSON.
     */
    public BookingRequest() {
    }

    public Long getBandId() {
        return bandId;
    }

    public void setBandId(Long bandId) {
        this.bandId = bandId;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }
}