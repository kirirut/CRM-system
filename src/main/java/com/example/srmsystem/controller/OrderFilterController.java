package com.example.srmsystem.controller;

import com.example.srmsystem.model.Order;
import com.example.srmsystem.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Validated
@Slf4j
@Tag(name = "Order Filter Controller", description = "Фильтрация заказов по имени клиента и дате заказа")
@RestController
@RequestMapping("/api/orders/filter")
@RequiredArgsConstructor
public class OrderFilterController {

    private final OrderService orderService;

    @Operation(summary = "Получить заказы по имени клиента", description = "Этот эндпоинт позволяет получить все заказы, связанные с клиентом по имени.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказы найдены", content = {
                    @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "404", description = "Заказы не найдены для указанного имени клиента")
    })
    @GetMapping("/customer")
    public List<Order> getOrdersByCustomerName(@RequestParam String name) {
        log.info("Запрос на получение заказов по имени клиента: {}", name);
        return orderService.getOrdersByCustomerName(name);
    }

    @Operation(summary = "Получить заказы по дате", description = "Этот эндпоинт позволяет получить все заказы, сделанные в указанный день.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Заказы найдены", content = {
                    @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Order.class))
            }),
            @ApiResponse(responseCode = "400", description = "Неверный формат даты", content = {
                    @io.swagger.v3.oas.annotations.media.Content(mediaType = "application/json",
                            schema = @io.swagger.v3.oas.annotations.media.Schema(implementation = Error.class))
            }),
            @ApiResponse(responseCode = "404", description = "Заказы не найдены для указанной даты")
    })
    @GetMapping("/date")
    public List<Order> getOrdersByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("Запрос на получение заказов по дате: {}", date);
        return orderService.getOrdersByDate(date);
    }
}
