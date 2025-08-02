package com.example.demo.controllers;

import com.example.demo.models.dtos.PriceListDto;
import com.example.demo.services.PriceListService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pricelist")
public class PriceListController {

    private final PriceListService priceListService;

    @Autowired
    public PriceListController(PriceListService priceListService) {
        this.priceListService = priceListService;
    }

    // Utwórz nowy wpis w cenniku
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PostMapping
    public PriceListDto create(@RequestBody PriceListDto dto) {
        return priceListService.create(dto);
    }

    // Pobierz wszystkie wpisy
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK', 'KLIENT')")
    @GetMapping
    public List<PriceListDto> getAll() {
        return priceListService.getAll();
    }

    // Pobierz wpis po ID
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK', 'KLIENT')")
    @GetMapping("/{id}")
    public PriceListDto getById(@PathVariable Integer id) {
        return priceListService.getById(id);
    }


    // Aktualizuj cały wpis (PUT)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @PutMapping("/{id}")
    public PriceListDto update(@PathVariable Integer id, @RequestBody PriceListDto dto) {
        return priceListService.update(id, dto);
    }

    // Usuń wpis
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        priceListService.delete(id);
    }
}
