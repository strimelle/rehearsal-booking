package lt.viko.eif.imazolevska.rehearsalbooking.entity;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.BookingStatus;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void newBookingShouldBeActive() {
        // Arrange: prepare a band, a room and the time
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        LocalDateTime start = LocalDateTime.of(2030, 1, 10, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 10, 20, 0);
        BigDecimal price = new BigDecimal("30.00");


        Booking booking = new Booking(band, room, start, end, price);


        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
        assertTrue(booking.isActive());
    }

    @Test
    void cancelShouldChangeStatusToCancelled() {
        // Arrange
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        LocalDateTime start = LocalDateTime.of(2030, 1, 10, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 10, 20, 0);
        BigDecimal price = new BigDecimal("30.00");
        Booking booking = new Booking(band, room, start, end, price);

        // Act
        booking.cancel();

        // Assert
        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        assertFalse(booking.isActive());
    }
}