package com.example.coffee_order.domain.customer;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CustomerDto {
    private final String name;
    private final String address;
    private final String phoneNumber;
}
