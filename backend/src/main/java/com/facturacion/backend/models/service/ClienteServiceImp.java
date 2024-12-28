package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.entities.ClienteEntity;
import com.facturacion.backend.models.repositories.ClienteRepository;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Data
public class ClienteServiceImp implements IClienteSevice {

    private final ClienteRepository clienteRepository;

    // CRUD CLIENTE
    @Override
    @Transactional
    public ClienteDTO save(ClienteDTO clienteDTO) {
        ClienteEntity cliente = mapearEntity(clienteDTO);
        ClienteEntity clienteGuardado = clienteRepository.save(cliente);
        return mapearDTO(clienteGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteDTO findById(Long id) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        return mapearDTO(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findAll(Pageable pageable) {
        Page<ClienteEntity> clientes = clienteRepository.findAll(pageable);
        return clientes.map(this::mapearDTO);
    }

    @Override
    @Transactional
    public ClienteDTO update(Long id, ClienteDTO clienteDTO) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        cliente.setNombre(clienteDTO.getNombre());

        if (clienteDTO.getApellido() != null) {
            cliente.setApellido(clienteDTO.getApellido());
        }
        cliente.setEmail(clienteDTO.getEmail());

        if (clienteDTO.getCreateAt() != null) {
            cliente.setCreateAt(clienteDTO.getCreateAt());
        }
        if (clienteDTO.getFoto() != null) {
            cliente.setFoto(clienteDTO.getFoto());
        }
        ClienteEntity clienteActualizado = clienteRepository.save(cliente);
        return null;
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
        clienteRepository.delete(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        Page<ClienteEntity> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        return clientes.map(this::mapearDTO);
    }

    // Convertir Entity a DTO
    private ClienteDTO mapearDTO(ClienteEntity clienteEntity) {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setId(clienteEntity.getId());
        clienteDTO.setNombre(clienteEntity.getNombre());
        clienteDTO.setApellido(clienteEntity.getApellido());
        clienteDTO.setEmail(clienteEntity.getEmail());
        clienteDTO.setCreateAt(clienteEntity.getCreateAt());
        clienteDTO.setFoto(clienteEntity.getFoto());

        return clienteDTO;
    }

    // Convertir DTO a Entity
    private ClienteEntity mapearEntity(ClienteDTO clienteDTO) {
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setNombre(clienteDTO.getNombre());
        clienteEntity.setApellido(clienteDTO.getApellido());
        clienteEntity.setEmail(clienteDTO.getEmail());
        clienteEntity.setCreateAt(clienteDTO.getCreateAt());
        clienteEntity.setFoto(clienteDTO.getFoto());

        return clienteEntity;
    }

}
