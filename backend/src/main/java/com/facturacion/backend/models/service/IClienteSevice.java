package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ClienteDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IClienteSevice {

    // CRUD CLIENTE
    ClienteDTO save(ClienteDTO clienteDTO);
    ClienteDTO findById(Long id);
    Page<ClienteDTO> findAll(Pageable pageable);
    ClienteDTO update(Long id, ClienteDTO clienteDTO);
    void delete(Long id);
    Page<ClienteDTO> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // FOTO CLIENTE
    ClienteDTO savePhoto(Long id, MultipartFile file);

}
