package com.mady.bloodorgandonorsystem;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mady.bloodorgandonorsystem.domain.*;
import com.mady.bloodorgandonorsystem.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MidtermRequirementsTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Autowired
    private DonorRepository donorRepository;

    @Autowired
    private CoordinatorRepository coordinatorRepository;

    @Autowired
    private CollectionSiteRepository collectionSiteRepository;

    @Autowired
    private DonationTypeRepository donationTypeRepository;

    @Autowired
    private DonationRepository donationRepository;

    @Autowired
    private BloodUnitRepository bloodUnitRepository;

    @Autowired
    private DataInitializer dataInitializer;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        dataInitializer.resetAndSeed();
    }

    // --- PART A ---
    @Test
    @Order(1)
    void testA2_CRUD_Donors() throws Exception {
        Donor donor = new Donor("TestFirst", "TestLast", "DN-9999", "AB-", "+250788000000");
        mockMvc.perform(post("/api/donors/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(donor)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Donor saved successfully"));

        mockMvc.perform(get("/api/donors/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        Donor savedDonor = donorRepository.findByDonorCode("DN-9999").orElseThrow();
        mockMvc.perform(get("/api/donors/" + savedDonor.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName", is("TestFirst")));

        savedDonor.setFirstName("UpdatedFirst");
        mockMvc.perform(put("/api/donors/update/" + savedDonor.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(savedDonor)))
                .andExpect(status().isOk())
                .andExpect(content().string("Donor updated successfully"));

        mockMvc.perform(delete("/api/donors/delete/" + savedDonor.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Donor deleted successfully"));

        mockMvc.perform(get("/api/donors/" + savedDonor.getId()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The donor with that id does not exist"));
    }

    // --- PART B ---
    @Test
    @Order(2)
    void testB1_FindDonorsByLastName() throws Exception {
        mockMvc.perform(get("/api/donors/by-last-name?lastName=tounkara"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].firstName", is("Aissatou")))
                .andExpect(jsonPath("$[1].firstName", is("Reteno")));
    }

    @Test
    @Order(3)
    void testB2_DonationsByStatus() throws Exception {
        mockMvc.perform(get("/api/donations/by-status?status=COLLECTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))))
                .andExpect(jsonPath("$[0].status", is("COLLECTED")));
    }

    @Test
    @Order(4)
    void testB3_DonationsInDateRange() throws Exception {
        mockMvc.perform(get("/api/donations/between?start=2026-10-01&end=2026-10-31"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    // --- PART C ---
    @Test
    @Order(5)
    void testC1_CoordinatorsByDonationType() throws Exception {
        mockMvc.perform(get("/api/coordinators/by-donation-type?name=plasma"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].firstName", is("Patrick")));
    }

    @Test
    @Order(6)
    void testC2_FreeCollectionSites() throws Exception {
        mockMvc.perform(get("/api/collection-sites/free"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].siteNumber", is(4)));
    }

    @Test
    @Order(7)
    void testC3_DonationTypesNobodyHolds() throws Exception {
        mockMvc.perform(get("/api/donation-types/unused"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].name", is("Bone Marrow")));
    }

    @Test
    @Order(8)
    void testC4_DonorsOfCoordinator() throws Exception {
        Coordinator coord1 = coordinatorRepository.findAll().stream()
                .filter(c -> c.getFirstName().equals("Patrick"))
                .findFirst().orElseThrow();

        mockMvc.perform(get("/api/donors/of-coordinator/" + coord1.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));

        mockMvc.perform(get("/api/donors/of-coordinator/" + UUID.randomUUID()))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The coordinator with that id does not exist"));
    }

    // --- PART D ---
    @Test
    @Order(9)
    void testD1_SafeSaving() throws Exception {
        Donor donor1 = donorRepository.findByDonorCode("DN-2048").orElseThrow();
        Coordinator coord1 = coordinatorRepository.findAll().get(0);

        // Date in past -> 400
        mockMvc.perform(post("/api/donations/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"donationDate\":\"2020-01-01\",\"urgency\":\"NORMAL\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Donation date cannot be in the past"));

        // Coordinator conflict (coord1 already has non-DEFERRED donation on date) -> 409
        LocalDate conflictDate = LocalDate.now().plusDays(3);
        donationRepository.save(new Donation(conflictDate, DonationStatus.REGISTERED, "HIGH", donor1, coord1));

        String conflictJson = String.format(
                "{\"donationDate\":\"%s\",\"urgency\":\"NORMAL\",\"donor\":{\"id\":\"%s\"},\"coordinator\":{\"id\":\"%s\"}}",
                conflictDate, donor1.getId(), coord1.getId());
        mockMvc.perform(post("/api/donations/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(conflictJson))
                .andExpect(status().isConflict())
                .andExpect(content().string("Coordinator already supervises a donation on that date"));
    }

    @Test
    @Order(10)
    void testD2_StatusFlow() throws Exception {
        Donor donor1 = donorRepository.findByDonorCode("DN-2048").orElseThrow();
        Coordinator coord2 = coordinatorRepository.findAll().get(1);

        // Create new donation for future date
        String futureJson = String.format(
                "{\"donationDate\":\"%s\",\"urgency\":\"NORMAL\",\"status\":\"SCREENED\",\"donor\":{\"id\":\"%s\"},\"coordinator\":{\"id\":\"%s\"}}",
                LocalDate.now().plusDays(10), donor1.getId(), coord2.getId());
        mockMvc.perform(post("/api/donations/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(futureJson))
                .andExpect(status().isCreated());

        List<Donation> list = donationRepository.findByStatusOrderByDonationDateAsc(DonationStatus.REGISTERED);
        Donation saved = list.stream().filter(d -> d.getDonationDate().isEqual(LocalDate.now().plusDays(10))).findFirst().orElseThrow();

        // 1. REGISTERED -> SCREENED (Allowed)
        mockMvc.perform(patch("/api/donations/" + saved.getId() + "/status?status=SCREENED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Donation status updated successfully"));

        // 2. SCREENED -> COLLECTED (Allowed)
        mockMvc.perform(patch("/api/donations/" + saved.getId() + "/status?status=COLLECTED"))
                .andExpect(status().isOk())
                .andExpect(content().string("Donation status updated successfully"));

        // 3. COLLECTED -> REGISTERED (Not allowed)
        mockMvc.perform(patch("/api/donations/" + saved.getId() + "/status?status=REGISTERED"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot change status from COLLECTED to REGISTERED"));
    }

    @Test
    @Order(11)
    void testD3_AddDonationType() throws Exception {
        Coordinator coord2 = coordinatorRepository.findAll().stream()
                .filter(c -> c.getFirstName().equals("Diane"))
                .findFirst().orElseThrow();

        DonationType boneMarrow = donationTypeRepository.findByNameIgnoreCase("Bone Marrow").orElseThrow();

        mockMvc.perform(put("/api/coordinators/" + coord2.getId() + "/donation-types/" + boneMarrow.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Donation type added to the coordinator"));

        // Adding again returns 409
        mockMvc.perform(put("/api/coordinators/" + coord2.getId() + "/donation-types/" + boneMarrow.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("The coordinator already has that donation type"));

        // Restore coordinator state
        coord2 = coordinatorRepository.findById(coord2.getId()).orElseThrow();
        coord2.getDonationTypes().remove(boneMarrow);
        coordinatorRepository.save(coord2);
    }

    @Test
    @Order(12)
    void testD4_MoveCollectionSite() throws Exception {
        Coordinator coord3 = coordinatorRepository.findAll().stream()
                .filter(c -> c.getFirstName().equals("Eric"))
                .findFirst().orElseThrow();

        // Site 1 is taken by Patrick (coord1) -> 409
        mockMvc.perform(patch("/api/coordinators/" + coord3.getId() + "/collection-site?siteNumber=1"))
                .andExpect(status().isConflict())
                .andExpect(content().string("That collection site is taken by another coordinator"));

        // Site 99 does not exist -> 404
        mockMvc.perform(patch("/api/coordinators/" + coord3.getId() + "/collection-site?siteNumber=99"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("The collection site with that siteNumber does not exist"));

        // Site 4 is free -> 200
        mockMvc.perform(patch("/api/coordinators/" + coord3.getId() + "/collection-site?siteNumber=4"))
                .andExpect(status().isOk())
                .andExpect(content().string("Collection site changed successfully"));

        // Restore coordinator state
        CollectionSite site3 = collectionSiteRepository.findBySiteNumber(3).orElseThrow();
        coord3 = coordinatorRepository.findById(coord3.getId()).orElseThrow();
        coord3.setCollectionSite(site3);
        coordinatorRepository.save(coord3);
    }

    // --- PART E ---
    @Test
    @Order(13)
    void testE1_WriteBloodUnit() throws Exception {
        Donation collectedDonation = donationRepository.findByStatusOrderByDonationDateAsc(DonationStatus.COLLECTED).get(0);
        BloodUnit unit = new BloodUnit("Red Cells", "Stored at fridge 3", 500, null, null);

        mockMvc.perform(post("/api/blood-units/save?donationId=" + collectedDonation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unit)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Blood unit saved successfully"));

        // Donation not collected
        Donation registeredDonation = donationRepository.findByStatusOrderByDonationDateAsc(DonationStatus.REGISTERED).get(0);
        mockMvc.perform(post("/api/blood-units/save?donationId=" + registeredDonation.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(unit)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Blood units are only for COLLECTED donations"));
    }

    @Test
    @Order(14)
    void testE2_BloodUnitHistoryOfDonor() throws Exception {
        mockMvc.perform(get("/api/blood-units/donor/DN-2048"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @Order(15)
    void testE3_DeleteAndProtect() throws Exception {
        Donation collected = donationRepository.findByStatusOrderByDonationDateAsc(DonationStatus.COLLECTED).get(0);
        // Deleting donation that has blood units -> 409
        mockMvc.perform(delete("/api/donations/delete/" + collected.getId()))
                .andExpect(status().isConflict())
                .andExpect(content().string("That donation has blood units, delete them first"));

        // Delete a temporary blood unit -> 200
        BloodUnit tempUnit = bloodUnitRepository.save(new BloodUnit("Plasma", "Temp test unit", 200, LocalDate.now(), collected));
        mockMvc.perform(delete("/api/blood-units/delete/" + tempUnit.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Blood unit deleted successfully"));
    }

    // --- PART F ---
    @Test
    @Order(16)
    void testF1_StatsByStatus() throws Exception {
        mockMvc.perform(get("/api/donations/stats/by-status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", not(empty())));
    }

    @Test
    @Order(17)
    void testF2_CoordinatorIsAbsent() throws Exception {
        Coordinator coord1 = coordinatorRepository.findAll().stream()
                .filter(c -> c.getFirstName().equals("Patrick"))
                .findFirst().orElseThrow();

        mockMvc.perform(patch("/api/donations/cancel-day?coordinatorId=" + coord1.getId() + "&date=2026-10-20"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("donations cancelled")));
    }

    // --- BONUS ---
    @Test
    @Order(18)
    void testBonus_Dashboard() throws Exception {
        mockMvc.perform(get("/api/dashboard/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.donors", greaterThanOrEqualTo(5)))
                .andExpect(jsonPath("$.coordinators", greaterThanOrEqualTo(3)))
                .andExpect(jsonPath("$.registered", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.bloodUnits", greaterThanOrEqualTo(1)));
    }

    @Test
    @Order(19)
    void testBonus_Pagination() throws Exception {
        mockMvc.perform(get("/api/donations/page?page=0&size=5&sort=donationDate,desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(lessThanOrEqualTo(5))))
                .andExpect(jsonPath("$.totalPages", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.totalElements", greaterThanOrEqualTo(1)));
    }
}
