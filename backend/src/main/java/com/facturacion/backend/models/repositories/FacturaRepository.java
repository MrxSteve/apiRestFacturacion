package com.facturacion.backend.models.repositories;

import com.facturacion.backend.models.entities.FacturaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface FacturaRepository extends JpaRepository<FacturaEntity, Long> {

    // Para la vista
    Page<FacturaEntity> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);

    // Para el reporte
    List<FacturaEntity> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate);

    // Buscar facturas por cliente
    Page<FacturaEntity> findAllByClienteId(Long clienteId, Pageable pageable);
    // Para calcular total dinero
    List<FacturaEntity> findAllByClienteId(Long clienteId);


}
