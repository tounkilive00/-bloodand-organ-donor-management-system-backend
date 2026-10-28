package com.mady.bloodorgandonorsystem.controller;

import com.mady.bloodorgandonorsystem.domain.Donor;
import com.mady.bloodorgandonorsystem.service.DonorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/donors")
public class DonorController {

    @Autowired
    private DonorService donorService;

    @PostMapping({"/save", ""})
    public ResponseEntity<String> saveDonor(@RequestBody Donor donor) {
        donorService.saveDonor(donor);
        return new ResponseEntity<>("Donor saved successfully", HttpStatus.CREATED);
    }

    @GetMapping({"/all", ""})
    public ResponseEntity<List<Donor>> getAllDonors() {
        return new ResponseEntity<>(donorService.getAllDonors(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonorById(@PathVariable UUID id) {
        Optional<Donor> donor = donorService.getDonorById(id);
        if (donor.isPresent()) {
            return new ResponseEntity<>(donor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The donor with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<String> updateDonor(@PathVariable UUID id, @RequestBody Donor donor) {
        String res = donorService.updateDonor(id, donor);
        if (res.equals("Donor updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteDonor(@PathVariable UUID id) {
        String res = donorService.deleteDonor(id);
        if (res.equals("Donor deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // B1: Find donors by lastName (DERIVED: IgnoreCase, OrderBy)
    @GetMapping("/by-last-name")
    public ResponseEntity<List<Donor>> getDonorsByLastName(@RequestParam String lastName) {
        return new ResponseEntity<>(donorService.findByLastName(lastName), HttpStatus.OK);
    }

    // C4: Donors of a coordinator (JPQL: DISTINCT, path navigation)
    @GetMapping("/of-coordinator/{coordinatorId}")
    public ResponseEntity<?> getDonorsOfCoordinator(@PathVariable UUID coordinatorId) {
        Optional<List<Donor>> donors = donorService.getDonorsOfCoordinator(coordinatorId);
        if (donors.isEmpty()) {
            return new ResponseEntity<>("The coordinator with that id does not exist", HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(donors.get(), HttpStatus.OK);
    }
}
