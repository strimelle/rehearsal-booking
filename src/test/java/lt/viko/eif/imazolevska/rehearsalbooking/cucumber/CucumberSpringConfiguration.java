package lt.viko.eif.imazolevska.rehearsalbooking.cucumber;

import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Starts the whole application on a random port before the Cucumber scenarios run.
 * The scenarios send real HTTP requests to it.
 */
@CucumberContextConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CucumberSpringConfiguration {
}
