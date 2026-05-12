package com.petstore.repository;

import com.petstore.entity.PetImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for PetImage entity.
 */
@Repository
public interface PetImageRepository extends JpaRepository<PetImage, String> {

    /**
     * Find all images for a pet, ordered by display order.
     */
    @Query("SELECT pi FROM PetImage pi WHERE pi.petId = :petId ORDER BY pi.displayOrder ASC")
    List<PetImage> findByPetIdOrderByDisplayOrder(@Param("petId") String petId);

    /**
     * Find the primary image (display order 0) for a pet.
     */
    @Query("SELECT pi FROM PetImage pi WHERE pi.petId = :petId AND pi.displayOrder = 0")
    Optional<PetImage> findPrimaryImageByPetId(@Param("petId") String petId);

    /**
     * Delete all images for a pet.
     */
    void deleteByPetId(String petId);

    /**
     * Count images for a pet.
     */
    long countByPetId(String petId);
}
