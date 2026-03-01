package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/car")
public class CarController {

    private static final String REDIRECT_CAR_LIST = "redirect:listCar";
    private static final String VIEW_CREATE_CAR = "CreateCar";
    private static final String VIEW_CAR_LIST = "CarList";
    private static final String VIEW_EDIT_CAR = "EditCar";

    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @GetMapping("/createCar")
    public String createCarPage(Model model) {
        model.addAttribute("car", new Car());
        return VIEW_CREATE_CAR;
    }

    @PostMapping("/createCar")
    public String createCarPost(@ModelAttribute Car car) {
        if (isInvalidCarPayload(car, false)) {
            return REDIRECT_CAR_LIST;
        }

        try {
            carService.create(car);
        } catch (IllegalArgumentException ignored) {
            return REDIRECT_CAR_LIST;
        }
        return REDIRECT_CAR_LIST;
    }

    @GetMapping("/listCar")
    public String carListPage(Model model) {
        List<Car> allCars = carService.findAll();
        model.addAttribute("cars", allCars);
        return VIEW_CAR_LIST;
    }

    @GetMapping("/editCar/{carId}")
    public String editCarPage(@PathVariable String carId, Model model) {
        if (!StringUtils.hasText(carId)) {
            return REDIRECT_CAR_LIST;
        }

        return carService.findById(carId)
                .map(car -> {
                    model.addAttribute("car", car);
                    return VIEW_EDIT_CAR;
                })
                .orElse(REDIRECT_CAR_LIST);
    }

    @PostMapping("/editCar")
    public String editCarPost(@ModelAttribute Car car) {
        if (isInvalidCarPayload(car, true)) {
            return REDIRECT_CAR_LIST;
        }

        try {
            carService.update(car.getCarId(), car);
        } catch (IllegalArgumentException ignored) {
            return REDIRECT_CAR_LIST;
        }
        return REDIRECT_CAR_LIST;
    }

    @PostMapping("/deleteCar")
    public String deleteCar(@RequestParam("carId") String carId) {
        if (!StringUtils.hasText(carId)) {
            return REDIRECT_CAR_LIST;
        }

        try {
            carService.deleteCarById(carId);
        } catch (IllegalArgumentException ignored) {
            return REDIRECT_CAR_LIST;
        }
        return REDIRECT_CAR_LIST;
    }

    private boolean isInvalidCarPayload(Car car, boolean carIdRequired) {
        if (car == null) {
            return true;
        }
        if (carIdRequired && !StringUtils.hasText(car.getCarId())) {
            return true;
        }
        return !StringUtils.hasText(car.getCarName())
                || !StringUtils.hasText(car.getCarColor())
                || car.getCarQuantity() < 0;
    }
}
