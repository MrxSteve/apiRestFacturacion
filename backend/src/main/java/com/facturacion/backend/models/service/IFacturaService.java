package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.FacturaDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.List;

public interface IFacturaService {

    // CRUD
    FacturaDTO save(FacturaDTO facturaDTO);
    FacturaDTO findById(Long id);
    void delete(Long id);
    Page<FacturaDTO> findAll(Pageable pageable);

    // Para la vista
    Page<FacturaDTO> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Para el reporte
    List<FacturaDTO> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate);

    // Buscar facturas por cliente
    Page<FacturaDTO> findAllByCliente(Long clienteId, Pageable pageable);
    // Para calcular total dinero
    List<FacturaDTO> findAllByCliente(Long clienteId);

    // PDF individual
    ByteArrayInputStream generateFacturaPdf(Long id) throws Exception;

    // reporte facturas PDF
    ByteArrayInputStream generatePdfByDateRange(LocalDate startDate, LocalDate endDate) throws Exception;

}
