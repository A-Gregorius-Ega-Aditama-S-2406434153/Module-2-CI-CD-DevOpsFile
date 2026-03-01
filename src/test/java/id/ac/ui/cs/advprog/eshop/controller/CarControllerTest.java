package id.ac.ui.cs.advprog.eshop.controller;

import id.ac.ui.cs.advprog.eshop.model.Car;
import id.ac.ui.cs.advprog.eshop.service.CarService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(CarController.class)
class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CarService carService;

    @Test
    void createCarPageReturnsCreateCarView() throws Exception {
        mockMvc.perform(get("/car/createCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CreateCar"))
                .andExpect(model().attributeExists("car"));
    }

    @Test
    void createCarPostRedirectsToListCar() throws Exception {
        Car createdCar = buildCar("car-1", "Toyota", "Black", 5);
        when(carService.create(any(Car.class))).thenReturn(createdCar);

        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Toyota")
                        .param("carColor", "Black")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).create(any(Car.class));
    }

    @Test
    void createCarPostRejectsBlankName() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", " ")
                        .param("carColor", "Black")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).create(any(Car.class));
    }

    @Test
    void createCarPostRejectsBlankColor() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Toyota")
                        .param("carColor", " ")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).create(any(Car.class));
    }

    @Test
    void createCarPostRejectsNegativeQuantity() throws Exception {
        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Toyota")
                        .param("carColor", "Black")
                        .param("carQuantity", "-1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).create(any(Car.class));
    }

    @Test
    void createCarPostRedirectsSafelyWhenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("invalid")).when(carService).create(any(Car.class));

        mockMvc.perform(post("/car/createCar")
                        .param("carName", "Toyota")
                        .param("carColor", "Black")
                        .param("carQuantity", "5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).create(any(Car.class));
    }

    @Test
    void carListPageReturnsCarListView() throws Exception {
        Car car = buildCar("car-2", "Honda", "White", 8);
        when(carService.findAll()).thenReturn(Collections.singletonList(car));

        mockMvc.perform(get("/car/listCar"))
                .andExpect(status().isOk())
                .andExpect(view().name("CarList"))
                .andExpect(model().attributeExists("cars"));

        verify(carService).findAll();
    }

    @Test
    void editCarPageReturnsEditCarViewWhenFound() throws Exception {
        Car car = buildCar("car-3", "Mazda", "Yellow", 1);
        when(carService.findById("car-3")).thenReturn(Optional.of(car));

        mockMvc.perform(get("/car/editCar/{carId}", "car-3"))
                .andExpect(status().isOk())
                .andExpect(view().name("EditCar"))
                .andExpect(model().attribute("car", car));

        verify(carService).findById("car-3");
    }

    @Test
    void editCarPageRedirectsWhenCarIdBlank() throws Exception {
        mockMvc.perform(get("/car/editCar/{carId}", " "))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).findById(any(String.class));
    }

    @Test
    void postEditCarRedirectsToListCar() throws Exception {
        Car updatedCar = buildCar("car-4", "BMW", "Blue", 3);
        when(carService.update(eq("car-4"), any(Car.class))).thenReturn(Optional.of(updatedCar));

        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-4")
                        .param("carName", "BMW")
                        .param("carColor", "Blue")
                        .param("carQuantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).update(eq("car-4"), any(Car.class));
    }

    @Test
    void postEditCarRedirectsWhenPayloadInvalid() throws Exception {
        mockMvc.perform(post("/car/editCar")
                        .param("carId", " ")
                        .param("carName", "BMW")
                        .param("carColor", "Blue")
                        .param("carQuantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).update(any(String.class), any(Car.class));
    }

    @Test
    void postEditCarRedirectsSafelyWhenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("invalid"))
                .when(carService).update(eq("car-4"), any(Car.class));

        mockMvc.perform(post("/car/editCar")
                        .param("carId", "car-4")
                        .param("carName", "BMW")
                        .param("carColor", "Blue")
                        .param("carQuantity", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).update(eq("car-4"), any(Car.class));
    }

    @Test
    void postDeleteCarRedirectsToListCar() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", "car-5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).deleteCarById("car-5");
    }

    @Test
    void postDeleteCarWithBlankCarIdRedirectsSafely() throws Exception {
        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", " "))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService, never()).deleteCarById(any(String.class));
    }

    @Test
    void postDeleteCarRedirectsSafelyWhenServiceThrows() throws Exception {
        doThrow(new IllegalArgumentException("invalid")).when(carService).deleteCarById("car-5");

        mockMvc.perform(post("/car/deleteCar")
                        .param("carId", "car-5"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:listCar"));

        verify(carService).deleteCarById("car-5");
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
