package com.example.srmsystem.repository;

import com.example.srmsystem.mapper.UserMapper;
import com.example.srmsystem.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

}
