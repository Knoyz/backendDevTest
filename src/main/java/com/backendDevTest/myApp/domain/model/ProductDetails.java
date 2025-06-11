package com.backendDevTest.myApp.domain.model;

import lombok.Data;
import lombok.NonNull;

import java.math.BigDecimal;

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
