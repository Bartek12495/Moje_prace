package com.example.demo.services;

import com.example.demo.models.dtos.CarDto;
import com.example.demo.models.entities.Car;
import com.example.demo.models.entities.User;
import com.example.demo.respositories.CarRepository;
import com.example.demo.respositories.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class CarService {

    private final CarRepository repository;
    private final UserRepository userRepository;

    public CarService(CarRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public CarDto save(CarDto carDto, Principal principal) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // ustaw owner w encji Car
        Car car = fromDto(carDto);
        car.setOwner(user);

        return CarDto.fromEntity(repository.save(car));
    }

    public List<CarDto> findAll() {
        return repository.findAll().stream().map(CarDto::fromEntity).collect(Collectors.toList());
    }

    public CarDto findById(Integer id) {
        return CarDto.fromEntity(
                repository.findById(id).orElseThrow(NoSuchElementException::new)
        );
    }
    public List<CarDto> findByUsername(String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        List<Car> cars = repository.findByOwner(user);
        return cars.stream().map(CarDto::fromEntity).collect(Collectors.toList());
    }
    public CarDto edit(CarDto carDto) {
        Car existing = repository.findById(carDto.getId()).orElseThrow(() -> new RuntimeException("Car not found"));

        User previousOwner = existing.getOwner();

        Car car = fromDto(carDto);
        if (carDto.getUserId() == null && previousOwner != null) {
            car.setOwner(previousOwner); // zachowaj dotychczasowego właściciela
        }

        return CarDto.fromEntity(repository.save(car));
    }

    public CarDto partialUpdate(Integer id, CarDto updateDto) {
        Car car = repository.findById(id)
                .orElseThrow(NoSuchElementException::new);

        if (updateDto.getMarka() != null) car.setMarka(updateDto.getMarka());
        if (updateDto.getModel() != null) car.setModel(updateDto.getModel());
        if (updateDto.getRokProdukcji() != 0) car.setRokProdukcji(updateDto.getRokProdukcji());
        if (updateDto.getNumerRejestracyjny() != null) car.setNumerRejestracyjny(updateDto.getNumerRejestracyjny());

        if (updateDto.getUserId() != null) {
            User user = userRepository.findById(updateDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            car.setOwner(user);
        }

        return CarDto.fromEntity(repository.save(car));
    }

    public void delete(Integer id) {
        repository.deleteById(id);
    }

    public List<CarDto> searchByMarkaOrModel(String marka, String model) {
        return repository.findAll().stream()
                .filter(car -> (marka == null || car.getMarka().equalsIgnoreCase(marka)) &&
                        (model == null || car.getModel().equalsIgnoreCase(model))).map(CarDto::fromEntity).collect(Collectors.toList());
    }

    private Car fromDto(CarDto dto) {
        Car car = new Car(dto.getId(), dto.getMarka(), dto.getModel(), dto.getRokProdukcji(), dto.getNumerRejestracyjny());
        if (dto.getUserId() != null) {
            User user = userRepository.findById(dto.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            car.setOwner(user);
        }
        return car;
    }
}
