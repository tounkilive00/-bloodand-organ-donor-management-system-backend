package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;
import com.mady.bloodorgandonorsystem.domain.Coordinator;
import com.mady.bloodorgandonorsystem.domain.DonationType;
import com.mady.bloodorgandonorsystem.repository.CollectionSiteRepository;
import com.mady.bloodorgandonorsystem.repository.CoordinatorRepository;
import com.mady.bloodorgandonorsystem.repository.DonationTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CoordinatorService {

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private DonationTypeRepository donationTypeRepository;

    @Autowired
    private CollectionSiteRepository collectionSiteRepository;

    public Coordinator saveCoordinator(Coordinator coordinator) {
        if (coordinator.getCollectionSite() != null && coordinator.getCollectionSite().getId() != null) {
            collectionSiteRepository.findById(coordinator.getCollectionSite().getId())
                    .ifPresent(coordinator::setCollectionSite);
        }
        if (coordinator.getDonationTypes() != null) {
            List<DonationType> resolvedTypes = new ArrayList<>();
            for (DonationType dt : coordinator.getDonationTypes()) {
                if (dt.getId() != null) {
                    donationTypeRepository.findById(dt.getId()).ifPresent(resolvedTypes::add);
                }
            }
            coordinator.setDonationTypes(resolvedTypes);
        }
        return coordinatorRepository.save(coordinator);
    }

    public List<Coordinator> getAllCoordinators() {
        return coordinatorRepository.findAll();
    }

    public Optional<Coordinator> getCoordinatorById(UUID id) {
        return coordinatorRepository.findById(id);
    }

    @Transactional
    public String updateCoordinator(UUID id, Coordinator updated) {
        Optional<Coordinator> existingOpt = coordinatorRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "The coordinator with that id does not exist";
        }
        Coordinator existing = existingOpt.get();
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        if (updated.getCollectionSite() != null && updated.getCollectionSite().getId() != null) {
            collectionSiteRepository.findById(updated.getCollectionSite().getId())
                    .ifPresent(existing::setCollectionSite);
        }
        coordinatorRepository.save(existing);
        return "Coordinator updated successfully";
    }

    @Transactional
    public String deleteCoordinator(UUID id) {
        if (!coordinatorRepository.existsById(id)) {
            return "The coordinator with that id does not exist";
        }
        coordinatorRepository.deleteById(id);
        return "Coordinator deleted successfully";
    }

    // C1: Coordinators by donation type (JPQL)
    public List<Coordinator> getCoordinatorsByDonationType(String name) {
        return coordinatorRepository.findByDonationTypeNameIgnoreCase(name);
    }

    // D3: Add one donation type to a coordinator
    @Transactional
    public String addDonationType(UUID coordinatorId, UUID donationTypeId) {
        Optional<Coordinator> coordOpt = coordinatorRepository.findById(coordinatorId);
        if (coordOpt.isEmpty()) {
            return "The coordinator with that id does not exist";
        }

        Optional<DonationType> typeOpt = donationTypeRepository.findById(donationTypeId);
        if (typeOpt.isEmpty()) {
            return "That donation type does not exist";
        }

        Coordinator coordinator = coordOpt.get();
        DonationType donationType = typeOpt.get();

        if (coordinator.getDonationTypes().contains(donationType)) {
            return "The coordinator already has that donation type";
        }

        coordinator.getDonationTypes().add(donationType);
        coordinatorRepository.save(coordinator);
        return "Donation type added to the coordinator";
    }

    // D4: Move a coordinator to another collection site
    @Transactional
    public String moveCollectionSite(UUID coordinatorId, int siteNumber) {
        Optional<Coordinator> coordOpt = coordinatorRepository.findById(coordinatorId);
        if (coordOpt.isEmpty()) {
            return "The coordinator with that id does not exist";
        }

        Optional<CollectionSite> siteOpt = collectionSiteRepository.findBySiteNumber(siteNumber);
        if (siteOpt.isEmpty()) {
            return "The collection site with that siteNumber does not exist";
        }

        CollectionSite targetSite = siteOpt.get();
        if (coordinatorRepository.existsByCollectionSite(targetSite)) {
            return "That collection site is taken by another coordinator";
        }

        Coordinator coordinator = coordOpt.get();
        coordinator.setCollectionSite(targetSite);
        coordinatorRepository.save(coordinator);
        return "Collection site changed successfully";
    }
}
