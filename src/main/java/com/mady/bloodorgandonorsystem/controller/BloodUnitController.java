package com.mady.bloodorgandonorsystem.controller;

import com.mady.bloodorgandonorsystem.domain.BloodUnit;
import com.mady.bloodorgandonorsystem.service.BloodUnitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/blood-units")
public class BloodUnitController {

    @Autowired
    private BloodUnitService bloodUnitService;

    // E1: Write a blood unit
    @PostMapping("/save")
    public ResponseEntity<String> saveBloodUnit(
            @RequestParam UUID donationId,
            @RequestBody BloodUnit bloodUnit) {
        String res = bloodUnitService.saveBloodUnit(donationId, bloodUnit);
        if (res.equals("Blood unit saved successfully")) {
            return new ResponseEntity<>(res, HttpStatus.CREATED);
        } else if (res.equals("The donation with that id does not exist")) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(res, HttpStatus.BAD_REQUEST);
        }
    }

    // E2: Blood unit history of a donor (JPQL: path navigation, ORDER BY)
    @GetMapping("/donor/{donorCode}")
    public ResponseEntity<List<BloodUnit>> getBloodUnitsByDonorCode(@PathVariable String donorCode) {
        return new ResponseEntity<>(bloodUnitService.getBloodUnitsByDonorCode(donorCode), HttpStatus.OK);
    }

    // E3: Delete blood unit
    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteBloodUnit(@PathVariable UUID id) {
        String res = bloodUnitService.deleteBloodUnit(id);
        if (res.equals("Blood unit deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // Read all
    @GetMapping({"/all", ""})
    public ResponseEntity<List<BloodUnit>> getAllBloodUnits() {
        return new ResponseEntity<>(bloodUnitService.getAllBloodUnits(), HttpStatus.OK);
    }

    // Read one
    @GetMapping("/{id}")
    public ResponseEntity<?> getBloodUnitById(@PathVariable UUID id) {
        Optional<BloodUnit> unit = bloodUnitService.getBloodUnitById(id);
        if (unit.isPresent()) {
            return new ResponseEntity<>(unit.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The blood unit with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }
}
