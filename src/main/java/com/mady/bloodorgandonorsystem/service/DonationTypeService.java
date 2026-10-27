package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.DonationType;
import com.mady.bloodorgandonorsystem.repository.DonationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonationTypeService {

    @Autowired
    private DonationTypeRepository donationTypeRepository;

    public DonationType saveDonationType(DonationType donationType) {
        return donationTypeRepository.save(donationType);
    }

    public List<DonationType> getAllDonationTypes() {
        return donationTypeRepository.findAll();
    }

    public Optional<DonationType> getDonationTypeById(UUID id) {
        return donationTypeRepository.findById(id);
    }

    @Transactional
    public String updateDonationType(UUID id, DonationType updated) {
        Optional<DonationType> existingOpt = donationTypeRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "The donation type with that id does not exist";
        }
        DonationType existing = existingOpt.get();
        existing.setName(updated.getName());
        donationTypeRepository.save(existing);
        return "Donation type updated successfully";
    }

    @Transactional
    public String deleteDonationType(UUID id) {
        if (!donationTypeRepository.existsById(id)) {
            return "The donation type with that id does not exist";
        }
        donationTypeRepository.deleteById(id);
        return "Donation type deleted successfully";
    }

    // C3: Donation types nobody holds (JPQL: IS EMPTY)
    public List<DonationType> getUnusedDonationTypes() {
        return donationTypeRepository.findUnusedDonationTypes();
    }
}
