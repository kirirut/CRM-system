package com.example.srmsystem.service;

import com.example.srmsystem.dto.CreateUserDto;
import com.example.srmsystem.dto.DisplayUserDto;
import com.example.srmsystem.exception.NotFoundException;
import com.example.srmsystem.mapper.UserMapper;
import com.example.srmsystem.model.User;
import com.example.srmsystem.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;



@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    public List<DisplayUserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDisplayUserDto)
                .toList();
    }

    public DisplayUserDto getUserById(final Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found with id " + id));
        return userMapper.toDisplayUserDto(user);
    }

    public DisplayUserDto createUser(final CreateUserDto userDto) {
        User user = userMapper.fromCreateUserDto(userDto);
        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);
    }

    public DisplayUserDto updateUser(final Long id, final CreateUserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        user.setUsername(userDto.getUsername());
        user.setEmail(userDto.getEmail());
        user.setPasswordHash(userDto.getPassword());

        User saved = userRepository.save(user);
        return userMapper.toDisplayUserDto(saved);

    }

    @Transactional
    public void deleteUser(final Long id) {
        User user = userRepository.findById(id).orElseThrow(
            () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found with id " + id));
        userRepository.delete(user);
    }

}
