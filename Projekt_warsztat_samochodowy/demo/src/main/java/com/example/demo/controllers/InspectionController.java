package com.example.demo.controllers;

import com.example.demo.models.dtos.InspectionDto;
import com.example.demo.services.InspectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/inspections")
public class InspectionController {

    private final InspectionService inspectionService;

    @Autowired
    public InspectionController(InspectionService inspectionService) {
        this.inspectionService = inspectionService;
    }

    // 1. Tworzenie nowego przeglądu
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PostMapping
    public ResponseEntity<InspectionDto> create(@RequestBody InspectionDto inspectionDto) {
        InspectionDto createdInspection = inspectionService.create(inspectionDto);
        return new ResponseEntity<>(createdInspection, HttpStatus.CREATED);
    }

    // 2. Pobieranie wszystkich przeglądów
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping
    public ResponseEntity<List<InspectionDto>> getAll() {
        List<InspectionDto> inspections = inspectionService.getAll();
        return new ResponseEntity<>(inspections, HttpStatus.OK);
    }

    // 3. Pobieranie przeglądu po ID
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping("/{id}")
    public ResponseEntity<InspectionDto> getById(@PathVariable Integer id) {
        InspectionDto inspectionDto = inspectionService.getById(id);
        return new ResponseEntity<>(inspectionDto, HttpStatus.OK);
    }

    // 4. Pełna aktualizacja przeglądu (PUT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PutMapping("/{id}")
    public ResponseEntity<InspectionDto> update(@PathVariable Integer id, @RequestBody InspectionDto inspectionDto) {
        InspectionDto updatedInspection = inspectionService.update(id, inspectionDto);
        return new ResponseEntity<>(updatedInspection, HttpStatus.OK);
    }

    // 5. Częściowa aktualizacja przeglądu (PATCH)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PatchMapping("/{id}")
    public ResponseEntity<InspectionDto> partialUpdate(@PathVariable Integer id, @RequestBody InspectionDto inspectionDto) {
        InspectionDto updatedInspection = inspectionService.partialUpdate(id, inspectionDto);
        return new ResponseEntity<>(updatedInspection, HttpStatus.OK);
    }

    // 6. Usuwanie przeglądu
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        inspectionService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // 7. Pobieranie przeglądu samochodu dla klienta
    @PreAuthorize("hasAnyRole('ADMIN', 'KLIENT')")
    @GetMapping("/cars/{carId}/summary")
    public ResponseEntity<Map<String, Object>> getSummaryByCarId(@PathVariable Integer carId) {
        Map<String, Object> summary = inspectionService.getInspectionSummaryByCarId(carId);
        return new ResponseEntity<>(summary, HttpStatus.OK);
    }

}
