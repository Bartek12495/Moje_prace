package com.example.demo.models.dtos;

import java.time.LocalDate;

public class ReceiptDto {
    private LocalDate dataWystawienia;    // data wystawienia paragonu
    private String nazwaMechanika;        // nazwa mechanika (username)
    private String markaSamochodu;        // marka
    private String modelSamochodu;        // model
    private String numerRejestracyjny;    // numer rejestracyjny
    private Double cenaNaprawy;           // cena naprawy

    // --- GETTERY I SETTERY ---
    public LocalDate getDataWystawienia() {
        return dataWystawienia;
    }

    public void setDataWystawienia(LocalDate dataWystawienia) {
        this.dataWystawienia = dataWystawienia;
    }

    public String getNazwaMechanika() {
        return nazwaMechanika;
    }

    public void setNazwaMechanika(String nazwaMechanika) {
        this.nazwaMechanika = nazwaMechanika;
    }

    public String getMarkaSamochodu() {
        return markaSamochodu;
    }

    public void setMarkaSamochodu(String markaSamochodu) {
        this.markaSamochodu = markaSamochodu;
    }

    public String getModelSamochodu() {
        return modelSamochodu;
    }

    public void setModelSamochodu(String modelSamochodu) {
        this.modelSamochodu = modelSamochodu;
    }

    public String getNumerRejestracyjny() {
        return numerRejestracyjny;
    }

    public void setNumerRejestracyjny(String numerRejestracyjny) {
        this.numerRejestracyjny = numerRejestracyjny;
    }

    public Double getCenaNaprawy() {
        return cenaNaprawy;
    }

    public void setCenaNaprawy(Double cenaNaprawy) {
        this.cenaNaprawy = cenaNaprawy;
    }
}
