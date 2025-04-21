package com.example.srmsystem.controller;

import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.service.OrderService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/customers/{customerId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<DisplayOrderDto>> getAllOrders(@PathVariable Long customerId) {
        List<DisplayOrderDto> orders = orderService.getAllOrdersByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId) {
        DisplayOrderDto order = orderService.getOrderById(customerId, orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(order);
    }

    @PostMapping
    public ResponseEntity<DisplayOrderDto> addOrderToCustomer(
            @PathVariable Long customerId, @RequestBody CreateOrderDto createOrderDto) {
        DisplayOrderDto createdOrder = orderService.createOrderForCustomer(customerId, createOrderDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @PutMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> updateOrder(
            @PathVariable Long customerId, @PathVariable Long orderId, @RequestBody CreateOrderDto createOrderDto) {
        DisplayOrderDto updatedOrder = orderService.updateOrder(customerId, orderId, createOrderDto);
        return ResponseEntity.ok(updatedOrder);
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        orderService.deleteOrder(customerId, orderId);
        return ResponseEntity.noContent().build();
    }
}
