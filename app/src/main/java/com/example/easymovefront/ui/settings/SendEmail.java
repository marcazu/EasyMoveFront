package com.example.easymovefront.ui.settings;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;



public class SendEmail {

    static Properties mailServerProperties;
    static Session getMailSession;
    static MimeMessage generateMailMessage;

    public static void generateAndSendEmail(String body) throws MessagingException {

        // Paso1 (no hace falta modificar puertos)
        System.out.println("\n 1 ===> Guardando propiedades del servidor");
        mailServerProperties = System.getProperties();
        mailServerProperties.put("mail.smtp.port", "587");
        mailServerProperties.put("mail.smtp.auth", "true");
        mailServerProperties.put("mail.smtp.starttls.enable", "true");
        System.out.println("Correcto");

        // Paso2
        System.out.println("\n 2 ===> Obteniendo sesión del correo");
        getMailSession = Session.getDefaultInstance(mailServerProperties, null);
        generateMailMessage = new MimeMessage(getMailSession);
        generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress("adem998@gmail.com"));
        generateMailMessage.setSubject("Feedback");
        String emailBody = body + "<br><br>Feedback from EasyMov app.";
        generateMailMessage.setContent(emailBody, "text/html");
        System.out.println("Correcto");

        // Paso3
        System.out.println("\n 3 ===> Iniciando sesión y enviando mail");
        Transport transport = getMailSession.getTransport("smtp");

        // Introducimos usuario y contraseña (no hace falta modificar)
        transport.connect("smtp.gmail.com", "easymovapp@gmail.com", "Qwerty123!");
        transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
        transport.close();
    }
}