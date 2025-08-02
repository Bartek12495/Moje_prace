package com.example.demo.models.entities;

import jakarta.persistence.*;

@Entity
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String marka;
    private String model;
    private int rokProdukcji;
    @Column(nullable = false, unique = true)
    private String numerRejestracyjny;

    // Powiązanie z właścicielem (klientem)
    @ManyToOne
    @JoinColumn(name = "owner_id")  // Kolumna w tabeli "car", klucz obcy do "user"
    private User owner;

    public Car() {
    }

    public Car(Integer id, String marka, String model, int rokProdukcji, String numerRejestracyjny) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.rokProdukcji = rokProdukcji;
        this.numerRejestracyjny = numerRejestracyjny;
    }

    // --- GETTERY ---

    public Integer getId() {
        return id;
    }

    public String getMarka() {
        return marka;
    }

    public String getModel() {
        return model;
    }

    public int getRokProdukcji() {
        return rokProdukcji;
    }

    public String getNumerRejestracyjny() {
        return numerRejestracyjny;
    }

    public User getOwner() {
        return owner;
    }

    // --- SETTERY ---

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMarka(String marka) {
        this.marka = marka;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public void setRokProdukcji(int rokProdukcji) {
        this.rokProdukcji = rokProdukcji;
    }

    public void setNumerRejestracyjny(String numerRejestracyjny) {
        this.numerRejestracyjny = numerRejestracyjny;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }
}
