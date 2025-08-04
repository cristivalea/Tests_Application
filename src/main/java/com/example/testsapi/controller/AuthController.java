package com.example.testsapi.controller;

import com.example.testsapi.dto.JwtResponse;
import com.example.testsapi.dto.LoginRequest;
import com.example.testsapi.dto.MessageResponse;
import com.example.testsapi.dto.SignupRequest;
import com.example.testsapi.entity.User;
import com.example.testsapi.entity.Email;
import com.example.testsapi.repository.UserRepository;
import com.example.testsapi.repository.EmailRepository;
import com.example.testsapi.service.JwtUtils;
import com.example.testsapi.service.UserIdGeneratorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;

    @Autowired
    UserIdGeneratorService userIdGeneratorService;

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
                            loginRequest.getPassword()));

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtils.generateJwtToken(authentication);

            User userDetails = (User) authentication.getPrincipal();
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(item -> item.getAuthority())
                    .collect(Collectors.toList());

            return ResponseEntity.ok(new JwtResponse(jwt,
                    userDetails.getIdUser(),
                    userDetails.getUsername(),
                    userDetails.getFirstName(),
                    userDetails.getSecondName(),
                    roles));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Username is already taken!"));
        }

        try {
            String userId = userIdGeneratorService.generateNextUserId();

            User user = new User(userId,
                    signUpRequest.getFirstName(),
                    signUpRequest.getSecondName(),
                    signUpRequest.getUsername(),
                    encoder.encode(signUpRequest.getPassword()));

            user.setDataBirth(signUpRequest.getDataBirth());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setRegistrationDate(LocalDate.now());

            user = userRepository.save(user);

            Email email = new Email(user, signUpRequest.getEmail());
            emailRepository.save(email);

            return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Failed to register user - " + e.getMessage()));
        }
    }

    @PostMapping("/signout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("User logged out successfully!"));
    }

    @GetMapping("/test-jwt")
    public ResponseEntity<?> testJwt() {
        return ResponseEntity.ok(new MessageResponse("JWT endpoint is working!"));
    }
}