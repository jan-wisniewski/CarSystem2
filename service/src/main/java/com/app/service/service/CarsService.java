package com.app.service.service;

import com.app.persistence.model.Car;
import com.app.persistence.converter.JsonCarsConverter;
import com.app.persistence.enums.Color;
import com.app.service.enums.SortCriterion;
import com.app.service.exception.CarsServiceException;
import com.app.service.validation.CarValidator;
import org.eclipse.collections.impl.collector.BigDecimalSummaryStatistics;
import org.eclipse.collections.impl.collector.Collectors2;

import java.math.BigDecimal;
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
                    var errors = carsValidator.validate(car);

                    if (carsValidator.hasErrors()) {
                        System.out.println("---------------------- Validation error -----------------");
                        System.out.println("---------------------- Car no. " + counter.get());
                        System.out.println(errors
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
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (v1, v2) -> v1,
                        LinkedHashMap::new
                ));
    }

    public Map<String, List<Car>> mostExpensiveModel() {
        return cars
                .stream()
                .collect(Collectors.groupingBy(Car::getModel))
                .entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> e.getValue()
                                .stream()
                                .collect(Collectors.groupingBy(Car::getPrice))
                                .entrySet().stream()
                                .max(Map.Entry.comparingByKey())
                                .orElseThrow()
                                .getValue()
                ));
    }

    public String priceStats() {
        StringBuilder sb = new StringBuilder();
        BigDecimalSummaryStatistics prices = cars
                .stream()
                .collect(Collectors2.summarizingBigDecimal(Car::getPrice));
        return sb.append("Average cars price: " + prices.getAverage()).append("\n")
                .append("The Cheapest Car: " + prices.getMin()).append("\n")
                .append("The Most Expensive Car: " + prices.getMax()).append("\n").toString();
    }

    public String mileageStats() {
        StringBuilder sb = new StringBuilder();
        DoubleSummaryStatistics carStats = cars
                .stream()
                .collect(Collectors.summarizingDouble(Car::getMileage));
        return sb.append("Average cars mileage: " + carStats.getAverage()).append("\n")
                .append("The biggest mileage: " + carStats.getMax()).append("\n")
                .append("The smallest mileage: " + carStats.getMin()).append("\n").toString();
    }

    public List<Car> mostExpensive() {
        return cars
                .stream()
                .filter(c -> c.getPrice().equals(biggestPrice()))
                .collect(Collectors.toList());
    }

    public BigDecimal biggestPrice() {
        return cars.stream()
                .sorted(Comparator.comparing(Car::getPrice, Comparator.reverseOrder()))
                .map(Car::getPrice)
                .findFirst()
                .orElseThrow();
    }

    public Set<Car> inPriceRange(BigDecimal priceFrom, BigDecimal priceTo) {
        if (priceFrom==null || priceTo == null){
            throw new CarsServiceException("One of the argument is null");
        }
        if (priceTo.compareTo(priceFrom) < 0 ){
            throw new CarsServiceException("Price to is smaller than price from");
        }
        return cars
                .stream()
                .filter(c -> c.getPrice().compareTo(priceFrom) > 0 && c.getPrice().compareTo(priceTo) < 0)
                .sorted(Comparator.comparing(Car::getModel))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public Map<String, List<Car>> componentsInCars() {
        return cars
                .stream()
                .flatMap(car -> car.getComponents().stream())
                .collect(Collectors.toMap(c -> c,
                        c -> cars.stream()
                                .filter(car -> car.getComponents().contains(c))
                                .collect(Collectors.toList()),
                        (s1,s2) -> s1,
                        LinkedHashMap::new)
                );
    }

    public List<Car> sortComponents() {
        return cars
                .stream()
                .peek(car -> car.setComponents(car.getComponents()
                        .stream()
                        .sorted()
                        .collect(Collectors.toCollection(LinkedHashSet::new)))
                )
                .collect(Collectors.toList());
    }

    @Override
    public String toString() {
        return cars.stream()
                .map(c -> c.getModel() + " ($" + c.getPrice() +
                        ", mileage: " + c.getMileage() +
                        ", components: " + c.getComponents())
                .collect(Collectors.joining("\n"));
    }
}