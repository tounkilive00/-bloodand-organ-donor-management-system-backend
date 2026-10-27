package com.mady.bloodorgandonorsystem.domain;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "coordinator")
public class Coordinator {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @ManyToOne
    @JoinColumn(name = "collection_site_id", nullable = true)
    private CollectionSite collectionSite;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "coordinator_donation_type",
        joinColumns = @JoinColumn(name = "coordinator_id"),
        inverseJoinColumns = @JoinColumn(name = "donation_type_id")
    )
    private List<DonationType> donationTypes = new ArrayList<>();

    public Coordinator() {
    }

    public Coordinator(String firstName, String lastName, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CollectionSite getCollectionSite() {
        return collectionSite;
    }

    public void setCollectionSite(CollectionSite collectionSite) {
        this.collectionSite = collectionSite;
    }

    public List<DonationType> getDonationTypes() {
        return donationTypes;
    }

    public void setDonationTypes(List<DonationType> donationTypes) {
        this.donationTypes = donationTypes;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Coordinator that = (Coordinator) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
