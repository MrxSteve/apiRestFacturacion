package com.facturacion.backend.models.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class FacturaDTO {
    private Long id;
    private String descripcion;
    private String observacion;
    private LocalDate createAt;
    private ClienteDTO cliente;
    List<DetalleFacturaDTO> items;
}
