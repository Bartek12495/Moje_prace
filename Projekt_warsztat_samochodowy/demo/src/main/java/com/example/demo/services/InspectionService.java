package com.example.demo.services;

import com.example.demo.models.dtos.InspectionDto;
import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.Inspection;
import com.example.demo.models.entities.PriceList;
import com.example.demo.respositories.InspectionRepository;
import com.example.demo.respositories.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class InspectionService {

    private final InspectionRepository inspectionRepository;
    private final PriceListRepository priceListRepository;

    @Autowired
    public InspectionService(InspectionRepository inspectionRepository, PriceListRepository priceListRepository) {
        this.inspectionRepository = inspectionRepository;
        this.priceListRepository = priceListRepository;
    }

    // 1. Tworzenie nowego przeglądu
    public InspectionDto create(InspectionDto inspectionDto) {
        Inspection inspection = inspectionDto.toEntity();

        if (inspectionDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(inspectionDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                PriceList priceList = priceListOpt.get();
                inspection.setPriceList(priceList);
            } else {
                throw new RuntimeException("PriceList not found with ID: " + inspectionDto.getPriceListId());
            }
        }

        Inspection savedInspection = inspectionRepository.save(inspection);
        return InspectionDto.fromEntity(savedInspection);
    }

    // 2. Pobieranie przeglądu po ID
    public InspectionDto getById(Integer id) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));
        return InspectionDto.fromEntity(inspection);
    }

    // 3. Pełna aktualizacja przeglądu (PUT)
    public InspectionDto update(Integer id, InspectionDto inspectionDto) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        inspection.setDataPrzegladu(inspectionDto.getDataPrzegladu());
        inspection.setOpis(inspectionDto.getOpis());
        inspection.setZaliczony(inspectionDto.getZaliczony());

        if (inspectionDto.getSamochodId() != null) {
            Car car = new Car();
            car.setId(inspectionDto.getSamochodId());
            inspection.setSamochod(car);
        }

        if (inspectionDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(inspectionDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                PriceList priceList = priceListOpt.get();
                inspection.setPriceList(priceList);
            } else {
                throw new RuntimeException("PriceList not found with ID: " + inspectionDto.getPriceListId());
            }
        }

        Inspection updatedInspection = inspectionRepository.save(inspection);
        return InspectionDto.fromEntity(updatedInspection);
    }

    // 4. Częściowa aktualizacja przeglądu (PATCH)
    public InspectionDto partialUpdate(Integer id, InspectionDto inspectionDto) {
        Inspection inspection = inspectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Inspection not found"));

        if (inspectionDto.getDataPrzegladu() != null) inspection.setDataPrzegladu(inspectionDto.getDataPrzegladu());
        if (inspectionDto.getOpis() != null) inspection.setOpis(inspectionDto.getOpis());
        if (inspectionDto.getZaliczony() != null) inspection.setZaliczony(inspectionDto.getZaliczony());

        if (inspectionDto.getSamochodId() != null) {
            Car car = new Car();
            car.setId(inspectionDto.getSamochodId());
            inspection.setSamochod(car);
        }

        if (inspectionDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(inspectionDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                PriceList priceList = priceListOpt.get();
                inspection.setPriceList(priceList);
            } else {
                throw new RuntimeException("PriceList not found with ID: " + inspectionDto.getPriceListId());
            }
        }

        Inspection updatedInspection = inspectionRepository.save(inspection);
        return InspectionDto.fromEntity(updatedInspection);
    }

    // 5. Usuwanie przeglądu
    public void delete(Integer id) {
        inspectionRepository.deleteById(id);
    }

    // 6. Pobieranie wszystkich przeglądów
    public List<InspectionDto> getAll() {
        List<Inspection> inspections = inspectionRepository.findAll();
        return inspections.stream()
                .map(InspectionDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 7. Podsumowanie przeglądu samochodu dla klienta (status, koszt, data)
    public Map<String, Object> getInspectionSummaryByCarId(Integer carId) {
        Optional<Inspection> optionalInspection = inspectionRepository.findAll().stream()
                .filter(i -> i.getSamochod() != null && i.getSamochod().getId().equals(carId))
                .max(Comparator.comparing(Inspection::getDataPrzegladu)); // najnowszy przegląd

        if (optionalInspection.isEmpty()) {
            throw new RuntimeException("No inspection found for car with ID: " + carId);
        }

        Inspection inspection = optionalInspection.get();

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("zaliczony", inspection.getZaliczony());
        summary.put("dataPrzegladu", inspection.getDataPrzegladu());
        summary.put("dataWaznosci",inspection.getDataWaznosci());
        summary.put("koszt", inspection.getKoszt());

        return summary;
    }
}
