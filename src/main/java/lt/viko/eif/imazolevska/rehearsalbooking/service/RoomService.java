package lt.viko.eif.imazolevska.rehearsalbooking.service;


import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ConflictException;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.RoomRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Business logic for rehearsal rooms.
 */
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final BookingRepository bookingRepository;

    /**
     * Creates the service. Spring passes the repositories automatically.
     *
     * @param roomRepository    the repository for rooms
     * @param bookingRepository the repository for bookings
     */
    public RoomService(RoomRepository roomRepository, BookingRepository bookingRepository) {
        this.roomRepository = roomRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Finds a room by its ID.
     *
     * @param id the room ID
     * @return the found room
     * @throws ResourceNotFoundException if the room does not exist
     */
    public Room getRoomById(Long id) {
        Optional<Room> foundRoom = roomRepository.findById(id);

        if(foundRoom.isEmpty()){
            throw new ResourceNotFoundException("Room with id " + id + " was not found");
        }
        Room room = foundRoom.get();
        return room;
    }

    /**
     * Returns all rooms.
     *
     * @return the list of all rooms
     */
    public List<Room> getAllRooms(){
        List<Room> rooms = roomRepository.findAll();
        return rooms;
    }

    /**
     * Creates and saves a new room.
     *
     * @param name        the room name, must be unique
     * @param capacity    how many people fit in the room
     * @param hourlyPrice the price of one hour
     * @return the saved room with its ID
     * @throws ConflictException if the room name is already in use
     */
    public Room createRoom(String name, int capacity, BigDecimal hourlyPrice){

        boolean nameTaken = roomRepository.existsByName(name);
        if(nameTaken){
            throw new ConflictException("Room name is already in use: " + name);
        }
        Room room = new Room(name, capacity, hourlyPrice);
        Room savedRoom =roomRepository.save(room);
        return  savedRoom;
    }

    /**
     * Changes the data of an existing room.
     *
     * @param id          the room ID
     * @param name        the new room name
     * @param capacity    the new capacity
     * @param hourlyPrice the new price of one hour
     * @return the saved room
     * @throws ResourceNotFoundException if the room does not exist
     * @throws ConflictException         if the new name is used by another room
     */
    public Room updateRoom(Long id, String name, int capacity, BigDecimal hourlyPrice) {
        Room room = getRoomById(id);

        boolean sameName = room.getName().equals(name);
        if (!sameName) {
            boolean nameTaken = roomRepository.existsByName(name);
            if(nameTaken){
                throw new ConflictException("Room name is already in use: " + name);
            }
        }
        room.setName(name);
        room.setCapacity(capacity);
        room.setHourlyPrice(hourlyPrice);

        Room savedRoom = roomRepository.save(room);
        return savedRoom;
    }

    /**
     * Deletes a room together with all its bookings.
     *
     * @param id the room ID
     * @throws ResourceNotFoundException if the room does not exist
     */
    @Transactional
    public void deleteRoom(Long id) {
        Room room = getRoomById(id);

        List<Booking> bookings = bookingRepository.findByRoomId(id);
        for(Booking booking : bookings){
            bookingRepository.delete(booking);
        }

        roomRepository.delete(room);
    }




}
