package com.example.srmsystem.mapper;

import com.example.srmsystem.dto.CreateUserDto;
import com.example.srmsystem.dto.DisplayUserDto;
import com.example.srmsystem.model.User;
import org.springframework.stereotype.Component;

@Component
public final class UserMapper {
    private UserMapper() {

    }

    public User fromCreateUserDto(final CreateUserDto createUserDto) {
        if(createUserDto==null) {
            return null;
        }
        User user = new User();
        user.setUsername(createUserDto.getUsername());
        user.setEmail(createUserDto.getEmail());
        user.setPasswordHash(createUserDto.getPassword());
        user.setRole(createUserDto.getRole()); // Установка роли пользователя

        return user;
    }
    public DisplayUserDto toDisplayUserDto(final User user){
        if(user==null) {
            return null;
        }
        return new DisplayUserDto(user.getId(), user.getUsername(),user.getEmail());
    }

}

