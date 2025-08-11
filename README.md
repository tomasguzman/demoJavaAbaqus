# Proyecto de Modelado de Portafolios de Inversión

Este proyecto implementa en **Java 11+** y **Spring Boot** un sistema para modelar y calcular la evolución de portafolios de inversión, a partir de datos de pesos (`weights`) y precios (`precios`) de distintos activos.  

##  Requisitos previos

- **Java 11** o superior
- **Maven** instalado (`mvn` en la terminal)

##  Ejecución del servidor

Desde la carpeta raíz del proyecto:

```bash
mvn spring-boot:run
```

Esto levantara el servidor en:

http://localhost:8080

## Endpoints:
http://localhost:8080/datos (Visualizacion de datos, cantidades iniciales entre ellos.)

http://localhost:8080/api/valores?fecha_inicio=2022-02-15&fecha_fin=2022-03-01 (Retorna un ejemplo puntual de la data procesada en la vista de graficos)

http://localhost:8080/charts (Vista de graficos del bonus 1)

http://localhost:8080/transacciones (Vista de compra y venta del bonus 2)

