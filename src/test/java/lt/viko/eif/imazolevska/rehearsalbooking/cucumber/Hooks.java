package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.Before;
import io.restassured.RestAssured;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BandRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Value;

/**
 * Code that runs before every scenario.
 */
public class Hooks {

    private final int port;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BandRepository bandRepository;

    /**
     * Creates the hooks. Spring passes the port and the repositories automatically.
     *
     * @param port              the random port the application runs on
     * @param bookingRepository the repository for bookings
     * @param roomRepository    the repository for rooms
     * @param bandRepository    the repository for bands
     */
    public Hooks(@Value("${local.server.port}") int port, BookingRepository bookingRepository,
                 RoomRepository roomRepository, BandRepository bandRepository) {
        this.port = port;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bandRepository = bandRepository;
    }

    /**
     * Points REST Assured to the running application and empties the database,
     * so that every scenario starts from a clean state.
     */
    @Before
    public void prepareScenario() {
        RestAssured.port = port;

        // Bookings first, because they refer to rooms and bands
        bookingRepository.deleteAll();
        roomRepository.deleteAll();
        bandRepository.deleteAll();
    }
}
