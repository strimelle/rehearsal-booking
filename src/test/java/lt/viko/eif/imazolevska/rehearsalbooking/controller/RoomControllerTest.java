package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.GlobalExceptionHandler;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BookingResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.RoomResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BookingService;
import lt.viko.eif.imazolevska.rehearsalbooking.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RoomControllerTest {

    @Mock
    private RoomService roomService;

    @Mock
    private BookingService bookingService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Real mappers, fake services
        RoomResponseMapper roomResponseMapper = new RoomResponseMapper();
        BookingResponseMapper bookingResponseMapper = new BookingResponseMapper();
        RoomController controller =
                new RoomController(roomService, roomResponseMapper, bookingService, bookingResponseMapper);

        // The exception handler turns exceptions into 404, 400 and 409 responses
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void getRoomByIdShouldReturnRoom() throws Exception {
        // Arrange
        Room room = createRoom(1L);
        when(roomService.getRoomById(1L)).thenReturn(room);

        // Act
        ResultActions result = mockMvc.perform(get("/rooms/1"));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.id").value(1));
        result.andExpect(jsonPath("$.name").value("Studija A"));
        result.andExpect(jsonPath("$.capacity").value(5));
    }

    @Test
    void getRoomByIdShouldReturn404WhenRoomDoesNotExist() throws Exception {
        // Arrange
        ResourceNotFoundException exception = new ResourceNotFoundException("Room with id 99 was not found");
        when(roomService.getRoomById(99L)).thenThrow(exception);

        // Act
        ResultActions result = mockMvc.perform(get("/rooms/99"));

        // Assert
        result.andExpect(status().isNotFound());
        result.andExpect(jsonPath("$.message").value("Room with id 99 was not found"));
        result.andExpect(jsonPath("$.path").value("/rooms/99"));
    }

    @Test
    void createRoomShouldReturn201AndLocation() throws Exception {
        // Arrange
        Room room = createRoom(1L);
        when(roomService.createRoom("Studija A", 5, new BigDecimal("15.00"))).thenReturn(room);

        String json = """
                {
                  "name": "Studija A",
                  "capacity": 5,
                  "hourlyPrice": 15.00
                }
                """;

        // Act
        ResultActions result = mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(header().string("Location", "http://localhost/rooms/1"));
        result.andExpect(jsonPath("$.name").value("Studija A"));
    }

    @Test
    void createRoomShouldReturn400WhenCapacityIsZero() throws Exception {
        // Arrange
        String json = """
                {
                  "name": "Studija A",
                  "capacity": 0,
                  "hourlyPrice": 15.00
                }
                """;

        // Act
        ResultActions result = mockMvc.perform(post("/rooms")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // Assert: validation stops the request before the service
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.message").value("capacity: Capacity must be at least 1"));
        verify(roomService, never()).createRoom(anyString(), anyInt(), any(BigDecimal.class));
    }

    @Test
    void deleteRoomShouldReturn204() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/rooms/1"));

        // Assert
        result.andExpect(status().isNoContent());
        verify(roomService).deleteRoom(1L);
    }

    /**
     * Creates a room with the given ID.
     */
    private Room createRoom(Long id) {
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        ReflectionTestUtils.setField(room, "id", id);
        return room;
    }
}
