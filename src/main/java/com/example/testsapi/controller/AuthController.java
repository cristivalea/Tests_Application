package com.example.testsapi.controller;

import com.example.testsapi.dto.JwtResponse;
import com.example.testsapi.dto.LoginRequest;
import com.example.testsapi.dto.MessageResponse;
import com.example.testsapi.dto.SignupRequest;
import com.example.testsapi.entity.Role;
import com.example.testsapi.entity.Status;
import com.example.testsapi.entity.User;
import com.example.testsapi.entity.Email;
import com.example.testsapi.repository.UserRepository;
import com.example.testsapi.repository.EmailRepository;
import com.example.testsapi.service.EmailService;
import com.example.testsapi.service.JwtUtils;
import com.example.testsapi.service.UserIdGeneratorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
public class AuthController {


    AuthenticationManager authenticationManager;
    UserRepository userRepository;
    EmailRepository emailRepository;

    PasswordEncoder encoder;

    JwtUtils jwtUtils;

    UserIdGeneratorService userIdGeneratorService;
    private EmailService emailService;

    public AuthController(AuthenticationManager authManage, UserRepository userRepo,  EmailRepository emailRepo, PasswordEncoder encoder, JwtUtils jwtUtils, UserIdGeneratorService userIdGen, EmailService emailService){
        this.authenticationManager = authManage;
        this.userRepository = userRepo;
        this.emailRepository = emailRepo;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.userIdGeneratorService = userIdGen;
        this.emailService = emailService;
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {
//        try {
//            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
//
//            SecurityContextHolder.getContext().setAuthentication(auth);
//            String token = jwtUtils.generateJwtToken(auth);
//
//            User user = (User) auth.getPrincipal();
//            ArrayList<String> roles = new ArrayList<>();
//            for(var v : user.getAuthorities()){
//                roles.add(v.getAuthority());
//            }
//
//            JwtResponse resp = new JwtResponse(token, user.getIdUser(), user.getUsername(), user.getFirstName(), user.getSecondName(), roles);
//
//            return ResponseEntity.ok(resp);
//        } catch (Exception e) {
//            return ResponseEntity.badRequest()
//                    .body(new MessageResponse("Error: Invalid username or password!"));
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication auth;
        try {
            // 1️⃣ Autentificare
            auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
        } catch (Exception e) {
            // dacă username sau parola sunt greșite
            return ResponseEntity.badRequest()
                    .body(new MessageResponse("Error: Invalid username or password!"));
        }

        SecurityContextHolder.getContext().setAuthentication(auth);
        String token = jwtUtils.generateJwtToken(auth);

        User user = (User) auth.getPrincipal();

        // 2️⃣ Extrage ultimul email fără a modifica User
        Email lastEmail = null;
        Set<Email> emails = user.getEmails();
        if (emails != null && !emails.isEmpty()) {
            for (Email e : emails) {
                lastEmail = e; // la finalul iterației, lastEmail va fi "ultimul" din iterator
            }
        }

        // 3️⃣ Trimite tokenul pe email, dacă există
        if (lastEmail != null) {
            try {
                emailService.sendEmail(
                        lastEmail.getEmail(),
                        "Tokenul tău de autentificare",
                        "Salut " + user.getFirstName() + ",\n\nTokenul tău JWT este:\n" + token
                );
            } catch (Exception e) {
                System.err.println("Eroare trimitere email: " + e.getMessage());
            }
        }

        // 4️⃣ Returnează mesaj de succes
        return ResponseEntity.ok(new MessageResponse("Login reușit! Tokenul a fost trimis pe email."));
    }


    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error - username exist"));
        }

        try {
            String userId = userIdGeneratorService.generateNextUserId();

            User user = new User(userId,
                    signUpRequest.getFirstName(),
                    signUpRequest.getSecondName(),
                    signUpRequest.getUsername(),
                    encoder.encode(signUpRequest.getPassword()),
                    Status.valueOf(signUpRequest.getStatus().toUpperCase()),
                    Role.valueOf(signUpRequest.getRole().toUpperCase())
            );

            user.setDataBirth(signUpRequest.getDataBirth());
            user.setPhoneNumber(signUpRequest.getPhoneNumber());
            user.setRegistrationDate(LocalDate.now());

            user = userRepository.save(user);

            Email email = new Email(user, signUpRequest.getEmail());
            emailRepository.save(email);

            return ResponseEntity.ok(new MessageResponse(userId));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error register"+ e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        return ResponseEntity.ok(new MessageResponse("Logged Out"));
    }

    @GetMapping("/returnJWT")
    public ResponseEntity<?> testJwt() {
        return ResponseEntity.ok(new MessageResponse("JWT successfully"));
    }

    @PostMapping("/verify-token")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> data) {
        String token = data.get("token");
        String username = data.get("username");

        if (jwtUtils.validateJwtToken(token) && jwtUtils.getUserNameFromJwtToken(token).equals(username)) {
            return ResponseEntity.ok(new MessageResponse("Token valid!"));
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new MessageResponse("Token invalid sau expirat!"));
        }
    }
}