package com.facturacion.backend.models.repositories;

import com.facturacion.backend.models.entities.DetalleFacturaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DetalleFacturaRepository extends JpaRepository<DetalleFacturaEntity, Long> {
}
