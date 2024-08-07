package Podogonnet.App.repository;

import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TurnoRepository extends JpaRepository<Turno, String> {
    List<Turno> findByStartTimeBetweenAndEstadoFalse(LocalDateTime startTime, LocalDateTime endTime);

    Optional<Turno> findById(String appointmentId);

    List<Turno> findByUsuario(Usuario usuario);

    boolean existsByStartTimeAndEndTime(LocalDateTime startTime, LocalDateTime endTime);

   // Optional<List<Turno>> findByDia();
   @Query(value = "SELECT t.* FROM turno t JOIN dia d ON t.dia_id = d.id WHERE d.fecha = :diaFecha", nativeQuery = true)
   List<Turno> findTurnosByDiaFecha(@Param("diaFecha") LocalDate diaFecha);
}
