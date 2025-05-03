package com.apica.user_service.repository;

import com.apica.user_service.entity.Roles;
import com.apica.user_service.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<Users, String> {

    Optional<Users> findByUsername(String username);
    List<Users> findAllByRolesId(String roleId);
}
