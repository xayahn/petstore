package com.petstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * DTO for pet list items (catalog listing).
 * 
 * Lightweight DTO used for GET /api/pets list responses.
 * Contains only essential information for displaying in catalog.
 */
public class PetListItemDTO {

    private String id;
    private String name;
    private String species;
    private String breed;
    private Double price;

    @JsonProperty("primary_image")
    private String primaryImage;

    @JsonProperty("availability_status")
    private String availabilityStatus;

    @JsonProperty("seller_id")
    private String sellerId;

    @JsonProperty("seller_name")
    private String sellerName;

    @JsonProperty("seller_rating")
    private Double sellerRating;

    // Constructors
    public PetListItemDTO() {
    }

    public PetListItemDTO(String id, String name, String species, String breed, Double price) {
        this.id = id;
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

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getPrimaryImage() {
        return primaryImage;
    }

    public void setPrimaryImage(String primaryImage) {
        this.primaryImage = primaryImage;
    }

    public String getAvailabilityStatus() {
        return availabilityStatus;
    }

    public void setAvailabilityStatus(String availabilityStatus) {
        this.availabilityStatus = availabilityStatus;
    }

    public String getSellerId() {
        return sellerId;
    }

    public void setSellerId(String sellerId) {
        this.sellerId = sellerId;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public Double getSellerRating() {
        return sellerRating;
    }

    public void setSellerRating(Double sellerRating) {
        this.sellerRating = sellerRating;
    }
}
