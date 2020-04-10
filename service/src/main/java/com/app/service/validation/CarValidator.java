package com.app.service.validation;

import com.app.persistence.model.Car;
import com.app.service.exception.CarValidatorException;
import com.app.service.validation.generic.AbstractValidator;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class CarValidator extends AbstractValidator<Car> {

    @Override
    public Map<String, String> validate(Car item) {
        Map<String, String> errors = new HashMap<>();
        if (!isModelHasOnlyUppercase(item)) {
            errors.put("Field", "Model doesnt have only uppercase chars");
        }
        if (!isMileageIsPositive(item)) {
            errors.put("Field", "Mileage is not positive");
        }
        if (!isPriceIsPositive(item)) {
            errors.put("Field", "Price is not positive");
        }
        if (!areComponentsSetHasOnlyUppercase(item)) {
            errors.put("Field", "Components Set has not only uppercase elements");
        }
        return errors;
    }

    private boolean isModelHasOnlyUppercase(Car item) {
        if (item == null) {
            throw new CarValidatorException("Item is null");
        }
        return item.getModel().matches("([A-Z]+?.)+");
    }

    private boolean isMileageIsPositive(Car item) {
        if (item == null) {
            throw new CarValidatorException("Item is null");
        }
        return item.getMileage() >= 0;
    }

    private boolean isPriceIsPositive(Car item) {
        if (item == null) {
            throw new CarValidatorException("Item is null");
        }
        return item.getPrice().compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean areComponentsSetHasOnlyUppercase(Car item) {
        if (item == null) {
            throw new CarValidatorException("Item is null");
        }
        return item.getComponents()
                .stream()
                .allMatch(c -> c.matches("([A-Z]+?.)+"));
    }

}
