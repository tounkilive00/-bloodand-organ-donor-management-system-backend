package com.mady.bloodorgandonorsystem.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mady.bloodorgandonorsystem.domain.Donation;
import com.mady.bloodorgandonorsystem.domain.DonationStatus;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {


    List<Donation> findByStatusOrderByDonationDateAsc(DonationStatus status);

    
    List<Donation> findByDonationDateBetweenOrderByDonationDateAsc(LocalDate start, LocalDate end);

        boolean existsByCoordinator_IdAndDonationDateAndStatusNot(UUID coordinatorId, LocalDate donationDate, DonationStatus status);

    
    @Query("SELECT d.status, COUNT(d) FROM Donation d GROUP BY d.status")
    List<Object[]> countDonationsPerStatus();

    @Modifying
    @Query("UPDATE Donation d SET d.status = :deferredStatus WHERE d.coordinator.id = :coordinatorId AND d.donationDate = :donationDate AND d.status <> :collectedStatus")
    int cancelDayForCoordinator(
            @Param("coordinatorId") UUID coordinatorId,
            @Param("donationDate") LocalDate donationDate,
            @Param("deferredStatus") DonationStatus deferredStatus,
            @Param("collectedStatus") DonationStatus collectedStatus
    );

    // Bonus dashboard counts 
    long countByDonationDate(LocalDate donationDate);

    long countByStatus(DonationStatus status);
}
