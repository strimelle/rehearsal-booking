package lt.viko.eif.imazolevska.rehearsalbooking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
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
}

