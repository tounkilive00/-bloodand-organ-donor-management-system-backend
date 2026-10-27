package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.BloodUnit;
import com.mady.bloodorgandonorsystem.domain.Donation;
import com.mady.bloodorgandonorsystem.domain.DonationStatus;
import com.mady.bloodorgandonorsystem.repository.BloodUnitRepository;
import com.mady.bloodorgandonorsystem.repository.DonationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BloodUnitService {

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Autowired
    private DonationRepository donationRepository;

    // E1: Write a blood unit
    @Transactional
    public String saveBloodUnit(UUID donationId, BloodUnit bloodUnit) {
        Optional<Donation> donationOpt = donationRepository.findById(donationId);
        if (donationOpt.isEmpty()) {
            return "The donation with that id does not exist";
        }

        Donation donation = donationOpt.get();
        if (donation.getStatus() != DonationStatus.COLLECTED) {
            return "Blood units are only for COLLECTED donations";
        }

        bloodUnit.setCollectedDate(LocalDate.now());
        bloodUnit.setDonation(donation);
        bloodUnitRepository.save(bloodUnit);
        return "Blood unit saved successfully";
    }

    public List<BloodUnit> getAllBloodUnits() {
        return bloodUnitRepository.findAll();
    }

    public Optional<BloodUnit> getBloodUnitById(UUID id) {
        return bloodUnitRepository.findById(id);
    }

    // E2: Blood unit history of a donor (JPQL: path navigation, ORDER BY)
    public List<BloodUnit> getBloodUnitsByDonorCode(String donorCode) {
        return bloodUnitRepository.findByDonorCodeOrderByCollectedDateDesc(donorCode);
    }

    // E3: Delete blood unit
    @Transactional
    public String deleteBloodUnit(UUID id) {
        if (!bloodUnitRepository.existsById(id)) {
            return "The blood unit with that id does not exist";
        }
        bloodUnitRepository.deleteById(id);
        return "Blood unit deleted successfully";
    }
}
