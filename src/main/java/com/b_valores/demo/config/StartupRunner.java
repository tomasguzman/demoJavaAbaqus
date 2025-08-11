package com.b_valores.demo.config;

import com.b_valores.demo.etl.CargaDatosService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {

    private final CargaDatosService cargaDatosService;

    public StartupRunner(CargaDatosService cargaDatosService) {
        this.cargaDatosService = cargaDatosService;
    }

    @Override
    public void run(String... args) {
        cargaDatosService.cargarDesdeExcel();
    }
}
