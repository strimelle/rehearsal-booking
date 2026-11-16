package lt.viko.eif.imazolevska.rehearsalbooking.client;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * The place part of a Meteo.lt answer. Only the fields we use are listed,
 * all other fields are ignored.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class MeteoPlace {

    private String code;
    private String name;

    /**
     * Empty constructor needed to create the object from JSON.
     */
    public MeteoPlace() {
    }

    /**
     * Returns the Meteo.lt place code, for example "vilnius".
     *
     * @return the place code
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the place code.
     *
     * @param code the place code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * Returns the place name, for example "Vilnius".
     *
     * @return the place name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the place name.
     *
     * @param name the place name
     */
    public void setName(String name) {
        this.name = name;
    }
}
