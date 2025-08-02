package com.example.demo.models.entities;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Inspection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDateTime dataPrzegladu;

    private LocalDateTime dataWaznosci;

    private String opis;

    private Boolean zaliczony;

    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car samochod;

    @ManyToOne
    @JoinColumn(name = "price_list_id")
    private PriceList priceList;

    public Inspection() {}

    // Gettery i settery

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getDataPrzegladu() {
        return dataPrzegladu;
    }

    public void setDataPrzegladu(LocalDateTime dataPrzegladu) {
        this.dataPrzegladu = dataPrzegladu;
        if (this.dataWaznosci == null && dataPrzegladu != null) {
            this.dataWaznosci = dataPrzegladu.plusYears(1);
        }
    }

    public LocalDateTime getDataWaznosci() {
        return dataWaznosci;
    }

    public void setDataWaznosci(LocalDateTime dataWaznosci) {
        this.dataWaznosci = dataWaznosci;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public Boolean getZaliczony() {
        return zaliczony;
    }

    public void setZaliczony(Boolean zaliczony) {
        this.zaliczony = zaliczony;
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

    // Koszt dynamicznie
    public Double getKoszt() {
        return priceList != null ? priceList.getCena() : 0.0;
    }
}