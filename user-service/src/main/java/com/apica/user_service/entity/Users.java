package com.apica.user_service.entity;

import com.apica.user_service.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {
    @Id
    private String id;

    private String username;
    private String password;
    private String fullName;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "roles_id", nullable = false)
    Roles roles;

    public Users() {
        this.id = CustomIdGenerator.generateId(this.getClass().getSimpleName());
    }

}
