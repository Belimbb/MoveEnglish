package com.project.MoveEnglish.entity.user;

import jakarta.persistence.*;
import lombok.*;

@ToString
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    private Long id;
    private String name;
    private String username;

    @Enumerated(EnumType.STRING)
    private UserState state;

    public UserState getState() {
        return state;
    }

    public void setState(UserState state) {
        this.state = state;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
