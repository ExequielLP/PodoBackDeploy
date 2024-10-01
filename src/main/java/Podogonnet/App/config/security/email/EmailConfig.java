package Podogonnet.App.config.security.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class EmailConfig {

    @Value("${email.username}")
    private String username;
    @Value("${email.password}")
    private String password;

    private Properties getMailProperties() {
        Properties properties = new Properties();
        properties.put("mail.smtp.auth","true");
        properties.put("mail.smtp.starttls.enable","true");  // Corrección
        properties.put("mail.smtp.host","smtp.gmail.com");   // Corrección
        properties.put("mail.smtp.port","587");
        properties.put("mail.transport.protocol","smtp");
        properties.put("mail.smtp.ssl.trust","smtp.gmail.com");
        properties.put("mail.debug","true");  // Para depuración opcional
        return properties;
    }

    @Bean
    public JavaMailSender javaMailSender() {

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setJavaMailProperties(getMailProperties());
        mailSender.setUsername(username.trim());
        mailSender.setPassword(password.trim());
        return mailSender;
    }

    @Bean
    public ResourceLoader resourceLoader() {
        return new DefaultResourceLoader();
    }
}
