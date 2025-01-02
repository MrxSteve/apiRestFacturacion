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
import com.facturacion.backend.models.repositories.FacturaRepository;
import com.facturacion.backend.models.repositories.ProductoRepository;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

@Service
@Data
public class FacturaServiceImp implements IFacturaService {
    private final FacturaRepository facturaRepository;
    private final ClienteRepository clienteRepository;
    private final ProductoRepository productoRepository;
    private final IEmailService emailService;

    @Override
    @Transactional
    public FacturaDTO save(FacturaDTO facturaDTO) {
        FacturaEntity facturaEntity = mapearEntity(facturaDTO);

        // Carga explícita del cliente desde la base de datos
        if (facturaEntity.getCliente() != null && facturaEntity.getCliente().getId() != null) {
            ClienteEntity cliente = clienteRepository.findById(facturaEntity.getCliente().getId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado"));
            facturaEntity.setCliente(cliente);
        }

        // Carga explícita de los productos desde la base de datos
        if (facturaEntity.getItems() != null) {
            facturaEntity.getItems().forEach(detalle -> {
                if (detalle.getProducto() != null && detalle.getProducto().getId() != null) {
                    ProductoEntity producto = productoRepository.findById(detalle.getProducto().getId())
                            .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                    detalle.setProducto(producto);
                }
            });
        }

        FacturaEntity facturaGuardada = facturaRepository.save(facturaEntity);

        FacturaDTO facturaGenerada = mapearDTO(facturaGuardada);

        try {
            System.out.println("Email del cliente: " + facturaGenerada.getCliente().getEmail());

            ByteArrayInputStream pdfStream = generateFacturaPdf(facturaGenerada.getId());
            byte[] pdfBytes = pdfStream.readAllBytes();

            String contenidoHtml = generarHtmlCorreo(facturaGenerada);

            emailService.enviarCorreoConAdjunto(
                    facturaGenerada.getCliente().getEmail(),
                    "Factura Generada: " + facturaGenerada.getDescripcion(),
                    contenidoHtml,
                    pdfBytes
            );

        } catch (Exception e) {
            System.err.println("Error al enviar el correo: " + e.getMessage());
        }

        return facturaGenerada;
    }

    private String generarHtmlCorreo(FacturaDTO factura) throws Exception {
        Path plantillaPath = Path.of("src/main/resources/templates/factura_email_template.html");
        String htmlTemplate = Files.readString(plantillaPath);

        StringBuilder productosHtml = new StringBuilder();
        for (DetalleFacturaDTO item : factura.getItems()) {
            productosHtml.append("<tr>")
                    .append("<td>").append(item.getProducto().getNombre()).append("</td>")
                    .append("<td>").append(String.format("$%.2f", item.getProducto().getPrecio())).append("</td>")
                    .append("<td>").append(item.getCantidad()).append("</td>")
                    .append("<td>").append(String.format("$%.2f", item.getCantidad() * item.getProducto().getPrecio())).append("</td>")
                    .append("</tr>");
        }

        Map<String, String> variables = new HashMap<>();
        variables.put("nombreCliente", factura.getCliente().getNombre() != null ? factura.getCliente().getNombre() : "N/A");
        variables.put("folio", String.valueOf(factura.getId()));
        variables.put("descripcion", factura.getDescripcion() != null ? factura.getDescripcion() : "N/A");
        variables.put("total", String.format("%.2f", factura.getItems().stream()
                .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio()).sum()));
        variables.put("fecha", factura.getCreateAt() != null ? factura.getCreateAt().toString() : "N/A");
        variables.put("productos", productosHtml.toString());
        variables.put("linkFactura", "http://localhost:5173/facturas/ver/" + factura.getId());

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            htmlTemplate = htmlTemplate.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }

        return htmlTemplate;
    }

    @Override
    @Transactional(readOnly = true)
    public FacturaDTO findById(Long id) {
        FacturaEntity facturaEntity = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        return mapearDTO(facturaEntity);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        FacturaEntity facturaEntity = facturaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        facturaRepository.delete(facturaEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacturaDTO> findAll(Pageable pageable) {
        Page<FacturaEntity> facturas = facturaRepository.findAll(pageable);

        return facturas.map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacturaDTO> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<FacturaEntity> facturas = facturaRepository.findAllByCreateAtBetween(startDate, endDate, pageable);

        return facturas.map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findAllByCreateAtBetween(LocalDate startDate, LocalDate endDate) {
        List<FacturaEntity> facturas = facturaRepository.findAllByCreateAtBetween(startDate, endDate);

        return facturas.stream().map(this::mapearDTO).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FacturaDTO> findAllByCliente(Long clienteId, Pageable pageable) {
        Page<FacturaEntity> facturas = facturaRepository.findAllByClienteId(clienteId, pageable);
        return facturas.map(this::mapearDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FacturaDTO> findAllByCliente(Long clienteId) {
        List<FacturaEntity> facturas = facturaRepository.findAllByClienteId(clienteId);

        return facturas.stream()
                .map(this::mapearDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream generateFacturaPdf(Long id) throws Exception {
        FacturaDTO factura = findById(id);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        PdfWriter.getInstance(document, out);
        document.open();

        // Título con color y alineación
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("Factura: " + factura.getDescripcion(), titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Espacio
        document.add(new Paragraph(" "));

        // Sección de cliente
        Font sectionFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.DARK_GRAY);
        Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.BLACK);

        Paragraph clienteSection = new Paragraph("Datos del Cliente", sectionFont);
        clienteSection.setSpacingAfter(5);
        document.add(clienteSection);

        PdfPTable clienteTable = new PdfPTable(2);
        clienteTable.setWidthPercentage(100);
        clienteTable.setSpacingAfter(10);

        clienteTable.addCell(new PdfPCell(new Phrase("Nombre:", contentFont)));
        clienteTable.addCell(new PdfPCell(new Phrase(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido(), contentFont)));

        clienteTable.addCell(new PdfPCell(new Phrase("Email:", contentFont)));
        clienteTable.addCell(new PdfPCell(new Phrase(factura.getCliente().getEmail(), contentFont)));

        document.add(clienteTable);

        // Sección de factura
        Paragraph facturaSection = new Paragraph("Datos de la Factura", sectionFont);
        facturaSection.setSpacingAfter(5);
        document.add(facturaSection);

        PdfPTable facturaTable = new PdfPTable(2);
        facturaTable.setWidthPercentage(100);
        facturaTable.setSpacingAfter(10);

        facturaTable.addCell(new PdfPCell(new Phrase("Folio:", contentFont)));
        facturaTable.addCell(new PdfPCell(new Phrase(String.valueOf(factura.getId()), contentFont)));

        facturaTable.addCell(new PdfPCell(new Phrase("Fecha:", contentFont)));
        facturaTable.addCell(new PdfPCell(new Phrase(factura.getCreateAt().toString(), contentFont)));

        facturaTable.addCell(new PdfPCell(new Phrase("Observación:", contentFont)));
        facturaTable.addCell(new PdfPCell(new Phrase(factura.getObservacion(), contentFont)));

        document.add(facturaTable);

        // Tabla de productos
        Paragraph productosSection = new Paragraph("Productos", sectionFont);
        productosSection.setSpacingAfter(5);
        document.add(productosSection);

        PdfPTable productosTable = new PdfPTable(4);
        productosTable.setWidthPercentage(100);
        productosTable.setWidths(new int[]{3, 1, 1, 1});
        productosTable.setSpacingAfter(10);

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        PdfPCell hcell;
        hcell = new PdfPCell(new Phrase("Producto", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productosTable.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Precio", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productosTable.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Cantidad", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productosTable.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Subtotal", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        productosTable.addCell(hcell);

        for (var item : factura.getItems()) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(item.getProducto().getNombre(), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            productosTable.addCell(cell);

            cell = new PdfPCell(new Phrase("$" + item.getProducto().getPrecio(), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            productosTable.addCell(cell);

            cell = new PdfPCell(new Phrase(String.valueOf(item.getCantidad()), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            productosTable.addCell(cell);

            cell = new PdfPCell(new Phrase("$" + (item.getProducto().getPrecio() * item.getCantidad()), contentFont));
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            productosTable.addCell(cell);
        }

        document.add(productosTable);

        // Total
        Paragraph totalParagraph = new Paragraph("Total: $" + factura.getItems().stream().mapToDouble(
                item -> item.getProducto().getPrecio() * item.getCantidad()).sum(), sectionFont);
        totalParagraph.setAlignment(Element.ALIGN_RIGHT);
        document.add(totalParagraph);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }

    @Override
    @Transactional(readOnly = true)
    public ByteArrayInputStream generatePdfByDateRange(LocalDate startDate, LocalDate endDate) throws Exception {
        List<FacturaDTO> facturas = findAllByCreateAtBetween(startDate, endDate);

        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);
        document.open();

        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLUE);
        Paragraph title = new Paragraph("Reporte de Facturas", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.setWidthPercentage(100);
        table.setWidths(new int[]{3, 2, 2, 2});

        Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, BaseColor.WHITE);

        PdfPCell hcell;
        hcell = new PdfPCell(new Phrase("Descripción", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Cliente", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Fecha", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(hcell);

        hcell = new PdfPCell(new Phrase("Total", headFont));
        hcell.setBackgroundColor(BaseColor.BLUE);
        hcell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(hcell);

        double totalDinero = 0;

        for (FacturaDTO factura : facturas) {
            PdfPCell cell;

            cell = new PdfPCell(new Phrase(factura.getDescripcion()));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(factura.getCliente().getNombre() + " " + factura.getCliente().getApellido()));
            cell.setPaddingLeft(5);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_LEFT);
            table.addCell(cell);

            cell = new PdfPCell(new Phrase(factura.getCreateAt().toString()));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(cell);

            double total = factura.getItems().stream()
                    .mapToDouble(item -> item.getCantidad() * item.getProducto().getPrecio())
                    .sum();
            totalDinero += total;
            cell = new PdfPCell(new Phrase("$" + String.format("%.2f", total)));
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
            table.addCell(cell);
        }

        document.add(table);

        // Agregar resumen al final
        document.add(new Paragraph(" "));
        Font summaryFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14, BaseColor.BLACK);
        Paragraph resumen = new Paragraph("Resumen:", summaryFont);
        document.add(resumen);

        Paragraph totalFacturas = new Paragraph("Cantidad de facturas encontradas: " + facturas.size(), summaryFont);
        totalFacturas.setSpacingBefore(5);
        document.add(totalFacturas);

        Paragraph totalDineroParagraph = new Paragraph("Total de todas las facturas: $" + String.format("%.2f", totalDinero), summaryFont);
        totalDineroParagraph.setSpacingBefore(5);
        document.add(totalDineroParagraph);

        document.close();

        return new ByteArrayInputStream(out.toByteArray());
    }


    // Convertir Entity a DTO
    private FacturaDTO mapearDTO(FacturaEntity facturaEntity) {
        FacturaDTO facturaDTO = new FacturaDTO();
        facturaDTO.setId(facturaEntity.getId());
        facturaDTO.setDescripcion(facturaEntity.getDescripcion());
        facturaDTO.setObservacion(facturaEntity.getObservacion());
        facturaDTO.setCreateAt(facturaEntity.getCreateAt());

        // Mapear Cliente
        if (facturaEntity.getCliente() != null) {
            ClienteDTO clienteDTO = new ClienteDTO();
            clienteDTO.setId(facturaEntity.getCliente().getId());
            clienteDTO.setNombre(facturaEntity.getCliente().getNombre());
            clienteDTO.setApellido(facturaEntity.getCliente().getApellido());
            clienteDTO.setEmail(facturaEntity.getCliente().getEmail()); // Asegúrate de mapear el email
            clienteDTO.setCreateAt(facturaEntity.getCliente().getCreateAt());
            facturaDTO.setCliente(clienteDTO);
        }

        // Mapear Detalles
        if (facturaEntity.getItems() != null) {
            List<DetalleFacturaDTO> items = facturaEntity.getItems().stream().map(detalle -> {
                DetalleFacturaDTO detalleDTO = new DetalleFacturaDTO();
                detalleDTO.setId(detalle.getId());
                detalleDTO.setCantidad(detalle.getCantidad());

                if (detalle.getProducto() != null) {
                    ProductoDTO productoDTO = new ProductoDTO();
                    productoDTO.setId(detalle.getProducto().getId());
                    productoDTO.setNombre(detalle.getProducto().getNombre());
                    productoDTO.setPrecio(detalle.getProducto().getPrecio() != null ? detalle.getProducto().getPrecio() : 0.0); // Validación aquí
                    detalleDTO.setProducto(productoDTO);
                }

                return detalleDTO;
            }).toList();
            facturaDTO.setItems(items);
        }

        return facturaDTO;
    }

    // Convertir DTO a Entity
    private FacturaEntity mapearEntity(FacturaDTO facturaDTO) {
        FacturaEntity facturaEntity = new FacturaEntity();
        facturaEntity.setDescripcion(facturaDTO.getDescripcion());
        facturaEntity.setObservacion(facturaDTO.getObservacion());
        facturaEntity.setCreateAt(facturaDTO.getCreateAt());

        // Mapear Cliente
        if (facturaDTO.getCliente() != null) {
            ClienteEntity clienteEntity = new ClienteEntity();
            clienteEntity.setId(facturaDTO.getCliente().getId()); // Solo asigna el ID
            facturaEntity.setCliente(clienteEntity);
        }

        // Mapear Detalles
        if (facturaDTO.getItems() != null) {
            List<DetalleFacturaEntity> items = facturaDTO.getItems().stream().map(detalleDTO -> {
                DetalleFacturaEntity detalleEntity = new DetalleFacturaEntity();
                detalleEntity.setCantidad(detalleDTO.getCantidad());
                if (detalleDTO.getProducto() != null) {
                    ProductoEntity productoEntity = new ProductoEntity();
                    productoEntity.setId(detalleDTO.getProducto().getId()); // Solo asigna el ID
                    detalleEntity.setProducto(productoEntity);
                }
                detalleEntity.setFactura(facturaEntity); // Relación bidireccional
                return detalleEntity;
            }).toList();
            facturaEntity.setItems(items);
        }

        return facturaEntity;
    }
}
