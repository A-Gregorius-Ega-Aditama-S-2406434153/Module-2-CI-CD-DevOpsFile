package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;

import java.util.List;
import java.util.Optional;

public interface CarService {
    Car create(Car car);

    List<Car> findAll();

    Optional<Car> findById(String carId);

    Optional<Car> update(String carId, Car car);

    void deleteCarById(String carId);
}
