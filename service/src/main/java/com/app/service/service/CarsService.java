package com.app.service.service;

import com.app.persistence.model.Car;
import com.app.persistence.model.converter.JsonCarsConverter;
import com.app.persistence.model.enums.Color;
import com.app.service.enums.SortCriterion;
import com.app.service.exception.CarsServiceException;
import com.app.service.validation.CarValidator;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CarsService {

    private final Set<Car> cars;

    public CarsService (String filename){
        this.cars = init(filename);
    }

    private Set<Car> init(String filename) {
        AtomicInteger counter = new AtomicInteger();
        return new JsonCarsConverter(filename)
                .fromJson()
                .orElseThrow(() -> new CarsServiceException("cannot parse date in json file"))
                .stream()
                .filter(car -> {
                    var carsValidator = new CarValidator();
                    var erros = carsValidator.validate(car);

                    if (carsValidator.hasErrors()) {
                        System.out.println("---------------------- Validation error -----------------");
                        System.out.println("---------------------- Car no. " + counter.get());
                        System.out.println(erros
                                .entrySet()
                                .stream()
                                .map(e -> e.getKey() + ": " + e.getValue())
                                .collect(Collectors.joining("\n"))
                        );
                        System.out.println("---------------------------------------------------------");
                    }
                    counter.incrementAndGet();
                    return !carsValidator.hasErrors();
                }).collect(Collectors.toSet());
    }


    public List<Car> sortCars(SortCriterion sortCriterion, boolean ascendingSort) {
        if (sortCriterion == null) {
            throw new CarsServiceException("sort criterion object is null");
        }
        Stream<Car> carStream = switch (sortCriterion) {
            case COLOR -> cars.stream().sorted(Comparator.comparing(Car::getColor));
            case MILEAGE -> cars.stream().sorted(Comparator.comparing(Car::getMileage));
            case MODEL -> cars.stream().sorted(Comparator.comparing(Car::getModel));
            default -> cars.stream().sorted(Comparator.comparing(Car::getPrice));
        };
        List<Car> sortedCars = carStream.collect(Collectors.toList());
        if (!ascendingSort) {
            Collections.reverse(sortedCars);
        }
        return sortedCars;
    }

    public List<Car> greaterMileage(double mileage) {
        if (mileage <= 0) {
            throw new CarsServiceException("mileage value is not correct: " + mileage);
        }
        return cars.stream()
                .filter(c -> c.getMileage() > mileage)
                .collect(Collectors.toList());
    }

    public Map<Color, Long> carColors() {
        return cars
                .stream()
                .collect(Collectors.groupingBy(Car::getColor, Collectors.counting()))
                .entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getValue, Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }


}
