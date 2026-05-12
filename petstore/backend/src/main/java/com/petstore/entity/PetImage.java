package com.petstore.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;

/**
 * PetImage entity for storing multiple images per pet.
 * 
 * Each pet can have multiple images with display order.
 */
@Entity
@Table(name = "pet_images", indexes = {
        @Index(name = "idx_pet_image_pet_id", columnList = "pet_id"),
        @Index(name = "idx_pet_image_order", columnList = "pet_id,display_order")
})
public class PetImage extends AuditedEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotBlank(message = "Pet ID is required")
    @Column(nullable = false, name = "pet_id", length = 36)
    private String petId;

    @NotBlank(message = "Image URL is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String imageUrl;

    @PositiveOrZero(message = "Display order must be 0 or positive")
    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer displayOrder = 0;

    // Constructors
    public PetImage() {
    }

    public PetImage(String petId, String imageUrl, Integer displayOrder) {
        this.petId = petId;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
