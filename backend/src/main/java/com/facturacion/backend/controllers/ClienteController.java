package com.facturacion.backend.controllers;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.service.IClienteSevice;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/clientes")
@Data
public class ClienteController {
    private final IClienteSevice clienteSevice;

    @PostMapping("/save")
    public ClienteDTO save(@RequestBody ClienteDTO clienteDTO) {
        return clienteSevice.save(clienteDTO);
    }

    @GetMapping("/find-one/{id}")
    public ClienteDTO findById(@PathVariable Long id) {
        return clienteSevice.findById(id);
    }

    @GetMapping("/find-all")
    public Page<ClienteDTO> findAll(Pageable pageable) {
        return clienteSevice.findAll(pageable);
    }

    @PutMapping("/update/{id}")
    public ClienteDTO update(@PathVariable Long id, @RequestBody ClienteDTO clienteDTO) {
        return clienteSevice.update(id, clienteDTO);
    }

    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        clienteSevice.delete(id);
    }

    @GetMapping("/find-by-nombre/{nombre}")
    public Page<ClienteDTO> findByNombreContainingIgnoreCase(@PathVariable String nombre, Pageable pageable) {
        return clienteSevice.findByNombreContainingIgnoreCase(nombre, pageable);
    }

    @PostMapping(value = "/upload-photo/{id}", consumes = "multipart/form-data")
    public ClienteDTO uploadPhoto(@PathVariable Long id, @RequestParam("file") MultipartFile file) {
        return clienteSevice.savePhoto(id, file);
    }
}
