package com.mady.bloodorgandonorsystem.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mady.bloodorgandonorsystem.domain.Donation;
import com.mady.bloodorgandonorsystem.domain.DonationStatus;
import com.mady.bloodorgandonorsystem.service.DonationService;

@RestController
@RequestMapping("/api/donations")
public class DonationController {

    @Autowired
    private DonationService donationService;

   
    @PostMapping({"/save", ""})
    public ResponseEntity<String> saveDonation(@RequestBody Donation donation) {
        if (donation.getDonationDate() != null && donation.getDonationDate().isBefore(LocalDate.now())) {
            return new ResponseEntity<>("Donation date cannot be in the past", HttpStatus.BAD_REQUEST);
        }

        if (donation.getCoordinator() != null && donation.getCoordinator().getId() != null
                && donation.getDonationDate() != null) {
            if (donationService.isCoordinatorSupervising(donation.getCoordinator().getId(), donation.getDonationDate())) {
                return new ResponseEntity<>("Coordinator already supervises a donation on that date", HttpStatus.CONFLICT);
            }
        }

        donation.setStatus(DonationStatus.REGISTERED);
        donationService.saveDonation(donation);
        return new ResponseEntity<>("Donation saved successfully", HttpStatus.CREATED);
    }

    // A2: Read all
    @GetMapping({"/all", ""})
    public ResponseEntity<List<Donation>> getAllDonations() {
        return new ResponseEntity<>(donationService.getAllDonations(), HttpStatus.OK);
    }

    // A2: Read one
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable UUID id) {
        Optional<Donation> donation = donationService.getDonationById(id);
        if (donation.isPresent()) {
            return new ResponseEntity<>(donation.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The donation with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }

    // A2 & D2: Update
    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<String> updateDonation(@PathVariable UUID id, @RequestBody Donation donation) {
        String res = donationService.updateDonation(id, donation);
        if (res.equals("Donation updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // A2 & E3: Delete and protect
    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteDonation(@PathVariable UUID id) {
        String res = donationService.deleteDonation(id);
        if (res.equals("Donation deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else if (res.equals("The donation with that id does not exist")) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        }
    }

    // B2: Donations by status (DERIVED)
    @GetMapping("/by-status")
    public ResponseEntity<List<Donation>> getByStatus(@RequestParam DonationStatus status) {
        return new ResponseEntity<>(donationService.findByStatus(status), HttpStatus.OK);
    }

    // B3: Donations in date range (DERIVED)
    @GetMapping("/between")
    public ResponseEntity<List<Donation>> getBetween(@RequestParam String start, @RequestParam String end) {
        LocalDate startDate = LocalDate.parse(start);
        LocalDate endDate = LocalDate.parse(end);
        return new ResponseEntity<>(donationService.findBetween(startDate, endDate), HttpStatus.OK);
    }

    // D2: Status flow
    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(@PathVariable UUID id, @RequestParam DonationStatus status) {
        String res = donationService.updateStatus(id, status);
        if (res.equals("Donation status updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else if (res.equals("The donation with that id does not exist")) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // F1: Donations per status (JPQL)
    @GetMapping("/stats/by-status")
    public ResponseEntity<List<Object[]>> getStatsByStatus() {
        return new ResponseEntity<>(donationService.getStatsByStatus(), HttpStatus.OK);
    }

    // F2: Coordinator is absent (JPQL: UPDATE, @Modifying, @Transactional)
    @PatchMapping("/cancel-day")
    public ResponseEntity<String> cancelDay(@RequestParam UUID coordinatorId, @RequestParam String date) {
        LocalDate localDate = LocalDate.parse(date);
        return new ResponseEntity<>(donationService.cancelDay(coordinatorId, localDate), HttpStatus.OK);
    }

    // Bonus +5: Donations page by page
    @GetMapping("/page")
    public ResponseEntity<Page<Donation>> getPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(defaultValue = "donationDate,desc") String sort) {
        String[] sortParts = sort.split(",");
        String field = sortParts[0];
        Sort.Direction dir = (sortParts.length > 1 && sortParts[1].equalsIgnoreCase("asc"))
                ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, field));
        return new ResponseEntity<>(donationService.getDonationsPage(pageable), HttpStatus.OK);
    }
}
