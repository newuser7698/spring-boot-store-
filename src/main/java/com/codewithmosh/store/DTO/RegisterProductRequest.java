package com.codewithmosh.store.DTO;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RegisterProductRequest {
    private String name;
    private String description;
    private BigDecimal price;
    private byte categoryId;
}
