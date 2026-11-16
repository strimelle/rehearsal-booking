package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

/**
 * Starts the whole application on a random port before the Cucumber scenarios run.
 * The scenarios send real HTTP requests to it.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {

    /**
     * Test beans that are added to the application only during the Cucumber tests.
     */
    @TestConfiguration
    static class FakeWeatherConfiguration {

        /**
         * Replaces the real Meteo.lt client with a fake one.
         * Primary means Spring uses this bean instead of MeteoClient.
         *
         * @return the fake weather client
         */
        @Bean
        @Primary
        public FakeWeatherClient fakeWeatherClient() {
            FakeWeatherClient fakeWeatherClient = new FakeWeatherClient();
            return fakeWeatherClient;
        }
    }
}
