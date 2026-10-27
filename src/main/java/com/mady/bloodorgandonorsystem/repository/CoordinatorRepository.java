package com.mady.bloodorgandonorsystem.repository;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;
import com.mady.bloodorgandonorsystem.domain.Coordinator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoordinatorRepository extends JpaRepository<Coordinator, UUID> {

    // C1: Coordinators by donation type (JPQL with JOIN on collection and LOWER)
    @Query("SELECT c FROM Coordinator c JOIN c.donationTypes dt WHERE LOWER(dt.name) = LOWER(:name)")
    List<Coordinator> findByDonationTypeNameIgnoreCase(@Param("name") String name);

    // D4: Derived query to check if a collection site is occupied
    boolean existsByCollectionSite(CollectionSite collectionSite);

    boolean existsByCollectionSite_SiteNumber(int siteNumber);

    Optional<Coordinator> findByCollectionSite(CollectionSite collectionSite);
}
