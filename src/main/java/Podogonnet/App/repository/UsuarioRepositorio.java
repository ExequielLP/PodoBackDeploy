package Podogonnet.App.repository;

import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepositorio extends JpaRepository<Usuario,String> {
    Optional<Usuario> findByUserName(String username);



//Page<Usuario>findAll(Pageable pageable);




}
