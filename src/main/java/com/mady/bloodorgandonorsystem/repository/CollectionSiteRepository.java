package com.mady.bloodorgandonorsystem.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;

@Repository
public interface CollectionSiteRepository extends JpaRepository<CollectionSite, UUID> {

    //  Free collection sites 
    @Query("SELECT cs FROM CollectionSite cs WHERE NOT EXISTS (SELECT c FROM Coordinator c WHERE c.collectionSite = cs) ORDER BY cs.siteNumber ASC")
    List<CollectionSite> findFreeCollectionSites();

    // Derived query to find collection site by siteNumber
    Optional<CollectionSite> findBySiteNumber(int siteNumber);
}
