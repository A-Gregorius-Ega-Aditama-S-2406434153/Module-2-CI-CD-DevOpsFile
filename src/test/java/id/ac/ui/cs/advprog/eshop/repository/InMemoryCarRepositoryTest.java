package id.ac.ui.cs.advprog.eshop.repository;

import id.ac.ui.cs.advprog.eshop.model.Car;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InMemoryCarRepositoryTest {

    private InMemoryCarRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCarRepository();
    }

    @Test
    void saveAndFindAllWorkCorrectly() {
        Car first = buildCar("car-1", "Toyota", "Black", 5);
        Car second = buildCar("car-2", "Honda", "White", 8);

        repository.create(first);
        repository.create(second);
        List<Car> cars = repository.findAll();

        assertEquals(2, cars.size());
        assertEquals("car-1", cars.get(0).getCarId());
        assertEquals("car-2", cars.get(1).getCarId());
    }

    @Test
    void findByIdReturnsStoredCar() {
        Car car = buildCar("car-3", "Mazda", "Yellow", 2);
        repository.create(car);

        Optional<Car> found = repository.findById("car-3");

        assertTrue(found.isPresent());
        assertEquals("Mazda", found.get().getCarName());
    }

    @Test
    void findByIdReturnsEmptyForMissingId() {
        Optional<Car> found = repository.findById("missing-id");
        assertTrue(found.isEmpty());
    }

    @Test
    void findByIdSkipsCarsWithNullId() {
        repository.create(buildCar(null, "NoId", "Green", 1));

        Optional<Car> found = repository.findById("some-id");

        assertTrue(found.isEmpty());
    }

    @Test
    void updateModifiesStoredInstance() {
        Car stored = buildCar("car-4", "BMW", "Blue", 3);
        repository.create(stored);
        Car updated = buildCar("car-4", "BMW M4", "Gray", 6);

        Optional<Car> result = repository.update("car-4", updated);

        assertTrue(result.isPresent());
        assertEquals("BMW M4", stored.getCarName());
        assertEquals("Gray", stored.getCarColor());
        assertEquals(6, stored.getCarQuantity());
    }

    @Test
    void updateNonExistentIdReturnsEmpty() {
        Car updated = buildCar("missing-id", "Tesla", "White", 1);

        Optional<Car> result = repository.update("missing-id", updated);

        assertTrue(result.isEmpty());
    }

    @Test
    void updateReturnsEmptyWhenStoredCarIdIsNull() {
        repository.create(buildCar(null, "NoId", "Green", 1));
        Car updated = buildCar("car-7", "Tesla", "White", 1);

        Optional<Car> result = repository.update("car-7", updated);

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteRemovesStoredCar() {
        Car car = buildCar("car-5", "Audi", "Red", 4);
        repository.create(car);

        repository.deleteById("car-5");

        assertTrue(repository.findById("car-5").isEmpty());
        assertEquals(0, repository.findAll().size());
    }

    @Test
    void deleteNonExistentIdIsNoOp() {
        repository.create(buildCar("car-6", "Hyundai", "Silver", 7));

        repository.deleteById("missing-id");

        assertFalse(repository.findAll().isEmpty());
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void deleteWithNullIdIsNoOp() {
        repository.create(buildCar("car-8", "Kia", "Black", 2));

        repository.deleteById(null);

        assertEquals(1, repository.findAll().size());
        assertTrue(repository.findById("car-8").isPresent());
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
