package com.example.demo.respositories;

import com.example.demo.models.entities.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InspectionRepository extends JpaRepository<Inspection, Integer> {

    // Pobiera ostatni przegląd techniczny dla danego samochodu (po dacie malejąco)
    Optional<Inspection> findTopBySamochodIdOrderByDataPrzegladuDesc(Integer samochodId);
}
