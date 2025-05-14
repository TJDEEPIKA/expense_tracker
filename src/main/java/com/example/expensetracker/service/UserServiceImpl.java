package com.example.expensetracker.service;

import com.example.expensetracker.DTO.CustomUserDetails;
import com.example.expensetracker.DTO.WebUser;
import com.example.expensetracker.model.Client;
import com.example.expensetracker.model.User;
import com.example.expensetracker.repository.ClientRepository;
import com.example.expensetracker.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Optional;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    ClientService clientService;
    PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ClientService clientService, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.clientService = clientService;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public User findUserByUserName(String username) {
        return userRepository.findByUserName(username);
    }

    @Transactional
    @Override
    public User save(WebUser webUser) {
        Client client = new Client();
        client.setFirstName(webUser.getFirstName());
        client.setLastName(webUser.getLastName());
        client.setEmail(webUser.getEmail());
        User user = new User();
        user.setUserName(webUser.getUsername());
        user.setPassword(passwordEncoder.encode(webUser.getPassword()));
        user.setClient(client);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if (user == null)
            throw new UsernameNotFoundException("user not found");
        return new CustomUserDetails(
            user.getUserName(),
            user.getPassword(),
            java.util.Collections.emptyList(),
            user.getClient().getId(),
            false
        );
    }

    @Override
    public List<User> getAllUsers() {
        throw new UnsupportedOperationException("getAllUsers is not supported");
    }

    @Override
    public boolean deleteUserById(int id) {
        throw new UnsupportedOperationException("deleteUserById is not supported");
    }
}
