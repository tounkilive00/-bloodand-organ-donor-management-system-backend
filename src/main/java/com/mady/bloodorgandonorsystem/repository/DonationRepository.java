package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.Donation;
import com.mady.bloodorgandonorsystem.domain.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface DonationRepository extends JpaRepository<Donation, UUID> {

    // B2: Donations by status (DERIVED: enum parameter, OrderBy)
    List<Donation> findByStatusOrderByDonationDateAsc(DonationStatus status);

    // B3: Donations in a date range (DERIVED: Between, OrderBy)
    List<Donation> findByDonationDateBetweenOrderByDonationDateAsc(LocalDate start, LocalDate end);

    // D1: Check if coordinator already supervises a non-DEFERRED donation on date (DERIVED: existsBy, And, Not)
    boolean existsByCoordinator_IdAndDonationDateAndStatusNot(UUID coordinatorId, LocalDate donationDate, DonationStatus status);

    // F1: Donations per status (JPQL: GROUP BY, COUNT)
    @Query("SELECT d.status, COUNT(d) FROM Donation d GROUP BY d.status")
    List<Object[]> countDonationsPerStatus();

    // F2: Coordinator is absent (JPQL: UPDATE, @Modifying)
    @Modifying
    @Query("UPDATE Donation d SET d.status = :deferredStatus WHERE d.coordinator.id = :coordinatorId AND d.donationDate = :donationDate AND d.status <> :collectedStatus")
    int cancelDayForCoordinator(
            @Param("coordinatorId") UUID coordinatorId,
            @Param("donationDate") LocalDate donationDate,
            @Param("deferredStatus") DonationStatus deferredStatus,
            @Param("collectedStatus") DonationStatus collectedStatus
    );

    // Bonus dashboard counts (DERIVED)
    long countByDonationDate(LocalDate donationDate);

    long countByStatus(DonationStatus status);
}
