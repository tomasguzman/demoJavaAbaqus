package com.b_valores.demo.etl;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import com.b_valores.demo.model.Activo;
import com.b_valores.demo.model.Cantidad;
import com.b_valores.demo.model.Portafolio;
import com.b_valores.demo.model.Weight;
import com.b_valores.demo.repository.ActivoRepository;
import com.b_valores.demo.repository.PortafolioRepository;
import com.b_valores.demo.repository.WeightRepository;
import com.b_valores.demo.repository.CantidadRepository;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDate;

@Service
public class CargaDatosService {

    private final ActivoRepository activoRepository;
    private final PortafolioRepository portafolioRepository;
    private final WeightRepository weightRepository;
    private final CantidadRepository cantidadRepository;

    public CargaDatosService(ActivoRepository activoRepository,
                             PortafolioRepository portafolioRepository,
                             WeightRepository weightRepository,
                             CantidadRepository cantidadRepository) {
        this.activoRepository = activoRepository;
        this.portafolioRepository = portafolioRepository;
        this.weightRepository = weightRepository;
        this.cantidadRepository = cantidadRepository;
    }

    public void cargarDesdeExcel() {
        try (InputStream archivo = getClass().getClassLoader().getResourceAsStream("datos.xlsx");
             Workbook workbook = new XSSFWorkbook(archivo)) {

            Sheet hojaWeights = workbook.getSheet("weights");
            Sheet hojaPrecios = workbook.getSheet("Precios");

            System.out.println("Archivo leído correctamente. Hojas:");
            System.out.println("- " + hojaWeights.getSheetName());
            System.out.println("- " + hojaPrecios.getSheetName());

            // Crear portafolios si no existen
            Portafolio p1 = portafolioRepository.findByNombre("Portafolio 1").orElseGet(() -> {
                Portafolio nuevo = new Portafolio();
                nuevo.setNombre("Portafolio 1");
                nuevo.setValorInicial(1_000_000_000);
                return portafolioRepository.save(nuevo);
            });

            Portafolio p2 = portafolioRepository.findByNombre("Portafolio 2").orElseGet(() -> {
                Portafolio nuevo = new Portafolio();
                nuevo.setNombre("Portafolio 2");
                nuevo.setValorInicial(1_000_000_000);
                return portafolioRepository.save(nuevo);
            });

            Row encabezadoPrecios = hojaPrecios.getRow(0);
            Map<String, Integer> colByName = new HashMap<>();
            for (int j = 1; j < encabezadoPrecios.getLastCellNum(); j++) {
                Cell h = encabezadoPrecios.getCell(j);
                if (h != null && h.getCellType() == CellType.STRING) {
                    colByName.put(h.getStringCellValue().trim().toLowerCase(), j);
                }
            }

            // Leer cada fila de weights
            for (int i = 1; i <= 17; i++) {
                Row row = hojaWeights.getRow(i);
                if (row == null) continue;

                String nombreActivo = row.getCell(1).getStringCellValue().trim();
                double w1 = row.getCell(2).getNumericCellValue();
                double w2 = row.getCell(3).getNumericCellValue();

                // Normalizar por si acaso (no deberia ser necesario)
                if (w1 > 1.0) w1 /= 100.0;
                if (w2 > 1.0) w2 /= 100.0;

                Cell cellFecha = row.getCell(0);
                Date fecha = null;

                if (cellFecha == null) {
                    System.out.println("Fila " + i + " sin fecha, saltando...");
                    continue;
                }

                if (cellFecha.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cellFecha)) {
                    fecha = cellFecha.getDateCellValue();
                } else if (cellFecha.getCellType() == CellType.STRING) {
                    try {
                        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                        fecha = sdf.parse(cellFecha.getStringCellValue().trim());
                    } catch (ParseException e) {
                        System.out.println("Fila " + i + " con texto no parseable como fecha, saltando...");
                        continue;
                    }
                } else {
                    System.out.println("Fila " + i + " sin tipo compatible de fecha, saltando...");
                    continue;
                }

                // Buscar/crear activo
                Activo activo = activoRepository.findByNombre(nombreActivo).orElseGet(() -> {
                    Activo nuevo = new Activo();
                    nuevo.setNombre(nombreActivo);
                    return activoRepository.save(nuevo);
                });

                LocalDate localFecha = fecha.toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDate();

                // Guardar pesos
                Weight weight1 = new Weight();
                weight1.setFecha(localFecha);
                weight1.setActivo(activo);
                weight1.setPortafolio(p1);
                weight1.setValor(w1);
                weightRepository.save(weight1);

                Weight weight2 = new Weight();
                weight2.setFecha(localFecha);
                weight2.setActivo(activo);
                weight2.setPortafolio(p2);
                weight2.setValor(w2);
                weightRepository.save(weight2);


                if (fecha.equals(new SimpleDateFormat("dd-MM-yyyy").parse("15-02-2022"))) {
                    Integer colIndex = colByName.get(nombreActivo.toLowerCase());
                    if (colIndex == null) {
                        System.out.println("No se encontró precio para activo: " + nombreActivo);
                        continue;
                    }
                    double precioActivo = hojaPrecios.getRow(1).getCell(colIndex).getNumericCellValue();

                    double cantidad1 = (w1 * p1.getValorInicial()) / precioActivo;
                    double cantidad2 = (w2 * p2.getValorInicial()) / precioActivo;

                    Cantidad c1 = new Cantidad();
                    c1.setActivo(activo);
                    c1.setPortafolio(p1);
                    c1.setCantidad(cantidad1);
                    c1.setFecha(fecha);
                    cantidadRepository.save(c1);

                    Cantidad c2 = new Cantidad();
                    c2.setActivo(activo);
                    c2.setPortafolio(p2);
                    c2.setCantidad(cantidad2);
                    c2.setFecha(fecha);
                    cantidadRepository.save(c2);
                }
            }

            System.out.println("Weights y cantidades cargados correctamente");

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al leer el archivo Excel");
        }
    }
}
