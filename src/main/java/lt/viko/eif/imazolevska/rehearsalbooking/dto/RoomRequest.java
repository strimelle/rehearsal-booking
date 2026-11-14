package lt.viko.eif.imazolevska.rehearsalbooking.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

/**
 * Data that the client sends to create or change a room.
 */
public class RoomRequest {
    @NotBlank(message = "Name ir required")
    @Size(max = 100, message = "Name must be at most 100 characters")
    private String name;

    @NotNull(message = "Capacity is required")
    @Min(value = 1, message = "Capacity must be at least 1")
    private Integer capacity;

    @NotNull(message = "Hourly price ir required")
    @Positive(message = "Hourly price must be greater than 0")
    private BigDecimal hourlyPrice;

    public RoomRequest() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public BigDecimal getHourlyPrice() {
        return hourlyPrice;
    }

    public void setHourlyPrice(BigDecimal hourlyPrice) {
        this.hourlyPrice = hourlyPrice;
    }
}

