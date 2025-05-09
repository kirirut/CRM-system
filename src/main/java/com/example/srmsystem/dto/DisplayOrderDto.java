package com.example.srmsystem.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DisplayOrderDto {

    private Long id;

    private String description;

    private LocalDateTime orderDate;

    private Long customerId;

    private String customerName;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}
