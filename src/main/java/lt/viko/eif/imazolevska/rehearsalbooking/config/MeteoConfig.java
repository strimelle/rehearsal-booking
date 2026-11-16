package lt.viko.eif.imazolevska.rehearsalbooking.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Configuration for the Meteo.lt weather API and for caching its answers.
 */
@Configuration
@EnableCaching
public class MeteoConfig {

    /**
     * Creates the RestClient that sends requests to Meteo.lt.
     *
     * @param baseUrl the Meteo.lt address from application.properties
     * @return the RestClient
     */
    @Bean
    public RestClient meteoRestClient(@Value("${meteo.base-url}") String baseUrl) {
        RestClient restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
        return restClient;
    }

    /**
     * Creates a simple cache in memory. The cache "forecasts" keeps Meteo.lt answers,
     * so the same forecast is not downloaded again for every request.
     *
     * @return the cache manager
     */
    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("forecasts");
        return cacheManager;
    }
}
