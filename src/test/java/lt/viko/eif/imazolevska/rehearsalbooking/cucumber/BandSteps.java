package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BandRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps for user stories about bands (US-4, US-5).
 */
public class BandSteps {

    private static final long MISSING_ID = 999999L;
    private static final String DEFAULT_EMAIL = "band@example.com";

    private final TestContext context;
    private final BandRepository bandRepository;
    private Long createdBandId;

    /**
     * Creates the steps. Spring passes the dependencies automatically.
     *
     * @param context        the data shared between steps
     * @param bandRepository the repository for bands
     */
    public BandSteps(TestContext context, BandRepository bandRepository) {
        this.context = context;
        this.bandRepository = bandRepository;
    }

    // ---------- Given ----------

    @Given("sistemoje yra grupė {string} su {int} nariais")
    public void thereIsBandWithMembers(String name, int membersCount) {
        saveBand(name, membersCount);
    }

    @Given("sistemoje yra grupė {string} su {int} narių")
    public void thereIsBandWithManyMembers(String name, int membersCount) {
        saveBand(name, membersCount);
    }

    @Given("sistemoje yra grupė {string}")
    public void thereIsBand(String name) {
        saveBand(name, 4);
    }

    // ---------- When ----------

    @When("grupės atstovas registruoja grupę {string} su el. paštu {string} ir {int} nariais")
    public void representativeRegistersBand(String name, String email, int membersCount) {
        sendCreateBandRequest(name, email, membersCount);
    }

    @When("grupės atstovas registruoja grupę {string} su el. paštu {string} ir {int} narių")
    public void representativeRegistersBandWithManyMembers(String name, String email, int membersCount) {
        sendCreateBandRequest(name, email, membersCount);
    }

    @When("ieškoma grupė, kurios ID neegzistuoja")
    public void missingBandIsRequested() {
        Response response = RestAssured.get("/bands/" + MISSING_ID);
        context.setLastResponse(response);
    }

    @When("grupės atstovas pakeičia grupės {string} narių skaičių į {int}")
    public void representativeChangesMembersCount(String name, int newMembersCount) {
        Long bandId = context.getBandId(name);
        Band band = findBand(bandId);
        String body = createBandJson(band.getName(), band.getContactEmail(), newMembersCount);

        RequestSpecification request = RestAssured.given();
        request.contentType(ContentType.JSON);
        request.body(body);
        Response response = request.put("/bands/" + bandId);

        context.setLastResponse(response);
    }

    @When("grupės atstovas pašalina grupę {string}")
    public void representativeDeletesBand(String name) {
        Long bandId = context.getBandId(name);
        Response response = RestAssured.delete("/bands/" + bandId);
        context.setLastResponse(response);
    }

    // ---------- Then ----------

    @Then("grupė sukuriama ir jai suteikiamas ID")
    public void bandIsCreatedWithId() {
        Response response = context.getLastResponse();
        assertEquals(201, response.getStatusCode());

        createdBandId = response.jsonPath().getLong("id");
        assertNotNull(createdBandId);
    }

    @Then("grupę galima rasti pagal šį ID")
    public void bandCanBeFoundById() {
        Response response = RestAssured.get("/bands/" + createdBandId);
        assertEquals(200, response.getStatusCode());
    }

    @Then("grupė nesukuriama")
    public void bandIsNotCreated() {
        int status = context.getLastResponse().getStatusCode();
        assertEquals(400, status);
    }

    @Then("grupės {string} narių skaičius yra {int}")
    public void bandMembersCountIs(String name, int expectedMembersCount) {
        Long bandId = context.getBandId(name);
        Band band = findBand(bandId);
        assertEquals(expectedMembersCount, band.getMembersCount());
    }

    @Then("grupės {string} sistemoje nebėra")
    public void bandNoLongerExists(String name) {
        int status = context.getLastResponse().getStatusCode();
        assertEquals(204, status);

        Long bandId = context.getBandId(name);
        boolean exists = bandRepository.existsById(bandId);
        assertFalse(exists, "Band " + name + " still exists");
    }

    // ---------- Helper methods ----------

    /**
     * Saves a band straight to the database and remembers its ID.
     */
    private void saveBand(String name, int membersCount) {
        Band band = new Band(name, DEFAULT_EMAIL, membersCount);
        Band savedBand = bandRepository.save(band);
        context.putBandId(name, savedBand.getId());
    }

    /**
     * Reads a band from the database.
     */
    private Band findBand(Long bandId) {
        Optional<Band> foundBand = bandRepository.findById(bandId);
        assertTrue(foundBand.isPresent(), "Band " + bandId + " was not found");
        Band band = foundBand.get();
        return band;
    }

    /**
     * Sends a request to create a band and remembers the response.
     */
    private void sendCreateBandRequest(String name, String email, int membersCount) {
        String body = createBandJson(name, email, membersCount);

        RequestSpecification request = RestAssured.given();
        request.contentType(ContentType.JSON);
        request.body(body);
        Response response = request.post("/bands");

        context.setLastResponse(response);
    }

    /**
     * Creates the JSON body for creating or changing a band.
     */
    private String createBandJson(String name, String email, int membersCount) {
        String json = "{"
                + "\"name\": \"" + name + "\", "
                + "\"contactEmail\": \"" + email + "\", "
                + "\"membersCount\": " + membersCount
                + "}";
        return json;
    }
}
