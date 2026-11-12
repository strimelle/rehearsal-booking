package lt.viko.eif.imazolevska.rehearsalbooking.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * A rehearsal room that bands can book.
 * Mapped to the "rooms" database table.
 */
@Entity
@Table(name="rooms")
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="name")
    private String name;

    @Column(name="capacity")
    private int capacity;

    @Column(name="hourly_price")
    private BigDecimal hourlyPrice;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(BigDecimal hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }

    /**
     * Empty constructor required by JPA.
     * Hibernate uses it to create the object when reading data from the database.
     */
    public Room() {

    }

    /**
     * Creates a new room. The ID is assigned by the database when the room is saved.
     *
     * @param name        the room name
     * @param capacity    how many people fit in the room
     * @param hourlyPrice the price of one hour
     */
    public Room(String name, int capacity, BigDecimal hourlyPrice) {
        this.name = name;
        this.capacity =capacity;
        this.hourlyPrice = hourlyPrice;

    }
}
