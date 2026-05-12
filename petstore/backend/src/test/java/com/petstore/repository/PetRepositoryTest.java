package com.petstore.repository;

import com.petstore.fixture.EntityFixtures;
import com.petstore.integration.DatabaseSetup;
import com.petstore.integration.IntegrationTestBase;
import com.petstore.entity.Pet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for PetRepository.
 * 
 * Tests custom query methods with real database (Testcontainers PostgreSQL).
 */
@DisplayName("PetRepository Integration Tests")
class PetRepositoryTest extends IntegrationTestBase {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private DatabaseSetup databaseSetup;

    @BeforeEach
    void setUp() {
        databaseSetup.truncateAllTables();
    }

    @Test
    @DisplayName("Should save and retrieve pet")
    void testSaveAndRetrievePet() {
        // Arrange
        Pet pet = EntityFixtures.aPet()
                .withName("Fluffy")
                .withSpecies("Cat")
                .withPrice(500.00)
                .build();

        // Act
        Pet saved = petRepository.save(new Pet(pet.getName(), pet.getSpecies(), pet.getBreed(), pet.getPrice()));
        Optional<Pet> retrieved = petRepository.findById(saved.getId());

        // Assert
        assertTrue(retrieved.isPresent());
        assertEquals(saved.getId(), retrieved.get().getId());
        assertEquals("Fluffy", retrieved.get().getName());
    }

    @Test
    @DisplayName("Should find pets by species")
    void testFindBySpecies() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Max", "Dog", "Labrador", 1100.00),
                new Pet("Whiskers", "Cat", "Persian", 500.00));

        // Act
        Page<Pet> dogsPage = petRepository.findBySpecies("Dog", PageRequest.of(0, 10));
        Page<Pet> catsPage = petRepository.findBySpecies("Cat", PageRequest.of(0, 10));

        // Assert
        assertThat(dogsPage.getContent(), hasSize(2));
        assertThat(catsPage.getContent(), hasSize(1));
        assertThat(dogsPage.getContent(), everyItem(hasProperty("species", equalTo("Dog"))));
    }

    @Test
    @DisplayName("Should find pets by breed")
    void testFindByBreed() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Charlie", "Dog", "Golden Retriever", 1150.00),
                new Pet("Max", "Dog", "Labrador", 1100.00));

        // Act
        Page<Pet> goldenRetrievers = petRepository.findByBreed("Golden Retriever", PageRequest.of(0, 10));

        // Assert
        assertThat(goldenRetrievers.getContent(), hasSize(2));
        assertThat(goldenRetrievers.getContent(), everyItem(hasProperty("breed", equalTo("Golden Retriever"))));
    }

    @Test
    @DisplayName("Should find pets by availability status")
    void testFindByAvailabilityStatus() {
        // Arrange
        Pet available = new Pet("Buddy", "Dog", "Golden Retriever", 1200.00);
        available.setAvailabilityStatus("available");

        Pet unavailable = new Pet("Max", "Dog", "Labrador", 1100.00);
        unavailable.setAvailabilityStatus("unavailable");

        savePets(available, unavailable);

        // Act
        Page<Pet> availablePets = petRepository.findByAvailabilityStatus("available", PageRequest.of(0, 10));

        // Assert
        assertThat(availablePets.getContent(), hasSize(1));
        assertEquals("Buddy", availablePets.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should find pets by price range")
    void testFindByPriceRange() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Max", "Dog", "Labrador", 900.00),
                new Pet("Whiskers", "Cat", "Persian", 500.00));

        // Act
        Page<Pet> expensive = petRepository.findByPriceRange(1000.00, 1500.00, PageRequest.of(0, 10));
        Page<Pet> cheap = petRepository.findByPriceRange(400.00, 700.00, PageRequest.of(0, 10));

        // Assert
        assertThat(expensive.getContent(), hasSize(1));
        assertThat(cheap.getContent(), hasSize(1));
        assertEquals("Whiskers", cheap.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should search pets by name")
    void testSearchByName() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Buddy's Friend", "Dog", "Labrador", 1100.00),
                new Pet("Max", "Dog", "Labrador", 950.00));

        // Act
        Page<Pet> results = petRepository.searchByNameSpeciesOrBreed("Buddy", PageRequest.of(0, 10));

        // Assert
        assertThat(results.getContent(), hasSize(2));
    }

    @Test
    @DisplayName("Should search pets by species")
    void testSearchBySpecies() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Max", "Dog", "Labrador", 1100.00),
                new Pet("Whiskers", "Cat", "Persian", 500.00));

        // Act
        Page<Pet> dogResults = petRepository.searchByNameSpeciesOrBreed("Dog", PageRequest.of(0, 10));
        Page<Pet> catResults = petRepository.searchByNameSpeciesOrBreed("Cat", PageRequest.of(0, 10));

        // Assert
        assertThat(dogResults.getContent(), hasSize(2));
        assertThat(catResults.getContent(), hasSize(1));
    }

    @Test
    @DisplayName("Should find pets by species and price range")
    void testFindBySpeciesAndPriceRange() {
        // Arrange
        savePets(
                new Pet("Buddy", "Dog", "Golden Retriever", 1200.00),
                new Pet("Max", "Dog", "Labrador", 900.00),
                new Pet("Whiskers", "Cat", "Persian", 500.00));

        // Act
        Page<Pet> expensiveDogs = petRepository.findBySpeciesAndPriceRange("Dog", 1000.00, 1500.00,
                PageRequest.of(0, 10));

        // Assert
        assertThat(expensiveDogs.getContent(), hasSize(1));
        assertEquals("Buddy", expensiveDogs.getContent().get(0).getName());
    }

    @Test
    @DisplayName("Should count available pets")
    void testCountByAvailabilityStatus() {
        // Arrange
        Pet available = new Pet("Buddy", "Dog", "Golden Retriever", 1200.00);
        available.setAvailabilityStatus("available");

        Pet unavailable = new Pet("Max", "Dog", "Labrador", 1100.00);
        unavailable.setAvailabilityStatus("unavailable");

        savePets(available, unavailable);

        // Act
        long count = petRepository.countByAvailabilityStatus("available");

        // Assert
        assertEquals(1L, count);
    }

    @Test
    @DisplayName("Should support pagination")
    void testPagination() {
        // Arrange
        for (int i = 0; i < 15; i++) {
            Pet pet = new Pet("Pet " + i, "Dog", "Breed " + i, 100.00 * (i + 1));
            petRepository.save(pet);
        }

        // Act
        Page<Pet> page1 = petRepository.findAll(PageRequest.of(0, 10));
        Page<Pet> page2 = petRepository.findAll(PageRequest.of(1, 10));

        // Assert
        assertEquals(2, page1.getTotalPages());
        assertThat(page1.getContent(), hasSize(10));
        assertThat(page2.getContent(), hasSize(5));
    }

    // Helper method
    private void savePets(Pet... pets) {
        for (Pet pet : pets) {
            petRepository.save(pet);
        }
    }
}
