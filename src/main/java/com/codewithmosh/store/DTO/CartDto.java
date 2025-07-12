package com.codewithmosh.store.DTO;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class CartDto {
    private UUID id;
    private List<CartItemDto> items = new ArrayList<>();    // if you do not make it = new ArrayList<>() you will get null
    private BigDecimal totalPrice = BigDecimal.ZERO;        // just like the items this is nullable field so you have to pass it a default value
}
