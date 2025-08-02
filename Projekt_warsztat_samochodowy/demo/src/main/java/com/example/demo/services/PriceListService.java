package com.example.demo.services;

import com.example.demo.models.dtos.PriceListDto;
import com.example.demo.models.entities.PriceList;
import com.example.demo.respositories.PriceListRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PriceListService {

    private final PriceListRepository priceListRepository;

    @Autowired
    public PriceListService(PriceListRepository priceListRepository) {
        this.priceListRepository = priceListRepository;
    }

    // Utwórz nowy wpis
    public PriceListDto create(PriceListDto dto) {
        PriceList entity = dto.toEntity();
        PriceList saved = priceListRepository.save(entity);
        return PriceListDto.fromEntity(saved);
    }

    // Pobierz po ID
    public PriceListDto getById(Integer id) {
        PriceList entity = priceListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cennik nie znaleziony"));
        return PriceListDto.fromEntity(entity);
    }

    // Aktualizacja pełna
    public PriceListDto update(Integer id, PriceListDto dto) {
        PriceList entity = priceListRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cennik nie znaleziony"));

        entity.setNazwaUslugi(dto.getNazwaUslugi());
        entity.setCena(dto.getCena());

        PriceList updated = priceListRepository.save(entity);
        return PriceListDto.fromEntity(updated);
    }

    // Usuwanie
    public void delete(Integer id) {
        priceListRepository.deleteById(id);
    }

    // Pobierz wszystkie
    public List<PriceListDto> getAll() {
        return priceListRepository.findAll()
                .stream()
                .map(PriceListDto::fromEntity)
                .collect(Collectors.toList());
    }
}
