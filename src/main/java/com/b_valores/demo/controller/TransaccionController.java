
//Ahora que hay vista desde interfaz este controller sobra

package com.b_valores.demo.controller;

import com.b_valores.demo.model.*;
import com.b_valores.demo.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/tx")
public class TransaccionController {

    private final TransaccionRepository txRepo;
    private final PortafolioRepository portafolioRepo;
    private final ActivoRepository activoRepo;

    public TransaccionController(TransaccionRepository txRepo,
                                 PortafolioRepository portafolioRepo,
                                 ActivoRepository activoRepo) {
        this.txRepo = txRepo;
        this.portafolioRepo = portafolioRepo;
        this.activoRepo = activoRepo;
    }


    public record TxDTO(LocalDate fecha, Long portafolioId, Long activoId, String tipo, double montoUsd, Double precioUsado) {}

    @PostMapping
    public ResponseEntity<?> crear(@RequestBody TxDTO dto) {
        Transaccion t = new Transaccion();
        t.setFecha(dto.fecha());
        t.setPortafolio(portafolioRepo.findById(dto.portafolioId()).orElseThrow());
        t.setActivo(activoRepo.findById(dto.activoId()).orElseThrow());
        t.setTipo(Transaccion.Tipo.valueOf(dto.tipo().toUpperCase()));
        t.setMontoUsd(dto.montoUsd());
        t.setPrecioUsado(dto.precioUsado());
        txRepo.save(t);
        return ResponseEntity.ok(t.getId());
    }

    @PostMapping("/ejemplo")
    public ResponseEntity<?> ejemplo() {
        LocalDate f = LocalDate.of(2022,5,15);
        Portafolio p1 = portafolioRepo.findByNombre("Portafolio 1").orElseThrow();
        Activo eeuu = activoRepo.findByNombre("EEUU").orElseThrow();
        Activo europa = activoRepo.findByNombre("Europa").orElseThrow();

        Transaccion sell = new Transaccion();
        sell.setFecha(f); sell.setPortafolio(p1); sell.setActivo(eeuu);
        sell.setTipo(Transaccion.Tipo.SELL); sell.setMontoUsd(200_000_000);
        txRepo.save(sell);

        Transaccion buy = new Transaccion();
        buy.setFecha(f); buy.setPortafolio(p1); buy.setActivo(europa);
        buy.setTipo(Transaccion.Tipo.BUY); buy.setMontoUsd(200_000_000);
        txRepo.save(buy);

        return ResponseEntity.ok("OK");
    }
}
