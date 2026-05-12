package com.petstore.service;

import com.petstore.dto.PetDTO;
import com.petstore.dto.PetListItemDTO;
import com.petstore.entity.Pet;
import com.petstore.entity.PetImage;
import com.petstore.entity.Seller;
import com.petstore.repository.PetImageRepository;
import com.petstore.repository.PetRepository;
import com.petstore.validator.PetListingValidator;
import layug.exception.NotFoundException;
import layug.exception.ValidationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Pet operations.
 * 
 * Handles pet listing, filtering, search, and management logic.
 */
@Service
@Transactional
public class PetService {

    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;
    private final PetListingValidator petListingValidator;

    public PetService(PetRepository petRepository, PetImageRepository petImageRepository,
            PetListingValidator petListingValidator) {
        this.petRepository = petRepository;
        this.petImageRepository = petImageRepository;
        this.petListingValidator = petListingValidator;
    }

    /**
     * Get a pet by ID with all details.
     * 
     * @param id Pet ID
     * @return Pet details
     */
    public PetDTO getPetById(String id) {
        return petRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> new RuntimeException("Pet not found: " + id));
    }

    /**
     * List all pets with optional filters and pagination.
     * 
     * @param species  Optional species filter
     * @param breed    Optional breed filter
     * @param minPrice Optional minimum price
     * @param maxPrice Optional maximum price
     * @param page     Page number (0-indexed)
     * @param size     Page size
     * @param sortBy   Sort field (name, price, created_at)
     * @return Page of pet list items
     */
    public Page<PetListItemDTO> listPets(
            String species,
            String breed,
            Double minPrice,
            Double maxPrice,
            int page,
            int size,
            String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc(sortBy)));

        Page<Pet> result;

        // Apply filters
        if (species != null && minPrice != null && maxPrice != null) {
            result = petRepository.findBySpeciesAndPriceRange(species, minPrice, maxPrice, pageable);
        } else if (species != null) {
            result = petRepository.findBySpecies(species, pageable);
        } else if (breed != null) {
            result = petRepository.findByBreed(breed, pageable);
        } else if (minPrice != null && maxPrice != null) {
            result = petRepository.findByPriceRange(minPrice, maxPrice, pageable);
        } else {
            result = petRepository.findAll(pageable);
        }

        return result.map(this::convertToListItemDTO);
    }

    /**
     * Search pets by query string (searches name, species, breed).
     * 
     * @param query Search query
     * @param page  Page number
     * @param size  Page size
     * @return Page of search results
     */
    public Page<PetListItemDTO> searchPets(String query, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return petRepository.searchByNameSpeciesOrBreed(query, pageable)
                .map(this::convertToListItemDTO);
    }

    /**
     * Get featured/trending pets.
     * 
     * @param limit Maximum number of featured pets to return
     * @return List of featured pets
     */
    public List<PetListItemDTO> getFeaturedPets(int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return petRepository.findFeaturedPets(pageable)
                .stream()
                .map(this::convertToListItemDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get pets by seller.
     * 
     * @param sellerId Seller ID
     * @param page     Page number
     * @param size     Page size
     * @return Page of pet list items
     */
    public Page<PetListItemDTO> getPetsBySeller(String sellerId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return petRepository.findBySellerId(sellerId, pageable)
                .map(this::convertToListItemDTO);
    }

    /**
     * Create a new pet listing for a seller.
     * 
     * @param seller  Seller entity
     * @param petData Pet data from request
     * @return Created pet DTO
     */
    public PetDTO createPetListing(Seller seller, Pet petData) {
        // Validate pet listing data
        petListingValidator.validateForCreation(petData);

        // Set seller and save
        petData.setSeller(seller);
        petData.setAvailabilityStatus("available");
        if (petData.getStockQuantity() == null) {
            petData.setStockQuantity(1);
        }

        Pet savedPet = petRepository.save(petData);
        return convertToDTO(savedPet);
    }

    /**
     * Update an existing pet listing.
     * 
     * @param petId   Pet ID to update
     * @param petData Updated pet data
     * @return Updated pet DTO
     */
    public PetDTO updateListing(String petId, Pet petData) {
        // Validate pet listing data
        petListingValidator.validateForUpdate(petData);

        Pet existing = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet not found: " + petId));

        // Update fields
        if (petData.getName() != null && !petData.getName().trim().isEmpty()) {
            existing.setName(petData.getName());
        }
        if (petData.getSpecies() != null && !petData.getSpecies().trim().isEmpty()) {
            existing.setSpecies(petData.getSpecies());
        }
        if (petData.getBreed() != null) {
            existing.setBreed(petData.getBreed());
        }
        if (petData.getAge() != null) {
            existing.setAge(petData.getAge());
        }
        if (petData.getPrice() != null && petData.getPrice() >= 0) {
            existing.setPrice(petData.getPrice());
        }
        if (petData.getDescription() != null) {
            existing.setDescription(petData.getDescription());
        }
        if (petData.getHealthStatus() != null) {
            existing.setHealthStatus(petData.getHealthStatus());
        }
        if (petData.getStockQuantity() != null && petData.getStockQuantity() >= 0) {
            existing.setStockQuantity(petData.getStockQuantity());
        }

        Pet updated = petRepository.save(existing);
        return convertToDTO(updated);
    }

    /**
     * Archive/mark a pet as unavailable.
     * 
     * @param petId Pet ID to archive
     * @return Updated pet DTO
     */
    public PetDTO archivePet(String petId) {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new NotFoundException("Pet not found: " + petId));

        pet.setAvailabilityStatus("unavailable");
        Pet updated = petRepository.save(pet);
        return convertToDTO(updated);
    }

    /**
     * Convert Pet entity to detailed DTO with images.
     */
    private PetDTO convertToDTO(Pet pet) {
        PetDTO dto = new PetDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setAge(pet.getAge());
        dto.setPrice(pet.getPrice());
        dto.setDescription(pet.getDescription());
        dto.setHealthStatus(pet.getHealthStatus());
        dto.setAvailabilityStatus(pet.getAvailabilityStatus());
        dto.setStockQuantity(pet.getStockQuantity());
        dto.setSellerId(pet.getSellerId());
        dto.setCreatedAt(pet.getCreatedAt());
        dto.setUpdatedAt(pet.getUpdatedAt());

        // Load images
        List<String> imageUrls = petImageRepository.findByPetIdOrderByDisplayOrder(pet.getId())
                .stream()
                .map(PetImage::getImageUrl)
                .collect(Collectors.toList());
        dto.setImages(imageUrls);

        return dto;
    }

    /**
     * Convert Pet entity to list item DTO (lightweight).
     */
    private PetListItemDTO convertToListItemDTO(Pet pet) {
        PetListItemDTO dto = new PetListItemDTO();
        dto.setId(pet.getId());
        dto.setName(pet.getName());
        dto.setSpecies(pet.getSpecies());
        dto.setBreed(pet.getBreed());
        dto.setPrice(pet.getPrice());
        dto.setAvailabilityStatus(pet.getAvailabilityStatus());
        dto.setSellerId(pet.getSellerId());

        // Load primary image only
        petImageRepository.findPrimaryImageByPetId(pet.getId())
                .ifPresent(img -> dto.setPrimaryImage(img.getImageUrl()));

        return dto;
    }

    /**
     * Get statistics for dashboard.
     */
    @Transactional(readOnly = true)
    public long getAvailablePetsCount() {
        return petRepository.countByAvailabilityStatus("available");
    }
}
