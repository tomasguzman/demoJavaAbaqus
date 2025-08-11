package com.b_valores.demo.model;

import jakarta.persistence.*;
import java.util.Date;

@Entity
public class Cantidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Portafolio portafolio;

    @ManyToOne
    private Activo activo;

    private Double cantidad;

    private Date fecha; 

    public Long getId() { return id; }

    public Portafolio getPortafolio() { return portafolio; }
    public void setPortafolio(Portafolio portafolio) { this.portafolio = portafolio; }

    public Activo getActivo() { return activo; }
    public void setActivo(Activo activo) { this.activo = activo; }

    public Double getCantidad() { return cantidad; }
    public void setCantidad(Double cantidad) { this.cantidad = cantidad; }


    
    public Date getFecha() { return fecha; }
    public void setFecha(Date fecha) { this.fecha = fecha; 
    }
}
