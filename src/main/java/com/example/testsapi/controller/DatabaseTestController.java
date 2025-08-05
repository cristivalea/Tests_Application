package com.example.testsapi.controller;

import com.example.testsapi.entity.Role;
import com.example.testsapi.entity.Status;
import com.example.testsapi.entity.User;
import com.example.testsapi.entity.Email;
import com.example.testsapi.repository.UserRepository;
import com.example.testsapi.repository.EmailRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/test")
public class DatabaseTestController {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailRepository emailRepository;

    @GetMapping("/db-connection")
    public String testDatabaseConnection() {
        try (Connection connection = dataSource.getConnection()) {
            String dbName = connection.getCatalog();
            String dbUrl = connection.getMetaData().getURL();
            return String.format("✅ Conexiune reușită la baza de date!\nDatabase: %s\nURL: %s", dbName, dbUrl);
        } catch (SQLException e) {
            return "❌ Eroare la conectarea la baza de date: " + e.getMessage();
        }
    }

    @GetMapping("/hello")
    public String hello() {
        return "🚀 API-ul funcționează!";
    }

    @PostMapping("/create-test-user")
    public String createTestUser() {
        try {
            // Generăm un ID unic
            Integer maxId = userRepository.findMaxUserIdNumber();
            String newId = String.format("U%05d", (maxId != null ? maxId + 1 : 1));

            // Creăm utilizatorul de test
            User testUser = new User(newId, "Test", "User", "testuser", "password123", Status.ACTIVE, Role.USER);
            testUser.setDataBirth(LocalDate.of(1990, 1, 1));
            testUser.setPhoneNumber("+40123456789");

            User savedUser = userRepository.save(testUser);

            // Adăugăm un email
            Email email = new Email(savedUser, "test@example.com");
            emailRepository.save(email);

            return "Utilizator de test creat cu succes!\nID: " + savedUser.getIdUser() +
                    "\nUsername: " + savedUser.getUsername();

        } catch (Exception e) {
            return "Eroare la crearea utilizatorului: " + e.getMessage();
        }
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/users/{id}")
    public String getUserById(@PathVariable String id) {
        return userRepository.findById(id)
                .map(user -> " Utilizator găsit: " + user.toString())
                .orElse(" Utilizatorul cu ID-ul " + id + " nu a fost găsit");
    }

    @GetMapping("/count-users")
    public String countUsers() {
        long count = userRepository.count();
        return "📊 Numărul total de utilizatori: " + count;
    }
}