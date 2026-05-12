package com.petstore.repository;

import com.petstore.entity.Pet;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Pet entity.
 * 
 * Provides data access methods for querying pets by various filters.
 */
@Repository
public interface PetRepository extends JpaRepository<Pet, String> {

    /**
     * Find all pets with optional filters.
     */
    Page<Pet> findAll(Pageable pageable);

    /**
     * Find pets by species.
     */
    Page<Pet> findBySpecies(String species, Pageable pageable);

    /**
     * Find pets by breed.
     */
    Page<Pet> findByBreed(String breed, Pageable pageable);

    /**
     * Find pets by availability status.
     */
    Page<Pet> findByAvailabilityStatus(String status, Pageable pageable);

    /**
     * Find pets by seller ID.
     */
    Page<Pet> findBySellerId(String sellerId, Pageable pageable);

    /**
     * Find pets within price range.
     */
    @Query("SELECT p FROM Pet p WHERE p.price >= :minPrice AND p.price <= :maxPrice")
    Page<Pet> findByPriceRange(
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    /**
     * Search pets by name, species, or breed.
     */
    @Query("SELECT p FROM Pet p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.species) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
            "LOWER(p.breed) LIKE LOWER(CONCAT('%', :query, '%'))")
    Page<Pet> searchByNameSpeciesOrBreed(@Param("query") String query, Pageable pageable);

    /**
     * Find featured pets (random sample of available pets).
     */
    @Query(value = "SELECT p FROM Pet p WHERE p.availabilityStatus = 'available' ORDER BY RAND()")
    List<Pet> findFeaturedPets(Pageable pageable);

    /**
     * Find pets by multiple criteria (species AND price range).
     */
    @Query("SELECT p FROM Pet p WHERE " +
            "p.species = :species AND " +
            "p.price >= :minPrice AND " +
            "p.price <= :maxPrice")
    Page<Pet> findBySpeciesAndPriceRange(
            @Param("species") String species,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable);

    /**
     * Count available pets.
     */
    long countByAvailabilityStatus(String status);

    /**
     * Count pets by seller and availability status.
     */
    @Query("SELECT COUNT(p) FROM Pet p WHERE p.seller.id = :sellerId AND p.availabilityStatus = :status")
    Long countBySellerIdAndAvailabilityStatus(
            @Param("sellerId") String sellerId,
            @Param("status") String status);
}
