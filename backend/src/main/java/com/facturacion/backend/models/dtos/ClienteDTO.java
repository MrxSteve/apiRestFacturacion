package com.facturacion.backend.models.dtos;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class ClienteDTO {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    private LocalDate createAt;
    private String foto;
    private List<FacturaDTO> facturas;
}
