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
        if (usuarioRepository.findByUserName("administrador").isEmpty()) {
            Usuario admin = new Usuario();
            admin.setUserName("administrador");
            admin.setNombre("Administrador");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setRol(Rol.ADMIN);

            usuarioRepository.save(admin);
        }
    }
}
