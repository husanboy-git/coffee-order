package com.example.coffee_order.service;

import com.example.coffee_order.domain.order.CreateOrder;
import com.example.coffee_order.domain.order.Order;
import com.example.coffee_order.domain.store.StoreProduct;
import com.example.coffee_order.repository.CustomerRepository;
import com.example.coffee_order.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final StoreService storeService;
    private final CustomerRepository customerRepository;

    public OrderService(
            OrderRepository orderRepository,
            StoreService storeService,
            CustomerRepository customerRepository
    ) {
        this.orderRepository = orderRepository;
        this.storeService = storeService;
        this.customerRepository = customerRepository;
    }

    public void newOrder(CreateOrder createOrder) {
        customerRepository.findById(createOrder.getCustomerId())
                .orElseThrow(() -> new RuntimeException("존재하지 않는 사용자입니다"));

        List<StoreProduct> storeProducts = new ArrayList<>();

        for(Map.Entry<Integer, Integer> entry : createOrder.getQuantityByProduct().entrySet()) {
            Integer productId = entry.getKey();
            Integer buyQuantity = entry.getValue();

            StoreProduct storeProduct = storeService.getStoreProduct(
                    createOrder.getStoreId(),
                    productId
            );

            int stockQuantity = storeProduct.getStockQuantity();
            if(buyQuantity > stockQuantity) {
                throw new RuntimeException("재고가 없습니다!");
            }

            storeProduct.adjustStockQuantity(buyQuantity);
            storeProducts.add(storeProduct);
        }

        Order entity = Order.newOrder(createOrder);
        orderRepository.save(entity);
        storeService.saveAll(storeProducts);
    }
}