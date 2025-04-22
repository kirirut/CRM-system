package com.example.srmsystem.controller;

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
import org.springframework.web.bind.annotation.*;

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
            log.warn("Заказы по имени клиента '{}' не найдены", name);
        } else {
            log.info("Найдено {} заказов по имени клиента '{}'", orders.size(), name);
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
        log.info("Запрос на получение заказов по дате: {}", date);
        List<Order> orders = orderRepository.findByOrderDate(date);
        if (orders.isEmpty()) {
            log.warn("Заказы на дату '{}' не найдены", date);
        } else {
            log.info("Найдено {} заказов на дату '{}'", orders.size(), date);
        }
        return orders;
    }
}
