package com.mady.bloodorgandonorsystem.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mady.bloodorgandonorsystem.domain.BloodUnit;

@Repository
public interface BloodUnitRepository extends JpaRepository<BloodUnit, UUID> {

    // E2: Blood unit history of a donor
    @Query("SELECT b FROM BloodUnit b WHERE b.donation.donor.donorCode = :donorCode ORDER BY b.collectedDate DESC")
    List<BloodUnit> findByDonorCodeOrderByCollectedDateDesc(@Param("donorCode") String donorCode);

    // E3: Protect donation deletion if it has blood units 
    boolean existsByDonation_Id(UUID donationId);
}
