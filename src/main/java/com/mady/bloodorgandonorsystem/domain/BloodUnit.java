package com.mady.bloodorgandonorsystem.domain;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "blood_unit")
public class BloodUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "component")
    private String component;

    @Column(name = "remarks")
    private String remarks;

    @Column(name = "volume_ml")
    private int volumeMl;

    @Column(name = "collected_date")
    private LocalDate collectedDate;

    @ManyToOne
    @JoinColumn(name = "donation_id")
    private Donation donation;

    public BloodUnit() {
    }

    public BloodUnit(String component, String remarks, int volumeMl, LocalDate collectedDate, Donation donation) {
        this.component = component;
        this.remarks = remarks;
        this.volumeMl = volumeMl;
        this.collectedDate = collectedDate;
        this.donation = donation;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComponent() {
        return component;
    }

    public void setComponent(String component) {
        this.component = component;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public int getVolumeMl() {
        return volumeMl;
    }

    public void setVolumeMl(int volumeMl) {
        this.volumeMl = volumeMl;
    }

    public LocalDate getCollectedDate() {
        return collectedDate;
    }

    public void setCollectedDate(LocalDate collectedDate) {
        this.collectedDate = collectedDate;
    }

    public Donation getDonation() {
        return donation;
    }

    public void setDonation(Donation donation) {
        this.donation = donation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BloodUnit bloodUnit = (BloodUnit) o;
        return Objects.equals(id, bloodUnit.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
