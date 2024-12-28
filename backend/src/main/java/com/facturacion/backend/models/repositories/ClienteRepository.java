package com.facturacion.backend.models.repositories;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.entities.ClienteEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<ClienteEntity, Long> {

    Page<ClienteEntity> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

}
