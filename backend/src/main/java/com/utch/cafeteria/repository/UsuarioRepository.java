package com.utch.cafeteria.repository;

import com.utch.cafeteria.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Capa de persistencia de Usuario, usada por el servicio de autenticacion.
 */
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByUsername(String username);
}
