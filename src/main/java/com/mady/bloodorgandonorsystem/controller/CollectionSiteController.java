package com.mady.bloodorgandonorsystem.controller;

import com.mady.bloodorgandonorsystem.domain.CollectionSite;
import com.mady.bloodorgandonorsystem.service.CollectionSiteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/collection-sites")
public class CollectionSiteController {

    @Autowired
    private CollectionSiteService collectionSiteService;

    @PostMapping({"/save", ""})
    public ResponseEntity<String> saveCollectionSite(@RequestBody CollectionSite collectionSite) {
        collectionSiteService.saveCollectionSite(collectionSite);
        return new ResponseEntity<>("Collection site saved successfully", HttpStatus.CREATED);
    }

    @GetMapping({"/all", ""})
    public ResponseEntity<List<CollectionSite>> getAllCollectionSites() {
        return new ResponseEntity<>(collectionSiteService.getAllCollectionSites(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCollectionSiteById(@PathVariable UUID id) {
        Optional<CollectionSite> collectionSite = collectionSiteService.getCollectionSiteById(id);
        if (collectionSite.isPresent()) {
            return new ResponseEntity<>(collectionSite.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("The collection site with that id does not exist", HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping({"/update/{id}", "/{id}"})
    public ResponseEntity<String> updateCollectionSite(@PathVariable UUID id, @RequestBody CollectionSite collectionSite) {
        String res = collectionSiteService.updateCollectionSite(id, collectionSite);
        if (res.equals("Collection site updated successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping({"/delete/{id}", "/{id}"})
    public ResponseEntity<String> deleteCollectionSite(@PathVariable UUID id) {
        String res = collectionSiteService.deleteCollectionSite(id);
        if (res.equals("Collection site deleted successfully")) {
            return new ResponseEntity<>(res, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
        }
    }

    // C2: Free collection sites (JPQL: NOT EXISTS subquery)
    @GetMapping("/free")
    public ResponseEntity<List<CollectionSite>> getFreeCollectionSites() {
        return new ResponseEntity<>(collectionSiteService.getFreeCollectionSites(), HttpStatus.OK);
    }
}
