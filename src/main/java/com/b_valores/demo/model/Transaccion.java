package com.b_valores.demo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class Transaccion {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate fecha;

    @ManyToOne(optional = false)
    private Portafolio portafolio;

    @ManyToOne(optional = false)
    private Activo activo;

    @Enumerated(EnumType.STRING)
    private Tipo tipo; // BUY | SELL

    // Monto de la operacion de compra venta en positivo
    private double montoUsd;

    // No util, era en caso de necesitar algun otro precio en la compra venta
    private Double precioUsado;

    public enum Tipo { BUY, SELL }

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getFecha() { return fecha; }
    public void setFecha(LocalDate fecha) { this.fecha = fecha; }
    public Portafolio getPortafolio() { return portafolio; }
    public void setPortafolio(Portafolio portafolio) { this.portafolio = portafolio; }
    public Activo getActivo() { return activo; }
    public void setActivo(Activo activo) { this.activo = activo; }
    public Tipo getTipo() { return tipo; }
    public void setTipo(Tipo tipo) { this.tipo = tipo; }
    public double getMontoUsd() { return montoUsd; }
    public void setMontoUsd(double montoUsd) { this.montoUsd = montoUsd; }
    public Double getPrecioUsado() { return precioUsado; }
    public void setPrecioUsado(Double precioUsado) { this.precioUsado = precioUsado; }
}
