package com.example.srmsystem.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateOrderDto {

    private String description;


    private LocalDateTime orderDate;

    private Long customerId;

    public CreateOrderDto(String description, LocalDateTime orderDate) {
        this.description = description;
        this.orderDate = orderDate;
    }
}
