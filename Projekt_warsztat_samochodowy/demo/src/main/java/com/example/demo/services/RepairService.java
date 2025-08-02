package com.example.demo.services;

import com.example.demo.models.dtos.ReceiptDto;
import com.example.demo.models.dtos.RepairDto;
import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.PriceList;
import com.example.demo.models.entities.Repair;
import com.example.demo.models.entities.User;
import com.example.demo.respositories.PriceListRepository;
import com.example.demo.respositories.RepairRepository;
import com.example.demo.respositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RepairService {

    private final RepairRepository repairRepository;
    private final PriceListRepository priceListRepository;
    private final UserRepository userRepository;

    @Autowired
    public RepairService(RepairRepository repairRepository,
                         PriceListRepository priceListRepository,
                         UserRepository userRepository) {
        this.repairRepository = repairRepository;
        this.priceListRepository = priceListRepository;
        this.userRepository = userRepository;
    }

    // 1. Tworzenie nowej naprawy
    public RepairDto create(RepairDto repairDto) {
        Repair repair = repairDto.toEntity();

        if (repairDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(repairDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                repair.setPriceList(priceListOpt.get());
            } else {
                throw new RuntimeException("PriceList not found");
            }
        }

        Repair savedRepair = repairRepository.save(repair);
        return RepairDto.fromEntity(savedRepair);
    }

    // 2. Pobieranie wszystkich napraw
    public List<RepairDto> getAll() {
        return repairRepository.findAll()
                .stream()
                .map(RepairDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 3. Pobieranie naprawy po ID
    public RepairDto getById(Integer id) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found"));
        return RepairDto.fromEntity(repair);
    }

    // 4. Pełna aktualizacja naprawy
    public RepairDto update(Integer id, RepairDto repairDto) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        repair.setOpis(repairDto.getOpis());
        repair.setStatus(repairDto.getStatus());
        repair.setSzacowanyCzas(repairDto.getSzacowanyCzas());

        if (repairDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(repairDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                repair.setPriceList(priceListOpt.get());
            } else {
                throw new RuntimeException("PriceList not found");
            }
        }

        if (repairDto.getSamochodId() != null) {
            Car car = new Car();
            car.setId(repairDto.getSamochodId());
            repair.setSamochod(car);
        }

        Repair updatedRepair = repairRepository.save(repair);
        return RepairDto.fromEntity(updatedRepair);
    }

    // 5. Częściowa aktualizacja naprawy
    public RepairDto partialUpdate(Integer id, RepairDto repairDto) {
        Repair repair = repairRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        if (repairDto.getOpis() != null) repair.setOpis(repairDto.getOpis());
        if (repairDto.getStatus() != null) repair.setStatus(repairDto.getStatus());
        if (repairDto.getSzacowanyCzas() != null) repair.setSzacowanyCzas(repairDto.getSzacowanyCzas());

        if (repairDto.getPriceListId() != null) {
            Optional<PriceList> priceListOpt = priceListRepository.findById(repairDto.getPriceListId());
            if (priceListOpt.isPresent()) {
                repair.setPriceList(priceListOpt.get());
            } else {
                throw new RuntimeException("PriceList not found");
            }
        }

        if (repairDto.getSamochodId() != null) {
            Car car = new Car();
            car.setId(repairDto.getSamochodId());
            repair.setSamochod(car);
        }

        Repair updatedRepair = repairRepository.save(repair);
        return RepairDto.fromEntity(updatedRepair);
    }

    // 6. Usuwanie naprawy
    public void delete(Integer id) {
        repairRepository.deleteById(id);
    }

    // 7. Przypisanie mechanika do naprawy
    public RepairDto assignMechanic(Integer repairId, Integer mechanicId) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Repair not found"));

        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic (User) not found"));

        repair.setMechanic(mechanic);
        Repair updatedRepair = repairRepository.save(repair);
        return RepairDto.fromEntity(updatedRepair);
    }

    // 8. Pobieranie napraw przypisanych do konkretnego mechanika
    public List<RepairDto> getRepairsByMechanic(Integer mechanicId) {
        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic (User) not found"));

        return repairRepository.findByMechanic(mechanic)
                .stream()
                .map(RepairDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 9. Pobieranie napraw przypisanych do mechanika o określonym statusie
    public List<RepairDto> getRepairsByMechanicAndStatus(Integer mechanicId, Repair.StatusNaprawy status) {
        User mechanic = userRepository.findById(mechanicId)
                .orElseThrow(() -> new RuntimeException("Mechanic (User) not found"));

        return repairRepository.findByMechanicAndStatus(mechanic, status)
                .stream()
                .map(RepairDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 10. Podsumowanie napraw klienta (statusy + łączny koszt)
    public Map<String, Object> getClientRepairSummary(Integer userId) {
        List<Repair> repairs = repairRepository.findBySamochodOwnerId(userId);

        double totalCost = repairs.stream()
                .filter(r -> r.getStatus() != Repair.StatusNaprawy.ZAKONCZONA)
                .mapToDouble(r -> r.getKoszt() != null ? r.getKoszt() : 0)
                .sum();

        List<Map<String, Object>> repairList = repairs.stream().map(r -> {
            Map<String, Object> item = new HashMap<>();
            item.put("carId", r.getSamochod().getId());
            item.put("repairId", r.getId());
            item.put("status", r.getStatus().name());
            item.put("cena", r.getKoszt());
            return item;
        }).collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("Kosztnaprawycalkowity", totalCost);
        result.put("repairs", repairList);
        return result;
    }
    // 11. Oznacz naprawę jako opłaconą i zakończoną
    public RepairDto markAsPaid(Integer repairId) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Naprawa nie znaleziona"));

        // Sprawdź czy status to DO_ZAPLATY
        if (repair.getStatus() != Repair.StatusNaprawy.ZAKONCZONA_DO_ZAPLATY) {
            throw new IllegalStateException("Naprawa nie oczekuje na zapłatę.");
        }

        // Zmień status na ZAKONCZONA
        repair.setStatus(Repair.StatusNaprawy.ZAKONCZONA);
        Repair updated = repairRepository.save(repair);

        return RepairDto.fromEntity(updated);
    }
    //12.Paragon za usluge naprawy
    public ReceiptDto getReceiptForRepair(Integer repairId) {
        Repair repair = repairRepository.findById(repairId)
                .orElseThrow(() -> new RuntimeException("Naprawa nie została znaleziona"));

        if (repair.getMechanic() == null || repair.getSamochod() == null) {
            throw new RuntimeException("Brakuje danych mechanika lub samochodu");
        }

        ReceiptDto paragon = new ReceiptDto();
        paragon.setDataWystawienia(repair.getDataUtworzenia().toLocalDate());
        paragon.setNazwaMechanika(repair.getMechanic().getUsername());
        paragon.setMarkaSamochodu(repair.getSamochod().getMarka());
        paragon.setModelSamochodu(repair.getSamochod().getModel());
        paragon.setNumerRejestracyjny(repair.getSamochod().getNumerRejestracyjny());
        paragon.setCenaNaprawy(repair.getKoszt());

        return paragon;
    }
}
