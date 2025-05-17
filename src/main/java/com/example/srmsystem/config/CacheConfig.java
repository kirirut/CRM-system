package com.example.srmsystem.config;

import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.model.Order;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;


@Component
public class CacheConfig {
    private static final int MAX_CUSTOMERS_CACHE_SIZE = 100;
    private static final int MAX_ORDERS_CACHE_SIZE = 100;

    private static final String ALL_CUSTOMERS_CACHE_KEY = "allCustomers";
    private static final String ALL_ORDERS_CACHE_KEY = "allOrders";

    private final LruCache<String, List<DisplayCustomerDto>> allCustomersCache =
            new LruCache<>(MAX_CUSTOMERS_CACHE_SIZE);
    private final LruCache<String, List<DisplayOrderDto>> allOrdersCache =
            new LruCache<>(MAX_ORDERS_CACHE_SIZE);

    public List<DisplayCustomerDto> getAllCustomers() {
        return allCustomersCache.get(ALL_CUSTOMERS_CACHE_KEY);
    }

    public void putAllCustomers(List<Customer> customers) {
        List<DisplayCustomerDto> displayCustomerDtos = customers.stream()
                .map(this::toDisplayCustomerDto)
                .toList();
        allCustomersCache.put(ALL_CUSTOMERS_CACHE_KEY, displayCustomerDtos);
    }

    public void removeAllCustomers() {
        allCustomersCache.remove(ALL_CUSTOMERS_CACHE_KEY);
    }

    public List<DisplayOrderDto> getAllOrders() {
        return allOrdersCache.get(ALL_ORDERS_CACHE_KEY);
    }

    public void putAllOrders(List<Order> orders) {
        List<DisplayOrderDto> displayOrderDtos = orders.stream()
                .map(this::toDisplayOrderDto)
                .toList();
        allOrdersCache.put(ALL_ORDERS_CACHE_KEY, displayOrderDtos);
    }


    public void removeAllOrders() {
        allOrdersCache.remove(ALL_ORDERS_CACHE_KEY);
    }

    private DisplayCustomerDto toDisplayCustomerDto(Customer customer) {
        if (customer == null) {
            return null;
        }

        List<DisplayOrderDto> orders = customer.getOrders()
                .stream()
                .map(this::toDisplayOrderDto)
                .collect(Collectors.toList());

        return new DisplayCustomerDto(
                customer.getId(),
                customer.getUsername(),
                customer.getEmail(),
                customer.getPhone(),
                customer.getAddress(),
                customer.getCompanyName(),
                orders
        );
    }

    private DisplayOrderDto toDisplayOrderDto(Order order) {
        if (order == null) {
            return null;
        }

        return new DisplayOrderDto(
                order.getId(),
                order.getDescription(),
                order.getOrderDate(),
                order.getCustomer().getId(),
                order.getCustomer().getUsername(),
                order.getCreatedAt(),
                order.getUpdatedAt()
        );
    }

}