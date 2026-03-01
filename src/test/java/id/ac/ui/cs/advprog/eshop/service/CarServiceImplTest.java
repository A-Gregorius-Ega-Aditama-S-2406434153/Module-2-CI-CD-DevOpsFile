package id.ac.ui.cs.advprog.eshop.service;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.repository.CarRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceImplTest {

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private CarServiceImpl carService;

    @Test
    void createGeneratesIdWhenMissingAndSaves() {
        Car car = buildCar(null, "Toyota Supra", "Red", 2);
        when(carRepository.create(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Car createdCar = carService.create(car);

        ArgumentCaptor<Car> carCaptor = ArgumentCaptor.forClass(Car.class);
        verify(carRepository).create(carCaptor.capture());
        assertNotNull(createdCar.getCarId());
        assertFalse(createdCar.getCarId().isBlank());
        assertEquals(createdCar.getCarId(), carCaptor.getValue().getCarId());
    }

    @Test
    void createKeepsExistingId() {
        Car car = buildCar("car-1", "BMW M3", "Blue", 3);
        when(carRepository.create(any(Car.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Car createdCar = carService.create(car);

        assertEquals("car-1", createdCar.getCarId());
        verify(carRepository).create(car);
    }

    @Test
    void createRejectsNegativeQuantity() {
        Car car = buildCar(null, "Honda Civic", "Gray", -1);

        assertThrows(IllegalArgumentException.class, () -> carService.create(car));
        verify(carRepository, never()).create(any(Car.class));
    }

    @Test
    void findAllReturnsListFromRepository() {
        Car first = buildCar("car-1", "Toyota", "Black", 10);
        Car second = buildCar("car-2", "Suzuki", "White", 20);
        when(carRepository.findAll()).thenReturn(Arrays.asList(first, second));

        List<Car> result = carService.findAll();

        assertEquals(2, result.size());
        assertEquals("car-1", result.get(0).getCarId());
        assertEquals("car-2", result.get(1).getCarId());
        verify(carRepository).findAll();
    }

    @Test
    void findByIdReturnsExpectedCar() {
        Car car = buildCar("car-3", "Mazda", "Yellow", 1);
        when(carRepository.findById("car-3")).thenReturn(Optional.of(car));

        Optional<Car> foundCar = carService.findById("car-3");

        assertTrue(foundCar.isPresent());
        assertEquals("car-3", foundCar.get().getCarId());
        verify(carRepository).findById("car-3");
    }

    @Test
    void findByIdReturnsEmptyWhenMissing() {
        when(carRepository.findById("missing-id")).thenReturn(Optional.empty());

        Optional<Car> foundCar = carService.findById("missing-id");

        assertTrue(foundCar.isEmpty());
        verify(carRepository).findById("missing-id");
    }

    @Test
    void findByIdRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> carService.findById(" "));
        verify(carRepository, never()).findById(any(String.class));
    }

    @Test
    void updateReturnsUpdatedCarWhenFound() {
        Car updatedCar = buildCar("car-4", "Nissan GTR", "Silver", 4);
        when(carRepository.update("car-4", updatedCar)).thenReturn(Optional.of(updatedCar));

        Optional<Car> result = carService.update("car-4", updatedCar);

        assertTrue(result.isPresent());
        assertEquals("Nissan GTR", result.get().getCarName());
        verify(carRepository).update("car-4", updatedCar);
    }

    @Test
    void updateReturnsEmptyWhenMissing() {
        Car updatedCar = buildCar("missing-id", "Nissan GTR", "Silver", 4);
        when(carRepository.update("missing-id", updatedCar)).thenReturn(Optional.empty());

        Optional<Car> result = carService.update("missing-id", updatedCar);

        assertTrue(result.isEmpty());
        verify(carRepository).update("missing-id", updatedCar);
    }

    @Test
    void updateRejectsMismatchedCarId() {
        Car updatedCar = buildCar("car-in-body", "Audi", "Black", 2);

        assertThrows(IllegalArgumentException.class, () -> carService.update("car-in-path", updatedCar));
        verify(carRepository, never()).update(any(String.class), any(Car.class));
    }

    @Test
    void deleteCarByIdCallsRepositoryDeleteOnce() {
        carService.deleteCarById("car-5");

        verify(carRepository).deleteById("car-5");
    }

    @Test
    void deleteCarByIdRejectsBlankId() {
        assertThrows(IllegalArgumentException.class, () -> carService.deleteCarById(" "));
        verify(carRepository, never()).deleteById(any(String.class));
    }

    private Car buildCar(String id, String name, String color, int quantity) {
        Car car = new Car();
        car.setCarId(id);
        car.setCarName(name);
        car.setCarColor(color);
        car.setCarQuantity(quantity);
        return car;
    }
}
