package com.example.testsapi.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
    @Column(name = "id")
    private Integer id;

    @Column(name = "idUser", length = 6)
    private String idUser;

    @Column(name = "idTest", length = 50)
    private String idTest;
}
