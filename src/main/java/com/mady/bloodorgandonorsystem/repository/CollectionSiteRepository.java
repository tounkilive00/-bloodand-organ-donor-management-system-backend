package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CollectionSiteRepository extends JpaRepository<CollectionSite, UUID> {

    // C2: Free collection sites (JPQL with NOT EXISTS subquery)
    @Query("SELECT cs FROM CollectionSite cs WHERE NOT EXISTS (SELECT c FROM Coordinator c WHERE c.collectionSite = cs) ORDER BY cs.siteNumber ASC")
    List<CollectionSite> findFreeCollectionSites();

    // D4: Derived query to find collection site by siteNumber
    Optional<CollectionSite> findBySiteNumber(int siteNumber);
}
