package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
public class CarServiceImpl implements CarService {

    private final CarRepository carRepository;

    public CarServiceImpl(CarRepository carRepository) {
        this.carRepository = Objects.requireNonNull(carRepository, "carRepository must not be null");
    }

    @Override
    public Car create(Car car) {
        validateCarPayload(car, false);
        if (!StringUtils.hasText(car.getCarId())) {
            car.setCarId(UUID.randomUUID().toString());
        }
        return carRepository.create(car);
    }

    @Override
    public List<Car> findAll() {
        return carRepository.findAll();
    }

    @Override
    public Optional<Car> findById(String carId) {
        validateCarId(carId);
        return carRepository.findById(carId);
    }

    @Override
    public Optional<Car> update(String carId, Car car) {
        validateCarId(carId);
        validateCarPayload(car, true);
        if (!carId.equals(car.getCarId())) {
            throw new IllegalArgumentException("carId must match the payload carId");
        }
        return carRepository.update(carId, car);
    }

    @Override
    public void deleteCarById(String carId) {
        validateCarId(carId);
        carRepository.deleteById(carId);
    }

    private void validateCarId(String carId) {
        if (!StringUtils.hasText(carId)) {
            throw new IllegalArgumentException("carId must not be blank");
        }
    }

    private void validateCarPayload(Car car, boolean requireCarId) {
        if (car == null) {
            throw new IllegalArgumentException("car must not be null");
        }
        if (requireCarId && !StringUtils.hasText(car.getCarId())) {
            throw new IllegalArgumentException("carId must not be blank");
        }
        if (!StringUtils.hasText(car.getCarName())) {
            throw new IllegalArgumentException("carName must not be blank");
        }
        if (!StringUtils.hasText(car.getCarColor())) {
            throw new IllegalArgumentException("carColor must not be blank");
        }
        if (car.getCarQuantity() < 0) {
            throw new IllegalArgumentException("carQuantity must not be negative");
        }
    }
}
