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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

/**
 * Unit tests for PetService.
 * 
 * Tests business logic with mocked repository layer.
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("PetService Unit Tests")
class PetServiceTest {

    @Mock
    private PetRepository petRepository;

    @Mock
    private PetImageRepository petImageRepository;

    @Mock
    private PetListingValidator petListingValidator;

    private PetService petService;

    @BeforeEach
    void setUp() {
        petService = new PetService(petRepository, petImageRepository, petListingValidator);
    }

    @Test
    @DisplayName("Should get pet by ID with images")
    void testGetPetById() {
        // Arrange
        Pet pet = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        pet.setId("pet-123");

        when(petRepository.findById("pet-123")).thenReturn(Optional.of(pet));
        when(petImageRepository.findByPetIdOrderByDisplayOrder("pet-123"))
                .thenReturn(Arrays.asList(
                        new PetImage("pet-123", "https://example.com/image1.jpg", 0),
                        new PetImage("pet-123", "https://example.com/image2.jpg", 1)));

        // Act
        PetDTO result = petService.getPetById("pet-123");

        // Assert
        assertNotNull(result);
        assertEquals("Buddy", result.getName());
        assertThat(result.getImages(), hasSize(2));
    }

    @Test
    @DisplayName("Should throw when pet not found")
    void testGetPetByIdNotFound() {
        // Arrange
        when(petRepository.findById("invalid")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> petService.getPetById("invalid"));
    }

    @Test
    @DisplayName("Should list all pets with pagination")
    void testListPetsNoFilters() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        pet1.setId("pet-1");
        Pet pet2 = new Pet("Max", "Dog", "Shepherd", 950.00);
        pet2.setId("pet-2");
        Page<Pet> page = new PageImpl<>(Arrays.asList(pet1, pet2), PageRequest.of(0, 10), 2);

        when(petRepository.findAll(any(Pageable.class))).thenReturn(page);
        when(petImageRepository.findPrimaryImageByPetId("pet-1"))
                .thenReturn(Optional.of(new PetImage("pet-1", "https://example.com/img1.jpg", 0)));
        when(petImageRepository.findPrimaryImageByPetId("pet-2"))
                .thenReturn(Optional.of(new PetImage("pet-2", "https://example.com/img2.jpg", 0)));

        // Act
        Page<PetListItemDTO> result = petService.listPets(null, null, null, null, 0, 10, "created_at");

        // Assert
        assertThat(result.getContent(), hasSize(2));
        assertEquals(2, result.getTotalElements());
    }

    @Test
    @DisplayName("Should search pets by query")
    void testSearchPets() {
        // Arrange
        Pet pet = new Pet("Golden Buddy", "Dog", "Golden Retriever", 1200.00);
        pet.setId("pet-1");
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet), PageRequest.of(0, 10), 1);

        when(petRepository.searchByNameSpeciesOrBreed("Buddy", PageRequest.of(0, 10)))
                .thenReturn(page);
        when(petImageRepository.findPrimaryImageByPetId("pet-1"))
                .thenReturn(Optional.empty());

        // Act
        Page<PetListItemDTO> result = petService.searchPets("Buddy", 0, 10);

        // Assert
        assertThat(result.getContent(), hasSize(1));
        assertEquals("Golden Buddy", result.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should get featured pets")
    void testGetFeaturedPets() {
        // Arrange
        Pet pet1 = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        pet1.setId("pet-1");
        Pet pet2 = new Pet("Luna", "Cat", "Persian", 500.00);
        pet2.setId("pet-2");

        when(petRepository.findFeaturedPets(any()))
                .thenReturn(Arrays.asList(pet1, pet2));
        when(petImageRepository.findPrimaryImageByPetId(anyString()))
                .thenReturn(Optional.empty());

        // Act
        List<PetListItemDTO> result = petService.getFeaturedPets(5);

        // Assert
        assertThat(result, hasSize(2));
    }

    @Test
    @DisplayName("Should filter by species and price")
    void testListPetsWithFilters() {
        // Arrange
        Pet pet = new Pet("Buddy", "Dog", "Labrador", 1200.00);
        pet.setId("pet-1");
        Page<Pet> page = new PageImpl<>(Collections.singletonList(pet), PageRequest.of(0, 10), 1);

        when(petRepository.findBySpeciesAndPriceRange("Dog", 1000.00, 1500.00, any()))
                .thenReturn(page);
        when(petImageRepository.findPrimaryImageByPetId("pet-1"))
                .thenReturn(Optional.empty());

        // Act
        Page<PetListItemDTO> result = petService.listPets("Dog", null, 1000.00, 1500.00, 0, 10, "price");

        // Assert
        assertThat(result.getContent(), hasSize(1));
        assertThat(result.getContent().get(0), hasProperty("species", equalTo("Dog")));
    }

    @Test
    @DisplayName("Should get available pets count")
    void testGetAvailablePetsCount() {
        // Arrange
        when(petRepository.countByAvailabilityStatus("available")).thenReturn(5L);

        // Act
        long count = petService.getAvailablePetsCount();

        // Assert
        assertEquals(5L, count);
    }

    @Test
    @DisplayName("Should create pet listing for seller")
    void testCreatePetListing() {
        // Arrange
        Seller seller = new Seller();
        seller.setId("seller-1");
        seller.setBusinessName("Test Pet Shop");

        Pet petData = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        petData.setDescription("A friendly golden retriever");

        Pet savedPet = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        savedPet.setId("pet-1");
        savedPet.setSeller(seller);
        savedPet.setAvailabilityStatus("available");
        savedPet.setStockQuantity(1);

        when(petRepository.save(any(Pet.class))).thenReturn(savedPet);
        when(petImageRepository.findByPetIdOrderByDisplayOrder("pet-1"))
                .thenReturn(Collections.emptyList());

        // Act
        PetDTO result = petService.createPetListing(seller, petData);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo("pet-1"));
        assertThat(result.getName(), equalTo("Buddy"));
        assertThat(result.getAvailabilityStatus(), equalTo("available"));
    }

    @Test
    @DisplayName("Should update pet listing")
    void testUpdatePetListing() {
        // Arrange
        Pet existing = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        existing.setId("pet-1");
        existing.setAvailabilityStatus("available");

        Pet updateData = new Pet();
        updateData.setPrice(1200.00);
        updateData.setDescription("Updated description");

        Pet updated = new Pet("Buddy", "Dog", "Labrador", 1200.00);
        updated.setId("pet-1");
        updated.setDescription("Updated description");
        updated.setAvailabilityStatus("available");

        when(petRepository.findById("pet-1")).thenReturn(Optional.of(existing));
        when(petRepository.save(any(Pet.class))).thenReturn(updated);
        when(petImageRepository.findByPetIdOrderByDisplayOrder("pet-1"))
                .thenReturn(Collections.emptyList());

        // Act
        PetDTO result = petService.updateListing("pet-1", updateData);

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo("pet-1"));
        assertThat(result.getPrice(), comparesEqualTo(1200.00));
    }

    @Test
    @DisplayName("Should throw NotFoundException when updating non-existent pet")
    void testUpdatePetListingNotFound() {
        // Arrange
        Pet updateData = new Pet();
        updateData.setPrice(1200.00);

        when(petRepository.findById("pet-999")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> {
            petService.updateListing("pet-999", updateData);
        });
    }

    @Test
    @DisplayName("Should archive pet listing")
    void testArchivePet() {
        // Arrange
        Pet pet = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        pet.setId("pet-1");
        pet.setAvailabilityStatus("available");

        Pet archived = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        archived.setId("pet-1");
        archived.setAvailabilityStatus("unavailable");

        when(petRepository.findById("pet-1")).thenReturn(Optional.of(pet));
        when(petRepository.save(any(Pet.class))).thenReturn(archived);
        when(petImageRepository.findByPetIdOrderByDisplayOrder("pet-1"))
                .thenReturn(Collections.emptyList());

        // Act
        PetDTO result = petService.archivePet("pet-1");

        // Assert
        assertThat(result, notNullValue());
        assertThat(result.getAvailabilityStatus(), equalTo("unavailable"));
    }
}
