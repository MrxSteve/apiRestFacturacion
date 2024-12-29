package com.facturacion.backend.models.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductoDTO {
    private Long id;
    private String nombre;
    private Double precio;
    private LocalDate createAt;
    private String foto;
}
