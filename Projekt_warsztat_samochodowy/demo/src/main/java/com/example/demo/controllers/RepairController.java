package com.example.demo.controllers;

import com.example.demo.models.dtos.ReceiptDto;
import com.example.demo.models.dtos.RepairDto;
import com.example.demo.models.entities.Repair;
import com.example.demo.services.RepairService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/repairs")
public class RepairController {

    private final RepairService repairService;

    public RepairController(RepairService repairService) {
        this.repairService = repairService;
    }

    // 1. Tworzenie nowej naprawy
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PostMapping
    public ResponseEntity<RepairDto> createRepair(@RequestBody RepairDto repairDto) {
        RepairDto savedRepair = repairService.create(repairDto);
        return ResponseEntity.ok(savedRepair);
    }

    // 2. Pobieranie wszystkich napraw
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping
    public ResponseEntity<List<RepairDto>> getAllRepairs() {
        List<RepairDto> repairs = repairService.getAll();
        return ResponseEntity.ok(repairs);
    }
    // 3. Pobieranie naprawy po ID
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping("/{id}")
    public ResponseEntity<RepairDto> getRepairById(@PathVariable Integer id) {
        RepairDto repair = repairService.getById(id);
        return ResponseEntity.ok(repair);
    }

    // 4. Pełna aktualizacja naprawy (PUT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PutMapping("/{id}")
    public ResponseEntity<RepairDto> updateRepair(@PathVariable Integer id, @RequestBody RepairDto repairDto) {
        RepairDto updatedRepair = repairService.update(id, repairDto);
        return ResponseEntity.ok(updatedRepair);
    }

    // 5. Częściowa aktualizacja naprawy (PATCH)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PatchMapping("/{id}")
    public ResponseEntity<RepairDto> partialUpdateRepair(@PathVariable Integer id, @RequestBody RepairDto repairDto) {
        RepairDto updatedRepair = repairService.partialUpdate(id, repairDto);
        return ResponseEntity.ok(updatedRepair);
    }

    // 6. Usuwanie naprawy
    @PreAuthorize("hasAnyRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRepair(@PathVariable Integer id) {
        repairService.delete(id);
        return ResponseEntity.noContent().build();
    }



    // 7. Przypisanie mechanika do naprawy
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PatchMapping("/{id}/assign-mechanic")
    public ResponseEntity<RepairDto> assignMechanicToRepair(
            @PathVariable Integer id,
            @RequestParam Integer mechanicId) {
        RepairDto updatedRepair = repairService.assignMechanic(id, mechanicId);
        return ResponseEntity.ok(updatedRepair);
    }

    // 8. Pobieranie napraw przypisanych do mechanika
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping("/mechanic/{mechanicId}")
    public ResponseEntity<List<RepairDto>> getRepairsByMechanic(@PathVariable Integer mechanicId) {
        List<RepairDto> repairs = repairService.getRepairsByMechanic(mechanicId);
        return ResponseEntity.ok(repairs);
    }

    // 9. Pobieranie napraw przypisanych do mechanika z filtrem statusu
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping("/mechanic/{mechanicId}/status/{status}")
    public ResponseEntity<List<RepairDto>> getRepairsByMechanicAndStatus(
            @PathVariable Integer mechanicId,
            @PathVariable Repair.StatusNaprawy status) {
        List<RepairDto> repairs = repairService.getRepairsByMechanicAndStatus(mechanicId, status);
        return ResponseEntity.ok(repairs);
    }
    // 10. Podsumowanie napraw klienta (status + łączny koszt)
    @PreAuthorize("hasAnyRole('ADMIN', 'KLIENT')")
    @GetMapping("/client/{userId}/summary")
    public ResponseEntity<?> getClientRepairSummary(@PathVariable Integer userId) {
        return ResponseEntity.ok(repairService.getClientRepairSummary(userId));
    }
    // 11. Oznaczenie naprawy jako opłaconej
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PatchMapping("/{id}/mark-paid")
    public ResponseEntity<RepairDto> markRepairAsPaid(@PathVariable Integer id) {
        RepairDto updatedRepair = repairService.markAsPaid(id);
        return ResponseEntity.ok(updatedRepair);
    }

    //12.Paragon
    @PreAuthorize("hasAnyRole('ADMIN', 'KLIENT','MECHANIK')")
    @GetMapping("/{id}/receipt")
    public ResponseEntity<ReceiptDto> getRepairReceipt(@PathVariable Integer id) {
        ReceiptDto receipt = repairService.getReceiptForRepair(id);
        return ResponseEntity.ok(receipt);
    }
}
