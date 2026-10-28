package com.mady.bloodorgandonorsystem.controller;

import com.mady.bloodorgandonorsystem.domain.Coordinator;
import com.mady.bloodorgandonorsystem.service.CoordinatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/coordinators")
public class CoordinatorController {

    @Autowired
    private CoordinatorService coordinatorService;

    @PostMapping({"/save", ""})
    public ResponseEntity<String> saveCoordinator(@RequestBody Coordinator coordinator) {
        coordinatorService.saveCoordinator(coordinator);
        return new ResponseEntity<>("Coordinator saved successfully", HttpStatus.CREATED);
    }

    @GetMapping({"/all", ""})
    public ResponseEntity<List<Coordinator>> getAllCoordinators() {
        return new ResponseEntity<>(coordinatorService.getAllCoordinators(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCoordinatorById(@PathVariable UUID id) {
        Optional<Coordinator> coordinator = coordinatorService.getCoordinatorById(id);
        if (coordinator.isPresent()) {
            return new ResponseEntity<>(coordinator.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The coordinator with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<String> updateCoordinator(@PathVariable UUID id, @RequestBody Coordinator coordinator) {
        String res = coordinatorService.updateCoordinator(id, coordinator);
        if (res.equals("Coordinator updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteCoordinator(@PathVariable UUID id) {
        String res = coordinatorService.deleteCoordinator(id);
        if (res.equals("Coordinator deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // C1: Coordinators by donation type (JPQL)
    @GetMapping("/by-donation-type")
    public ResponseEntity<List<Coordinator>> getCoordinatorsByDonationType(@RequestParam String name) {
        return new ResponseEntity<>(coordinatorService.getCoordinatorsByDonationType(name), HttpStatus.OK);
    }

    // D3: Add one donation type to a coordinator
    @PutMapping("/{coordinatorId}/donation-types/{donationTypeId}")
    public ResponseEntity<String> addDonationType(
            @PathVariable UUID coordinatorId,
            @PathVariable UUID donationTypeId) {
        String res = coordinatorService.addDonationType(coordinatorId, donationTypeId);
        if (res.equals("Donation type added to the coordinator")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else if (res.contains("does not exist")) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        }
    }

    // D4: Move a coordinator to another collection site
    @PatchMapping("/{coordinatorId}/collection-site")
    public ResponseEntity<String> moveCollectionSite(
            @PathVariable UUID coordinatorId,
            @RequestParam int siteNumber) {
        String res = coordinatorService.moveCollectionSite(coordinatorId, siteNumber);
        if (res.equals("Collection site changed successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else if (res.contains("does not exist")) {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        } else {
            return new ResponseEntity<>(res, HttpStatus.CONFLICT);
        }
    }
}
