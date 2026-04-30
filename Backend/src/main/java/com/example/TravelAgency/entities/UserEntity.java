package com.example.TravelAgency.entities;

import com.example.TravelAgency.Enums.UserRole;
import com.example.TravelAgency.Enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "Users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long id;

    @Column(length = 20, nullable = false, unique = true)
    private String rut;

    @Column(length = 200, nullable = false)
    private String fullName;

    @Column(length = 80)
    private String nationality;

    @Column(length = 50, nullable = false, unique = true)
    private String email;

    @Column(length = 30, nullable = false)
    private String password;

   @Column (length = 12)
   private String phone;

    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status;

}
