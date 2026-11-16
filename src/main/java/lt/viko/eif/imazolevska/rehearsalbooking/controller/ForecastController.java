package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ErrorResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ForecastResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.service.WeatherService;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST endpoint for the weather forecast of a booking.
 * The forecast comes from the public Meteo.lt API.
 */
@RestController
@RequestMapping("/bookings")
@Tag(name = "Weather forecast", description = "Weather forecast from Meteo.lt for the time of a booking")
public class ForecastController {

    private final WeatherService weatherService;

    /**
     * Creates the controller. Spring passes the dependencies automatically.
     *
     * @param weatherService the service that finds forecasts
     */
    public ForecastController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    /**
     * Returns the weather forecast for the start of a booking.
     *
     * @param id the booking ID from the URL
     * @return the forecast with status 200
     */
    @GetMapping("/{id}/forecast")
    @Operation(summary = "See the weather forecast for a booking")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "The forecast was found"),
            @ApiResponse(responseCode = "404", description = "The booking does not exist or there is no forecast for its time yet",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
            @ApiResponse(responseCode = "502", description = "Meteo.lt is not available",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<ForecastResponse> getForecast(@PathVariable("id") Long id) {
        ForecastResponse response = weatherService.getForecastForBooking(id);

        Link selfLink = linkTo(methodOn(ForecastController.class).getForecast(id)).withSelfRel();
        Link bookingLink = linkTo(methodOn(BookingController.class).getBookingById(id)).withRel("booking");
        response.add(selfLink);
        response.add(bookingLink);

        return ResponseEntity.ok(response);
    }
}
