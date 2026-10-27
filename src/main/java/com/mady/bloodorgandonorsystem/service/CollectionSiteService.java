package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;
import com.mady.bloodorgandonorsystem.repository.CollectionSiteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class CollectionSiteService {

    @Autowired
    private CollectionSiteRepository collectionSiteRepository;

    public CollectionSite saveCollectionSite(CollectionSite collectionSite) {
        return collectionSiteRepository.save(collectionSite);
    }

    public List<CollectionSite> getAllCollectionSites() {
        return collectionSiteRepository.findAll();
    }

    public Optional<CollectionSite> getCollectionSiteById(UUID id) {
        return collectionSiteRepository.findById(id);
    }

    @Transactional
    public String updateCollectionSite(UUID id, CollectionSite updated) {
        Optional<CollectionSite> existingOpt = collectionSiteRepository.findById(id);
        if (existingOpt.isEmpty()) {
            return "The collection site with that id does not exist";
        }
        CollectionSite existing = existingOpt.get();
        existing.setName(updated.getName());
        existing.setSiteNumber(updated.getSiteNumber());
        collectionSiteRepository.save(existing);
        return "Collection site updated successfully";
    }

    @Transactional
    public String deleteCollectionSite(UUID id) {
        if (!collectionSiteRepository.existsById(id)) {
            return "The collection site with that id does not exist";
        }
        collectionSiteRepository.deleteById(id);
        return "Collection site deleted successfully";
    }

    // C2: Free collection sites (JPQL: NOT EXISTS subquery)
    public List<CollectionSite> getFreeCollectionSites() {
        return collectionSiteRepository.findFreeCollectionSites();
    }
}
