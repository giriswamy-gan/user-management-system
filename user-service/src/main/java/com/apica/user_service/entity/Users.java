package com.apica.user_service.entity;

import com.apica.user_service.utils.CustomIdGenerator;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
public class Users {
    @Id
    private String id;

    @Column(unique = true)
    private String username;

    private String password;
    private String fullName;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "roles_id")
    )
    private Set<Roles> roles = new HashSet<>();

    public Users() {
        this.id = CustomIdGenerator.generateId(this.getClass().getSimpleName());
    }

}
