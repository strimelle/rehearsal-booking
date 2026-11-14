package lt.viko.eif.imazolevska.rehearsalbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web configuration: makes API responses cacheable (NFR-3).
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Adds an ETag header to every GET response and answers
     * with 304 Not Modified when the data has not changed.
     *
     * @return the ETag filter
     */


    @Bean
    public ShallowEtagHeaderFilter shallowEtagHeaderFilter() {
        ShallowEtagHeaderFilter filter = new ShallowEtagHeaderFilter();
        return filter;
    }

    /**
     * Registers the interceptor that adds the Cache-Control header.
     *
     * @param registry the list of interceptors used by Spring
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CacheControlInterceptor cacheControlInterceptor = new CacheControlInterceptor();
        registry.addInterceptor(cacheControlInterceptor);
    }

    /**
     * Allows Swagger Editor (editor.swagger.io) to send requests to this API from the browser.
     *
     * @param registry the CORS settings used by Spring
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        CorsRegistration registration = registry.addMapping("/**");
        registration.allowedOrigins("https://editor.swagger.io");
        registration.allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}

