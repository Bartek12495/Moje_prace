package com.example.demo.models.dtos;

import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.Inspection;
import com.example.demo.models.entities.PriceList;

import java.time.LocalDateTime;

public class InspectionDto {
    private Integer id;
    private LocalDateTime dataPrzegladu;
    private LocalDateTime dataWaznosci;
    private String opis;
    private Boolean zaliczony;
    private Integer samochodId;
    private Integer priceListId;
    private Double koszt;

    // GETTERY I SETTERY

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

    public Integer getSamochodId() {
        return samochodId;
    }

    public void setSamochodId(Integer samochodId) {
        this.samochodId = samochodId;
    }

    public Integer getPriceListId() {
        return priceListId;
    }

    public void setPriceListId(Integer priceListId) {
        this.priceListId = priceListId;
    }

    public Double getKoszt() {
        return koszt;
    }

    // Usunięty setter dla koszt — pole tylko do odczytu
    // public void setKoszt(Double koszt) { this.koszt = koszt; }

    // Konwersja DTO -> ENTITY
    public Inspection toEntity() {
        Inspection inspection = new Inspection();
        inspection.setDataPrzegladu(this.dataPrzegladu);
        inspection.setOpis(this.opis);
        inspection.setZaliczony(this.zaliczony);

        if (this.dataWaznosci != null) {
            inspection.setDataWaznosci(this.dataWaznosci);
        } else if (this.dataPrzegladu != null) {
            inspection.setDataWaznosci(this.dataPrzegladu.plusYears(1));
        }

        if (this.samochodId != null) {
            Car car = new Car();
            car.setId(this.samochodId);
            inspection.setSamochod(car);
        }

        if (this.priceListId != null) {
            PriceList priceList = new PriceList();
            priceList.setId(this.priceListId);
            inspection.setPriceList(priceList);
        }

        // koszt NIE jest ustawiany tutaj
        return inspection;
    }

    // Konwersja ENTITY -> DTO
    public static InspectionDto fromEntity(Inspection inspection) {
        InspectionDto dto = new InspectionDto();
        dto.setId(inspection.getId());
        dto.setDataPrzegladu(inspection.getDataPrzegladu());
        dto.setDataWaznosci(inspection.getDataWaznosci());
        dto.setOpis(inspection.getOpis());
        dto.setZaliczony(inspection.getZaliczony());
        dto.koszt = inspection.getKoszt(); // ustawiamy wewnętrznie

        if (inspection.getSamochod() != null) {
            dto.setSamochodId(inspection.getSamochod().getId());
        }

        if (inspection.getPriceList() != null) {
            dto.setPriceListId(inspection.getPriceList().getId());
        }

        return dto;
    }
}
