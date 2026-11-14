package lt.viko.eif.imazolevska.rehearsalbooking.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BandRequest;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BandResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import lt.viko.eif.imazolevska.rehearsalbooking.mapper.BandResponseMapper;
import lt.viko.eif.imazolevska.rehearsalbooking.service.BandService;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * REST endpoints for music bands.
 */
@RestController
@RequestMapping("/bands")
@Tag(name = "Bands", description = "Music bands")
public class BandController {

    private final BandService bandService;
    private final BandResponseMapper bandResponseMapper;

    /**
     * Creates the controller. Spring passes the dependencies automatically.
     *
     * @param bandService        the service for bands
     * @param bandResponseMapper the mapper that creates responses
     */
    public BandController(BandService bandService, BandResponseMapper bandResponseMapper) {
        this.bandService = bandService;
        this.bandResponseMapper = bandResponseMapper;
    }

    /**
     * Returns one band.
     *
     * @param id the band ID from the URL
     * @return the band with status 200
     */
    @GetMapping("/{id}")
    @Operation(summary = "See a band")
    public ResponseEntity<BandResponse> getBandById(@PathVariable("id") Long id) {
        Band band = bandService.getBandById(id);
        BandResponse response = bandResponseMapper.toResponse(band);
        return ResponseEntity.ok(response);
    }

    /**
     * Returns all bands.
     *
     * @return the list of bands with a link to itself
     */
    @GetMapping
    @Operation(summary = "See all bands")
    public CollectionModel<BandResponse> getAllBands() {
        List<Band> bands = bandService.getAllBands();

        List<BandResponse> responses = new ArrayList<>();
        for (Band band : bands) {
            BandResponse response = bandResponseMapper.toResponse(band);
            responses.add(response);
        }

        Link selfLink = linkTo(methodOn(BandController.class).getAllBands()).withSelfRel();
        CollectionModel<BandResponse> collection = CollectionModel.of(responses, selfLink);
        return collection;
    }

    /**
     * Creates a new band.
     *
     * @param request the band data from the request body
     * @return the created band with status 201 and a Location header
     */
    @PostMapping
    @Operation(summary = "Create band")
    public ResponseEntity<BandResponse> createBand(@Valid @RequestBody BandRequest request) {
        Band band = bandService.createBand(request.getName(), request.getContactEmail(), request.getMembersCount());
        BandResponse response = bandResponseMapper.toResponse(band);

        URI location = response.getRequiredLink(IanaLinkRelations.SELF).toUri();
        return ResponseEntity.created(location).body(response);
    }

    /**
     * Changes an existing band.
     *
     * @param id      the band ID from the URL
     * @param request the new band data
     * @return the changed band with status 200
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update band")
    public ResponseEntity<BandResponse> updateBand(@PathVariable("id") Long id,
                                                   @Valid @RequestBody BandRequest request) {
        Band band = bandService.updateBand(id, request.getName(), request.getContactEmail(),
                request.getMembersCount());
        BandResponse response = bandResponseMapper.toResponse(band);
        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a band together with its bookings.
     *
     * @param id the band ID from the URL
     * @return an empty response with status 204
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete band")
    public ResponseEntity<Void> deleteBand(@PathVariable("id") Long id) {
        bandService.deleteBand(id);
        return ResponseEntity.noContent().build();
    }
}