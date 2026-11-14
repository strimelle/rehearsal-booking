package lt.viko.eif.imazolevska.rehearsalbooking.service;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.BookingStatus;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.BusinessRuleException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ConflictException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.awt.print.Book;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for bookings. All booking rules are checked here.
 */
@Service
public class BookingService {

    private static final int MIN_HOURS = 1;
    private static final int MAX_HOURS = 3;
    private static final int CANCEL_LIMIT_HOURS = 24;

    private final BookingRepository bookingRepository;
    private final RoomService roomService;
    private final BandService bandService;

    /**
     * Returns all bookings.
     *
     * @return the list of all bookings
     */
    public List<Booking> getAllBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        return bookings;
    }

    /**
     * Finds a booking by its ID.
     *
     * @param id the booking ID
     * @return the found booking
     * @throws ResourceNotFoundException if the booking does not exist
     */
    public Booking getBookingById(Long id) {
        Optional<Booking> foundBooking = bookingRepository.findById(id);
        if (foundBooking.isEmpty()) {
            throw new ResourceNotFoundException("Booking with id " + id + " was not found");
        }
        Booking booking = foundBooking.get();
        return booking;
    }

    /**
     * Returns active bookings of a room sorted by start time,
     * so that bands can see which times are taken.
     *
     * @param roomId the room ID
     * @return the list of active bookings
     * @throws ResourceNotFoundException if the room does not exist
     */
    public List<Booking> getActiveBookingsForRoom(Long roomId) {
        roomService.getRoomById(roomId);

        List<Booking> bookings =
                bookingRepository.findByRoomIdAndStatusOrderByStartTimeAsc(roomId, BookingStatus.ACTIVE);
        return bookings;
    }


    /**
     * Creates the service. Spring passes the dependencies automatically.
     *
     * @param bookingRepository the repository for bookings
     * @param roomService       the service for rooms
     * @param bandService       the service for bands
     */
    public BookingService(BookingRepository bookingRepository, RoomService roomService, BandService bandService) {
        this.bookingRepository =bookingRepository;
        this.roomService = roomService;
        this.bandService = bandService;
    }

    /**
     * Creates a new booking after checking all booking rules.
     *
     * @param bandId    the ID of the band that makes the booking
     * @param roomId    the ID of the booked room
     * @param startTime the start of the rehearsal
     * @param endTime   the end of the rehearsal
     * @return the saved booking
     */

    public Booking createBooking(Long bandId, Long roomId, LocalDateTime startTime, LocalDateTime endTime){
        Band band = bandService.getBandById(bandId);
        Room room = roomService.getRoomById(roomId);

        checkTimes(startTime, endTime);
        checkCapacity(band, room);
        checkRoomIsFree(roomId, startTime, endTime);

        BigDecimal price = calculatePrice(room, startTime, endTime);
        Booking booking = new Booking(band, room, startTime, endTime, price);
        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking;

    }

    /**
     * Calculates the price
     */
    private BigDecimal calculatePrice(Room room, LocalDateTime startTime, LocalDateTime endTime){
        Duration duration = Duration.between(startTime, endTime);
        long hours = duration.toHours();
        BigDecimal hoursAsNumber = BigDecimal.valueOf(hours);
        BigDecimal price = room.getHourlyPrice().multiply(hoursAsNumber);
        return price;
    }

    /**
     * Checks the booking times
     */
    private void checkTimes(LocalDateTime startTime, LocalDateTime endTime){
        boolean endIsAfterStart = endTime.isAfter(startTime);
        if(!endIsAfterStart){
            throw new BusinessRuleException("End time must be after start time");
        }

        LocalDateTime now =LocalDateTime.now();
        boolean startIsInPast = startTime.isBefore(now);
        if(startIsInPast){
            throw new BusinessRuleException("Booking cannot be made in the past");
        }

        Duration duration = Duration.between(startTime, endTime);
        long minutes = duration.toMinutes();
        long minutesOverFullHour = minutes % 60;
        if(minutesOverFullHour != 0){
            throw new BusinessRuleException("Booking must last a whole number of hours");
        }

        long hours = minutes / 60;
        if(hours < MIN_HOURS || hours > MAX_HOURS) {
            throw new BusinessRuleException("Booking must last from 1 to 3 hours");
        }
    }

    private void checkCapacity(Band band, Room room){
        int membersCount = band.getMembersCount();
        int capacity = room.getCapacity();
        if (membersCount > capacity){
            throw new BusinessRuleException("Band has more members than the room capacity");
        }
    }

    private void checkRoomIsFree(Long roomId, LocalDateTime startTime, LocalDateTime endTime) {
        List<Booking> activeBookings =bookingRepository.findByRoomIdAndStatusOrderByStartTimeAsc(roomId, BookingStatus.ACTIVE);

        for (Booking otherBooking : activeBookings) {
            boolean startsBeforeOtherEnds = startTime.isBefore(otherBooking.getEndTime());
            boolean endsAfterOtherStarts = endTime.isAfter(otherBooking.getStartTime());
            if(startsBeforeOtherEnds && endsAfterOtherStarts){
                throw new ConflictException("Room is already booked at this time");
            }
        }
    }

    /**
     * Cancels a booking. Cancelling is allowed no later than
     * 24 hours before the booking starts (rule 6).
     *
     * @param id the booking ID
     * @return the canceled booking
     * @throws ResourceNotFoundException if the booking does not exist
     * @throws ConflictException         if the booking is already canceled or it is too late
     */
    public Booking cancelBooking(Long id) {
        Booking booking = getBookingById(id);

        boolean active = booking.isActive();
        if (!active) {
            throw new ConflictException("Booking is already canceled");
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cancelDeadline = booking.getStartTime().minusHours(CANCEL_LIMIT_HOURS);
        boolean tooLate = now.isAfter(cancelDeadline);
        if (tooLate) {
            throw new ConflictException("Booking can be cancelled no later than 24 hours before it starts");
        }

        booking.cancel();
        Booking savedBooking = bookingRepository.save(booking);
        return savedBooking;
    }


}
