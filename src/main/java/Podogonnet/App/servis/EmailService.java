package Podogonnet.App.servis;

import Podogonnet.App.dto.EmailDto;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.auth.JwtService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.HashMap;
import java.util.Map;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    public void sendMail(String email) throws MessagingException {
        try {
            // busco el usuario en base al mail
            Usuario usuario = usuarioServicio.findByEmail(email);
            UserDetails user = usuarioServicio.findOneByEmail(usuario.getEmail());
            String jwt = jwtService.generateTokenPassword(user, generateExtraClaimsPassword((Usuario) user));
            InternetAddress emailAddr = new InternetAddress(email);
            emailAddr.validate();
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(email.trim());  // Usar trim para eliminar espacios
            helper.setSubject("Recuperacion de contraseña PodoGonnet");
            Context context = new Context();
// incorporo direccion de donde va el redirect del mail
            String environment = System.getenv("ENTORNO");
            String domein = "";
            if ("localDBlocal".equalsIgnoreCase(environment)) {
                domein = "http://localhost:5173/create-new-password/";
                System.out.println(domein);
            } else {
                domein = "https://podogonnet.netlify.app/create-new-password/";
                System.out.println(domein);
            }
            String urlRerirec = domein + jwt;
            context.setVariable("link", urlRerirec);
            String contentHTML = templateEngine.process("email", context);
            helper.setText(contentHTML, true);
            javaMailSender.send(message);
        } catch (Throwable e) {

            throw new RuntimeException(e);
        }
    }

    private Map<String, Object> generateExtraClaimsPassword(Usuario user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("id", user.getId());
        extraClaims.put("userName", user.getUsername());
        return extraClaims;

    }
}
