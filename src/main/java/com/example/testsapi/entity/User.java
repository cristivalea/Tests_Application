package com.example.testsapi.entity;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @Column(name = "idUser", length = 6)
    private String idUser;


    @Column(name = "firstName")
    private String firstName;

    @Column(name = "secondName")
    private String secondName;

    @Column(name = "dataBirth")
    private LocalDate dataBirth;

    @Column(name = "registrationDate")
    private LocalDate registrationDate;


    @Column(name = "phoneNumber")
    private String phoneNumber;


    @Column(name = "username", unique = true)
    private String username;


    @Column(name = "passsword") // Notă: typo din baza de date existentă
    private String password;

    // Relații - le vom adăuga mai târziu
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Email> emails;

    // Constructori
    public User() {}

    public User(String idUser, String firstName, String secondName, String username, String password) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.secondName = secondName;
        this.username = username;
        this.password = password;
        this.registrationDate = LocalDate.now();
    }

    // Getters și Setters
    public String getIdUser() { return idUser; }
    public void setIdUser(String idUser) { this.idUser = idUser; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getSecondName() { return secondName; }
    public void setSecondName(String secondName) { this.secondName = secondName; }

    public LocalDate getDataBirth() { return dataBirth; }
    public void setDataBirth(LocalDate dataBirth) { this.dataBirth = dataBirth; }

    public LocalDate getRegistrationDate() { return registrationDate; }
    public void setRegistrationDate(LocalDate registrationDate) { this.registrationDate = registrationDate; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Set<Email> getEmails() { return emails; }
    public void setEmails(Set<Email> emails) { this.emails = emails; }

    @PrePersist
    public void prePersist() {
        if (registrationDate == null) {
            registrationDate = LocalDate.now();
        }
    }

    @Override
    public String toString() {
        return String.format("User{idUser='%s', username='%s', firstName='%s', secondName='%s'}",
                idUser, username, firstName, secondName);
    }
}