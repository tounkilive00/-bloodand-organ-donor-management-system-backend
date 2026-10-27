package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.BloodUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BloodUnitRepository extends JpaRepository<BloodUnit, UUID> {

    // E2: Blood unit history of a donor (JPQL with path navigation and ORDER BY)
    @Query("SELECT b FROM BloodUnit b WHERE b.donation.donor.donorCode = :donorCode ORDER BY b.collectedDate DESC")
    List<BloodUnit> findByDonorCodeOrderByCollectedDateDesc(@Param("donorCode") String donorCode);

    // E3: Protect donation deletion if it has blood units (DERIVED: existsByDonation)
    boolean existsByDonation_Id(UUID donationId);
}
