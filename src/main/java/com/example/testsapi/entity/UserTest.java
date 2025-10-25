package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "user_test")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserTest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "idUser", length = 6)
    private String idUser;

    @Column(name = "idTest", length = 50)
    private String idTest;
}
