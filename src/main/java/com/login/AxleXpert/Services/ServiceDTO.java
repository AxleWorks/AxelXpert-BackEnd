package com.login.AxleXpert.Services;

import java.math.BigDecimal;

public class ServiceDTO {
    private Long id;
    private String name;
    private BigDecimal price;
    private Integer durationMinutes;
    private String description;

    public ServiceDTO() {}

    public ServiceDTO(Long id, String name, BigDecimal price, Integer durationMinutes, String description) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.durationMinutes = durationMinutes;
        this.description = description;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public static ServiceDTO fromEntity(Service s) {
        if (s == null) return null;
        return new ServiceDTO(s.getId(), s.getName(), s.getPrice(), s.getDurationMinutes(), s.getDescription());
    }
}
