package com.b_valores.demo.controller;

import com.b_valores.demo.model.Cantidad;
import com.b_valores.demo.model.Portafolio;
import com.b_valores.demo.model.Weight;
import com.b_valores.demo.repository.CantidadRepository;
import com.b_valores.demo.repository.PortafolioRepository;
import com.b_valores.demo.repository.WeightRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class DatosController {

    private final CantidadRepository cantidadRepository;
    private final WeightRepository weightRepository;
    private final PortafolioRepository portafolioRepository;

    public DatosController(CantidadRepository cantidadRepository,
                                 WeightRepository weightRepository,
                                 PortafolioRepository portafolioRepository) {
        this.cantidadRepository = cantidadRepository;
        this.weightRepository = weightRepository;
        this.portafolioRepository = portafolioRepository;
    }

    @GetMapping("/datos")
    public String verDatos(Model model) {
        List<Cantidad> cantidades = cantidadRepository.findAll();
        List<Weight> weights = weightRepository.findAll();
        List<Portafolio> portafolios = portafolioRepository.findAll();

        model.addAttribute("cantidades", cantidades);
        model.addAttribute("weights", weights);
        model.addAttribute("portafolios", portafolios);

        return "datos";
    }
}
