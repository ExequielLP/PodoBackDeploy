package Podogonnet.App.servis;

import Podogonnet.App.dto.EmailDto;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendMail(String email) throws MessagingException {
        try {
            System.out.println("--------------------------------email");
            System.out.println(email);
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email.trim());  // Usar trim para eliminar espacios
            helper.setSubject("Recuperacion de contraseña PodoGonnet");
            Context context = new Context();

//            context.setVariable("message", "Diablooo que dificil");
            String contentHTML = templateEngine.process("email", context);

            helper.setText(contentHTML, true);
            javaMailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Error" + "al enviar correo a" + e.getMessage());
        }
    }
}
