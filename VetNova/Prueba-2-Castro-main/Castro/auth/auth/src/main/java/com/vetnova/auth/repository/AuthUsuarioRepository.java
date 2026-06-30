package com.vetnova.auth.repository;

import com.vetnova.auth.model.AuthUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AuthUsuarioRepository extends JpaRepository<AuthUsuario, Long> {

    Optional<AuthUsuario> findByEmail(String email);

    boolean existsByEmail(String email);
}