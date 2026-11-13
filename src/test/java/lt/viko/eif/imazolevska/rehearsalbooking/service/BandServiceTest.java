package lt.viko.eif.imazolevska.rehearsalbooking.service;

import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Room;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BandRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BandServiceTest {

    @Mock
    private BandRepository bandRepository;

    @Mock
    private BookingRepository bookingRepository;

    @InjectMocks
    private BandService bandService;

    @Test
    void getBandByIdShouldReturnBandWhenItExists() {
        // Arrange
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));

        // Act
        Band result = bandService.getBandById(1L);

        // Assert
        assertEquals("Vakaro Aidas", result.getName());
    }

    @Test
    void getBandByIdShouldThrowExceptionWhenBandDoesNotExist() {
        // Arrange
        when(bandRepository.findById(99L)).thenReturn(Optional.empty());

        // Act and Assert
        try {
            bandService.getBandById(99L);
            fail("Expected ResourceNotFoundException");
        } catch (ResourceNotFoundException exception) {
            assertEquals("Band with id 99 was not found", exception.getMessage());
        }
    }

    @Test
    void createBandShouldSaveNewBand() {
        // Act
        bandService.createBand("Vakaro Aidas", "aidas@example.com", 4);

        // Assert: catch the band that was passed to save() and check it
        ArgumentCaptor<Band> captor = ArgumentCaptor.forClass(Band.class);
        verify(bandRepository).save(captor.capture());
        Band savedBand = captor.getValue();
        assertEquals("Vakaro Aidas", savedBand.getName());
        assertEquals("aidas@example.com", savedBand.getContactEmail());
        assertEquals(4, savedBand.getMembersCount());
    }

    @Test
    void updateBandShouldChangeMembersCount() {
        // Arrange
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));

        // Act
        bandService.updateBand(1L, "Vakaro Aidas", "aidas@example.com", 6);

        // Assert
        assertEquals(6, band.getMembersCount());
        verify(bandRepository).save(band);
    }

    @Test
    void deleteBandShouldDeleteBandAndItsBookings() {
        // Arrange: the band exists and has one booking
        Band band = new Band("Vakaro Aidas", "aidas@example.com", 4);
        Room room = new Room("Studija A", 5, new BigDecimal("15.00"));
        LocalDateTime start = LocalDateTime.of(2030, 1, 10, 18, 0);
        LocalDateTime end = LocalDateTime.of(2030, 1, 10, 20, 0);
        Booking booking = new Booking(band, room, start, end, new BigDecimal("30.00"));

        List<Booking> bookings = new ArrayList<>();
        bookings.add(booking);

        when(bandRepository.findById(1L)).thenReturn(Optional.of(band));
        when(bookingRepository.findByBandId(1L)).thenReturn(bookings);

        // Act
        bandService.deleteBand(1L);

        // Assert
        verify(bookingRepository).delete(booking);
        verify(bandRepository).delete(band);
    }
}