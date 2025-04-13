package com.example.srmsystem.mapper;

import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.model.Customer;
import com.example.srmsystem.model.Order;
import org.springframework.stereotype.Component;

@Component
public final class OrderMapper {

    private OrderMapper() {
    }

    public Order fromCreateOrderDto(final CreateOrderDto createOrderDto, final Customer customer) {
        if (createOrderDto == null || customer == null) {
            return null;
        }

        Order order = new Order();
        order.setDescription(createOrderDto.getDescription());
        order.setOrderDate(createOrderDto.getOrderDate());
        order.setCustomer(customer);

        return order;
    }

    public DisplayOrderDto toDisplayOrderDto(Order order) {
        if (order == null) {
            return null;
        }
        DisplayOrderDto dto = new DisplayOrderDto();
        dto.setId(order.getId());
        dto.setDescription(order.getDescription());
        dto.setOrderDate(order.getOrderDate());
        dto.setCustomerId(order.getCustomer().getId());
        dto.setCustomerName(order.getCustomer().getName());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setUpdatedAt(order.getUpdatedAt());
        return dto;
    }
}
