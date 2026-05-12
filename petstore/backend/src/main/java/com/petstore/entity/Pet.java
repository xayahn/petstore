package com.petstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * Pet entity representing a pet listing in the marketplace.
 * 
 * Each pet belongs to a seller and contains details about the pet including
 * basic info, health status, and availability. Supports multiple images
 * via PetImage entities.
 */
@Entity
@Table(name = "pets", indexes = {
        @Index(name = "idx_pet_seller_id", columnList = "seller_id"),
        @Index(name = "idx_pet_species", columnList = "species"),
        @Index(name = "idx_pet_breed", columnList = "breed"),
        @Index(name = "idx_pet_status", columnList = "availability_status"),
        @Index(name = "idx_pet_created_at", columnList = "created_at"),
        @Index(name = "idx_pet_name_species_breed", columnList = "name,species,breed")
})
public class Pet extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Pet name is required")
    @Column(nullable = false, length = 255)
    private String name;

    @NotBlank(message = "Species is required")
    @Column(nullable = false, length = 100)
    private String species;

    @Column(length = 100)
    private String breed;

    @Column(length = 50)
    private String age;

    @NotNull(message = "Price is required")
    @PositiveOrZero(message = "Price must be 0 or positive")
    @Column(nullable = false, precision = 10, scale = 2)
    private Double price;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100, nullable = false, columnDefinition = "VARCHAR(100) DEFAULT 'healthy'")
    private String healthStatus = "healthy";

    @Column(length = 50, nullable = false, columnDefinition = "VARCHAR(50) DEFAULT 'available'")
    private String availabilityStatus = "available";

    @PositiveOrZero(message = "Stock quantity must be 0 or positive")
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer stockQuantity = 1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = true, foreignKey = @ForeignKey(name = "fk_pet_seller_id"))
    private Seller seller;

    // Constructors
    public Pet() {
    }

    public Pet(String name, String species, String breed, Double price) {
        this.name = name;
        this.species = species;
        this.breed = breed;
        this.price = price;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public void setHealthStatus(String healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public Seller getSeller() {
        return seller;
    }

    public void setSeller(Seller seller) {
        this.seller = seller;
    }

    public String getSellerId() {
        return seller != null ? seller.getId() : null;
    }

    @Override
    public String toString() {
        return "Pet{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", species='" + species + '\'' +
                ", breed='" + breed + '\'' +
                ", price=" + price +
                ", availabilityStatus='" + availabilityStatus + '\'' +
                '}';
    }
}
