package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.Coordinator;
import com.mady.bloodorgandonorsystem.domain.Donation;
import com.mady.bloodorgandonorsystem.domain.DonationStatus;
import com.mady.bloodorgandonorsystem.domain.Donor;
import com.mady.bloodorgandonorsystem.repository.BloodUnitRepository;
import com.mady.bloodorgandonorsystem.repository.CoordinatorRepository;
import com.mady.bloodorgandonorsystem.repository.DonationRepository;
import com.mady.bloodorgandonorsystem.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonationService {

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    // D1: Check if coordinator already supervises a non-DEFERRED donation on date (DERIVED)
    public boolean isCoordinatorSupervising(UUID coordinatorId, LocalDate date) {
        return donationRepository.existsByCoordinator_IdAndDonationDateAndStatusNot(
                coordinatorId, date, DonationStatus.DEFERRED);
    }

    public Donation saveDonation(Donation donation) {
        if (donation.getDonor() != null && donation.getDonor().getId() != null) {
            donorRepository.findById(donation.getDonor().getId()).ifPresent(donation::setDonor);
        }
        if (donation.getCoordinator() != null && donation.getCoordinator().getId() != null) {
            coordinatorRepository.findById(donation.getCoordinator().getId()).ifPresent(donation::setCoordinator);
        }
        return donationRepository.save(donation);
    }

    public List<Donation> getAllDonations() {
        return donationRepository.findAll();
    }

    public Optional<Donation> getDonationById(UUID id) {
        return donationRepository.findById(id);
    }

    // A2 & D2: general PUT update must no longer change the status; it only changes in PATCH status
    @Transactional
    public String updateDonation(UUID id, Donation updated) {
        Optional<Donation> existingOpt = donationRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "The donation with that id does not exist";
        }
        Donation existing = existingOpt.get();
        existing.setDonationDate(updated.getDonationDate());
        existing.setUrgency(updated.getUrgency());
        if (updated.getDonor() != null && updated.getDonor().getId() != null) {
            donorRepository.findById(updated.getDonor().getId()).ifPresent(existing::setDonor);
        }
        if (updated.getCoordinator() != null && updated.getCoordinator().getId() != null) {
            coordinatorRepository.findById(updated.getCoordinator().getId()).ifPresent(existing::setCoordinator);
        }
        // status is intentionally preserved (not modified) as per D2 rule
        donationRepository.save(existing);
        return "Donation updated successfully";
    }

    // A2 & E3: Delete and protect (DERIVED existsByDonation)
    @Transactional
    public String deleteDonation(UUID id) {
        if (!donationRepository.existsById(id)) {
            return "The donation with that id does not exist";
        }
        if (bloodUnitRepository.existsByDonation_Id(id)) {
            return "That donation has blood units, delete them first";
        }
        donationRepository.deleteById(id);
        return "Donation deleted successfully";
    }

    // B2: Donations by status (DERIVED)
    public List<Donation> findByStatus(DonationStatus status) {
        return donationRepository.findByStatusOrderByDonationDateAsc(status);
    }

    // B3: Donations in date range (DERIVED)
    public List<Donation> findBetween(LocalDate start, LocalDate end) {
        return donationRepository.findByDonationDateBetweenOrderByDonationDateAsc(start, end);
    }

    // D2: Status flow
    @Transactional
    public String updateStatus(UUID id, DonationStatus newStatus) {
        Optional<Donation> opt = donationRepository.findById(id);
        if (opt.isEmpty()) {
            return "The donation with that id does not exist";
        }

        Donation donation = opt.get();
        DonationStatus current = donation.getStatus();

        boolean allowed = false;
        if (current == DonationStatus.REGISTERED) {
            if (newStatus == DonationStatus.SCREENED || newStatus == DonationStatus.DEFERRED) {
                allowed = true;
            }
        } else if (current == DonationStatus.SCREENED) {
            if (newStatus == DonationStatus.COLLECTED || newStatus == DonationStatus.DEFERRED) {
                allowed = true;
            }
        }

        if (!allowed) {
            return "Cannot change status from " + current + " to " + newStatus;
        }

        donation.setStatus(newStatus);
        donationRepository.save(donation);
        return "Donation status updated successfully";
    }

    // F1: Donations per status (JPQL)
    public List<Object[]> getStatsByStatus() {
        return donationRepository.countDonationsPerStatus();
    }

    // F2: Coordinator is absent (JPQL: UPDATE, @Modifying, @Transactional)
    @Transactional
    public String cancelDay(UUID coordinatorId, LocalDate date) {
        int cancelled = donationRepository.cancelDayForCoordinator(
                coordinatorId, date, DonationStatus.DEFERRED, DonationStatus.COLLECTED);
        return cancelled + " donations cancelled";
    }

    // Bonus +5: Donations page by page
    public Page<Donation> getDonationsPage(Pageable pageable) {
        return donationRepository.findAll(pageable);
    }
}
