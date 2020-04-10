package com.app.persistence.model.converter;

import com.app.persistence.model.Car;
import com.app.persistence.model.converter.generic.JsonConverter;

import java.util.List;

public class JsonCarsConverter extends JsonConverter<List<Car>> {
    public JsonCarsConverter(String jsonFilename) {
        super(jsonFilename);
    }
}
