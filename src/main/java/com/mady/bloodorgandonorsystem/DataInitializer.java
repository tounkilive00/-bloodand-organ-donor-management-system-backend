package com.mady.bloodorgandonorsystem;

import com.mady.bloodorgandonorsystem.domain.*;
import com.mady.bloodorgandonorsystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private DonationTypeRepository donationTypeRepository;

    @Autowired
    private CollectionSiteRepository collectionSiteRepository;

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Override
    public void run(String... args) throws Exception {
        if (donationTypeRepository.count() == 0) {
            resetAndSeed();
        }
    }

    public void resetAndSeed() {
        System.out.println(">>> Initializing Step 0 Seed Data...");

        bloodUnitRepository.deleteAll();
        donationRepository.deleteAll();
        coordinatorRepository.deleteAll();
        collectionSiteRepository.deleteAll();
        donationTypeRepository.deleteAll();
        donorRepository.deleteAll();

        // 1. 4 DonationTypes (Nobody holds Bone Marrow)
        DonationType wholeBlood = donationTypeRepository.save(new DonationType("Whole Blood"));
        DonationType plasma = donationTypeRepository.save(new DonationType("Plasma"));
        DonationType platelets = donationTypeRepository.save(new DonationType("Platelets"));
        DonationType boneMarrow = donationTypeRepository.save(new DonationType("Bone Marrow"));

        // 2. 4 CollectionSites numbered 1, 2, 3, 4 (Site 4 has no coordinator)
        CollectionSite site1 = collectionSiteRepository.save(new CollectionSite(1, "Downtown Center"));
        CollectionSite site2 = collectionSiteRepository.save(new CollectionSite(2, "Kigali Heights Unit"));
        CollectionSite site3 = collectionSiteRepository.save(new CollectionSite(3, "Remera Health Post"));
        CollectionSite site4 = collectionSiteRepository.save(new CollectionSite(4, "Nyamirambo Station"));

        // 3. 3 Coordinators, each with a collection site
        Coordinator coord1 = new Coordinator("Patrick", "Mugisha", "patrick.m@blooddonor.org");
        coord1.setCollectionSite(site1);
        coord1.setDonationTypes(new ArrayList<>(List.of(wholeBlood, plasma)));
        coord1 = coordinatorRepository.save(coord1);

        Coordinator coord2 = new Coordinator("Diane", "Gasana", "diane.g@blooddonor.org");
        coord2.setCollectionSite(site2);
        coord2.setDonationTypes(new ArrayList<>(List.of(platelets)));
        coord2 = coordinatorRepository.save(coord2);

        Coordinator coord3 = new Coordinator("Eric", "Kwizera", "eric.k@blooddonor.org");
        coord3.setCollectionSite(site3);
        coord3.setDonationTypes(new ArrayList<>(List.of(wholeBlood)));
        coord3 = coordinatorRepository.save(coord3);

        // 4. 5 Donors (Two share lastName 'Tounkara', one has donorCode 'DN-2048')
        Donor donor1 = donorRepository.save(new Donor("Reteno", "Tounkara", "DN-2048", "O+", "+250788111222"));
        Donor donor2 = donorRepository.save(new Donor("Aissatou", "Tounkara", "DN-2049", "A+", "+250788222333"));
        Donor donor3 = donorRepository.save(new Donor("Jean", "Dupont", "DN-3001", "B+", "+250788333444"));
        Donor donor4 = donorRepository.save(new Donor("Marie", "Curie", "DN-3002", "AB+", "+250788444555"));
        Donor donor5 = donorRepository.save(new Donor("Alice", "Uwase", "DN-3003", "O-", "+250788555666"));

        // 5. 10 Donations spread over October and November 2026 using all four statuses:
        Donation d1 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 5), DonationStatus.COLLECTED, "NORMAL", donor1, coord1));

        Donation d2 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 15), DonationStatus.COLLECTED, "URGENT", donor1, coord2));

        Donation d3 = donationRepository.save(new Donation(
                LocalDate.of(2026, 11, 2), DonationStatus.REGISTERED, "NORMAL", donor1, coord3));

        Donation d4 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 10), DonationStatus.SCREENED, "HIGH", donor2, coord2));

        Donation d5 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 20), DonationStatus.DEFERRED, "NORMAL", donor3, coord1));

        Donation d6 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 20), DonationStatus.REGISTERED, "HIGH", donor4, coord1));

        Donation d7 = donationRepository.save(new Donation(
                LocalDate.of(2026, 10, 25), DonationStatus.SCREENED, "NORMAL", donor5, coord3));

        Donation d8 = donationRepository.save(new Donation(
                LocalDate.of(2026, 11, 5), DonationStatus.REGISTERED, "NORMAL", donor2, coord1));

        Donation d9 = donationRepository.save(new Donation(
                LocalDate.of(2026, 11, 10), DonationStatus.REGISTERED, "URGENT", donor3, coord2));

        Donation d10 = donationRepository.save(new Donation(
                LocalDate.of(2026, 11, 15), DonationStatus.DEFERRED, "NORMAL", donor4, coord3));

        // 6. Blood units for the COLLECTED donations (Part E)
        bloodUnitRepository.save(new BloodUnit(
                "Red Cells", "Stored at bank fridge 2", 450, LocalDate.of(2026, 10, 5), d1));
        bloodUnitRepository.save(new BloodUnit(
                "Plasma", "Stored at freezer unit 1", 250, LocalDate.of(2026, 10, 6), d1));
        bloodUnitRepository.save(new BloodUnit(
                "Platelets", "Stored at room agitation", 300, LocalDate.of(2026, 10, 15), d2));

        System.out.println(">>> Step 0 Seed Data initialized successfully!");
    }
}
