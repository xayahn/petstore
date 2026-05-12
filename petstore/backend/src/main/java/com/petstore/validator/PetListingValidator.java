package com.petstore.validator;

import com.petstore.entity.Pet;
import layug.exception.ValidationException;
import org.springframework.stereotype.Component;

/**
 * PetListingValidator
 * 
 * Validates pet listing data for seller-submitted pet listings.
 * Ensures all required fields are present and valid before saving.
 */
@Component
public class PetListingValidator {

    /**
     * Validate pet listing data for creation
     * 
     * @param pet Pet entity to validate
     * @throws ValidationException if validation fails
     */
    public void validateForCreation(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Pet data is required");
        }

        validateName(pet.getName());
        validateSpecies(pet.getSpecies());
        validatePrice(pet.getPrice());
        validateOptionalFields(pet);
    }

    /**
     * Validate pet listing data for update
     * 
     * @param pet Pet entity to validate
     * @throws ValidationException if validation fails
     */
    public void validateForUpdate(Pet pet) {
        if (pet == null) {
            throw new ValidationException("Pet data is required");
        }

        // For updates, only validate non-null fields
        if (pet.getName() != null) {
            validateName(pet.getName());
        }
        if (pet.getSpecies() != null) {
            validateSpecies(pet.getSpecies());
        }
        if (pet.getPrice() != null) {
            validatePrice(pet.getPrice());
        }
        validateOptionalFields(pet);
    }

    /**
     * Validate pet name
     * 
     * @param name Pet name to validate
     * @throws ValidationException if name is invalid
     */
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Pet name is required");
        }
        if (name.length() > 255) {
            throw new ValidationException("Pet name must not exceed 255 characters");
        }
    }

    /**
     * Validate pet species
     * 
     * @param species Pet species to validate
     * @throws ValidationException if species is invalid
     */
    private void validateSpecies(String species) {
        if (species == null || species.trim().isEmpty()) {
            throw new ValidationException("Pet species is required");
        }
        if (species.length() > 100) {
            throw new ValidationException("Pet species must not exceed 100 characters");
        }
    }

    /**
     * Validate pet price
     * 
     * @param price Pet price to validate
     * @throws ValidationException if price is invalid
     */
    private void validatePrice(Double price) {
        if (price == null) {
            throw new ValidationException("Pet price is required");
        }
        if (price < 0) {
            throw new ValidationException("Pet price must be positive");
        }
        if (price > 999999.99) {
            throw new ValidationException("Pet price must not exceed $999,999.99");
        }
    }

    /**
     * Validate optional pet listing fields
     * 
     * @param pet Pet entity to validate optional fields
     * @throws ValidationException if optional fields are invalid
     */
    private void validateOptionalFields(Pet pet) {
        if (pet.getBreed() != null && pet.getBreed().length() > 100) {
            throw new ValidationException("Pet breed must not exceed 100 characters");
        }

        if (pet.getAge() != null && pet.getAge().length() > 50) {
            throw new ValidationException("Pet age must not exceed 50 characters");
        }

        if (pet.getDescription() != null && pet.getDescription().length() > 5000) {
            throw new ValidationException("Pet description must not exceed 5000 characters");
        }

        if (pet.getHealthStatus() != null) {
            validateHealthStatus(pet.getHealthStatus());
        }

        if (pet.getAvailabilityStatus() != null) {
            validateAvailabilityStatus(pet.getAvailabilityStatus());
        }

        if (pet.getStockQuantity() != null && pet.getStockQuantity() < 0) {
            throw new ValidationException("Stock quantity must not be negative");
        }
    }

    /**
     * Validate health status enum value
     * 
     * @param healthStatus Health status value to validate
     * @throws ValidationException if status is invalid
     */
    private void validateHealthStatus(String healthStatus) {
        if (!isValidHealthStatus(healthStatus)) {
            throw new ValidationException(
                    "Invalid health status: " + healthStatus +
                            ". Valid values: healthy, recovering, special_needs");
        }
    }

    /**
     * Validate availability status enum value
     * 
     * @param availabilityStatus Availability status value to validate
     * @throws ValidationException if status is invalid
     */
    private void validateAvailabilityStatus(String availabilityStatus) {
        if (!isValidAvailabilityStatus(availabilityStatus)) {
            throw new ValidationException(
                    "Invalid availability status: " + availabilityStatus +
                            ". Valid values: available, unavailable, reserved");
        }
    }

    /**
     * Check if health status is valid
     */
    private boolean isValidHealthStatus(String status) {
        return status != null && (status.equals("healthy") ||
                status.equals("recovering") ||
                status.equals("special_needs"));
    }

    /**
     * Check if availability status is valid
     */
    private boolean isValidAvailabilityStatus(String status) {
        return status != null && (status.equals("available") ||
                status.equals("unavailable") ||
                status.equals("reserved"));
    }
}
