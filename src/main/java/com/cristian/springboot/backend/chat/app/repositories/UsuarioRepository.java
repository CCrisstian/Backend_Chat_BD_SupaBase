package com.cristian.springboot.backend.chat.app.repositories;

import com.cristian.springboot.backend.chat.app.models.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
