package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BookingResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BookingResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.RoomResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BookingService;
import lt.viko.eif.imazolevska.rehearsalbooking.service.RoomService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST endpoints for rehearsal rooms.
 */
@RestController
@RequestMapping("/rooms")
@Tag(name = "Rooms", description = "Rehearsal rooms and their bookings")
public class RoomController {

    private final RoomService roomService;
    private final RoomResponseMapper roomResponseMapper;
    private final BookingService bookingService;
    private final BookingResponseMapper bookingResponseMapper;

    /**
     * Creates the controller. Spring passes the dependencies automatically.
     *@Tag(name = "Rooms", description = "Rehearsal rooms and their bo
     * @param roomService        the service for rooms
     * @param roomResponseMapper the mapper that creates responses
     */
    public RoomController(RoomService roomService, RoomResponseMapper roomResponseMapper, BookingService bookingService, BookingResponseMapper bookingResponseMapper) {
        this.roomResponseMapper = roomResponseMapper;
        this.roomService = roomService;
        this.bookingService=bookingService;
        this.bookingResponseMapper=bookingResponseMapper;
    }

    /**
     * Returns one room.
     *
     * @param id the room ID from the URL
     * @return the room with status 200
     */
    @GetMapping("/{id}")
    @Operation(summary = "See a room")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id") Long id) {
        Room room = roomService.getRoomById(id);
        RoomResponse response = roomResponseMapper.toResponse(room);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns active bookings of a room, so that bands can see which times are taken.
     *
     * @param id the room ID from the URL
     * @return the list of active bookings with links to itself and to the room
     */
    @GetMapping("/{id}/bookings")
    @Operation(summary = "See all bookings")
    public CollectionModel<BookingResponse> getRoomBookings(@PathVariable("id") Long id) {
        List<Booking> bookings = bookingService.getActiveBookingsForRoom(id);

        List<BookingResponse> responses = new ArrayList<>();
        for (Booking booking : bookings) {
            BookingResponse response = bookingResponseMapper.toResponse(booking);
            responses.add(response);
        }

        Link selfLink = linkTo(methodOn(RoomController.class).getRoomBookings(id)).withSelfRel();
        Link roomLink = linkTo(methodOn(RoomController.class).getRoomById(id)).withRel("room");
        CollectionModel<BookingResponse> collection = CollectionModel.of(responses, selfLink, roomLink);
        return collection;
    }

    /**
     * Returns all rooms.
     *
     * @return the list of rooms with a link to itself
     */
    @GetMapping
    @Operation(summary = "See all rooms")
    public CollectionModel<RoomResponse> getAllRooms() {
        List<Room> rooms = roomService.getAllRooms();

        List<RoomResponse> responses = new ArrayList<>();
        for (Room room : rooms) {
            RoomResponse response = roomResponseMapper.toResponse(room);
            responses.add(response);
        }

        Link selfLink = linkTo(methodOn(RoomController.class).getAllRooms()).withSelfRel();
        CollectionModel<RoomResponse> collection = CollectionModel.of(responses, selfLink);
        return collection;
    }

    /**
     * Creates a new room.
     *
     * @param request the room data from the request body
     * @return the created room with status 201 and a Location header
     */
    @PostMapping
    @Operation(summary = "Create room")
    public ResponseEntity<RoomResponse> createRoom(@Valid @RequestBody RoomRequest request){
        Room room=roomService.createRoom(request.getName(),request.getCapacity(), request.getHourlyPrice());
        RoomResponse response=roomResponseMapper.toResponse(room);

        URI location=response.getRequiredLink(IanaLinkRelations.SELF).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Changes an existing room.
     *
     * @param id      the room ID from the URL
     * @param request the new room data
     * @return the changed room with status 200
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update room")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable("id") Long id, @Valid @RequestBody RoomRequest request){
        Room room=roomService.updateRoom(id, request.getName(), request.getCapacity(), request.getHourlyPrice());
        RoomResponse response = roomResponseMapper.toResponse(room);
        return ResponseEntity.ok(response);

    }

    /**
     * Deletes a room together with its bookings.
     *
     * @param id the room ID from the URL
     * @return an empty response with status 204
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete room")
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
