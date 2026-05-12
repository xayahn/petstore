package com.petstore.controller;

import com.petstore.dto.PetDTO;
import com.petstore.dto.PetListItemDTO;
import com.petstore.entity.Pet;
import com.petstore.entity.Seller;
import com.petstore.fixture.EntityFixtures;
import com.petstore.service.PetService;
import com.petstore.service.SellerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for PetController.
 * 
 * Tests REST endpoints with MockMvc (no server startup).
 */
@WebMvcTest(PetController.class)
@DisplayName("PetController Unit Tests")
class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetService petService;

    @MockBean
    private SellerService sellerService;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("Should list pets with 200 status")
    void testListPets() throws Exception {
        // Arrange
        PetListItemDTO pet1 = new PetListItemDTO("1", "Buddy", "Dog", "Labrador", 1000.00);
        PetListItemDTO pet2 = new PetListItemDTO("2", "Max", "Dog", "Shepherd", 950.00);

        when(petService.listPets(isNull(), isNull(), isNull(), isNull(), eq(0), eq(20), anyString()))
                .thenReturn(new PageImpl<>(Arrays.asList(pet1, pet2)));

        // Act & Assert
        mockMvc.perform(get("/api/pets"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].name", equalTo("Buddy")))
                .andExpect(jsonPath("$.content[1].name", equalTo("Max")))
                .andExpect(jsonPath("$.totalElements", equalTo(2)));
    }

    @Test
    @DisplayName("Should list pets with species filter")
    void testListPetsWithSpeciesFilter() throws Exception {
        // Arrange
        PetListItemDTO dog = new PetListItemDTO("1", "Buddy", "Dog", "Labrador", 1000.00);

        when(petService.listPets(eq("Dog"), isNull(), isNull(), isNull(), eq(0), eq(20), anyString()))
                .thenReturn(new PageImpl<>(Collections.singletonList(dog)));

        // Act & Assert
        mockMvc.perform(get("/api/pets?species=Dog"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].species", equalTo("Dog")));
    }

    @Test
    @DisplayName("Should list pets with price range filter")
    void testListPetsWithPriceRangeFilter() throws Exception {
        // Arrange
        PetListItemDTO expensive = new PetListItemDTO("1", "Buddy", "Dog", "Labrador", 1200.00);

        when(petService.listPets(isNull(), isNull(), eq(1000.00), eq(1500.00), eq(0), eq(20), anyString()))
                .thenReturn(new PageImpl<>(Collections.singletonList(expensive)));

        // Act & Assert
        mockMvc.perform(get("/api/pets?minPrice=1000&maxPrice=1500"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].price", equalTo(1200.00)));
    }

    @Test
    @DisplayName("Should search pets by query")
    void testSearchPets() throws Exception {
        // Arrange
        PetListItemDTO result = new PetListItemDTO("1", "Golden Buddy", "Dog", "Labrador", 1000.00);

        when(petService.searchPets("Buddy", 0, 20))
                .thenReturn(new PageImpl<>(Collections.singletonList(result)));

        // Act & Assert
        mockMvc.perform(get("/api/pets/search?q=Buddy"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name", containsString("Buddy")));
    }

    @Test
    @DisplayName("Should get featured pets")
    void testGetFeaturedPets() throws Exception {
        // Arrange
        PetListItemDTO featured1 = new PetListItemDTO("1", "Buddy", "Dog", "Labrador", 1000.00);
        PetListItemDTO featured2 = new PetListItemDTO("2", "Luna", "Cat", "Persian", 500.00);

        when(petService.getFeaturedPets(6))
                .thenReturn(Arrays.asList(featured1, featured2));

        // Act & Assert
        mockMvc.perform(get("/api/pets/featured"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name", equalTo("Buddy")))
                .andExpect(jsonPath("$[1].name", equalTo("Luna")));
    }

    @Test
    @DisplayName("Should get pet detail by ID")
    void testGetPetDetail() throws Exception {
        // Arrange
        PetDTO petDetail = new PetDTO("1", "Buddy", "Dog", "Labrador", 1000.00);
        petDetail.setDescription("A friendly golden dog");
        petDetail.setHealthStatus("healthy");
        petDetail.setAvailabilityStatus("available");
        petDetail.setImages(Arrays.asList("img1.jpg", "img2.jpg"));

        when(petService.getPetById("1")).thenReturn(petDetail);

        // Act & Assert
        mockMvc.perform(get("/api/pets/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", equalTo("Buddy")))
                .andExpect(jsonPath("$.description", equalTo("A friendly golden dog")))
                .andExpect(jsonPath("$.images", hasSize(2)));
    }

    @Test
    @DisplayName("Should return 404 for non-existent pet")
    void testGetPetDetailNotFound() throws Exception {
        // Arrange
        when(petService.getPetById("invalid")).thenThrow(new RuntimeException("Pet not found"));

        // Act & Assert
        mockMvc.perform(get("/api/pets/invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should support pagination")
    void testPagination() throws Exception {
        // Arrange
        PetListItemDTO pet = new PetListItemDTO("1", "Buddy", "Dog", "Labrador", 1000.00);

        when(petService.listPets(isNull(), isNull(), isNull(), isNull(), eq(1), eq(10), anyString()))
                .thenReturn(new PageImpl<>(Collections.singletonList(pet),
                        org.springframework.data.domain.PageRequest.of(1, 10), 15));

        // Act & Assert
        mockMvc.perform(get("/api/pets?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements", equalTo(15)))
                .andExpect(jsonPath("$.totalPages", equalTo(2)));
    }

    @Test
    @DisplayName("Should create pet listing for seller")
    void testCreatePetListing() throws Exception {
        // Arrange
        Pet petData = new Pet("Buddy", "Dog", "Labrador", 1000.00);
        petData.setDescription("A friendly dog");

        PetDTO createdDTO = new PetDTO("pet-1", "Buddy", "Dog", "Labrador", 1000.00);
        createdDTO.setDescription("A friendly dog");
        createdDTO.setAvailabilityStatus("available");
        createdDTO.setSellerId("seller-1");

        Seller seller = new Seller();
        seller.setId("seller-1");

        when(com.petstore.security.SecurityUtils.getCurrentUserId()).thenReturn("user-1");
        when(sellerService.getSellerByUserId("user-1")).thenReturn(seller);
        when(petService.createPetListing(any(Seller.class), any(Pet.class))).thenReturn(createdDTO);

        // Act & Assert
        mockMvc.perform(post("/api/pets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(petData)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", equalTo("pet-1")))
                .andExpect(jsonPath("$.name", equalTo("Buddy")));
    }

    @Test
    @DisplayName("Should update pet listing")
    void testUpdatePetListing() throws Exception {
        // Arrange
        Pet updateData = new Pet();
        updateData.setPrice(1200.00);

        PetDTO updatedDTO = new PetDTO("pet-1", "Buddy", "Dog", "Labrador", 1200.00);
        updatedDTO.setAvailabilityStatus("available");

        when(petService.updateListing("pet-1", updateData)).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(put("/api/pets/pet-1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo("pet-1")))
                .andExpect(jsonPath("$.price", comparesEqualTo(1200.00)));
    }

    @Test
    @DisplayName("Should update pet availability status")
    void testUpdatePetAvailability() throws Exception {
        // Arrange
        PetDTO updatedDTO = new PetDTO("pet-1", "Buddy", "Dog", "Labrador", 1000.00);
        updatedDTO.setAvailabilityStatus("unavailable");

        when(petService.updateListing(eq("pet-1"), any(Pet.class))).thenReturn(updatedDTO);

        // Act & Assert
        mockMvc.perform(patch("/api/pets/pet-1/availability")
                .param("status", "unavailable"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.availabilityStatus", equalTo("unavailable")));
    }

    @Test
    @DisplayName("Should return 404 when updating non-existent pet")
    void testUpdatePetNotFound() throws Exception {
        // Arrange
        Pet updateData = new Pet();
        updateData.setPrice(1200.00);

        when(petService.updateListing(eq("pet-999"), any(Pet.class)))
                .thenThrow(new RuntimeException("Pet not found"));

        // Act & Assert
        mockMvc.perform(put("/api/pets/pet-999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateData)))
                .andExpect(status().isBadRequest());
    }
}
