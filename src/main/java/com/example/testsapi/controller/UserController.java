package com.example.testsapi.controller;

import com.example.testsapi.dto.MessageResponse;
import com.example.testsapi.entity.User;
import com.example.testsapi.repository.UserRepository;
import com.example.testsapi.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    public UserDetailsServiceImpl userService;

    public UserController(UserDetailsServiceImpl userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();

            Optional<User> user = userRepository.findByUsername(username);

            if (user.isPresent()) {
                User currentUser = user.get();
                // Returnăm informațiile utilizatorului fără parolă
                currentUser.setPassword("[PROTECTED]");
                return ResponseEntity.ok(currentUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<Page<User>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "registrationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ?
                    Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();

            Pageable pageable = PageRequest.of(page, size, sort);
            Page<User> users = userRepository.findAll(pageable);

            // Ascundem parolele pentru securitate
            users.getContent().forEach(user -> user.setPassword("[PROTECTED]"));

            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{idUser}")
    public ResponseEntity<?> getUserById(@PathVariable String idUser) {
        try {
            Optional<User> user = userRepository.findById(idUser);

            if (user.isPresent()) {
                User foundUser = user.get();
                foundUser.setPassword("[PROTECTED]");
                return ResponseEntity.ok(foundUser);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsersByName(@RequestParam String name) {
        try {
            var users = userRepository.findByFirstNameContainingIgnoreCase(name);
            users.forEach(user -> user.setPassword("[PROTECTED]"));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: " + e.getMessage()));
        }
    }

    @PatchMapping("/{userId}/name")
    public ResponseEntity<String> updateUserName(
            @PathVariable String userId,
            @RequestParam String newName) {
        userService.updateUserFirstName(userId, newName);
        return ResponseEntity.ok("User name updated successfully");
    }

    @PatchMapping("/{userId}/second_name")
    public ResponseEntity<String> updateUserSecondName(@PathVariable String userId, @RequestParam String newName) {
        userService.updateUserSecondName(userId, newName);
        return ResponseEntity.ok("User second name updated successfully");
    }

    @PatchMapping("/{userId}/password")
    public ResponseEntity<String> updateUserPassword(@PathVariable String userId, @RequestParam String newPassword) {
        userService.updatePassword(userId, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PatchMapping("/{userId}/status")
    public ResponseEntity<String> updateStatus(@PathVariable String userId){
        userService.updateStatus(userId);
        return ResponseEntity.ok("Status update successfully");
    }

    @PatchMapping("/{userId}/role")
    public ResponseEntity<String> updateRole(@PathVariable String userId){
        userService.updateRole(userId);
        return ResponseEntity.ok("Role update successfully");
    }
}