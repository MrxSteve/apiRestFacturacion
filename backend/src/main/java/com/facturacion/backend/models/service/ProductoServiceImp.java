package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ProductoDTO;
import com.facturacion.backend.models.entities.ProductoEntity;
import com.facturacion.backend.models.repositories.ProductoRepository;
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

@Service
@Data
public class ProductoServiceImp implements IProductoService {

    private final ProductoRepository productoRepository;
    private final String uploadPath = "uploads/productos"; // Ruta donde se guardan las fotos

    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO productoDTO) {
        ProductoEntity productoEntity = mapearEntity(productoDTO);
        ProductoEntity productoGuardado = productoRepository.save(productoEntity);

        return mapearDTO(productoGuardado);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDTO findById(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        return mapearDTO(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findAll(Pageable pageable) {
        Page<ProductoEntity> productos = productoRepository.findAll(pageable);

        return productos.map(this::mapearDTO);
    }

    @Override
    @Transactional
    public ProductoDTO update(Long id, ProductoDTO productoDTO) {
        ProductoEntity productoEntity = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        productoEntity.setNombre(productoDTO.getNombre());
        productoEntity.setPrecio(productoDTO.getPrecio());
        if (productoDTO.getFoto() != null) {
            productoEntity.setFoto(productoDTO.getFoto());
        }
        if (productoDTO.getCreateAt() != null) {
            productoEntity.setCreateAt(productoDTO.getCreateAt());
        }

        ProductoEntity productoActualizado = productoRepository.save(productoEntity);

        return mapearDTO(productoActualizado);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        // Borrar la foto del sistema si existe
        if (producto.getFoto() != null) {
            Path filePath = Paths.get(uploadPath).resolve(producto.getFoto());
            try {
                Files.deleteIfExists(filePath);
            } catch (IOException e) {
                throw new RuntimeException("Error al borrar la foto del producto", e);
            }
        }

        productoRepository.delete(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoDTO> findByNombreContainingIgnoreCase(String nombre, Pageable pageable) {
        Page<ProductoEntity> productos = productoRepository.findByNombreContainingIgnoreCase(nombre, pageable);

        return productos.map(this::mapearDTO);
    }

    // Guardar la foto del producto
    @Override
    @Transactional
    public ProductoDTO savePhoto(Long id, MultipartFile file) {
        ProductoEntity producto = productoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

        try {
            // Borrar la foto anterior si existe
            if (producto.getFoto() != null) {
                Path oldFilePath = Paths.get(uploadPath).resolve(producto.getFoto());
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

            // Actualizar la foto del producto
            producto.setFoto(fileName);
            ProductoEntity productoActualizado = productoRepository.save(producto);

            // Retornar DTO con la foto actualizada
            return mapearDTO(productoActualizado);

        } catch (IOException e) {
            throw new RuntimeException("Error al borrar la foto del producto", e);
        }
    }

    // Convertir Entity a DTO
    private ProductoDTO mapearDTO(ProductoEntity productoEntity) {
        ProductoDTO productoDTO = new ProductoDTO();
        productoDTO.setId(productoEntity.getId());
        productoDTO.setNombre(productoEntity.getNombre());
        productoDTO.setPrecio(productoEntity.getPrecio());
        productoDTO.setCreateAt(productoEntity.getCreateAt());
        productoDTO.setFoto(productoEntity.getFoto());

        return productoDTO;
    }

    // Convertir DTO a Entity
    private ProductoEntity mapearEntity(ProductoDTO productoDTO) {
        ProductoEntity productoEntity = new ProductoEntity();
        productoEntity.setNombre(productoDTO.getNombre());
        productoEntity.setPrecio(productoDTO.getPrecio());
        productoEntity.setCreateAt(productoDTO.getCreateAt());
        productoEntity.setFoto(productoDTO.getFoto());

        return productoEntity;
    }

}
