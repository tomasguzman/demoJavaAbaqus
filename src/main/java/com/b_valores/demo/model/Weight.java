package com.b_valores.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Weight {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne
    private Activo activo;

    @ManyToOne
    private Portafolio portafolio;

    private double valor;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }

    public Activo getActivo() { return activo; }
    public void setActivo(Activo activo) { this.activo = activo; }

    public Portafolio getPortafolio() { return portafolio; }
    public void setPortafolio(Portafolio portafolio) { this.portafolio = portafolio; }

    public double getValor() { return valor; }
    public void setValor(double valor) { this.valor = valor; }
}
