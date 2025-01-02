package com.facturacion.backend.models.service;

import jakarta.mail.MessagingException;

public interface IEmailService {
    void enviarCorreoConAdjunto(String destinatario, String asunto, String htmlContenido, byte[] pdfBytes) throws MessagingException;
}
