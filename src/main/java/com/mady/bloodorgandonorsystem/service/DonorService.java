package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.Donor;
import com.mady.bloodorgandonorsystem.repository.CoordinatorRepository;
import com.mady.bloodorgandonorsystem.repository.DonorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DonorService {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    public Donor saveDonor(Donor donor) {
        return donorRepository.save(donor);
    }

    public List<Donor> getAllDonors() {
        return donorRepository.findAll();
    }

    public Optional<Donor> getDonorById(UUID id) {
        return donorRepository.findById(id);
    }

    @Transactional
    public String updateDonor(UUID id, Donor updated) {
        Optional<Donor> existingOpt = donorRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "The donor with that id does not exist";
        }
        Donor existing = existingOpt.get();
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setDonorCode(updated.getDonorCode());
        existing.setBloodType(updated.getBloodType());
        existing.setPhone(updated.getPhone());
        donorRepository.save(existing);
        return "Donor updated successfully";
    }

    @Transactional
    public String deleteDonor(UUID id) {
        if (!donorRepository.existsById(id)) {
            return "The donor with that id does not exist";
        }
        donorRepository.deleteById(id);
        return "Donor deleted successfully";
    }

    // B1: Find donors by lastName (DERIVED: IgnoreCase, OrderBy)
    public List<Donor> findByLastName(String lastName) {
        return donorRepository.findByLastNameIgnoreCaseOrderByFirstNameAsc(lastName);
    }

    // C4: Donors of a coordinator (JPQL: DISTINCT, path navigation)
    public Optional<List<Donor>> getDonorsOfCoordinator(UUID coordinatorId) {
        if (!coordinatorRepository.existsById(coordinatorId)) {
            return Optional.empty();
        }
        return Optional.of(donorRepository.findDistinctDonorsByCoordinatorId(coordinatorId));
    }
}
