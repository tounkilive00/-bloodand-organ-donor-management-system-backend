package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.Donor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DonorRepository extends JpaRepository<Donor, UUID> {

    // B1: Find donors by lastName (DERIVED: IgnoreCase, OrderBy)
    List<Donor> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    // C4: Donors of a coordinator (JPQL with DISTINCT and path navigation)
    @Query("SELECT DISTINCT d.donor FROM Donation d WHERE d.coordinator.id = :coordinatorId")
    List<Donor> findDistinctDonorsByCoordinatorId(@Param("coordinatorId") UUID coordinatorId);

    // Helper: find by donorCode
    Optional<Donor> findByDonorCode(String donorCode);
}
