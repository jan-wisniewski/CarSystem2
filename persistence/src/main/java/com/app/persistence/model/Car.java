package com.app.persistence.model;

import com.app.persistence.model.enums.Color;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Car {
    private String model;
    private BigDecimal price;
    private Color color;
    private double mileage;
    private Set<String> components;
}
