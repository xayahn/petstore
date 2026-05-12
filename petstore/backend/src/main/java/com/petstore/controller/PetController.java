package com.petstore.controller;

import com.petstore.dto.PetDTO;
import com.petstore.dto.PetListItemDTO;
import com.petstore.entity.Pet;
import com.petstore.service.PetService;
import com.petstore.service.SellerService;
import layug.exception.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Pet operations.
 * 
 * Public endpoints for browsing pet catalog.
 * No authentication required for listing and searching.
 */
@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet catalog and browsing endpoints")
public class PetController {

    private final PetService petService;
    private final SellerService sellerService;

    public PetController(PetService petService, SellerService sellerService) {
        this.petService = petService;
        this.sellerService = sellerService;
    }

    /**
     * List all pets with optional filters and pagination.
     * 
     * GET /api/pets?species=Dog&minPrice=100&maxPrice=2000&page=0&size=20
     * 
     * @param species  Optional filter by species
     * @param breed    Optional filter by breed
     * @param minPrice Optional minimum price filter
     * @param maxPrice Optional maximum price filter
     * @param page     Page number (0-indexed, default 0)
     * @param size     Page size (default 20)
     * @param sortBy   Sort field: name, price, created_at (default: created_at)
     * @return Page of pet list items
     */
    @GetMapping
    @Operation(summary = "List pets", description = "Get paginated list of pets with optional filters", responses = {
            @ApiResponse(responseCode = "200", description = "List of pets", content = @Content(schema = @Schema(implementation = Page.class)))
    })
    public ResponseEntity<Page<PetListItemDTO>> listPets(
            @RequestParam(required = false) String species,
            @RequestParam(required = false) String breed,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "created_at") String sortBy) {

        Page<PetListItemDTO> pets = petService.listPets(species, breed, minPrice, maxPrice, page, size, sortBy);
        return ResponseEntity.ok(pets);
    }

    /**
     * Search pets by query string.
     * 
     * GET /api/pets/search?q=golden+retriever&page=0&size=20
     * 
     * @param q    Search query (searches name, species, breed)
     * @param page Page number (0-indexed, default 0)
     * @param size Page size (default 20)
     * @return Page of search results
     */
    @GetMapping("/search")
    @Operation(summary = "Search pets", description = "Search for pets by name, species, or breed")
    public ResponseEntity<Page<PetListItemDTO>> searchPets(
            @RequestParam String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Page<PetListItemDTO> results = petService.searchPets(q, page, size);
        return ResponseEntity.ok(results);
    }

    /**
     * Get featured/trending pets.
     * 
     * GET /api/pets/featured?limit=6
     * 
     * @param limit Maximum number of featured pets (default 6)
     * @return List of featured pets
     */
    @GetMapping("/featured")
    @Operation(summary = "Get featured pets", description = "Get a curated list of featured pets for homepage")
    public ResponseEntity<List<PetListItemDTO>> getFeaturedPets(
            @RequestParam(defaultValue = "6") int limit) {

        List<PetListItemDTO> featured = petService.getFeaturedPets(limit);
        return ResponseEntity.ok(featured);
    }

    /**
     * Get pet detail by ID.
     * 
     * GET /api/pets/{id}
     * 
     * @param id Pet ID
     * @return Complete pet details with all images
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get pet detail", description = "Get complete details of a specific pet including all images", responses = {
            @ApiResponse(responseCode = "200", description = "Pet details", content = @Content(schema = @Schema(implementation = PetDTO.class))),
            @ApiResponse(responseCode = "404", description = "Pet not found")
    })
    public ResponseEntity<PetDTO> getPetDetail(@PathVariable String id) {
        try {
            PetDTO pet = petService.getPetById(id);
            return ResponseEntity.ok(pet);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Create a new pet listing (seller only).
     * 
     * POST /api/pets
     * Required: name, species, price
     * Optional: breed, age, description, healthStatus, stockQuantity
     * 
     * @param petData Pet data for new listing
     * @return Created pet DTO with HTTP 201
     */
    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Create pet listing", description = "Create a new pet listing (seller only)")
    public ResponseEntity<PetDTO> createPetListing(@RequestBody Pet petData) {
        try {
            String currentUserId = com.petstore.security.SecurityUtils.getCurrentUserId();
            com.petstore.entity.Seller seller = sellerService.getSellerByUserId(currentUserId);

            PetDTO created = petService.createPetListing(seller, petData);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update a pet listing (seller only).
     * 
     * PUT /api/pets/{id}
     * 
     * @param id      Pet ID
     * @param petData Updated pet data
     * @return Updated pet DTO
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update pet listing", description = "Update an existing pet listing (seller only)")
    public ResponseEntity<PetDTO> updatePetListing(
            @PathVariable String id,
            @RequestBody Pet petData) {
        try {
            PetDTO updated = petService.updateListing(id, petData);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Update pet availability status (seller only).
     * 
     * PATCH /api/pets/{id}/availability
     * Body: { "availabilityStatus": "available" or "unavailable" }
     * 
     * @param id     Pet ID
     * @param status New availability status
     * @return Updated pet DTO
     */
    @PatchMapping("/{id}/availability")
    @PreAuthorize("hasRole('SELLER')")
    @Operation(summary = "Update pet availability", description = "Update availability status of a pet listing (seller only)")
    public ResponseEntity<PetDTO> updatePetAvailability(
            @PathVariable String id,
            @RequestParam String status) {
        try {
            Pet petUpdate = new Pet();
            petUpdate.setAvailabilityStatus(status);
            PetDTO updated = petService.updateListing(id, petUpdate);
            return ResponseEntity.ok(updated);
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Health check endpoint for monitoring.
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Pet service is healthy");
    }
}
