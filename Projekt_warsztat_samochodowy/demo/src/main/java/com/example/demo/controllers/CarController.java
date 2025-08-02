package com.example.demo.controllers;

import com.example.demo.models.dtos.CarDto;
import com.example.demo.services.CarService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;


@RestController
@RequestMapping("/cars")// wszystkie metody w tej klasie będą dostępne pod adresem URL, który zaczyna się od /cars.
public class CarController {
    private final CarService service;

    // Konstruktor wstrzykujący zależność serwisu CarService
    public CarController(CarService service) {
        this.service = service;
    }

    // Tworzenie nowego samochodu (metoda POST)
    @PreAuthorize("hasAnyRole('KLIENT', 'ADMIN')")
    @PostMapping
    public ResponseEntity<CarDto> save(@RequestBody CarDto carDto, Principal principal) {
        return ResponseEntity.ok(service.save(carDto, principal));
    }
    // Pobieranie listy wszystkich samochodów (metoda GET)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping
    public ResponseEntity<List<CarDto>> findAll() {
        return ResponseEntity.ok(service.findAll());
    }

    // Pobieranie jednego samochodu po ID (metoda GET)
    @PreAuthorize("hasAnyRole('ADMIN', 'MECHANIK')")
    @GetMapping("/{id}")
    public ResponseEntity<CarDto> findById(@PathVariable Integer id) {
        return ResponseEntity.ok(service.findById(id));
    }

    // Pobieranie wszystkich samochodów danego klienta
    @PreAuthorize("hasAnyRole('KLIENT', 'ADMIN')")
    @GetMapping("/my")
    public ResponseEntity<List<CarDto>> getMyCars(Principal principal) {
        String login = principal.getName();
        return ResponseEntity.ok(service.findByUsername(login));
    }


    // Aktualizacja wszystkich danych samochodu po ID (metoda PUT)
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<CarDto> updateCar(@PathVariable Integer id, @RequestBody CarDto carDto) {
        carDto = new CarDto(id, carDto.getMarka(), carDto.getModel(), carDto.getRokProdukcji(), carDto.getNumerRejestracyjny());
        return ResponseEntity.ok(service.edit(carDto));
    }

    // Częściowa aktualizacja danych samochodu (metoda PATCH)
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}")
    public ResponseEntity<CarDto> partialUpdateCar(@PathVariable Integer id, @RequestBody CarDto carDto) {
        return ResponseEntity.ok(service.partialUpdate(id, carDto));
    }

    // Usuwanie samochodu po ID (metoda DELETE)
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        service.delete(id);
    }

    // Wyszukiwanie samochodów po marce lub modelu (metoda GET z parametrami)
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/search")
    public ResponseEntity<List<CarDto>> searchCars(@RequestParam(required = false) String marka, @RequestParam(required = false) String model) {
        return ResponseEntity.ok(service.searchByMarkaOrModel(marka, model));
    }
}
