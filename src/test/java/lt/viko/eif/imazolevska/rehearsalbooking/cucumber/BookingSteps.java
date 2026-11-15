package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BandRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.RoomRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Steps for user stories about bookings (US-6, US-7, US-8)
 * and for bookings used in other stories.
 */
public class BookingSteps {

    private final TestContext context;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final BandRepository bandRepository;

    /**
     * Creates the steps. Spring passes the dependencies automatically.
     *
     * @param context           the data shared between steps
     * @param bookingRepository the repository for bookings
     * @param roomRepository    the repository for rooms
     * @param bandRepository    the repository for bands
     */
    public BookingSteps(TestContext context, BookingRepository bookingRepository,
                        RoomRepository roomRepository, BandRepository bandRepository) {
        this.context = context;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.bandRepository = bandRepository;
    }

    // ---------- Given ----------

    @Given("kambaryje {string} yra rezervacija {word} nuo {word} iki {word}")
    public void roomHasBooking(String roomName, String day, String from, String to) {
        Room room = findRoomByName(roomName);
        Band band = findAnyBand();
        LocalDateTime start = toDateTime(day, from);
        LocalDateTime end = toDateTime(day, to);
        BigDecimal price = calculatePrice(room, start, end);
        saveBooking(band, room, start, end, price, false);
    }

    @Given("kambaryje {string} yra rezervacija {word} nuo {word} iki {word}, kurios kaina {bigdecimal}")
    public void roomHasBookingWithPrice(String roomName, String day, String from, String to, BigDecimal price) {
        Room room = findRoomByName(roomName);
        Band band = findAnyBand();
        LocalDateTime start = toDateTime(day, from);
        LocalDateTime end = toDateTime(day, to);
        saveBooking(band, room, start, end, price, false);
    }

    @Given("kambaryje {string} yra atšaukta rezervacija {word} nuo {word} iki {word}")
    public void roomHasCanceledBooking(String roomName, String day, String from, String to) {
        Room room = findRoomByName(roomName);
        Band band = findAnyBand();
        LocalDateTime start = toDateTime(day, from);
        LocalDateTime end = toDateTime(day, to);
        BigDecimal price = calculatePrice(room, start, end);
        saveBooking(band, room, start, end, price, true);
    }

    @Given("kambaryje {string} yra {int} rezervacijos")
    public void roomHasSeveralBookings(String roomName, int count) {
        Room room = findRoomByName(roomName);
        Band band = findAnyBand();
        for (int i = 1; i <= count; i++) {
            LocalDateTime start = LocalDate.now().plusDays(i).atTime(18, 0);
            LocalDateTime end = start.plusHours(2);
            BigDecimal price = calculatePrice(room, start, end);
            saveBooking(band, room, start, end, price, false);
        }
    }

    @Given("grupė {string} turi {int} rezervaciją")
    public void bandHasBookings(String bandName, int count) {
        Band band = findBandByName(bandName);
        Room room = findAnyRoom();
        for (int i = 1; i <= count; i++) {
            LocalDateTime start = LocalDate.now().plusDays(i).atTime(18, 0);
            LocalDateTime end = start.plusHours(2);
            BigDecimal price = calculatePrice(room, start, end);
            saveBooking(band, room, start, end, price, false);
        }
    }

    @Given("grupė {string} turi rezervaciją po {int} dienų nuo {word} iki {word}")
    public void bandHasBookingInDays(String bandName, int days, String from, String to) {
        saveBookingInDays(bandName, days, from, to, false);
    }

    @Given("grupė {string} turi atšauktą rezervaciją po {int} dienų nuo {word} iki {word}")
    public void bandHasCanceledBookingInDays(String bandName, int days, String from, String to) {
        saveBookingInDays(bandName, days, from, to, true);
    }

    @Given("grupė {string} turi rezervaciją, kuri prasideda po {int} valandų ir trunka {int} valandas")
    public void bandHasBookingInHours(String bandName, int hoursUntilStart, int duration) {
        Band band = findBandByName(bandName);
        Room room = findAnyRoom();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.plusHours(hoursUntilStart).withSecond(0).withNano(0);
        LocalDateTime end = start.plusHours(duration);
        BigDecimal price = calculatePrice(room, start, end);
        saveBooking(band, room, start, end, price, false);
    }

    // ---------- When ----------

    @When("grupė {string} rezervuoja kambarį {string} {word} nuo {word} iki {word}")
    public void bandBooksRoom(String bandName, String roomName, String day, String from, String to) {
        Long bandId = context.getBandId(bandName);
        Long roomId = context.getRoomId(roomName);
        LocalDateTime start = toDateTime(day, from);
        LocalDateTime end = toDateTime(day, to);

        String body = "{"
                + "\"bandId\": " + bandId + ", "
                + "\"roomId\": " + roomId + ", "
                + "\"startTime\": \"" + start + "\", "
                + "\"endTime\": \"" + end + "\""
                + "}";

        RequestSpecification request = RestAssured.given();
        request.contentType(ContentType.JSON);
        request.body(body);
        Response response = request.post("/bookings");

        context.setLastResponse(response);
    }

    @When("grupės atstovas peržiūri kambario {string} užimtumą")
    public void representativeViewsRoomSchedule(String roomName) {
        Long roomId = context.getRoomId(roomName);
        Response response = RestAssured.get("/rooms/" + roomId + "/bookings");
        context.setLastResponse(response);
    }

    @When("grupės atstovas peržiūri tą rezervaciją pagal ID")
    public void representativeViewsBooking() {
        Long bookingId = context.getLastBookingId();
        Response response = RestAssured.get("/bookings/" + bookingId);
        context.setLastResponse(response);
    }

    @When("grupės atstovas atšaukia tą rezervaciją")
    public void representativeCancelsBooking() {
        Long bookingId = context.getLastBookingId();
        Response response = RestAssured.post("/bookings/" + bookingId + "/cancel");
        context.setLastResponse(response);
    }

    // ---------- Then ----------

    @Then("rezervacija sukuriama su būsena {string}")
    public void bookingIsCreatedWithStatus(String expectedStatus) {
        Response response = context.getLastResponse();
        assertEquals(201, response.getStatusCode(), "Response was: " + response.asString());

        String actualStatus = response.jsonPath().getString("status");
        assertEquals(expectedStatus, actualStatus);
    }

    @Then("rezervacijos kaina yra {bigdecimal}")
    public void bookingPriceIs(BigDecimal expectedPrice) {
        Response response = context.getLastResponse();
        String priceText = response.jsonPath().getString("price");
        BigDecimal actualPrice = new BigDecimal(priceText);

        // compareTo is used, because equals() thinks 30.0 and 30.00 are different
        assertEquals(0, expectedPrice.compareTo(actualPrice), "Price was " + actualPrice);
    }

    @Then("rezervacija nesukuriama")
    public void bookingIsNotCreated() {
        int status = context.getLastResponse().getStatusCode();
        assertTrue(status >= 400, "Expected an error status, but it was " + status);
    }

    @Then("tos rezervacijos kaina lieka {bigdecimal}")
    public void thatBookingPriceStays(BigDecimal expectedPrice) {
        Booking booking = findBooking(context.getLastBookingId());
        BigDecimal actualPrice = booking.getPrice();
        assertEquals(0, expectedPrice.compareTo(actualPrice), "Price was " + actualPrice);
    }

    @Then("matomos {int} rezervacijos")
    public void bookingsAreShown(int expectedCount) {
        Response response = context.getLastResponse();
        assertEquals(200, response.getStatusCode());

        List<Object> bookings = response.jsonPath().getList("_embedded.bookings");
        assertEquals(expectedCount, bookings.size());
    }

    @Then("atšaukta rezervacija nerodoma")
    public void canceledBookingIsNotShown() {
        Response response = context.getLastResponse();
        List<String> statuses = response.jsonPath().getList("_embedded.bookings.status", String.class);

        for (String status : statuses) {
            assertEquals("ACTIVE", status);
        }
    }

    @Then("matoma grupė {string}, kambarys {string}, būsena {string} ir kaina {bigdecimal}")
    public void bookingDetailsAreShown(String bandName, String roomName, String status, BigDecimal price) {
        Response response = context.getLastResponse();
        assertEquals(200, response.getStatusCode());

        assertEquals(bandName, response.jsonPath().getString("bandName"));
        assertEquals(roomName, response.jsonPath().getString("roomName"));
        assertEquals(status, response.jsonPath().getString("status"));

        BigDecimal actualPrice = new BigDecimal(response.jsonPath().getString("price"));
        assertEquals(0, price.compareTo(actualPrice), "Price was " + actualPrice);
    }

    @Then("rezervacijos būsena yra {string}")
    public void bookingStatusIs(String expectedStatus) {
        checkBookingStatus(expectedStatus);
    }

    @Then("rezervacija neatšaukiama")
    public void bookingIsNotCanceled() {
        int status = context.getLastResponse().getStatusCode();
        assertEquals(409, status);
    }

    @Then("rezervacijos būsena lieka {string}")
    public void bookingStatusStays(String expectedStatus) {
        checkBookingStatus(expectedStatus);
    }

    // ---------- Helper methods ----------

    /**
     * Turns words like "rytoj" and "18:00" into a date and time.
     */
    private LocalDateTime toDateTime(String day, String time) {
        LocalDate today = LocalDate.now();
        LocalDate date = null;

        switch (day) {
            case "vakar":
                date = today.minusDays(1);
                break;
            case "šiandien":
                date = today;
                break;
            case "rytoj":
                date = today.plusDays(1);
                break;
            default:
                fail("Unknown day: " + day);
        }

        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime dateTime = date.atTime(localTime);
        return dateTime;
    }

    /**
     * Saves a booking that starts the given number of days from today.
     */
    private void saveBookingInDays(String bandName, int days, String from, String to, boolean canceled) {
        Band band = findBandByName(bandName);
        Room room = findAnyRoom();
        LocalDate date = LocalDate.now().plusDays(days);
        LocalDateTime start = date.atTime(LocalTime.parse(from));
        LocalDateTime end = date.atTime(LocalTime.parse(to));
        BigDecimal price = calculatePrice(room, start, end);
        saveBooking(band, room, start, end, price, canceled);
    }

    /**
     * Saves a booking straight to the database, without the API rules,
     * and remembers its ID.
     */
    private void saveBooking(Band band, Room room, LocalDateTime start, LocalDateTime end,
                             BigDecimal price, boolean canceled) {
        Booking booking = new Booking(band, room, start, end, price);
        if (canceled) {
            booking.cancel();
        }
        Booking savedBooking = bookingRepository.save(booking);
        context.addGivenBooking(savedBooking.getId());
    }

    /**
     * Calculates the price the same way as the application: hours multiplied by the hourly price.
     */
    private BigDecimal calculatePrice(Room room, LocalDateTime start, LocalDateTime end) {
        long hours = Duration.between(start, end).toHours();
        BigDecimal hoursAsNumber = BigDecimal.valueOf(hours);
        BigDecimal price = room.getHourlyPrice().multiply(hoursAsNumber);
        return price;
    }

    /**
     * Checks the status of the last booking in the database.
     */
    private void checkBookingStatus(String expectedStatus) {
        Booking booking = findBooking(context.getLastBookingId());
        String actualStatus = booking.getStatus().name();
        assertEquals(expectedStatus, actualStatus);
    }

    private Booking findBooking(Long bookingId) {
        Optional<Booking> foundBooking = bookingRepository.findById(bookingId);
        assertTrue(foundBooking.isPresent(), "Booking " + bookingId + " was not found");
        Booking booking = foundBooking.get();
        return booking;
    }

    private Room findRoomByName(String roomName) {
        Long roomId = context.getRoomId(roomName);
        Optional<Room> foundRoom = roomRepository.findById(roomId);
        assertTrue(foundRoom.isPresent(), "Room " + roomName + " was not found");
        Room room = foundRoom.get();
        return room;
    }

    private Band findBandByName(String bandName) {
        Long bandId = context.getBandId(bandName);
        Optional<Band> foundBand = bandRepository.findById(bandId);
        assertTrue(foundBand.isPresent(), "Band " + bandName + " was not found");
        Band band = foundBand.get();
        return band;
    }

    /**
     * Returns any band from the database. If there is none, creates one.
     * Used when a scenario needs a booking but does not care which band made it.
     */
    private Band findAnyBand() {
        List<Band> bands = bandRepository.findAll();
        if (bands.isEmpty()) {
            Band band = new Band("Testinė grupė", "test@example.com", 2);
            Band savedBand = bandRepository.save(band);
            return savedBand;
        }
        Band firstBand = bands.get(0);
        return firstBand;
    }

    /**
     * Returns any room from the database. If there is none, creates one.
     * Used when a scenario needs a booking but does not care which room it is in.
     */
    private Room findAnyRoom() {
        List<Room> rooms = roomRepository.findAll();
        if (rooms.isEmpty()) {
            Room room = new Room("Testinis kambarys", 5, new BigDecimal("15.00"));
            Room savedRoom = roomRepository.save(room);
            return savedRoom;
        }
        Room firstRoom = rooms.get(0);
        return firstRoom;
    }
}
