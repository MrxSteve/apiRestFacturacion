package com.facturacion.backend.controllers;

import com.facturacion.backend.models.dtos.ProductoDTO;
import com.facturacion.backend.models.service.IProductoService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
@Data
public class ProductoController {
    private final IProductoService productoService;

    @PostMapping("/save")
    public ProductoDTO save(@RequestBody ProductoDTO productoDTO) {
        return productoService.save(productoDTO);
    }

    @GetMapping("/find-one/{id}")
    public ProductoDTO findById(@PathVariable Long id) {
        return productoService.findById(id);
    }

    @GetMapping("/find-all")
    public Page<ProductoDTO> findAll(Pageable pageable) {
        return productoService.findAll(pageable);
    }

    @PutMapping("/update/{id}")
    public ProductoDTO update(@PathVariable Long id, @RequestBody ProductoDTO productoDTO) {
        return productoService.update(id, productoDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        productoService.delete(id);
    }

    @GetMapping("/find-by-nombre/{nombre}")
    public Page<ProductoDTO> findByNombreContainingIgnoreCase(@PathVariable String nombre, Pageable pageable) {
        return productoService.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @PostMapping(value = "/upload-photo/{id}", consumes = "multipart/form-data")
    public ProductoDTO uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return productoService.savePhoto(id, file);
    }

}
