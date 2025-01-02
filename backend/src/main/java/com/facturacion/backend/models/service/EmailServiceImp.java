package com.facturacion.backend.models.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Data;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Data
public class EmailServiceImp implements IEmailService {
    private final JavaMailSender mailSender;

    @Override
    public void enviarCorreoConAdjunto(String destinatario, String asunto, String htmlContenido, byte[] pdfBytes) throws MessagingException {
        MimeMessage mensaje = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

        helper.setTo(destinatario);
        helper.setSubject(asunto);
        helper.setText(htmlContenido, true); // true para indicar que es HTML

        // Adjuntar el archivo PDF
        helper.addAttachment("Factura.pdf", new ByteArrayResource(pdfBytes));

        mailSender.send(mensaje);
    }
}
