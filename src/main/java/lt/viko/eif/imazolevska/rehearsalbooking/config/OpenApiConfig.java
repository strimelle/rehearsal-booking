package lt.viko.eif.imazolevska.rehearsalbooking.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * OpenAPI (Swagger) configuration: the title and description of the API (NFR-8).
 */
@Configuration
public class OpenApiConfig {

    /**
     * Creates the general information shown at the top of the Swagger page.
     *
     * @return the OpenAPI description of the API
     */
    @Bean
    public OpenAPI rehearsalBookingOpenApi() {
        Info info = new Info();
        info.setTitle("Rehearsal Room Booking API");
        info.setDescription("REST API for booking rehearsal rooms for music bands.");
        info.setVersion("1.0");

        OpenAPI openApi = new OpenAPI();
        openApi.setInfo(info);
        return openApi;
    }
}