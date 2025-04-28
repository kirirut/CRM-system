package com.example.srmsystem.mapper;

import com.example.srmsystem.dto.CreateCustomerDto;
import com.example.srmsystem.dto.DisplayCustomerDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.model.Customer;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public final class CustomerMapper {
    private CustomerMapper() {
    }

    public Customer fromCreateCustomerDto(final CreateCustomerDto createCustomerDto) {
        if (createCustomerDto == null) {
            return null;
        }

        Customer customer = new Customer();
        customer.setUsername(createCustomerDto.getUsername());
        customer.setPassword(createCustomerDto.getPassword());
        customer.setEmail(createCustomerDto.getEmail());
        customer.setPhone(createCustomerDto.getPhone());
        customer.setAddress(createCustomerDto.getAddress());
        customer.setCompanyName(createCustomerDto.getCompanyName());

        return customer;
    }

    public DisplayCustomerDto toDisplayCustomerDto(final Customer customer) {
        if (customer == null) {
            return null;
        }

        List<DisplayOrderDto> orders = customer.getOrders()
                .stream()
                .map(order -> new DisplayOrderDto(order.getId(),
                        order.getDescription(),
                        order.getOrderDate(),
                        customer.getId(),
                        customer.getUsername(),
                        order.getCreatedAt(),
                        order.getUpdatedAt()
                ))
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

}
