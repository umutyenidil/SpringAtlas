package com.umutyenidil.atlas.repository;

import com.umutyenidil.atlas.entity.Auth;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<Auth, UUID> {

    Optional<Auth> findByEmail(String email);
}
