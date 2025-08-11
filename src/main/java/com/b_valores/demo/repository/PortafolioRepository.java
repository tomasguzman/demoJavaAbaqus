package com.b_valores.demo.repository;

import com.b_valores.demo.model.Portafolio;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortafolioRepository extends JpaRepository<Portafolio, Long> {
    Optional<Portafolio> findByNombre(String nombre);
}
