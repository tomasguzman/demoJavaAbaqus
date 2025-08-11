package com.b_valores.demo.controller;

import com.b_valores.demo.model.*;
import com.b_valores.demo.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.List;

@Controller
public class TransactionsViewController {

    private final TransaccionRepository txRepo;
    private final PortafolioRepository portafolioRepo;
    private final ActivoRepository activoRepo;

    public TransactionsViewController(TransaccionRepository txRepo,
                                      PortafolioRepository portafolioRepo,
                                      ActivoRepository activoRepo) {
        this.txRepo = txRepo;
        this.portafolioRepo = portafolioRepo;
        this.activoRepo = activoRepo;
    }

    public static class TxForm {
        @NotNull
        private LocalDate fecha;
        @NotNull
        private Long portafolioId;
        @NotNull
        private Long activoId;
        @NotNull
        private Transaccion.Tipo tipo; 
        @Positive
        private double montoUsd;
        private Double precioUsado; 

        // getters/setters
        public LocalDate getFecha() { return fecha; }
        public void setFecha(LocalDate fecha) { this.fecha = fecha; }
        public Long getPortafolioId() { return portafolioId; }
        public void setPortafolioId(Long portafolioId) { this.portafolioId = portafolioId; }
        public Long getActivoId() { return activoId; }
        public void setActivoId(Long activoId) { this.activoId = activoId; }
        public Transaccion.Tipo getTipo() { return tipo; }
        public void setTipo(Transaccion.Tipo tipo) { this.tipo = tipo; }
        public double getMontoUsd() { return montoUsd; }
        public void setMontoUsd(double montoUsd) { this.montoUsd = montoUsd; }
        public Double getPrecioUsado() { return precioUsado; }
        public void setPrecioUsado(Double precioUsado) { this.precioUsado = precioUsado; }
    }

    @GetMapping("/transacciones")
    public String listarYForm(Model model) {
        List<Transaccion> txs = txRepo.findAll(); 
        List<Portafolio> portafolios = portafolioRepo.findAll();
        List<Activo> activos = activoRepo.findAll();

        model.addAttribute("txs", txs);
        model.addAttribute("portafolios", portafolios);
        model.addAttribute("activos", activos);
        model.addAttribute("form", new TxForm());
        return "transactions"; 
    }

    @PostMapping("/transacciones")
    public String crear(@ModelAttribute("form") TxForm form, BindingResult br, Model model) {
        if (br.hasErrors()) {
            model.addAttribute("txs", txRepo.findAll());
            model.addAttribute("portafolios", portafolioRepo.findAll());
            model.addAttribute("activos", activoRepo.findAll());
            model.addAttribute("error", "Revisa los campos del formulario.");
            return "transactions";
        }
        Transaccion t = new Transaccion();
        t.setFecha(form.getFecha());
        t.setPortafolio(portafolioRepo.findById(form.getPortafolioId()).orElseThrow());
        t.setActivo(activoRepo.findById(form.getActivoId()).orElseThrow());
        t.setTipo(form.getTipo());
        t.setMontoUsd(form.getMontoUsd());
        t.setPrecioUsado(form.getPrecioUsado());
        txRepo.save(t);
        return "redirect:/transacciones";
    }

    @PostMapping("/transacciones/{id}/eliminar")
    public String eliminar(@PathVariable Long id) {
        txRepo.deleteById(id);
        return "redirect:/transacciones";
    }
}
