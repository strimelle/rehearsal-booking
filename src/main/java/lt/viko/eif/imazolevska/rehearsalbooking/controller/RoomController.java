package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import jakarta.validation.Valid;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.RoomResponseMapper;
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
public class RoomController {

    private final RoomService roomService;
    private final RoomResponseMapper roomResponseMapper;

    /**
     * Creates the controller. Spring passes the dependencies automatically.
     *
     * @param roomService        the service for rooms
     * @param roomResponseMapper the mapper that creates responses
     */
    public RoomController(RoomService roomService, RoomResponseMapper roomResponseMapper) {
        this.roomResponseMapper = roomResponseMapper;
        this.roomService = roomService;
    }

    /**
     * Returns one room.
     *
     * @param id the room ID from the URL
     * @return the room with status 200
     */
    @GetMapping("/{id}")
    public ResponseEntity<RoomResponse> getRoomById(@PathVariable("id") Long id) {
        Room room = roomService.getRoomById(id);
        RoomResponse response = roomResponseMapper.toResponse(room);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns all rooms.
     *
     * @return the list of rooms with a link to itself
     */
    @GetMapping
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
    public ResponseEntity<Void> deleteRoom(@PathVariable("id") Long id){
        roomService.deleteRoom(id);
        return ResponseEntity.noContent().build();
    }
}
