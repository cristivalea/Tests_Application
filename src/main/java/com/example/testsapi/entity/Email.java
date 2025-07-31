package com.example.testsapi.entity;

import jakarta.persistence.*;


@Entity
@Table(name = "email")
public class Email {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idUser", referencedColumnName = "idUser")
    private User user;


    @Column(name = "email")
    private String email;
    @Id
    private Long id;

    // Constructori
    public Email() {}

    public Email(User user, String email) {
        this.user = user;
        this.email = email;
    }


    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    @Override
    public String toString() {
        return String.format("Email{email='%s'}", email);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}