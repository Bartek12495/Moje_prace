package com.example.demo.models.entities;

import jakarta.persistence.*;

@Entity
public class PriceList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nazwaUslugi;  // Nazwa usługi (np. wymiana oleju, naprawa hamulców)

    private Double cena;  // Cena za usługę

    // Konstruktor bezparametrowy (wymagany przez JPA)
    public PriceList() {}

    // Gettery i settery
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
}