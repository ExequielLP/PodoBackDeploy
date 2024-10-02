package Podogonnet.App.servis;

import Podogonnet.App.dto.SaveUser;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioServicio {
    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Page<Usuario> listaDeUser(Pageable pageable) {
        Page<Usuario> list = usuarioRepositorio.findAll(pageable);
        return list;
    }

    public Usuario registerOneCostumer(SaveUser newUser) throws Exception {
        validatePassword(newUser);
        Usuario user = new Usuario();
        user.setNombre(newUser.getName());
        user.setUserName(newUser.getUserName());
        user.setRol(Rol.USER);
        user.setEmail(newUser.getEmail());
        user.setPassword(passwordEncoder.encode(newUser.getPassword()));
        usuarioRepositorio.save(user);


        return user;
    }

    private void validatePassword(SaveUser newUser) throws Exception {
        if (newUser.getPassword().isEmpty() || newUser.getRepeatePassword().isEmpty()) {
            throw new Exception("PASSWORD VACIA");
        } else if (!newUser.getPassword().equals(newUser.getRepeatePassword())) {
            throw new Exception("LOS PASSWORD NO SON IGUALES");
        }
    }

    public Usuario findOneByEmail(String email) {
        Usuario usuario = usuarioRepositorio.findByEmail(email).get();
        return usuario;
    }

    public Usuario findByEmail(String email) throws Throwable {
        return (Usuario) usuarioRepositorio.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario con email " + email + " no encontrado"));
    }
}