package com.zoohee.auth.entity;

import com.zoohee.auth.dto.request.SignupRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String nickname;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    public User(Long id, String username, String password, String nickname, UserRole role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.role = role;
    }

    public static User of(SignupRequest request, String encodedPassword) {
        return new User(
                null,
                request.username(),
                encodedPassword,
                request.nickname(),
                request.role()
        );
    }

    public void updateRole(UserRole newRole) {
        this.role = newRole;
    }

    public void updateCreatedBy() {
        this.createdBy = "system";
    }

    public void delete() {
        this.nickname = "deleted user";
        this.deletedAt = LocalDateTime.now();
    }
}
