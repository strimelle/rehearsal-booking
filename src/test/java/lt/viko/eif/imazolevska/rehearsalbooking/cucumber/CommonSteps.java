package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.en.Then;
import io.restassured.response.Response;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Steps that are used by several user stories.
 */
public class CommonSteps {

    private final TestContext context;
    private final BookingRepository bookingRepository;
    private final Map<String, String> expectedMessages = new HashMap<>();

    /**
     * Creates the steps and fills the table of expected error messages.
     *
     * @param context           the data shared between steps
     * @param bookingRepository the repository for bookings
     */
    public CommonSteps(TestContext context, BookingRepository bookingRepository) {
        this.context = context;
        this.bookingRepository = bookingRepository;
        fillExpectedMessages();
    }

    /**
     * Connects the Lithuanian text from the scenarios with the English message the API returns.
     */
    private void fillExpectedMessages() {
        expectedMessages.put("toks pavadinimas jau naudojamas", "Room name is already in use");
        expectedMessages.put("talpa turi būti ne mažesnė nei 1", "Capacity must be at least 1");
        expectedMessages.put("kambarys nerastas", "Room with id");
        expectedMessages.put("kaina turi būti didesnė nei 0", "Hourly price must be greater than 0");
        expectedMessages.put("el. pašto formatas neteisingas", "Email format is invalid");
        expectedMessages.put("narių skaičius turi būti ne mažesnis nei 1", "Members count must be at least 1");
        expectedMessages.put("grupė nerasta", "Band with id");
        expectedMessages.put("kambarys tuo metu užimtas", "Room is already booked at this time");
        expectedMessages.put("grupės narių skaičius viršija kambario talpą",
                "Band has more members than the room capacity");
        expectedMessages.put("negalima rezervuoti praeityje", "Booking cannot be made in the past");
        expectedMessages.put("rezervacija turi trukti nuo 1 iki 3 valandų", "Booking must last from 1 to 3 hours");
        expectedMessages.put("rezervacija turi trukti sveiką valandų skaičių",
                "Booking must last a whole number of hours");
        expectedMessages.put("pabaigos laikas turi būti vėlesnis už pradžios laiką",
                "End time must be after start time");
        expectedMessages.put("atšaukti galima ne vėliau kaip prieš 24 valandas", "no later than 24 hours");
        expectedMessages.put("rezervacija jau atšaukta", "Booking is already canceled");
        expectedMessages.put("orų prognozės šiam laikui dar nėra",
                "Weather forecast for this booking time is not available");
        expectedMessages.put("orų tarnyba nepasiekiama", "Weather service is not available");
    }

    @Then("gaunamas pranešimas, kad {}")
    public void messageIsReceived(String lithuanianText) {
        String expectedText = expectedMessages.get(lithuanianText);
        assertNotNull(expectedText, "No English message is defined for: " + lithuanianText);

        Response response = context.getLastResponse();
        String actualMessage = response.jsonPath().getString("message");
        assertNotNull(actualMessage, "The response has no message");

        boolean containsText = actualMessage.contains(expectedText);
        assertTrue(containsText, "Expected the message to contain '" + expectedText + "', but it was: " + actualMessage);
    }

    @Then("tų rezervacijų sistemoje nebėra")
    public void thoseBookingsNoLongerExist() {
        checkGivenBookingsAreDeleted();
    }

    @Then("tos rezervacijos sistemoje nebėra")
    public void thatBookingNoLongerExists() {
        checkGivenBookingsAreDeleted();
    }

    /**
     * Checks that every booking created in the Given steps was deleted.
     */
    private void checkGivenBookingsAreDeleted() {
        List<Long> bookingIds = context.getGivenBookingIds();
        assertFalse(bookingIds.isEmpty(), "The scenario did not create any bookings");

        for (Long bookingId : bookingIds) {
            boolean exists = bookingRepository.existsById(bookingId);
            assertFalse(exists, "Booking " + bookingId + " still exists");
        }
    }
}
