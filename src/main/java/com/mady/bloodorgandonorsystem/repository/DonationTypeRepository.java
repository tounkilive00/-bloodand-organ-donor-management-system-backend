package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.DonationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DonationTypeRepository extends JpaRepository<DonationType, UUID> {

    // C3: Donation types nobody holds (JPQL with IS EMPTY)
    @Query("SELECT dt FROM DonationType dt WHERE dt.coordinators IS EMPTY")
    List<DonationType> findUnusedDonationTypes();

    Optional<DonationType> findByNameIgnoreCase(String name);
}
