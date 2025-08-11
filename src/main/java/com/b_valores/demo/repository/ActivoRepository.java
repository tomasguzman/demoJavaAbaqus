package com.b_valores.demo.repository;

import com.b_valores.demo.model.Activo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ActivoRepository extends JpaRepository<Activo, Long> {
    Optional<Activo> findByNombre(String nombre);
}
