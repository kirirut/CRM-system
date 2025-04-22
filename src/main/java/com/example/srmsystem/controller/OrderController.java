package com.example.srmsystem.controller;

import com.example.srmsystem.dto.CreateOrderDto;
import com.example.srmsystem.dto.DisplayOrderDto;
import com.example.srmsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
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
            @ApiResponse(responseCode = "400", description = "Некорректный ID клиента")
    })
    @GetMapping
    public ResponseEntity<List<DisplayOrderDto>> getAllOrders(@PathVariable Long customerId) {
        log.info("Получен запрос на получение всех заказов клиента с ID {}", customerId);
        List<DisplayOrderDto> orders = orderService.getAllOrdersByCustomerId(customerId);
        if (orders == null || orders.isEmpty()) {
            log.info("Заказы клиента с ID {} не найдены", customerId);
            return ResponseEntity.noContent().build();
        }
        log.info("Найдено {} заказов для клиента с ID {}", orders.size(), customerId);
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Получить заказ клиента по ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ найден"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @GetMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> getOrderById(@PathVariable Long customerId, @PathVariable Long orderId) {
        log.info("Получен запрос на получение заказа ID {} для клиента ID {}", orderId, customerId);
        DisplayOrderDto order = orderService.getOrderById(customerId, orderId);
        if (order == null) {
            log.warn("Заказ ID {} для клиента ID {} не найден", orderId, customerId);
            return ResponseEntity.notFound().build();
        }
        log.info("Заказ ID {} для клиента ID {} успешно найден", orderId, customerId);
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Создать заказ для клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Заказ успешно создан"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные заказа")
    })
    @PostMapping
    public ResponseEntity<DisplayOrderDto> addOrderToCustomer(
            @PathVariable Long customerId, @RequestBody CreateOrderDto createOrderDto) {
        log.info("Получен запрос на создание нового заказа для клиента ID {}: {}", customerId, createOrderDto);
        DisplayOrderDto createdOrder = orderService.createOrderForCustomer(customerId, createOrderDto);
        log.info("Создан заказ ID {} для клиента ID {}", createdOrder.getId(), customerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Обновить заказ клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказ успешно обновлён"),
            @ApiResponse(responseCode = "404", description = "Заказ не найден")
    })
    @PutMapping("/{orderId}")
    public ResponseEntity<DisplayOrderDto> updateOrder(
            @PathVariable Long customerId, @PathVariable Long orderId, @RequestBody CreateOrderDto createOrderDto) {
        log.info("Получен запрос на обновление заказа ID {} для клиента ID {}: {}", orderId, customerId, createOrderDto);
        DisplayOrderDto updatedOrder = orderService.updateOrder(customerId, orderId, createOrderDto);
        log.info("Заказ ID {} для клиента ID {} успешно обновлён", orderId, customerId);
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Удалить заказ клиента")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Заказ успешно удалён")
    })
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long customerId, @PathVariable Long orderId) {
        log.info("Получен запрос на удаление заказа ID {} клиента ID {}", orderId, customerId);
        orderService.deleteOrder(customerId, orderId);
        log.info("Заказ ID {} клиента ID {} успешно удалён", orderId, customerId);
        return ResponseEntity.noContent().build();
    }
}
