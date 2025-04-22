package com.example.srmsystem.config;

import com.example.srmsystem.model.Customer;
import com.example.srmsystem.model.Order;
import com.example.srmsystem.repository.CustomerRepository;
import com.example.srmsystem.repository.OrderRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.github.javafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(CustomerRepository customerRepo, OrderRepository orderRepo) {
        return args -> {
            if (customerRepo.count() > 0) return;

            Faker faker = new Faker();
            List<Customer> customers = new ArrayList<>();
            List<Order> orders = new ArrayList<>();

            for (int i = 0; i < 30; i++) {
                Customer customer = new Customer();
                customer.setUsername(faker.name().username());
                customer.setPassword("pass" + i);
                customer.setEmail(faker.internet().emailAddress());
                customer.setPhone(faker.phoneNumber().cellPhone());
                customer.setAddress(faker.address().fullAddress());
                customer.setCompanyName(faker.company().name());
                customer.setCreatedAt(LocalDateTime.now());
                customers.add(customer);
            }

            customerRepo.saveAll(customers);

            for (Customer customer : customers) {
                int orderCount = faker.number().numberBetween(1, 6);
                for (int j = 0; j < orderCount; j++) {
                    Order order = new Order();
                    order.setDescription(faker.commerce().productName());
                    order.setOrderDate(LocalDateTime.now().minusDays(faker.number().numberBetween(1, 30)));
                    order.setCustomer(customer);
                    order.setCreatedAt(LocalDateTime.now());
                    orders.add(order);
                }
            }

            orderRepo.saveAll(orders);
        };
    }
}
