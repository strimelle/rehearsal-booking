package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecast;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoForecastTimestamp;
import lt.viko.eif.imazolevska.rehearsalbooking.client.MeteoPlace;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Steps for the weather forecast of a booking (US-9).
 */
public class ForecastSteps {

    private static final ZoneId VILNIUS_ZONE = ZoneId.of("Europe/Vilnius");
    private static final DateTimeFormatter METEO_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final TestContext context;
    private final FakeWeatherClient fakeWeatherClient;

    /**
     * Creates the steps. Spring passes the dependencies automatically.
     *
     * @param context           the data shared between steps
     * @param fakeWeatherClient the fake weather service
     */
    public ForecastSteps(TestContext context, FakeWeatherClient fakeWeatherClient) {
        this.context = context;
        this.fakeWeatherClient = fakeWeatherClient;
    }

    // ---------- Given ----------

    @Given("orų tarnyba {word} {word} prognozuoja {double} laipsnio ir {string}")
    public void weatherServiceForecasts(String day, String time, double temperature, String conditionCode) {
        LocalDateTime vilniusTime = toDateTime(day, time);

        // Meteo.lt gives the time in UTC, so the fake does the same
        ZonedDateTime inVilnius = vilniusTime.atZone(VILNIUS_ZONE);
        ZonedDateTime inUtc = inVilnius.withZoneSameInstant(ZoneOffset.UTC);
        String timeUtc = inUtc.toLocalDateTime().format(METEO_TIME_FORMAT);

        MeteoForecastTimestamp timestamp = new MeteoForecastTimestamp();
        timestamp.setForecastTimeUtc(timeUtc);
        timestamp.setAirTemperature(temperature);
        timestamp.setConditionCode(conditionCode);

        List<MeteoForecastTimestamp> timestamps = new ArrayList<>();
        timestamps.add(timestamp);

        MeteoPlace place = new MeteoPlace();
        place.setCode("vilnius");
        place.setName("Vilnius");

        MeteoForecast forecast = new MeteoForecast();
        forecast.setPlace(place);
        forecast.setForecastTimestamps(timestamps);

        fakeWeatherClient.setForecast(forecast);
    }

    @Given("orų tarnyba nepasiekiama")
    public void weatherServiceIsNotAvailable() {
        fakeWeatherClient.setAvailable(false);
    }

    // ---------- When ----------

    @When("grupės atstovas peržiūri tos rezervacijos orų prognozę")
    public void representativeViewsForecast() {
        Long bookingId = context.getLastBookingId();
        Response response = RestAssured.get("/bookings/" + bookingId + "/forecast");
        context.setLastResponse(response);
    }

    // ---------- Then ----------

    @Then("matoma prognozė: {double} laipsnio, {string}")
    public void forecastIsShown(double expectedTemperature, String expectedCondition) {
        Response response = context.getLastResponse();
        assertEquals(200, response.getStatusCode(), "Response was: " + response.asString());

        double actualTemperature = response.jsonPath().getDouble("airTemperature");
        assertEquals(expectedTemperature, actualTemperature, 0.001);

        String actualCondition = response.jsonPath().getString("condition");
        assertEquals(expectedCondition, actualCondition);
    }

    @Then("prognozė negaunama")
    public void forecastIsNotReceived() {
        int status = context.getLastResponse().getStatusCode();
        assertTrue(status >= 400, "Expected an error status, but it was " + status);
    }

    // ---------- Helper methods ----------

    /**
     * Turns words like "rytoj" and "18:00" into a date and time.
     */
    private LocalDateTime toDateTime(String day, String time) {
        LocalDate today = LocalDate.now();
        LocalDate date = null;

        switch (day) {
            case "šiandien":
                date = today;
                break;
            case "rytoj":
                date = today.plusDays(1);
                break;
            case "poryt":
                date = today.plusDays(2);
                break;
            default:
                fail("Unknown day: " + day);
        }

        LocalTime localTime = LocalTime.parse(time);
        LocalDateTime dateTime = date.atTime(localTime);
        return dateTime;
    }
}
