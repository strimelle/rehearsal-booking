package lt.viko.eif.imazolevska.rehearsalbooking.entity;


import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * A booking of a room made by a band for a specific time.
 * Mapped to the "bookings" database table.
 */
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name="start_time")
    private LocalDateTime startTime;

    @Column(name="end_time")
    private LocalDateTime endTime;

    @Column(name="price")
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name="band_id")
    private Band band;

    @ManyToOne
    @JoinColumn(name="room_id")
    private Room room;

    @Enumerated(EnumType.STRING)
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name="status")
    private BookingStatus status;

    /**
     * Empty constructor required by JPA.
     */
    protected Booking() {
    }

    /**
     * Creates a new active booking.
     * The price is calculated in the service layer and passed in ready.
     *
     * @param band      the band that makes the booking
     * @param room      the booked room
     * @param startTime the start of the rehearsal
     * @param endTime   the end of the rehearsal
     * @param price     the total price of the booking
     */
    public Booking(LocalDateTime startTime, LocalDateTime endTime, BigDecimal price, BookingStatus status, Band band, Room room) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.price = price;
        this.status = status;
        this.band = band;
        this.room = room;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Band getBand() {
        return band;
    }

    public Room getRoom() {
        return room;
    }

    public BookingStatus getStatus() {
        return status;
    }

    /**
     * Marks the booking as canceled.
     * Whether cancelling is allowed (the 24-hour rule) is checked by the service layer.
     */
    public void cancel(){
        this.status = BookingStatus.CANCELLED;
    }


    /**
     * Checks whether the booking is active.
     *
     * @return true if the booking is active, false if it is canceled
     */
    public boolean isActive(){
        if (status == BookingStatus.ACTIVE){
            return true;
        }
        return false;
    }

}
