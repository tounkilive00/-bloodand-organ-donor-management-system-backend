package com.mady.bloodorgandonorsystem.controller;

import com.mady.bloodorgandonorsystem.domain.DonationType;
import com.mady.bloodorgandonorsystem.service.DonationTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/donation-types")
public class DonationTypeController {

    @Autowired
    private DonationTypeService donationTypeService;

    @PostMapping({"/save", ""})
    public ResponseEntity<String> saveDonationType(@RequestBody DonationType donationType) {
        donationTypeService.saveDonationType(donationType);
        return new ResponseEntity<>("Donation type saved successfully", HttpStatus.CREATED);
    }

    @GetMapping({"/all", ""})
    public ResponseEntity<List<DonationType>> getAllDonationTypes() {
        return new ResponseEntity<>(donationTypeService.getAllDonationTypes(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationTypeById(@PathVariable UUID id) {
        Optional<DonationType> donationType = donationTypeService.getDonationTypeById(id);
        if (donationType.isPresent()) {
            return new ResponseEntity<>(donationType.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The donation type with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<String> updateDonationType(@PathVariable UUID id, @RequestBody DonationType donationType) {
        String res = donationTypeService.updateDonationType(id, donationType);
        if (res.equals("Donation type updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteDonationType(@PathVariable UUID id) {
        String res = donationTypeService.deleteDonationType(id);
        if (res.equals("Donation type deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // C3: Donation types nobody holds (JPQL: IS EMPTY)
    @GetMapping("/unused")
    public ResponseEntity<List<DonationType>> getUnusedDonationTypes() {
        return new ResponseEntity<>(donationTypeService.getUnusedDonationTypes(), HttpStatus.OK);
    }
}
