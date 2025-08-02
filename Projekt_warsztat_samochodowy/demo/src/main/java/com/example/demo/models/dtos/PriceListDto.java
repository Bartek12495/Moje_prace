package com.example.demo.models.dtos;

import com.example.demo.models.entities.PriceList;

public class PriceListDto {

    private Integer id;
    private String nazwaUslugi;
    private Double cena;

    // --- Konstruktory ---

    public PriceListDto() {}

    public PriceListDto(Integer id, String nazwaUslugi, Double cena) {
        this.id = id;
        this.nazwaUslugi = nazwaUslugi;
        this.cena = cena;
    }

    // --- Gettery i settery ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNazwaUslugi() {
        return nazwaUslugi;
    }

    public void setNazwaUslugi(String nazwaUslugi) {
        this.nazwaUslugi = nazwaUslugi;
    }

    public Double getCena() {
        return cena;
    }

    public void setCena(Double cena) {
        this.cena = cena;
    }

    // --- Konwersje ---

    public static PriceListDto fromEntity(PriceList entity) {
        return new PriceListDto(
                entity.getId(),
                entity.getNazwaUslugi(),
                entity.getCena()
        );
    }

    public PriceList toEntity() {
        PriceList entity = new PriceList();
        entity.setId(this.id);
        entity.setNazwaUslugi(this.nazwaUslugi);
        entity.setCena(this.cena);
        return entity;
    }
}
