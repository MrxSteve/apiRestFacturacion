package com.facturacion.backend.models.dtos;

import lombok.Data;

@Data
public class DetalleFacturaDTO {
    private Long id;
    private Integer cantidad;
    private ProductoDTO producto;
    private FacturaDTO factura;
}
