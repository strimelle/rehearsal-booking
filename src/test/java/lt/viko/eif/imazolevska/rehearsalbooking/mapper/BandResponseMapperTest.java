package lt.viko.eif.imazolevska.rehearsalbooking.mapper;

import lt.viko.eif.imazolevska.rehearsalbooking.dto.BandResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.hateoas.Link;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BandResponseMapperTest {

    private final BandResponseMapper mapper = new BandResponseMapper();

    @BeforeEach
    void setUp() {
        // A fake request, so that linkTo() knows the server address
        MockHttpServletRequest request = new MockHttpServletRequest();
        ServletRequestAttributes attributes = new ServletRequestAttributes(request);
        RequestContextHolder.setRequestAttributes(attributes);
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
    }

    @Test
    void toResponseShouldCopyAllFields() {
        // Arrange
        Band band = createBand(2L);

        // Act
        BandResponse response = mapper.toResponse(band);

        // Assert
        Long expectedId = 2L;
        assertEquals(expectedId, response.getId());
        assertEquals("Vakaro Aidas", response.getName());
        assertEquals("aidas@example.com", response.getContactEmail());
        assertEquals(4, response.getMembersCount());
    }

    @Test
    void toResponseShouldAddLinks() {
        // Arrange
        Band band = createBand(2L);

        // Act
        BandResponse response = mapper.toResponse(band);

        // Assert
        Link selfLink = response.getRequiredLink("self");
        assertEquals("http://localhost/bands/2", selfLink.getHref());

        Link allBandsLink = response.getRequiredLink("bands");
        assertEquals("http://localhost/bands", allBandsLink.getHref());
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
