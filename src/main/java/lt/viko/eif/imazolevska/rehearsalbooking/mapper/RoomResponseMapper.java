package lt.viko.eif.imazolevska.rehearsalbooking.mapper;


import lt.viko.eif.imazolevska.rehearsalbooking.controller.RoomController;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.RoomResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts Room entities to RoomResponse objects and adds HATEOAS links.
 */
@Component
public class RoomResponseMapper {
    /**
     * Converts a room to a response with links.
     *
     * @param room the room entity
     * @return the response for the API
     */
    public RoomResponse toResponse(Room room){
        RoomResponse response=new RoomResponse(room.getId(), room.getName(), room.getCapacity(), room.getHourlyPrice());

        Link selfLink=linkTo(methodOn(RoomController.class).getRoomById(room.getId())).withSelfRel();
        Link allRoomsLink=linkTo(methodOn(RoomController.class).getAllRooms()).withRel("rooms");

        response.add(selfLink);
        response.add(allRoomsLink);
        return response;
    }



}
