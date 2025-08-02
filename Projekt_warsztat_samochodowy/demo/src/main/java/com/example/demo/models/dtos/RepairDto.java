package com.example.demo.models.dtos;

import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.PriceList;
import com.example.demo.models.entities.Repair;
import com.example.demo.models.entities.User;
import com.example.demo.models.entities.Repair.StatusNaprawy;

import java.time.Duration;
import java.time.LocalDateTime;

public class RepairDto {

    private Integer id;
    private String opis;
    private StatusNaprawy status;
    private Double cena;  // tylko do odczytu!
    private Duration szacowanyCzas;
    private LocalDateTime dataUtworzenia;
    private Integer samochodId;
    private Integer priceListId;
    private Integer mechanicId;

    // Konstruktor
    public RepairDto(Integer id, String opis, StatusNaprawy status, Double cena, Duration szacowanyCzas,
                     LocalDateTime dataUtworzenia, Integer samochodId, Integer priceListId, Integer mechanicId) {
        this.id = id;
        this.opis = opis;
        this.status = status;
        this.cena = cena;
        this.szacowanyCzas = szacowanyCzas;
        this.dataUtworzenia = dataUtworzenia;
        this.samochodId = samochodId;
        this.priceListId = priceListId;
        this.mechanicId = mechanicId;
    }

    // Gettery
    public Integer getId() { return id; }
    public String getOpis() { return opis; }
    public StatusNaprawy getStatus() { return status; }
    public Double getCena() { return cena; }  // tylko do odczytu
    public Duration getSzacowanyCzas() { return szacowanyCzas; }
    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public Integer getSamochodId() { return samochodId; }
    public Integer getPriceListId() { return priceListId; }
    public Integer getMechanicId() { return mechanicId; }

    // Settery (bez setCena!)
    public void setId(Integer id) { this.id = id; }
    public void setOpis(String opis) { this.opis = opis; }
    public void setStatus(StatusNaprawy status) { this.status = status; }
    public void setSzacowanyCzas(Duration szacowanyCzas) { this.szacowanyCzas = szacowanyCzas; }
    public void setDataUtworzenia(LocalDateTime dataUtworzenia) { this.dataUtworzenia = dataUtworzenia; }
    public void setSamochodId(Integer samochodId) { this.samochodId = samochodId; }
    public void setPriceListId(Integer priceListId) { this.priceListId = priceListId; }
    public void setMechanicId(Integer mechanicId) { this.mechanicId = mechanicId; }


    // Konwersja z DTO do encji (bez ustawiania ceny!)
    public Repair toEntity() {
        Repair repair = new Repair();
        repair.setId(this.id);
        repair.setOpis(this.opis);
        repair.setStatus(this.status);
        repair.setSzacowanyCzas(this.szacowanyCzas);

        if (this.samochodId != null) {
            Car car = new Car();
            car.setId(this.samochodId);
            repair.setSamochod(car);
        }

        if (this.priceListId != null) {
            PriceList priceList = new PriceList();
            priceList.setId(this.priceListId);
            repair.setPriceList(priceList);
        }

        if (this.mechanicId != null) {
            User mechanic = new User();
            mechanic.setId(this.mechanicId);
            repair.setMechanic(mechanic);
        }

        return repair;
    }

    // Konwersja z encji do DTO
    public static RepairDto fromEntity(Repair repair) {
        return new RepairDto(
                repair.getId(),
                repair.getOpis(),
                repair.getStatus(),
                repair.getKoszt(),
                repair.getSzacowanyCzas(),
                repair.getDataUtworzenia(),
                repair.getSamochod() != null ? repair.getSamochod().getId() : null,
                repair.getPriceList() != null ? repair.getPriceList().getId() : null,
                repair.getMechanic() != null ? repair.getMechanic().getId() : null
        );
    }


}
