package com.app.ui.menu;

import com.app.persistence.model.Car;
import com.app.service.enums.SortCriterion;
import com.app.service.service.CarsService;
import com.app.ui.user_data.UserDataService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@RequiredArgsConstructor
public final class MenuService {

    private final CarsService carsService;

    public void mainMenu() {
        while (true) {
            try {
                System.out.println("0. Exit");
                System.out.println("1. Show all cars");
                System.out.println("2. Sort cars");
                System.out.println("3. Find cars with greater mileage");
                System.out.println("4. Count cars with specific color");
                System.out.println("5. Find most expensive models");
                System.out.println("6. Show cars statistics");
                System.out.println("7. Most Expensive Car/s");
                System.out.println("8. Cars with sorted components");
                System.out.println("9. Components and car list");
                System.out.println("10. Cars in price range");
                int decision = UserDataService.getInteger("Choose option:");
                switch (decision) {
                    case 0 -> {
                        System.out.println("Goodbye");
                        return;
                    }
                    case 1 -> option1();
                    case 2 -> option2();
                    case 3 -> option3();
                    case 4 -> option4();
                    case 5 -> option5();
                    case 6 -> option6();
                    case 7 -> option7();
                    case 8 -> option8();
                    case 9 -> option9();
                    case 10 -> option10();
                    default -> System.out.println("No option with this number");
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private void option10() {
        BigDecimal priceFrom = UserDataService.getDecimal("Price from?");
        BigDecimal priceTo = UserDataService.getDecimal("Price to?");
        System.out.println(toJson(carsService.inPriceRange(priceFrom,priceTo)));
    }

    private void option9() {
        System.out.println(toJson(carsService.componentsInCars()));
    }

    private void option8() {
        System.out.println(toJson(carsService.sortComponents()));
    }

    private void option7() {
        System.out.println(toJson(carsService.mostExpensive()));
    }

    private void option6() {
        System.out.println(carsService.priceStats());
    }

    private void option5() {
        System.out.println(toJson(carsService.mostExpensiveModel()));
    }

    private void option4() {
        System.out.println(toJson(carsService.carColors()));
    }

    private void option1() {
        System.out.println(toJson(carsService));
    }

    private void option2() {
        SortCriterion sortCriterion = UserDataService.getSortCriterion();
        boolean ascending = UserDataService.getBoolean("Ascending?");
        List<Car> cars = carsService.sortCars(sortCriterion, ascending);
        System.out.println(toJson(cars));
    }

    private void option3() {
        double mileage = UserDataService.getDouble("Type mileage");
        System.out.println(toJson(carsService.greaterMileage(mileage)));
    }

    private static <T> String toJson(T item) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        return gson.toJson(item);
    }

}
