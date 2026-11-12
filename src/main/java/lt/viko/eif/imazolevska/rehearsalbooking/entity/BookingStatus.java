package lt.viko.eif.imazolevska.rehearsalbooking.entity;


/**
 * The status of a booking.
 */
public enum BookingStatus {
    /** The booking is valid and occupies the room time. */
    ACTIVE,

    /** The booking is canceled and no longer occupies the room time. */
    CANCELLED
}
