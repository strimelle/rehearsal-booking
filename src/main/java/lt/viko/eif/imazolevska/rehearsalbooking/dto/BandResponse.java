package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

/**
 * Band data that the API returns, together with HATEOAS links.
 */
@Relation(collectionRelation = "bands", itemRelation = "band")
public class BandResponse extends RepresentationModel<BandResponse> {

    private final Long id;
    private final String name;
    private final String contactEmail;
    private final int membersCount;

    /**
     * Creates the response.
     *
     * @param id           the band ID
     * @param name         the band name
     * @param contactEmail the contact email address
     * @param membersCount how many members the band has
     */
    public BandResponse(Long id, String name, String contactEmail, int membersCount) {
        this.id = id;
        this.name = name;
        this.contactEmail = contactEmail;
        this.membersCount = membersCount;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public int getMembersCount() {
        return membersCount;
    }
}