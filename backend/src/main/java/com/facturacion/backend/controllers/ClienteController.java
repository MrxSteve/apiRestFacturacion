package com.facturacion.backend.controllers;

import com.facturacion.backend.models.dtos.ClienteDTO;
import com.facturacion.backend.models.service.IClienteSevice;
import com.facturacion.backend.models.service.photo.IUploadFileService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("api/clientes")
@Data
public class ClienteController {
    private final IClienteSevice clienteSevice;
    private final IUploadFileService uploadFileService;

    @PostMapping("/save")
    public ClienteDTO save(
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("email") String email,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {
        ClienteDTO clienteDTO = new ClienteDTO();
        clienteDTO.setNombre(nombre);
        clienteDTO.setApellido(apellido);
        clienteDTO.setEmail(email);
        clienteDTO.setCreateAt(fecha);

        if (foto != null) {
            try {
                // Genera y guarda el nombre real del archivo
                String nombreFoto = uploadFileService.copy(foto, "clientes");
                clienteDTO.setFoto(nombreFoto);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la foto", e);
            }
        }

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
    public ClienteDTO update(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("apellido") String apellido,
            @RequestParam("email") String email,
            @RequestParam("fecha") LocalDate fecha,
            @RequestParam(value = "foto", required = false) MultipartFile foto
    ) {
        ClienteDTO clienteExistente = clienteSevice.findById(id);
        clienteExistente.setNombre(nombre);
        clienteExistente.setApellido(apellido);
        clienteExistente.setEmail(email);
        clienteExistente.setCreateAt(fecha);

        if (foto != null) {
            try {
                // Genera y guarda el nombre real del archivo
                String nombreFoto = uploadFileService.copy(foto, "clientes");
                clienteExistente.setFoto(nombreFoto);
            } catch (IOException e) {
                throw new RuntimeException("Error al guardar la foto", e);
            }
        }

        return clienteSevice.update(id, clienteExistente);
    }


    @DeleteMapping("/delete/{id}")
    public void delete(@PathVariable Long id) {
        clienteSevice.delete(id);
    }

    @GetMapping("/find-by-nombre/{nombre}")
    public Page<ClienteDTO> findByNombreContainingIgnoreCase(@PathVariable String nombre, Pageable pageable) {
        return clienteSevice.findByNombreContainingIgnoreCase(nombre, pageable);
    }
}
