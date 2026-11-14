package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Data that the client sends to create or change a band.
 */
public class BandRequest {

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotBlank(message = "Contact email is required")
    @Email(message = "Email format is invalid")
    private String contactEmail;

    @NotNull(message = "Members count is required")
    @Min(value = 1, message = "Members count must be at least 1")
    private Integer membersCount;

    /**
     * Empty constructor needed to create the object from JSON.
     */
    public BandRequest() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public Integer getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(Integer membersCount) {
        this.membersCount = membersCount;
    }
}