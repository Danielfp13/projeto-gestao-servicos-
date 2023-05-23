package com.api.clientes.Service;

import com.api.clientes.model.entity.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    public void enviarEmailRedefinicaoSenha(Usuario usuario, String token) {

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(usuario.getUsername());
            message.setSubject("Redefinição de senha");
            message.setText(String.format(
                    "Olá %s,\n\n" + "Recebemos uma solicitação para redefinir a senha associada" + " à sua conta.\n" +
                            "Para prosseguir com a redefinição, por favor, clique no link abaixo:" + "\n\n" +
                            "http://localhost:4200/forgot?token=%s\n\n" + "Este link terá validade de 1 hora. Caso não " +
                            "tenha solicitado a redefinição de senha," + " por favor, ignore este e-mail e " +
                            "certifique-se de manter sua conta segura.\n\n" + "Atenciosamente,\n" + "Equipe de Suporte.",
                    usuario.getNome(), token));
            emailSender.send(message);
        } catch (MailException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao enviar email.");
        }
    }
}
