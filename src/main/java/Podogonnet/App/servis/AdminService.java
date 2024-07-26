package Podogonnet.App.servis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;

@Component
public class AdminService implements CommandLineRunner {

    @Autowired
    private UsuarioRepositorio usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        try {

            if (usuarioRepository.findByUserName("admin").isEmpty()) {
                Usuario admin = new Usuario();
                admin.setUserName("admin");
                admin.setNombre("Administrador");
                admin.setEmail("admin@example.com");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setRol(Rol.ADMIN);
                System.out.println("ADMIN GENERADO CON EXITO!");
                usuarioRepository.save(admin);
                // hola
            }

        } catch (Exception e) {
            throw new Exception(e.getMessage() + "Error al cargar el admin manualmente");

        }
    }
}
