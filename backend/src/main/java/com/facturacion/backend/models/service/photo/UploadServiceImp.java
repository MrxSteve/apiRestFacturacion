package com.facturacion.backend.models.service.photo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class UploadServiceImp implements IUploadFileService {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private static final String UPLOADS_FOLDER = System.getProperty("user.dir") + "/uploads";

    @Override
    public Resource load(String filename, String subfolder) throws MalformedURLException {
        Path pathFoto = getPath(filename, subfolder);
        log.info("Cargando archivo: " + pathFoto);

        Resource recurso;
        try {
            recurso = new UrlResource(pathFoto.toUri());
            if (!recurso.exists() || !recurso.isReadable()) {
                throw new RuntimeException("Error: No se puede leer el archivo " + pathFoto);
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error al cargar el archivo: " + filename, e);
        }

        return recurso;
    }

    @Override
    public String copy(MultipartFile file, String subfolder) throws IOException {
        String uniqueFilename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path rootPath = getPath(uniqueFilename, subfolder);

        Files.createDirectories(rootPath.getParent()); // Crea la carpeta si no existe
        Files.copy(file.getInputStream(), rootPath); // Copia el archivo
        log.info("Archivo guardado en: " + rootPath);
        return uniqueFilename; // Retorna el nombre del archivo guardado
    }

    @Override
    public boolean delete(String filename, String subfolder) {
        Path rootPath = getPath(filename, subfolder);
        File archivo = rootPath.toFile();

        if (archivo.exists() && archivo.canRead()) {
            if (archivo.delete()) {
                log.info("Archivo eliminado: " + filename);
                return true;
            }
        }
        return false;
    }

    private Path getPath(String filename, String subfolder) {
        Path uploadsPath = Paths.get(UPLOADS_FOLDER, subfolder);
        try {
            if (!Files.exists(uploadsPath)) {
                Files.createDirectories(uploadsPath); // Crea el directorio si no existe
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo crear el directorio de uploads: " + uploadsPath, e);
        }
        return uploadsPath.resolve(filename).toAbsolutePath();
    }
}
