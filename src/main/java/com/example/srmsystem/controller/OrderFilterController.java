package com.example.srmsystem.controller;

import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.model.Order;
import com.example.srmsystem.repository.OrderRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@Tag(name = "Order Filter Controller", description = "Фильтрация заказов по имени клиента и дате заказа")
@RestController
@RequestMapping("/api/orders/filter")
public class OrderFilterController {

    private final OrderRepository orderRepository;

    public OrderFilterController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Operation(summary = "Получить заказы по имени клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказы найдены"),
            @ApiResponse(responseCode = "404", description = "Заказы не найдены")
    })
    @GetMapping("/customer")
    public List<Order> getOrdersByCustomerName(@RequestParam String name) {
        log.info("Запрос на получение заказов по имени клиента: {}", name);
        List<Order> orders = orderRepository.findByCustomerName(name);
        if (orders.isEmpty()) {
            log.warn("No orders found for customer with name '{}'", name);
            throw new EntityNotFoundException("Orders not found for customer with name: " + name);
        } else {
            log.info("Found {} orders for customer with name '{}'", orders.size(), name);
        }
        return orders;
    }

    @Operation(summary = "Получить заказы по дате")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказы найдены"),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты"),
            @ApiResponse(responseCode = "404", description = "Заказы не найдены")
    })
    @GetMapping("/date")
    public List<Order> getOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Request to get orders for date: {}", date);
        List<Order> orders = orderRepository.findByOrderDate(date);
        if (orders.isEmpty()) {
            log.warn("No orders found for date '{}'", date);
            throw new EntityNotFoundException("Orders not found for date: " + date);
        } else {
            log.info("Found {} orders for date '{}'", orders.size(), date);
        }
        return orders;
    }
}
