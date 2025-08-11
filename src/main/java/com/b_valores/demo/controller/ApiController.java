package com.b_valores.demo.controller;

import com.b_valores.demo.model.Cantidad;
import com.b_valores.demo.repository.CantidadRepository;
import com.b_valores.demo.repository.ActivoRepository;
import com.b_valores.demo.service.PreciosProvider;
import com.b_valores.demo.service.TransaccionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final CantidadRepository cantidadRepository;
    private final PreciosProvider preciosProvider;
    private final TransaccionService transaccionService;

    public ApiController(CantidadRepository cantidadRepository,
                         ActivoRepository activoRepository,
                         PreciosProvider preciosProvider,
                         TransaccionService transaccionService) {
        this.cantidadRepository = cantidadRepository;
        this.preciosProvider = preciosProvider;
        this.transaccionService = transaccionService;
    }

    @GetMapping("/valores")
    public Map<LocalDate, Object> obtenerValores(
            @RequestParam("fecha_inicio") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam("fecha_fin") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {


        List<Cantidad> cantidades = cantidadRepository.findAll();
        Map<Long, Map<String, Double>> cantidadesBasePorPortafolio = new HashMap<>();
        for (Cantidad c : cantidades) {
            long pid = c.getPortafolio().getId();
            String activo = c.getActivo().getNombre();
            cantidadesBasePorPortafolio
                    .computeIfAbsent(pid, k -> new HashMap<>())
                    .merge(activo, c.getCantidad(), Double::sum);
        }

        Map<LocalDate, Object> resultado = new LinkedHashMap<>();

        for (LocalDate fecha = fechaInicio; !fecha.isAfter(fechaFin); fecha = fecha.plusDays(1)) {
            Map<String, Double> preciosHoy = preciosProvider.preciosEn(fecha);
            if (preciosHoy == null || preciosHoy.isEmpty()) continue; 

            Map<Long, Double> valorPortafolio = new HashMap<>();
            Map<Long, Map<String, Double>> valoresPorActivo = new HashMap<>();

            // Aplicamos transacciones a cada portafolio
            for (Map.Entry<Long, Map<String, Double>> entry : cantidadesBasePorPortafolio.entrySet()) {
                Long pid = entry.getKey();
                Map<String, Double> cAjustadas = new HashMap<>(entry.getValue());

                // Aplica las compra y ventas
                transaccionService.aplicarHasta(fecha, pid, cAjustadas);

                // Calculamos x_{i,t}
                for (Map.Entry<String, Double> e : cAjustadas.entrySet()) {
                    String activo = e.getKey();
                    Double p = preciosHoy.get(activo);
                    if (p == null) continue; 

                    double val = e.getValue() * p;
                    valorPortafolio.put(pid, valorPortafolio.getOrDefault(pid, 0.0) + val);

                    valoresPorActivo
                            .computeIfAbsent(pid, k -> new HashMap<>())
                            .put(activo, val);
                }
            }


            Map<String, Object> valoresHoy = new HashMap<>();
            for (Long pid : valoresPorActivo.keySet()) {
                double Vt = valorPortafolio.getOrDefault(pid, 0.0);
                Map<String, Double> xPorActivo = valoresPorActivo.get(pid);

                Map<String, Double> w = new HashMap<>();
                if (Vt > 0) {
                    for (Map.Entry<String, Double> e : xPorActivo.entrySet()) {
                        w.put(e.getKey(), e.getValue() / Vt);
                    }
                } else {

                    for (String act : xPorActivo.keySet()) {
                        w.put(act, 0.0);
                    }
                }

                Map<String, Object> portafolioData = new HashMap<>();
                portafolioData.put("V_t", Vt);
                portafolioData.put("w_i_t", w);

                valoresHoy.put("Portafolio " + pid, portafolioData);
            }

            if (!valoresHoy.isEmpty()) {
                resultado.put(fecha, valoresHoy);
            }
        }

        return resultado;
    }
}
