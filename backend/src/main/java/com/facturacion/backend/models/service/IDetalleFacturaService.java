package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.DetalleFacturaDTO;

public interface IDetalleFacturaService {
    DetalleFacturaDTO save(DetalleFacturaDTO detalleFacturaDTO);
}
