package lt.viko.eif.imazolevska.rehearsalbooking.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.config.CacheControlInterceptor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CacheControlInterceptorTest {

    private final CacheControlInterceptor interceptor = new CacheControlInterceptor();

    @Test
    void getRequestShouldGetNoCacheHeader() {
        // Arrange: a fake GET request and response
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("GET");

        // Act
        boolean result = interceptor.preHandle(request, response, null);

        // Assert: the request continues and the header is set
        assertTrue(result);
        verify(response).setHeader("Cache-Control", "no-cache");
    }

    @Test
    void postRequestShouldGetNoStoreHeader() {
        // Arrange: a fake POST request and response
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        when(request.getMethod()).thenReturn("POST");

        // Act
        boolean result = interceptor.preHandle(request, response, null);

        // Assert
        assertTrue(result);
        verify(response).setHeader("Cache-Control", "no-store");
    }
}