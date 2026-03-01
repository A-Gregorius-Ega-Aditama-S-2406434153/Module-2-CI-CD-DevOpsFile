package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class InMemoryCarRepository implements CarRepository {

    private final List<Car> carData = new ArrayList<>();

    @Override
    public Car create(Car car) {
        carData.add(car);
        return car;
    }

    @Override
    public List<Car> findAll() {
        return new ArrayList<>(carData);
    }

    @Override
    public Optional<Car> findById(String carId) {
        for (Car car : carData) {
            if (car.getCarId() != null && car.getCarId().equals(carId)) {
                return Optional.of(car);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Car> update(String carId, Car updatedCar) {
        for (Car car : carData) {
            if (car.getCarId() != null && car.getCarId().equals(carId)) {
                car.setCarName(updatedCar.getCarName());
                car.setCarColor(updatedCar.getCarColor());
                car.setCarQuantity(updatedCar.getCarQuantity());
                return Optional.of(car);
            }
        }
        return Optional.empty();
    }

    @Override
    public void deleteById(String carId) {
        carData.removeIf(car -> car.getCarId() != null && car.getCarId().equals(carId));
    }
}
