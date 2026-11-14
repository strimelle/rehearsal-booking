package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import java.math.BigDecimal;

/**
 * Room data that the API returns, together with HATEOAS links.
 */
@Relation(collectionRelation="rooms", itemRelation="room")
public class RoomResponse extends RepresentationModel<RoomResponse> {
    private final Long id;
    private final String name;
    private final int capacity;
    private final BigDecimal hourlyPrice;

    /**
     * Creates the response.
     *
     * @param id          the room ID
     * @param name        the room name
     * @param capacity    how many people fit in the room
     * @param hourlyPrice the price of one hour
     */
    public RoomResponse(Long id, String name, int capacity, BigDecimal hourlyPrice){
        this.id=id;
        this.name=name;
        this.capacity=capacity;
        this.hourlyPrice=hourlyPrice;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getCapacity() {
        return capacity;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }
}
