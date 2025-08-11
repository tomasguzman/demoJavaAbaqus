package com.b_valores.demo.service;

import com.b_valores.demo.model.Transaccion;
import com.b_valores.demo.repository.TransaccionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class TransaccionService {

    private final TransaccionRepository repo;
    private final PreciosProvider precios;

    public TransaccionService(TransaccionRepository repo, PreciosProvider precios) {
        this.repo = repo;
        this.precios = precios;
    }

    // cantidadesPorActivo
    public void aplicarHasta(LocalDate fechaCorte, Long portafolioId, Map<String, Double> cantidadesPorActivo) {
        List<Transaccion> txs = repo.findByPortafolioIdAndFechaLessThanEqualOrderByFechaAsc(portafolioId, fechaCorte);
        for (Transaccion tx : txs) {
            String nombre = tx.getActivo().getNombre();
            double precio = (tx.getPrecioUsado() != null) ? tx.getPrecioUsado()
                             : precios.precioDe(tx.getFecha(), nombre) != null ? precios.precioDe(tx.getFecha(), nombre) : Double.NaN;
            if (Double.isNaN(precio) || precio <= 0) continue; 

            double delta = tx.getMontoUsd() / precio;
            if (tx.getTipo() == Transaccion.Tipo.SELL) delta = -delta;

            cantidadesPorActivo.merge(nombre, delta, Double::sum);
        }
    }
}
