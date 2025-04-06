package com.example.coffee_order.service;

import com.example.coffee_order.domain.customer.CreateCustomer;
import com.example.coffee_order.domain.order.CreateOrder;
import com.example.coffee_order.domain.store.StoreProduct;
import com.example.coffee_order.repository.CustomerRepository;
import com.example.coffee_order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    OrderRepository orderRepository;

    @Mock
    StoreService storeService;

    @Mock
    CustomerRepository customerRepository;

    @InjectMocks
    OrderService orderService;

    @Test
    @DisplayName("고객이 존재하지 않으면 예외를 던지다")
    void customer_not_exists_then_throw_runtime_exception() {
        //givne
        CreateOrder createOrder = CreateOrder.builder()
                .customerId(999)
                .storeId(1)
                .quantityByProduct(Map.of(1,1))
                .build();

        when(customerRepository.findById(999)).thenReturn(Optional.empty());

        //when & then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            orderService.newOrder(createOrder);
        });

        assertEquals("존재하지 않는 사용자입니다", exception.getMessage());
    }

    @Test
    @DisplayName("구매 수량이 재고 수량부다 적을 때, 정상 주문이 가능하다.")
    public void stockQuantityTest_success() {
        //given
        int buyQuantity = 10;
        int stockQuantity = 50;
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, buyQuantity);
        CreateOrder createOrder = CreateOrder.builder()
                .storeId(1)
                .customerId(1)
                .quantityByProduct(map)
                .build();

        StoreProduct stock = StoreProduct.builder()
                .stockQuantity(stockQuantity)
                .build();

        when(storeService.getStoreProduct(1, 1)).thenReturn(stock);

        //when
        orderService.newOrder(createOrder);

        //then
        assertThat(stock.getStockQuantity()).isEqualTo(stockQuantity - buyQuantity);
    }

    @Test
    @DisplayName("구매 수량이 재고 수량보다 많을때, 정상 주문은 불가능하다.")
    public void stockQuantityTest_failure() {
        //given
        int buyQuantity = 100;
        int stockQuantity = 50;
        HashMap<Integer, Integer> map = new HashMap<>();
        map.put(1, buyQuantity);
        CreateOrder createOrder = CreateOrder.builder()
                .storeId(1)
                .customerId(1)
                .quantityByProduct(map)
                .build();

        StoreProduct stock = StoreProduct.builder()
                .stockQuantity(stockQuantity)
                .build();

        when(storeService.getStoreProduct(1, 1)).thenReturn(stock);

        //when & then
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            orderService.newOrder(createOrder);
        });

        assertThat(runtimeException.getMessage()).isEqualTo("재고가 없습니다!");
    }
}