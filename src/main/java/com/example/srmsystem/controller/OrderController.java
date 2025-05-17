package com.example.srmsystem.controller;

import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.exception.BadRequestException;
import com.example.srmsystem.exception.EntityNotFoundException;
import com.example.srmsystem.exception.NoContentException;
import com.example.srmsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.Transactional;
import java.util.List;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@Slf4j
@Tag(name = "Order Controller", description = "Управление заказами клиента: создание, обновление, удаление, просмотр")
@RestController
@RequestMapping("/api/customers/{customerId}/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(final OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Получить все заказы клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Список заказов получен"),
            @ApiResponse(responseCode = "204", description = "Список заказов пуст"),
            @ApiResponse(responseCode = "404", description = "Некорректный ID клиента"),
            @ApiResponse(responseCode = "409", description = "Такой клиент уже существует")
    })
    @GetMapping
    public ResponseEntity<List<DisplayOrderDto>> getAllOrders(@PathVariable Long customerId) {
        log.info("Received request to get all orders for customer with ID {}", customerId);
        if (customerId == null || customerId <= 0) {
            log.warn("Invalid customer ID: {}", customerId);
            throw new BadRequestException("Invalid customer ID:" + customerId);
        }
        List<DisplayOrderDto> orders = orderService.getAllOrdersByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            log.info("No orders found for customer with ID {}", customerId);
            throw new NoContentException("No orders found for customer with ID " + customerId);
        }
        log.info("Found {} orders for customer with ID {}", orders.size(), customerId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Получить заказ клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId) {
        log.info("Received request to get order with ID {} for customer with ID {}", orderId, customerId);

        DisplayOrderDto order = orderService.getOrderById(customerId, orderId);
        if (order == null) {
            log.warn("Order with ID {} for customer with ID {} not found", orderId, customerId);
            throw new EntityNotFoundException(String.format("Order with ID %d for customer with ID %d not found", orderId, customerId));
        }
        log.info("Order with ID {} for customer with ID {} successfully found", orderId, customerId);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Создать заказ для клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные заказа")
    })
    @PostMapping
    public ResponseEntity<DisplayOrderDto> addOrderToCustomer(
            @PathVariable Long customerId,  @RequestBody CreateOrderDto createOrderDto) {
        log.info("Received request to create a new order for customer ID {}: {}", customerId, createOrderDto);
        DisplayOrderDto createdOrder = orderService.createOrderForCustomer(customerId, createOrderDto);
        log.info("Created order ID {} for customer ID {}", createdOrder.getId(), customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Обновить заказ клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> updateOrder(
            @PathVariable Long customerId, @PathVariable Long orderId, @Valid @RequestBody CreateOrderDto createOrderDto) {
        log.info("Received request to update order ID {} for customer ID {}: {}", orderId, customerId, createOrderDto);
        DisplayOrderDto updatedOrder = orderService.updateOrder(customerId, orderId, createOrderDto);
        if (updatedOrder == null) {
            log.warn("Order ID {} for customer ID {} not found", orderId, customerId);
            throw new EntityNotFoundException("Order with ID " + orderId + " for customer ID " + customerId + " not found");
        }
        log.info("Order ID {} for customer ID {} successfully updated", orderId, customerId);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Удалить заказ клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заказ успешно удалён")
    })
    @Transactional
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        log.info("Received request to delete order ID {} for customer ID {}", orderId, customerId);
        orderService.deleteOrder(customerId, orderId);
        log.info("Order ID {} for customer ID {} successfully deleted", orderId, customerId);
        return ResponseEntity.noContent().build();
    }
}