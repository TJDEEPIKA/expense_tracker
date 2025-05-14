package com.example.expensetracker.service;

import com.example.expensetracker.DTO.WebUser;
import com.example.expensetracker.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    List<User> getAllUsers();
    boolean deleteUserById(int id);
    User findUserByUserName(String userName);
    User save(WebUser webUser);
}
