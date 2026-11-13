package lt.viko.eif.imazolevska.rehearsalbooking.entity;


import jakarta.persistence.*;

/**
 * A music band that books rehearsal rooms.
 * Mapped to the "bands" database table.
 */
@Entity
@Table(name="bands")
public class Band {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "contact_email")
    private String contactEmail;

    @Column(name = "members_count")
    private int membersCount;

    /**
     * Empty constructor required by JPA.
     */
    protected Band() {
    }

    /**
     * Creates a new band. The ID is assigned by the database when the band is saved.
     *
     * @param name         the band name
     * @param contactEmail the contact email address
     * @param membersCount how many members the band has
     */
    public Band(String name, String contactEmail, int membersCount) {
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

    public void setName(String name) {
        this.name = name;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public int getMembersCount() {
        return membersCount;
    }

    public void setMembersCount(int membersCount) {
        this.membersCount = membersCount;
    }
}
