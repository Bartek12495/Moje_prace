package com.example.demo.models.entities;

import jakarta.persistence.*;
import java.time.Duration;
import java.time.LocalDateTime;

@Entity
public class Repair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String opis;

    @Enumerated(EnumType.STRING)
    private StatusNaprawy status = StatusNaprawy.OCZEKUJE;

    private Duration szacowanyCzas;

    private LocalDateTime dataUtworzenia = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car samochod;

    @ManyToOne
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    @ManyToOne
    @JoinColumn(name = "mechanic_id")
    private User mechanic;

    public Repair() {}

    // --- GETTERY I SETTERY ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public StatusNaprawy getStatus() {
        return status;
    }

    public void setStatus(StatusNaprawy status) {
        this.status = status;
    }

    public Duration getSzacowanyCzas() {
        return szacowanyCzas;
    }

    public void setSzacowanyCzas(Duration szacowanyCzas) {
        this.szacowanyCzas = szacowanyCzas;
    }

    public LocalDateTime getDataUtworzenia() {
        return dataUtworzenia;
    }

    public Car getSamochod() {
        return samochod;
    }

    public void setSamochod(Car samochod) {
        this.samochod = samochod;
    }

    public PriceList getPriceList() {
        return priceList;
    }

    public void setPriceList(PriceList priceList) {
        this.priceList = priceList;
    }

    public User getMechanic() {
        return mechanic;
    }

    public void setMechanic(User mechanic) {
        this.mechanic = mechanic;
    }

    // 🔥 Dynamiczne pobieranie ceny z PriceList
    public Double getKoszt() {
        return priceList != null ? priceList.getCena() : 0.0;
    }

    // Enum statusów naprawy
    public enum StatusNaprawy {
        OCZEKUJE,
        W_TRAKCIE,
        ZAKONCZONA_DO_ZAPLATY,
        ZAKONCZONA
    }
}