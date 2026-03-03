package com.example.testsapi.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Session {
    @Id
    @Column(name = "id", nullable = false, unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSourceCode;

    @Column(name = "token", nullable = false, unique = true)
    private String token;

    @Column(name = "userId", nullable = false)
    private String userId;

    @Column(name = "start_session", nullable = false)
    private LocalDateTime startSession;

    @Column(name = "end_session", nullable = false)
    private LocalDateTime endSession;
}
