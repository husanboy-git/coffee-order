package com.example.coffee_order.domain.order;

import lombok.Builder;
import lombok.Getter;

import java.util.Map;

@Getter
@Builder
public class CreateOrder {
    private final int customerId;
    private final int storeId;
    private final Map<Integer, Integer> quantityByProduct;
}
