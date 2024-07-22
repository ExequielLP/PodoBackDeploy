package Podogonnet.App.repository;

import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno , String> {
    List<Turno> findByStartTimeBetween(LocalDateTime startTime, LocalDateTime endTime);

    Optional<Turno> findById(String appointmentId);


    List<Turno> findByUsuario(Usuario usuario);
}
