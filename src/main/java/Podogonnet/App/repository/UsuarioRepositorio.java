package Podogonnet.App.repository;


import Podogonnet.App.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario, String> {
    Optional<Usuario> findByEmail(String username);

    // Page<Usuario>findAll(Pageable pageable);

}
