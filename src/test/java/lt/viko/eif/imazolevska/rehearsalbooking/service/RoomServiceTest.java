package lt.viko.eif.imazolevska.rehearsalbooking.service;


import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ConflictException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private RoomService roomService;

    @Test
    void getRoomByIDShouldReturnRoomWhenItExists() {
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        Room result = roomService.getRoomById(1L);

        assertEquals("Studija A", result.getName());
    }

    @Test
    void getRoomByIdShouldThrowExceptionWhenRoomDoesNotExist() {
        when(roomRepository.findById(99L)).thenReturn(Optional.empty());

        try {
            roomService.getRoomById(99L);
            fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException exception) {
            assertEquals("Room with id 99 was not found", exception.getMessage());
        }
    }

    @Test
    void createRoomShouldSaveNewRoom() {
        when(roomRepository.existsByName("Studija A")).thenReturn(false);

        roomService.createRoom("Studija A", 5, new BigDecimal("15.00"));

        ArgumentCaptor<Room> captor = ArgumentCaptor.forClass(Room.class);
        verify(roomRepository).save(captor.capture());
        Room savedRoom = captor.getValue();
        assertEquals("Studija A", savedRoom.getName());
        assertEquals(5, savedRoom.getCapacity());
        assertEquals(new BigDecimal("15.00"), savedRoom.getHourlyPrice());
    }

    @Test
    void createRoomShouldThrowExceptionWhenNameIsTaken() {
        // Arrange: a room with this name already exists
        when(roomRepository.existsByName("Studija A")).thenReturn(true);

        // Act and Assert
        try {
            roomService.createRoom("Studija A", 8, new BigDecimal("20.00"));
            fail("Expected ConflictException");
        } catch (ConflictException exception) {
            assertEquals("Room name is already in use: Studija A", exception.getMessage());
        }

        // The room must not be saved
        verify(roomRepository, never()).save(any(Room.class));
    }

    @Test
    void updateRoomShouldChangePrice() {
        // Arrange: the room exists
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // Act: only the price changes, the name stays the same
        roomService.updateRoom(1L, "Studija A", 5, new BigDecimal("20.00"));

        // Assert
        assertEquals(new BigDecimal("20.00"), room.getHourlyPrice());
        verify(roomRepository).save(room);
    }

    @Test
    void deleteRoomShouldDeleteRoomAndItsBookings() {
        // Arrange: the room exists and has two bookings
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        LocalDateTime start = LocalDateTime.of(2030, 1, 10, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 10, 20, 0);
        Booking firstBooking = new Booking(band, room, start, end, new BigDecimal("30.00"));
        Booking secondBooking = new Booking(band, room, start.plusDays(1), end.plusDays(1), new BigDecimal("30.00"));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(firstBooking);
        bookings.add(secondBooking);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(bookingRepository.findByRoomId(1L)).thenReturn(bookings);

        // Act
        roomService.deleteRoom(1L);

        // Assert: both bookings and the room are deleted
        verify(bookingRepository).delete(firstBooking);
        verify(bookingRepository).delete(secondBooking);
        verify(roomRepository).delete(room);
    }

}
