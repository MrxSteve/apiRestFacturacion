package com.facturacion.backend.controllers;

import com.facturacion.backend.models.dtos.FacturaDTO;
import com.facturacion.backend.models.service.IFacturaService;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/facturas")
@Data
public class FacturaController {

    private final IFacturaService facturaService;

    // Crear una factura
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveFactura(@RequestBody FacturaDTO facturaDTO) {
        Map<String, String> response = new HashMap<>();
        try {
            // Crear la factura
            FacturaDTO nuevaFactura = facturaService.save(facturaDTO);

            // Respuesta exitosa
            response.put("message", "Factura creada con éxito y correo enviado al cliente.");
            response.put("facturaId", nuevaFactura.getId().toString());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Manejar errores
            response.put("error", "Hubo un problema al crear la factura: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Obtener una factura por ID
    @GetMapping("/{id}")
    public FacturaDTO findById(@PathVariable Long id) {
        return facturaService.findById(id);
    }

    // Obtener todas las facturas con paginación
    @GetMapping("/all")
    public Page<FacturaDTO> findAll(Pageable pageable) {
        return facturaService.findAll(pageable);
    }

    // Obtener facturas por rango de fechas con paginación
    @GetMapping("/by-date-range")
    public ResponseEntity<Map<String, Object>> findByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            Pageable pageable) {

        Page<FacturaDTO> facturasPage = facturaService.findAllByCreateAtBetween(startDate, endDate, pageable);

        // Calcular el total de dinero de todas las facturas en este rango
        List<FacturaDTO> todasFacturas = facturaService.findAllByCreateAtBetween(startDate, endDate);
        double totalDinero = todasFacturas.stream()
                .flatMap(factura -> factura.getItems().stream())
                .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                .sum();

        // Crear la respuesta con la paginación y el total global
        Map<String, Object> response = new HashMap<>();
        response.put("content", facturasPage.getContent());
        response.put("totalPages", facturasPage.getTotalPages());
        response.put("totalElements", facturasPage.getTotalElements());
        response.put("currentPage", facturasPage.getNumber());
        response.put("totalDinero", totalDinero); // Total acumulado

        return ResponseEntity.ok(response);
    }

    // Obtener facturas por rango de fechas sin paginación
    @GetMapping("/by-date-range/pdf")
    public ResponseEntity<byte[]> exportFacturasByDateRangeToPDF(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        try {
            // Generar el PDF utilizando el metodo del servicio
            ByteArrayInputStream pdfStream = facturaService.generatePdfByDateRange(startDate, endDate);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=facturas_rango.pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    // Eliminar una factura por ID
    @DeleteMapping("/delete/{id}")
    public void deleteFactura(@PathVariable Long id) {
        facturaService.delete(id);
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generatePdf(@PathVariable Long id) {
        try {
            ByteArrayInputStream pdfStream = facturaService.generateFacturaPdf(id);

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "inline; filename=factura_" + id + ".pdf");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfStream.readAllBytes());
        } catch (Exception e) {
            throw new RuntimeException("Error al generar el PDF", e);
        }
    }

    @GetMapping("/cliente/{clienteId}")
    public ResponseEntity<Map<String, Object>> getFacturasByCliente(
            @PathVariable Long clienteId,
            Pageable pageable) {

        // Obtener las facturas paginadas
        Page<FacturaDTO> facturasPage = facturaService.findAllByCliente(clienteId, pageable);

        // Calcular el total de dinero de todas las facturas de este cliente
        List<FacturaDTO> todasFacturas = facturaService.findAllByCliente(clienteId);
        double totalDinero = todasFacturas.stream()
                .flatMap(factura -> factura.getItems().stream())
                .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                .sum();

        // Crear la respuesta con la paginación y el total
        Map<String, Object> response = new HashMap<>();
        response.put("content", facturasPage.getContent());
        response.put("totalPages", facturasPage.getTotalPages());
        response.put("totalElements", facturasPage.getTotalElements());
        response.put("currentPage", facturasPage.getNumber());
        response.put("totalDinero", totalDinero);

        return ResponseEntity.ok(response);
    }


}
