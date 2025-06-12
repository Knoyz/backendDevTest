package com.myapps.myapp.domain.model;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
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
