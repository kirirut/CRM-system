package com.example.srmsystem.enums;

public enum Role {
    ADMIN,    // Администратор (может всё)
    MANAGER,  // Менеджер (обрабатывает заказы)
    SALES,    // Продавец (видит заказы, но не меняет)
    CUSTOMER  // Обычный клиент (делает заказы)
}
