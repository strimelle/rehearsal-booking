package lt.viko.eif.imazolevska.rehearsalbooking.exception;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.BookingStatus;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BandService;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BookingService;
import lt.viko.eif.imazolevska.rehearsalbooking.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private RoomService roomService;

    @Mock
    private BandService bandService;

    @InjectMocks
    private BookingService bookingService;

    /**
     * Prepares band 1 and room 1. The room "Studija A" has capacity 5
     * and costs 15.00 per hour.
     */
    private void prepareBandAndRoom(int membersCount) {
        Band band = new Band("Vakaro Aidas", "aidas@example.com", membersCount);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        when(bandService.getBandById(1L)).thenReturn(band);
        when(roomService.getRoomById(1L)).thenReturn(room);
    }

    /**
     * Returns tomorrow's date at the given hour, for example tomorrow at 18:00.
     */
    private LocalDateTime tomorrowAt(int hour) {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalDateTime time = tomorrow.atTime(hour, 0);
        return time;
    }

    /**
     * Creates an active booking for the given time.
     */
    private Booking createBooking(LocalDateTime start, LocalDateTime end) {
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        Booking booking = new Booking(band, room, start, end, new BigDecimal("30.00"));
        return booking;
    }

    @Test
    void createBookingShouldSaveBookingWithCalculatedPrice() {
        // band with 4 members, room for 5 people
        prepareBandAndRoom(4);
        LocalDateTime start = tomorrowAt(18);
        LocalDateTime end = tomorrowAt(20);


        bookingService.createBooking(1L, 1L, start, end);


        ArgumentCaptor<Booking> captor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(captor.capture());
        Booking savedBooking = captor.getValue();
        assertEquals(new BigDecimal("30.00"), savedBooking.getPrice());
        assertEquals(BookingStatus.ACTIVE, savedBooking.getStatus());
        assertEquals(start, savedBooking.getStartTime());
    }

    @Test
    void createBookingShouldThrowExceptionWhenEndIsBeforeStart() {
        // from 20:00 to 18:00
        prepareBandAndRoom(4);
        LocalDateTime start = tomorrowAt(20);
        LocalDateTime end = tomorrowAt(18);


        try {
            bookingService.createBooking(1L, 1L, start, end);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException exception) {
            assertEquals("End time must be after start time", exception.getMessage());
        }

        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void createBookingShouldThrowExceptionWhenBookingIsInThePast() {
        // yesterday from 18:00 to 20:00
        prepareBandAndRoom(4);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        LocalDateTime start = yesterday.atTime(18, 0);
        LocalDateTime end = yesterday.atTime(20, 0);


        try {
            bookingService.createBooking(1L, 1L, start, end);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException exception) {
            assertEquals("Booking cannot be made in the past", exception.getMessage());
        }
    }

    @Test
    void createBookingShouldThrowExceptionWhenBookingIsTooLong() {
        // from 18:00 to 22:00, 4 hours
        prepareBandAndRoom(4);
        LocalDateTime start = tomorrowAt(18);
        LocalDateTime end = tomorrowAt(22);


        try {
            bookingService.createBooking(1L, 1L, start, end);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException exception) {
            assertEquals("Booking must last from 1 to 3 hours", exception.getMessage());
        }
    }

    @Test
    void createBookingShouldThrowExceptionWhenDurationIsNotWholeHours() {
        // from 18:00 to 19:30
        prepareBandAndRoom(4);
        LocalDateTime start = tomorrowAt(18);
        LocalDateTime end = tomorrowAt(19).plusMinutes(30);


        try {
            bookingService.createBooking(1L, 1L, start, end);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException exception) {
            assertEquals("Booking must last a whole number of hours", exception.getMessage());
        }
    }

    @Test
    void createBookingShouldThrowExceptionWhenBandIsTooBigForRoom() {
        // band with 12 members, room for 5 people
        prepareBandAndRoom(12);
        LocalDateTime start = tomorrowAt(18);
        LocalDateTime end = tomorrowAt(20);

        try {
            bookingService.createBooking(1L, 1L, start, end);
            fail("Expected BusinessRuleException");
        } catch (BusinessRuleException exception) {
            assertEquals("Band has more members than the room capacity", exception.getMessage());
        }
    }

    @Test
    void createBookingShouldThrowExceptionWhenRoomIsAlreadyBooked() {
        // Arrange: the room is booked from 18:00 to 20:00
        prepareBandAndRoom(4);
        Booking existingBooking = createBooking(tomorrowAt(18), tomorrowAt(20));
        List<Booking> activeBookings = new ArrayList<>();
        activeBookings.add(existingBooking);
        when(bookingRepository.findByRoomIdAndStatusOrderByStartTimeAsc(1L, BookingStatus.ACTIVE))
                .thenReturn(activeBookings);

        // Act and Assert: try to book from 19:00 to 21:00
        try {
            bookingService.createBooking(1L, 1L, tomorrowAt(19), tomorrowAt(21));
            fail("Expected ConflictException");
        } catch (ConflictException exception) {
            assertEquals("Room is already booked at this time", exception.getMessage());
        }
    }

    @Test
    void createBookingShouldAllowBookingRightAfterAnotherBooking() {

        prepareBandAndRoom(4);
        Booking existingBooking = createBooking(tomorrowAt(18), tomorrowAt(20));
        List<Booking> activeBookings = new ArrayList<>();
        activeBookings.add(existingBooking);
        when(bookingRepository.findByRoomIdAndStatusOrderByStartTimeAsc(1L, BookingStatus.ACTIVE))
                .thenReturn(activeBookings);

        bookingService.createBooking(1L, 1L, tomorrowAt(20), tomorrowAt(22));


        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void getBookingByIdShouldThrowExceptionWhenBookingDoesNotExist() {

        when(bookingRepository.findById(99L)).thenReturn(Optional.empty());


        try {
            bookingService.getBookingById(99L);
            fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException exception) {
            assertEquals("Booking with id 99 was not found", exception.getMessage());
        }
    }

    @Test
    void cancelBookingShouldCancelWhenMoreThan24HoursLeft() {

        LocalDateTime start = LocalDateTime.now().plusDays(3);
        Booking booking = createBooking(start, start.plusHours(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        bookingService.cancelBooking(1L);


        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository).save(booking);
    }

    @Test
    void cancelBookingShouldThrowExceptionWhenLessThan24HoursLeft() {

        LocalDateTime start = LocalDateTime.now().plusHours(10);
        Booking booking = createBooking(start, start.plusHours(2));
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));


        try {
            bookingService.cancelBooking(1L);
            fail("Expected ConflictException");
        } catch (ConflictException exception) {
            assertEquals("Booking can be cancelled no later than 24 hours before it starts",
                    exception.getMessage());
        }


        assertEquals(BookingStatus.ACTIVE, booking.getStatus());
    }

    @Test
    void cancelBookingShouldThrowExceptionWhenAlreadyCancelled() {

        LocalDateTime start = LocalDateTime.now().plusDays(3);
        Booking booking = createBooking(start, start.plusHours(2));
        booking.cancel();
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));


        try {
            bookingService.cancelBooking(1L);
            fail("Expected ConflictException");
        } catch (ConflictException exception) {
            assertEquals("Booking is already cancelled", exception.getMessage());
        }
    }
}