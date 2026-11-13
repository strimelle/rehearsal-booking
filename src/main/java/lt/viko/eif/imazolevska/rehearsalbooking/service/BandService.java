package lt.viko.eif.imazolevska.rehearsalbooking.service;

import org.springframework.transaction.annotation.Transactional;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Booking;
import lt.viko.eif.imazolevska.rehearsalbooking.exception.ResourceNotFoundException;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BandRepository;
import lt.viko.eif.imazolevska.rehearsalbooking.repository.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BandService {

    /**
     * Business logic for music bands.
     */
    private final BandRepository bandRepository;
    private final BookingRepository bookingRepository;

    /**
     * Creates the service. Spring passes the repositories automatically.
     *
     * @param bandRepository    the repository for bands
     * @param bookingRepository the repository for bookings
     */
    public BandService(BandRepository bandRepository, BookingRepository bookingRepository) {
        this.bandRepository = bandRepository;
        this.bookingRepository = bookingRepository;
    }

    /**
     * Returns all bands.
     *
     * @return the list of all bands
     */
    public List<Band> getAllBands() {
        List<Band> bands = bandRepository.findAll();
        return bands;
    }

    /**
     * Finds a band by its ID.
     *
     * @param id the band ID
     * @return the found band
     * @throws ResourceNotFoundException if the band does not exist
     */
    public Band getBandById(Long id) {
        Optional<Band> foundBand = bandRepository.findById(id);
        if (foundBand.isEmpty()) {
            throw new ResourceNotFoundException("Band with id " + id + " was not found");
        }
        Band band = foundBand.get();
        return band;
    }

    /**
     * Creates and saves a new band.
     *
     * @param name         the band name
     * @param contactEmail the contact email address
     * @param membersCount how many members the band has
     * @return the saved band with its ID
     */
    public Band createBand(String name, String contactEmail, int membersCount) {
        Band band = new Band(name, contactEmail, membersCount);
        Band savedBand = bandRepository.save(band);
        return savedBand;
    }

    /**
     * Changes the data of an existing band.
     *
     * @param id           the band ID
     * @param name         the new band name
     * @param contactEmail the new contact email address
     * @param membersCount the new number of members
     * @return the saved band
     * @throws ResourceNotFoundException if the band does not exist
     */
    public Band updateBand(Long id, String name, String contactEmail, int membersCount) {
        Band band = getBandById(id);

        band.setName(name);
        band.setContactEmail(contactEmail);
        band.setMembersCount(membersCount);

        Band savedBand = bandRepository.save(band);
        return savedBand;
    }

    /**
     * Deletes a band together with all its bookings.
     *
     * @param id the band ID
     * @throws ResourceNotFoundException if the band does not exist
     */
    @Transactional
    public void deleteBand(Long id) {
        Band band = getBandById(id);

        List<Booking> bookings = bookingRepository.findByBandId(id);
        for (Booking booking : bookings) {
            bookingRepository.delete(booking);
        }

        bandRepository.delete(band);
    }
}

