package com.example.testsapi.service;

import com.example.testsapi.entity.Role;
import com.example.testsapi.entity.Status;
import com.example.testsapi.entity.User;
import com.example.testsapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found: " + username));

        return user;
    }

    public void updateUserFirstName(String userId, String newName){
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOpt.get();
        user.setFirstName(newName);
        userRepository.save(user);
    }

    public void updateUserSecondName(String userId, String newName){
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOpt.get();
        user.setSecondName(newName);
        userRepository.save(user);
    }

    public void updatePassword(String userId, String newPassword) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        String encodedPassword = passwordEncoder.encode(newPassword);
        user.setPassword(encodedPassword);
        userRepository.save(user);
    }

    public void updateStatus(String userId){
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOpt.get();
        if (user.getStatus() == Status.ACTIVE) {
            user.setStatus(Status.INACTIV);
        } else if (user.getStatus() == Status.INACTIV) {
            user.setStatus(Status.ACTIVE);
        }
        userRepository.save(user);
    }

    public void updateRole(String userId){
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("UserId must not be null or empty");
        }
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            throw new RuntimeException("User not found with id: " + userId);
        }
        User user = userOpt.get();
        if (user.getRole() == Role.USER) {
            user.setRole(Role.ADMIN);
        } else if (user.getRole() == Role.ADMIN) {
            user.setRole(Role.USER);
        }
        userRepository.save(user);
    }

    public Optional<User> getUserByPhoneNumber(String phoneNumber) {
        // eliminăm spațiile la început/sfârșit
        phoneNumber = phoneNumber.trim();
        return userRepository.findByPhoneNumber(phoneNumber);
    }
}