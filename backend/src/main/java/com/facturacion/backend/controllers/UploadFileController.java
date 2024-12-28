package com.facturacion.backend.controllers;

import com.facturacion.backend.models.service.photo.IUploadFileService;
import lombok.Data;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;

@RestController
@RequestMapping("/api/uploads")
@Data
public class UploadFileController {

    private final IUploadFileService uploadFileService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("subfolder") String subfolder) {
        try {
            String filename = uploadFileService.copy(file, subfolder);
            return ResponseEntity.ok("Archivo subido con éxito: " + filename);
        } catch (IOException e) {
            return ResponseEntity.status(500).body("Error al subir el archivo: " + e.getMessage());
        }
    }

    @GetMapping("/files/{subfolder}/{filename:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String subfolder, @PathVariable String filename) {
        try {
            Resource file = uploadFileService.load(filename, subfolder);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                    .body(file);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(404).body(null);
        }
    }

    @DeleteMapping("/delete/{subfolder}/{filename:.+}")
    public ResponseEntity<String> deleteFile(@PathVariable String subfolder, @PathVariable String filename) {
        if (uploadFileService.delete(filename, subfolder)) {
            return ResponseEntity.ok("Archivo eliminado con éxito: " + filename);
        } else {
            return ResponseEntity.status(500).body("Error al eliminar el archivo: " + filename);
        }
    }
}
