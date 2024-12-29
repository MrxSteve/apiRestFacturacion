package com.facturacion.backend.models.service;

import com.facturacion.backend.models.dtos.ProductoDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface IProductoService {

    // CRUD PRODUCTO
    ProductoDTO save(ProductoDTO productoDTO);
    ProductoDTO findById(Long id);
    Page<ProductoDTO> findAll(Pageable pageable);
    ProductoDTO update(Long id, ProductoDTO productoDTO);
    void delete(Long id);
    Page<ProductoDTO> findByNombreContainingIgnoreCase(String nombre, Pageable pageable);

    // FOTO PRODUCTO
    ProductoDTO savePhoto(Long id, MultipartFile file);

}
