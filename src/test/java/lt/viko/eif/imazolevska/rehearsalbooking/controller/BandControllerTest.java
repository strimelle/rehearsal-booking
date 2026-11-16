package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.GlobalExceptionHandler;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BandResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BandService;
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
class BandControllerTest {

    @Mock
    private BandService bandService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        BandResponseMapper bandResponseMapper = new BandResponseMapper();
        BandController controller = new BandController(bandService, bandResponseMapper);
        GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(exceptionHandler)
                .build();
    }

    @Test
    void getBandByIdShouldReturnBand() throws Exception {
        // Arrange
        Band band = createBand(2L);
        when(bandService.getBandById(2L)).thenReturn(band);

        // Act
        ResultActions result = mockMvc.perform(get("/bands/2"));

        // Assert
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.name").value("Vakaro Aidas"));
        result.andExpect(jsonPath("$.membersCount").value(4));
    }

    @Test
    void createBandShouldReturn201AndLocation() throws Exception {
        // Arrange
        Band band = createBand(2L);
        when(bandService.createBand("Vakaro Aidas", "aidas@example.com", 4)).thenReturn(band);

        String json = """
                {
                  "name": "Vakaro Aidas",
                  "contactEmail": "aidas@example.com",
                  "membersCount": 4
                }
                """;

        // Act
        ResultActions result = mockMvc.perform(post("/bands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // Assert
        result.andExpect(status().isCreated());
        result.andExpect(header().string("Location", "http://localhost/bands/2"));
    }

    @Test
    void createBandShouldReturn400WhenEmailIsInvalid() throws Exception {
        // Arrange
        String json = """
                {
                  "name": "Vakaro Aidas",
                  "contactEmail": "ne-el-pastas",
                  "membersCount": 4
                }
                """;

        // Act
        ResultActions result = mockMvc.perform(post("/bands")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json));

        // Assert
        result.andExpect(status().isBadRequest());
        result.andExpect(jsonPath("$.message").value("contactEmail: Email format is invalid"));
        verify(bandService, never()).createBand(anyString(), anyString(), anyInt());
    }

    @Test
    void deleteBandShouldReturn204() throws Exception {
        // Act
        ResultActions result = mockMvc.perform(delete("/bands/2"));

        // Assert
        result.andExpect(status().isNoContent());
        verify(bandService).deleteBand(2L);
    }

    /**
     * Creates a band with the given ID.
     */
    private Band createBand(Long id) {
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        ReflectionTestUtils.setField(band, "id", id);
        return band;
    }
}
