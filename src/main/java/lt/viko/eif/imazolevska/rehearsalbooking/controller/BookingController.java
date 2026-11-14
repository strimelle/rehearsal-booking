package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BookingRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BookingResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BookingResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BookingService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST endpoints for bookings.
 */
@RestController
@RequestMapping("/bookings")
@Tag(name = "Bookings", description = "Room bookings")
public class BookingController {

    private final BookingService bookingService;
    private final BookingResponseMapper bookingResponseMapper;

    /**
     * Creates the controller. Spring passes the dependencies automatically.
     *
     * @param bookingService        the service for bookings
     * @param bookingResponseMapper the mapper that creates responses
     */
    public BookingController(BookingService bookingService, BookingResponseMapper bookingResponseMapper) {
        this.bookingService = bookingService;
        this.bookingResponseMapper = bookingResponseMapper;
    }

    /**
     * Returns one booking.
     *
     * @param id the booking ID from the URL
     * @return the booking with status 200
     */
    @GetMapping("/{id}")
    @Operation(summary = "See a booking")
    public ResponseEntity<BookingResponse> getBookingById(@PathVariable("id") Long id) {
        Booking booking = bookingService.getBookingById(id);
        BookingResponse response = bookingResponseMapper.toResponse(booking);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns all bookings.
     *
     * @return the list of bookings with a link to itself
     */
    @GetMapping
    @Operation(summary = "See all bookings")
    public CollectionModel<BookingResponse> getAllBookings() {
        List<Booking> bookings = bookingService.getAllBookings();

        List<BookingResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse response = bookingResponseMapper.toResponse(booking);
            responses.add(response);
        }

        Link selfLink = linkTo(methodOn(BookingController.class).getAllBookings()).withSelfRel();
        CollectionModel<BookingResponse> collection = CollectionModel.of(responses, selfLink);
        return collection;
    }

    /**
     * Creates a new booking after checking all booking rules.
     *
     * @param request the booking data from the request body
     * @return the created booking with status 201 and a Location header
     */
    @PostMapping
    @Operation(summary = "Book a room")
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        Booking booking = bookingService.createBooking(request.getBandId(), request.getRoomId(),
                request.getStartTime(), request.getEndTime());
        BookingResponse response = bookingResponseMapper.toResponse(booking);

        URI location = response.getRequiredLink(IanaLinkRelations.SELF).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Cancels a booking no later than 24 hours before it starts.
     *
     * @param id the booking ID from the URL
     * @return the cancelled booking with status 200
     */
    @PostMapping("/{id}/cancel")
    @Operation(summary = "Cancel booking")
    public ResponseEntity<BookingResponse> cancelBooking(@PathVariable("id") Long id) {
        Booking booking = bookingService.cancelBooking(id);
        BookingResponse response = bookingResponseMapper.toResponse(booking);
        return ResponseEntity.ok(response);
    }
}