package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.dtos.DetalleFacturaDTO;
import com.facturacion.backend.models.dtos.FacturaDTO;
import com.facturacion.backend.models.dtos.ProductoDTO;
import com.facturacion.backend.models.entities.ClienteEntity;
import com.facturacion.backend.models.entities.DetalleFacturaEntity;
import com.facturacion.backend.models.entities.FacturaEntity;
import com.facturacion.backend.models.entities.ProductoEntity;
import com.facturacion.backend.models.repositories.ClienteRepository;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Service
@Data
public class ClienteServiceImp implements IClienteSevice {

    private final ClienteRepository clienteRepository;
    private final String uploadPath = "uploads/clientes"; // Ruta para guardar fotos

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
        return mapearDTO(clienteActualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        // Borrar la foto del sistema de archivos si existe
        if (cliente.getFoto() != null) {
            Path filePath = Paths.get(uploadPath).resolve(cliente.getFoto());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al borrar la foto del cliente", e);
            }
        }

        clienteRepository.delete(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ClienteDTO> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        Page<ClienteEntity> clientes = clienteRepository.findByNombreContainingIgnoreCase(nombre, pageable);
        return clientes.map(this::mapearDTO);
    }

    // Guardar foto del cliente

    @Override
    @Transactional
    public ClienteDTO savePhoto(Long id, MultipartFile file) {
        ClienteEntity cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));

        try {
            // Borrar la foto anterior si existe
            if (cliente.getFoto() != null) {
                Path oldFilePath = Paths.get(uploadPath).resolve(cliente.getFoto());
                Files.deleteIfExists(oldFilePath);
            }

            // Crear el directorio si no existe
            Path directory = Paths.get(uploadPath);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // Guardar la nueva foto
            String fileName = id + "_" + file.getOriginalFilename();
            Path filePath = directory.resolve(fileName);
            Files.copy(file.getInputStream(), filePath);

            // Actualizar la entidad con la nueva ruta de la foto
            cliente.setFoto(fileName);
            ClienteEntity clienteActualizado = clienteRepository.save(cliente);

            // Retornar el DTO actualizado
            return mapearDTO(clienteActualizado);

        } catch (IOException e) {
            throw new RuntimeException("Error al guardar la foto", e);
        }
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

        // Mapear facturas directamente dentro del metodo
        if (clienteEntity.getFacturas() != null) {
            clienteDTO.setFacturas(clienteEntity.getFacturas().stream().map(facturaEntity -> {
                FacturaDTO facturaDTO = new FacturaDTO();
                facturaDTO.setId(facturaEntity.getId());
                facturaDTO.setDescripcion(facturaEntity.getDescripcion());
                facturaDTO.setObservacion(facturaEntity.getObservacion());
                facturaDTO.setCreateAt(facturaEntity.getCreateAt());

                // Mapear detalles de factura
                if (facturaEntity.getItems() != null) {
                    facturaDTO.setItems(facturaEntity.getItems().stream().map(detalleEntity -> {
                        DetalleFacturaDTO detalleDTO = new DetalleFacturaDTO();
                        detalleDTO.setId(detalleEntity.getId());
                        detalleDTO.setCantidad(detalleEntity.getCantidad());

                        // Mapear producto
                        if (detalleEntity.getProducto() != null) {
                            ProductoDTO productoDTO = new ProductoDTO();
                            productoDTO.setId(detalleEntity.getProducto().getId());
                            productoDTO.setNombre(detalleEntity.getProducto().getNombre());
                            productoDTO.setPrecio(detalleEntity.getProducto().getPrecio());
                            detalleDTO.setProducto(productoDTO);
                        }

                        return detalleDTO;
                    }).toList());
                }

                return facturaDTO;
            }).toList());
        }

        return clienteDTO;
    }

    // Convertir DTO a Entity
    private ClienteEntity mapearEntity(ClienteDTO clienteDTO) {
        ClienteEntity clienteEntity = new ClienteEntity();
        clienteEntity.setId(clienteDTO.getId());
        clienteEntity.setNombre(clienteDTO.getNombre());
        clienteEntity.setApellido(clienteDTO.getApellido());
        clienteEntity.setEmail(clienteDTO.getEmail());
        clienteEntity.setCreateAt(clienteDTO.getCreateAt());
        clienteEntity.setFoto(clienteDTO.getFoto());

        // Mapear facturas directamente dentro del metodo
        if (clienteDTO.getFacturas() != null) {
            clienteEntity.setFacturas(clienteDTO.getFacturas().stream().map(facturaDTO -> {
                FacturaEntity facturaEntity = new FacturaEntity();
                facturaEntity.setId(facturaDTO.getId());
                facturaEntity.setDescripcion(facturaDTO.getDescripcion());
                facturaEntity.setObservacion(facturaDTO.getObservacion());
                facturaEntity.setCreateAt(facturaDTO.getCreateAt());

                // Mapear detalles de factura
                if (facturaDTO.getItems() != null) {
                    facturaEntity.setItems(facturaDTO.getItems().stream().map(detalleDTO -> {
                        DetalleFacturaEntity detalleEntity = new DetalleFacturaEntity();
                        detalleEntity.setId(detalleDTO.getId());
                        detalleEntity.setCantidad(detalleDTO.getCantidad());

                        // Mapear producto
                        if (detalleDTO.getProducto() != null) {
                            ProductoEntity productoEntity = new ProductoEntity();
                            productoEntity.setId(detalleDTO.getProducto().getId());
                            detalleEntity.setProducto(productoEntity);
                        }

                        return detalleEntity;
                    }).toList());
                }

                return facturaEntity;
            }).toList());
        }

        return clienteEntity;
    }

}
