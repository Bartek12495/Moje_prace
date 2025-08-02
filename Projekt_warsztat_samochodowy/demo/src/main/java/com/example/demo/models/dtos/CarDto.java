package com.example.demo.models.dtos;

import com.example.demo.models.entities.Car;

public class CarDto {
    private Integer id;
    private String marka;
    private String model;
    private int rokProdukcji;
    private String numerRejestracyjny;
    private Integer userId; // Dodane pole userId – właściciel

    // Pusty konstruktor (potrzebny np. do deserializacji JSON)
    public CarDto() {
    }

    // Konstruktor ze wszystkimi polami
    public CarDto(Integer id, String marka, String model, int rokProdukcji, String numerRejestracyjny, Integer userId) {
        this.id = id;
        this.marka = marka;
        this.model = model;
        this.rokProdukcji = rokProdukcji;
        this.numerRejestracyjny = numerRejestracyjny;
        this.userId = userId;
    }

    // Konstruktor bez userId – pomocny w PUT-ach itp.
    public CarDto(Integer id, String marka, String model, int rokProdukcji, String numerRejestracyjny) {
        this(id, marka, model, rokProdukcji, numerRejestracyjny, null);
    }

    // GETTERY
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

    public Integer getUserId() {
        return userId;
    }

    // SETTERY
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

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    // Konwersja z Entity na DTO
    public static CarDto fromEntity(Car car) {
        return new CarDto(
                car.getId(),
                car.getMarka(),
                car.getModel(),
                car.getRokProdukcji(),
                car.getNumerRejestracyjny(),
                car.getOwner() != null ? car.getOwner().getId() : null
        );
    }
}
