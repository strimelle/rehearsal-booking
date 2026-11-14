package lt.viko.eif.imazolevska.rehearsalbooking.mapper;

import lt.viko.eif.imazolevska.rehearsalbooking.controller.BandController;
import lt.viko.eif.imazolevska.rehearsalbooking.dto.BandResponse;
import lt.viko.eif.imazolevska.rehearsalbooking.entity.Band;
import org.springframework.hateoas.Link;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

/**
 * Converts Band entities to BandResponse objects and adds HATEOAS links.
 */
@Component
public class BandResponseMapper {

    /**
     * Converts a band to a response with links.
     pavarde.     *
     * @param band the band entity
     * @return the response for the API
     */
    public BandResponse toResponse(Band band) {
        BandResponse response = new BandResponse(
                band.getId(),
                band.getName(),
                band.getContactEmail(),
                band.getMembersCount()
        );

        Link selfLink = linkTo(methodOn(BandController.class).getBandById(band.getId())).withSelfRel();
        Link allBandsLink = linkTo(methodOn(BandController.class).getAllBands()).withRel("bands");

        response.add(selfLink);
        response.add(allBandsLink);
        return response;
    }
}