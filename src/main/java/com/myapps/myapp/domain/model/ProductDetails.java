package com.myapps.myapp.domain.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NonNull;

@Data
public class ProductDetails {

    @NonNull
    private String id;
    @NonNull
    private String name;
    @NonNull
    private BigDecimal price;
    @NonNull
    private Boolean availability;

}
