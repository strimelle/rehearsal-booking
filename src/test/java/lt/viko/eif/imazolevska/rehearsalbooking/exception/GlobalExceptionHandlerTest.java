package lt.viko.eif.imazolevska.rehearsalbooking.exception;

import jakarta.servlet.http.HttpServletRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void notFoundExceptionShouldReturnStatus404() {
        // Arrange: a fake request to /rooms/99
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/rooms/99");
        ResourceNotFoundException exception = new ResourceNotFoundException("Room with id 99 was not found");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleNotFound(exception, request);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        ErrorResponse body = response.getBody();
        assertEquals(404, body.getStatus());
        assertEquals("Not Found", body.getError());
        assertEquals("Room with id 99 was not found", body.getMessage());
        assertEquals("/rooms/99", body.getPath());
    }

    @Test
    void businessRuleExceptionShouldReturnStatus400() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/bookings");
        BusinessRuleException exception = new BusinessRuleException("Booking cannot be made in the past");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleBusinessRule(exception, request);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Booking cannot be made in the past", response.getBody().getMessage());
    }

    @Test
    void conflictExceptionShouldReturnStatus409() {
        // Arrange
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getRequestURI()).thenReturn("/bookings");
        ConflictException exception = new ConflictException("Room is already booked at this time");

        // Act
        ResponseEntity<ErrorResponse> response = handler.handleConflict(exception, request);

        // Assert
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Room is already booked at this time", response.getBody().getMessage());
    }
}