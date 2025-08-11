package com.b_valores.demo.repository;

import com.b_valores.demo.model.Cantidad;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CantidadRepository extends JpaRepository<Cantidad, Long> {
}
