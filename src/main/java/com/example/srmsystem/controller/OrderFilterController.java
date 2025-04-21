package com.example.srmsystem.controller;

import com.example.srmsystem.model.Order;
import com.example.srmsystem.repository.OrderRepository;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders/filter")
public class OrderFilterController {

    private final OrderRepository orderRepository;

    public OrderFilterController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping("/customer")
    public List<Order> getOrdersByCustomerName(@RequestParam String name) {
        return orderRepository.findByCustomerName(name);
    }

    @GetMapping("/date")
    public List<Order> getOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return orderRepository.findByOrderDate(date);
    }
}
