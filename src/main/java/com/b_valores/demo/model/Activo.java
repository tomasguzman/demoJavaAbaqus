package com.b_valores.demo.model;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
public class Activo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String nombre;

    // Getters, setters y equals

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Podriamos definirlo por id si se esperara alterar los nombres a futuro
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Activo)) return false;
        Activo that = (Activo) o;
        return Objects.equals(nombre, that.nombre);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombre);
    }
}
