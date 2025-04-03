package com.example.srmsystem.controller;


import com.example.srmsystem.dto.CreateUserDto;
import com.example.srmsystem.dto.DisplayUserDto;
import com.example.srmsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")

public class UserController {
    private final UserService userService;
    public UserController(final UserService userService) {
        this.userService = userService;
    }
    @GetMapping
    public ResponseEntity<List<DisplayUserDto>> getAllUsers() {
        List<DisplayUserDto> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisplayUserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }
    @PostMapping
    public ResponseEntity<DisplayUserDto> addUser(@RequestBody CreateUserDto createUserDto) {
        DisplayUserDto createUser=userService.createUser(createUserDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisplayUserDto> updateUser(@PathVariable Long id, @RequestBody CreateUserDto createUserDto) {
        DisplayUserDto updateUser=userService.updateUser(id, createUserDto);
        return ResponseEntity.ok(updateUser);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
    }

