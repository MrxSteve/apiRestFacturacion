package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.dtos.DetalleFacturaDTO;
import com.facturacion.backend.models.dtos.FacturaDTO;
import com.facturacion.backend.models.dtos.ProductoDTO;
import com.facturacion.backend.models.entities.DetalleFacturaEntity;
import com.facturacion.backend.models.entities.FacturaEntity;
import com.facturacion.backend.models.entities.ProductoEntity;
import com.facturacion.backend.models.repositories.DetalleFacturaRepository;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
@Data
public class DetalleFacturaServiceImp implements IDetalleFacturaService {

    private final DetalleFacturaRepository detalleFacturaRepository;

    @Override
    public DetalleFacturaDTO save(DetalleFacturaDTO detalleFacturaDTO) {
        // Convertir DTO a Entity
        DetalleFacturaEntity detalleFacturaEntity = mapearEntity(detalleFacturaDTO);

        // Guardar la entidad en la base de datos
        DetalleFacturaEntity detalleFacturaGuardado = detalleFacturaRepository.save(detalleFacturaEntity);

        // Convertir Entity guardada a DTO y retornarla
        return mapearDTO(detalleFacturaGuardado);
    }

    // Convertir Entity a DTO
    private DetalleFacturaDTO mapearDTO(DetalleFacturaEntity detalleFacturaEntity) {
        DetalleFacturaDTO detalleFacturaDTO = new DetalleFacturaDTO();
        detalleFacturaDTO.setId(detalleFacturaEntity.getId());
        detalleFacturaDTO.setCantidad(detalleFacturaEntity.getCantidad());

        // Mapear Producto
        if (detalleFacturaEntity.getProducto() != null) {
            ProductoDTO productoDTO = new ProductoDTO();
            productoDTO.setId(detalleFacturaEntity.getProducto().getId());
            productoDTO.setNombre(detalleFacturaEntity.getProducto().getNombre());
            productoDTO.setPrecio(detalleFacturaEntity.getProducto().getPrecio());
            detalleFacturaDTO.setProducto(productoDTO);
        }

        // Mapear Factura
        if (detalleFacturaEntity.getFactura() != null) {
            FacturaDTO facturaDTO = new FacturaDTO();
            facturaDTO.setId(detalleFacturaEntity.getFactura().getId());
            facturaDTO.setDescripcion(detalleFacturaEntity.getFactura().getDescripcion());
            facturaDTO.setObservacion(detalleFacturaEntity.getFactura().getObservacion());
            facturaDTO.setCreateAt(detalleFacturaEntity.getFactura().getCreateAt());

            // Mapear Cliente (si existe)
            if (detalleFacturaEntity.getFactura().getCliente() != null) {
                ClienteDTO clienteDTO = new ClienteDTO();
                clienteDTO.setId(detalleFacturaEntity.getFactura().getCliente().getId());
                clienteDTO.setNombre(detalleFacturaEntity.getFactura().getCliente().getNombre());
                clienteDTO.setApellido(detalleFacturaEntity.getFactura().getCliente().getApellido());
                clienteDTO.setEmail(detalleFacturaEntity.getFactura().getCliente().getEmail());
                facturaDTO.setCliente(clienteDTO);
            }

            detalleFacturaDTO.setFactura(facturaDTO);
        }
        return detalleFacturaDTO;
    }

    // Convertir DTO a Entity
    private DetalleFacturaEntity mapearEntity(DetalleFacturaDTO detalleFacturaDTO) {
        DetalleFacturaEntity detalleFacturaEntity = new DetalleFacturaEntity();
        detalleFacturaEntity.setCantidad(detalleFacturaDTO.getCantidad());

        // Mapear Producto
        if (detalleFacturaDTO.getProducto() != null) {
            ProductoEntity productoEntity = new ProductoEntity();
            productoEntity.setId(detalleFacturaDTO.getProducto().getId()); // Solo asigna el ID
            detalleFacturaEntity.setProducto(productoEntity);
        }

        // Mapear Factura
        if (detalleFacturaDTO.getFactura() != null) {
            FacturaEntity facturaEntity = new FacturaEntity();
            facturaEntity.setId(detalleFacturaDTO.getFactura().getId()); // Solo asigna el ID
            detalleFacturaEntity.setFactura(facturaEntity);
        }

        return detalleFacturaEntity;
    }
}
