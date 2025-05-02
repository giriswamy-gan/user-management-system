package com.apica.user_service.entity;

import com.apica.user_service.utils.CustomIdGenerator;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
public class Roles {
    @Id
    private String id;

    @Column(unique = true, nullable = false)
    private String name;

    public Roles() {
        this.id = CustomIdGenerator.generateId(this.getClass().getSimpleName());
    }
}
