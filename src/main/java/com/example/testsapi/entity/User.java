package com.example.testsapi.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users")
public class User implements UserDetails {

    @Id
    @Column(name = "idUser", length = 6)
    private String idUser;

    @NotBlank
    @Size(max = 50)
    @Column(name = "firstName")
    private String firstName;

    @NotBlank
    @Size(max = 200)
    @Column(name = "secondName")
    private String secondName;

    @Column(name = "dataBirth")
    private LocalDate dataBirth;

    @Column(name = "registrationDate")
    private LocalDate registrationDate;

    @NotBlank
    @Size(max = 20)
    @Column(name = "phoneNumber")
    private String phoneNumber;

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank
    @Size(max = 500)
    @Column(name = "passsword")
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Email> emails;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Transient
    private boolean enabled = true;

    @Transient
    private boolean accountNonExpired = true;

    @Transient
    private boolean accountNonLocked = true;

    @Transient
    private boolean credentialsNonExpired = true;


    public User() {}

    public User(String idUser, String firstName, String secondName, String username, String password, Status status, Role role) {
        this.idUser = idUser;
        this.firstName = firstName;
        this.secondName = secondName;
        this.username = username;
        this.password = password;
        this.registrationDate = LocalDate.now();
        this.status = status;
        this.role = role;
    }

    // Implementarea UserDetails
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public boolean isAccountNonExpired() { return accountNonExpired; }

    @Override
    public boolean isAccountNonLocked() { return accountNonLocked; }

    @Override
    public boolean isCredentialsNonExpired() { return credentialsNonExpired; }

    @Override
    public boolean isEnabled() { return enabled; }

    // Metode utilitare
    public String getFullName() {
        return firstName + " " + secondName;
    }

    public String getPrimaryEmail() {
        return emails != null && !emails.isEmpty()
                ? emails.iterator().next().getEmail()
                : null;
    }

    // Getters și Setters (păstrați din versiunea anterioară)
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

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public void setAccountNonExpired(boolean accountNonExpired) { this.accountNonExpired = accountNonExpired; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    public void setCredentialsNonExpired(boolean credentialsNonExpired) { this.credentialsNonExpired = credentialsNonExpired; }

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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SecurityQuestion> securityQuestions;

    public Set<SecurityQuestion> getSecurityQuestions() { return securityQuestions; }
    public void setSecurityQuestions(Set<SecurityQuestion> securityQuestions) { this.securityQuestions = securityQuestions; }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}