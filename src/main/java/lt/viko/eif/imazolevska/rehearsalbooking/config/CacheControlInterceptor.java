package lt.viko.eif.imazolevska.rehearsalbooking.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Adds a Cache-Control header to every API response (NFR-3).
 * GET responses may be cached but must be checked with the ETag before use.
 * Responses to POST, PUT and DELETE must not be stored.
 */
public class CacheControlInterceptor implements HandlerInterceptor {
    private static final String CACHE_CONTROL_HEADER = "Cache-Control";
    private static final String CACHE_WITH_REVALIDATION = "no-cache";
    private static final String DO_NOT_STORE = "no-store";

    /**
     * Runs before the controller and sets the Cache-Control header.
     *
     * @param request  the current HTTP request
     * @param response the HTTP response that will be sent
     * @param handler  the controller method that will handle the request
     * @return always true, so that the request continues to the controller
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String method = request.getMethod();
        if (method.equals("GET")) {
            response.setHeader(CACHE_CONTROL_HEADER, CACHE_WITH_REVALIDATION);
        } else {
            response.setHeader(CACHE_CONTROL_HEADER, DO_NOT_STORE);
        }
        return true;
    }

}
