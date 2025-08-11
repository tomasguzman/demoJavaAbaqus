package com.b_valores.demo.service;

import jakarta.annotation.PostConstruct;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Component
public class PreciosProvider {
    private Map<LocalDate, Map<String, Double>> preciosPorFecha;

    @PostConstruct
    public void init() {
        preciosPorFecha = new HashMap<>();
        try (InputStream archivo = getClass().getClassLoader().getResourceAsStream("datos.xlsx");
             Workbook workbook = new XSSFWorkbook(archivo)) {

            Sheet hoja = workbook.getSheet("Precios");
            Row header = hoja.getRow(0);
            int cols = header.getLastCellNum();

            for (int i = 1; i <= hoja.getLastRowNum(); i++) {
                Row row = hoja.getRow(i);
                if (row == null) continue;

                Cell fechaCell = row.getCell(0);
                if (fechaCell == null || fechaCell.getCellType() != CellType.NUMERIC || !DateUtil.isCellDateFormatted(fechaCell))
                    continue;

                LocalDate fecha = fechaCell.getDateCellValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                Map<String, Double> map = new HashMap<>();

                for (int j = 1; j < cols; j++) {
                    Cell h = header.getCell(j);
                    Cell c = row.getCell(j);
                    if (h != null && h.getCellType() == CellType.STRING && c != null && c.getCellType() == CellType.NUMERIC) {
                        map.put(h.getStringCellValue().trim(), c.getNumericCellValue());
                    }
                }
                preciosPorFecha.put(fecha, map);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando precios.xlsx", e);
        }
    }

    public Map<String, Double> preciosEn(LocalDate fecha) {
        return preciosPorFecha.getOrDefault(fecha, Collections.emptyMap());
    }

    public Double precioDe(LocalDate fecha, String activo) {
        return preciosEn(fecha).get(activo);
    }
}
