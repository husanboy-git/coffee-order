package com.example.coffee_order.controller;

import com.example.coffee_order.domain.customer.CreateCustomer;
import com.example.coffee_order.domain.customer.Customer;
import com.example.coffee_order.domain.customer.CustomerDto;
import com.example.coffee_order.service.CustomerService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/api/v1/customers")
    public Response<CustomerDto> createNewCustomer(
            @RequestParam String name,
            @RequestParam String address,
            @RequestParam String phoneNumber
    ) {
        return Response.success(customerService.newCustomer(CreateCustomer
                .builder()
                .address(address)
                .name(name)
                .phoneNumber(phoneNumber)
                .build())
        );
    }
}
