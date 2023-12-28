package com.example.task.service;

import com.example.task.exception.EntityNotFound;
import com.example.task.model.User;
import com.example.task.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public User add(User user) {
        return userRepository.save(user);
    }

    public User findByUsername(String username) throws EntityNotFound {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new EntityNotFound("User:" + username + " not found"));
    }

    public User findById(int userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFound("User with ID:" + userId + " not found"));
    }

}
