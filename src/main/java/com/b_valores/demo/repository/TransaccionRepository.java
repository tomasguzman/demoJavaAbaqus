package com.b_valores.demo.repository;

import com.b_valores.demo.model.Transaccion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface TransaccionRepository extends JpaRepository<Transaccion, Long> {
    List<Transaccion> findByPortafolioIdAndFechaLessThanEqualOrderByFechaAsc(Long portafolioId, LocalDate fecha);
}
