package lt.viko.eif.imazolevska.rehearsalbooking.mapper;

import lt.viko.eif.imazolevska.rehearsalbooking.controller.BandController;
import lt.viko.eif.imazolevska.rehearsalbooking.controller.BookingController;
import lt.viko.eif.imazolevska.rehearsalbooking.controller.RoomController;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BookingResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.BookingStatus;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts Booking entities to BookingResponse objects and adds HATEOAS links.
 * The "cancel" link is added only to active bookings.
 */
@Component
public class BookingResponseMapper {

    /**
     * Converts a booking to a response with links.
     *
     * @param booking the booking entity
     * @return the response for the API
     */
    public BookingResponse toResponse(Booking booking) {
        Band band = booking.getBand();
        Room room = booking.getRoom();
        BookingStatus bookingStatus = booking.getStatus();
        String status = bookingStatus.name();

        BookingResponse response = new BookingResponse(
                booking.getId(),
                band.getId(),
                band.getName(),
                room.getId(),
                room.getName(),
                booking.getStartTime(),
                booking.getEndTime(),
                status,
                booking.getPrice()
        );

        Link selfLink = linkTo(methodOn(BookingController.class).getBookingById(booking.getId())).withSelfRel();
        Link allBookingsLink = linkTo(methodOn(BookingController.class).getAllBookings()).withRel("bookings");
        Link bandLink = linkTo(methodOn(BandController.class).getBandById(band.getId())).withRel("band");
        Link roomLink = linkTo(methodOn(RoomController.class).getRoomById(room.getId())).withRel("room");

        response.add(selfLink);
        response.add(allBookingsLink);
        response.add(bandLink);
        response.add(roomLink);

        // Only an active booking can be cancelled, so only it gets the link
        boolean active = booking.isActive();
        if (active) {
            Link cancelLink = linkTo(methodOn(BookingController.class).cancelBooking(booking.getId()))
                    .withRel("cancel");
            response.add(cancelLink);
        }

        return response;
    }
}