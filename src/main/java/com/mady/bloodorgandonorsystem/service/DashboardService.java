package com.mady.bloodorgandonorsystem.service;

import com.mady.bloodorgandonorsystem.domain.DonationStatus;
import com.mady.bloodorgandonorsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class DashboardService {

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private CollectionSiteRepository collectionSiteRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    public Map<String, Long> getSummary() {
        Map<String, Long> summary = new LinkedHashMap<>();
        summary.put("donors", donorRepository.count());
        summary.put("coordinators", coordinatorRepository.count());
        summary.put("freeCollectionSites", (long) collectionSiteRepository.findFreeCollectionSites().size());
        summary.put("donationsToday", donationRepository.countByDonationDate(LocalDate.now()));
        summary.put("registered", donationRepository.countByStatus(DonationStatus.REGISTERED));
        summary.put("bloodUnits", bloodUnitRepository.count());
        return summary;
    }
}
