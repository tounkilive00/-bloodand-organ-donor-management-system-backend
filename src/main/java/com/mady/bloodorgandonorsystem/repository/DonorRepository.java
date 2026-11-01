package com.mady.bloodorgandonorsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mady.bloodorgandonorsystem.domain.Donor;

@Repository
public interface DonorRepository extends JpaRepository<Donor, UUID> {

    
    List<Donor> findByLastNameIgnoreCaseOrderByFirstNameAsc(String lastName);

    
    @Query("SELECT DISTINCT d.donor FROM Donation d WHERE d.coordinator.id = :coordinatorId")
    List<Donor> findDistinctDonorsByCoordinatorId(@Param("coordinatorId") UUID coordinatorId);

    
    Optional<Donor> findByDonorCode(String donorCode);
}
