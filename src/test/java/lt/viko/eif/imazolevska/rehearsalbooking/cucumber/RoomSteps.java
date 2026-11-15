package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.RoomRepository;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for user stories about rooms (US-1, US-2, US-3).
 */
public class RoomSteps {

    private static final long MISSING_ID = 999999L;

    private final TestContext context;
    private final RoomRepository roomRepository;
    private Long createdRoomId;

    /**
     * Creates the steps. Spring passes the dependencies automatically.
     *
     * @param context        the data shared between steps
     * @param roomRepository the repository for rooms
     */
    public RoomSteps(TestContext context, RoomRepository roomRepository) {
        this.context = context;
        this.roomRepository = roomRepository;
    }

    // ---------- Given ----------

    @Given("sistemoje nėra kambario pavadinimu {string}")
    public void thereIsNoRoomNamed(String name) {
        boolean exists = roomRepository.existsByName(name);
        assertFalse(exists, "Room " + name + " should not exist");
    }

    @Given("sistemoje yra kambarys {string}")
    public void thereIsRoom(String name) {
        saveRoom(name, 5, new BigDecimal("15.00"));
    }

    @Given("sistemoje yra kambarys {string}, kurio talpa {int} ir valandos kaina {bigdecimal}")
    public void thereIsRoomWithCapacityAndPrice(String name, int capacity, BigDecimal hourlyPrice) {
        saveRoom(name, capacity, hourlyPrice);
    }

    @Given("sistemoje yra kambarys {string} be rezervacijų")
    public void thereIsRoomWithoutBookings(String name) {
        saveRoom(name, 5, new BigDecimal("15.00"));
    }

    // ---------- When ----------

    @When("administratorius registruoja kambarį {string}, kurio talpa {int} ir valandos kaina {bigdecimal}")
    public void adminRegistersRoom(String name, int capacity, BigDecimal hourlyPrice) {
        String body = createRoomJson(name, capacity, hourlyPrice);

        RequestSpecification request = RestAssured.given();
        request.contentType(ContentType.JSON);
        request.body(body);
        Response response = request.post("/rooms");

        context.setLastResponse(response);
    }

    @When("ieškomas kambarys, kurio ID neegzistuoja")
    public void missingRoomIsRequested() {
        Response response = RestAssured.get("/rooms/" + MISSING_ID);
        context.setLastResponse(response);
    }

    @When("administratorius pakeičia kambario {string} valandos kainą į {bigdecimal}")
    public void adminChangesRoomPrice(String name, BigDecimal newPrice) {
        Long roomId = context.getRoomId(name);
        Room room = findRoom(roomId);
        String body = createRoomJson(room.getName(), room.getCapacity(), newPrice);

        RequestSpecification request = RestAssured.given();
        request.contentType(ContentType.JSON);
        request.body(body);
        Response response = request.put("/rooms/" + roomId);

        context.setLastResponse(response);
    }

    @When("administratorius pašalina kambarį {string}")
    public void adminDeletesRoom(String name) {
        Long roomId = context.getRoomId(name);
        Response response = RestAssured.delete("/rooms/" + roomId);
        context.setLastResponse(response);
    }

    @When("administratorius šalina kambarį, kurio ID neegzistuoja")
    public void adminDeletesMissingRoom() {
        Response response = RestAssured.delete("/rooms/" + MISSING_ID);
        context.setLastResponse(response);
    }

    // ---------- Then ----------

    @Then("kambarys sukuriamas ir jam suteikiamas ID")
    public void roomIsCreatedWithId() {
        Response response = context.getLastResponse();
        assertEquals(201, response.getStatusCode());

        createdRoomId = response.jsonPath().getLong("id");
        assertNotNull(createdRoomId);
    }

    @Then("kambarį galima rasti pagal šį ID")
    public void roomCanBeFoundById() {
        Response response = RestAssured.get("/rooms/" + createdRoomId);
        assertEquals(200, response.getStatusCode());
    }

    @Then("kambarys nesukuriamas")
    public void roomIsNotCreated() {
        int status = context.getLastResponse().getStatusCode();
        assertTrue(status >= 400, "Expected an error status, but it was " + status);
    }

    @Then("kambario {string} valandos kaina yra {bigdecimal}")
    public void roomPriceIs(String name, BigDecimal expectedPrice) {
        Long roomId = context.getRoomId(name);
        Room room = findRoom(roomId);
        BigDecimal actualPrice = room.getHourlyPrice();

        // compareTo is used, because equals() thinks 20.0 and 20.00 are different
        assertEquals(0, expectedPrice.compareTo(actualPrice), "Price was " + actualPrice);
    }

    @Then("kambario duomenys nepakeičiami")
    public void roomDataIsNotChanged() {
        int status = context.getLastResponse().getStatusCode();
        assertEquals(400, status);
    }

    @Then("kambario {string} sistemoje nebėra")
    public void roomNoLongerExists(String name) {
        int status = context.getLastResponse().getStatusCode();
        assertEquals(204, status);

        Long roomId = context.getRoomId(name);
        boolean exists = roomRepository.existsById(roomId);
        assertFalse(exists, "Room " + name + " still exists");
    }

    // ---------- Helper methods ----------

    /**
     * Saves a room straight to the database and remembers its ID.
     */
    private void saveRoom(String name, int capacity, BigDecimal hourlyPrice) {
        Room room = new Room(name, capacity, hourlyPrice);
        Room savedRoom = roomRepository.save(room);
        context.putRoomId(name, savedRoom.getId());
    }

    /**
     * Reads a room from the database.
     */
    private Room findRoom(Long roomId) {
        Optional<Room> foundRoom = roomRepository.findById(roomId);
        assertTrue(foundRoom.isPresent(), "Room " + roomId + " was not found");
        Room room = foundRoom.get();
        return room;
    }

    /**
     * Creates the JSON body for creating or changing a room.
     */
    private String createRoomJson(String name, int capacity, BigDecimal hourlyPrice) {
        String json = "{"
                + "\"name\": \"" + name + "\", "
                + "\"capacity\": " + capacity + ", "
                + "\"hourlyPrice\": " + hourlyPrice
                + "}";
        return json;
    }
}
