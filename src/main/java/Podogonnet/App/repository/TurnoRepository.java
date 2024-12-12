package Podogonnet.App.repository;


import Podogonnet.App.dto.TurnoDto;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
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


    Page<Turno> findByEstadoTrue(Pageable pageable);

    List<Turno> findByStartTimeBetween(LocalDateTime startOfMonth, LocalDateTime endOfMonth);
//
//    @Query(value = "SELECT * FROM turno t WHERE t.usuario_nombre LIKE %:nombre%", nativeQuery = true)
//    List<Turno> findByUsuarioNombre(String nombre);

    List<Turno> findByServicioPodoNombre(String servicio);


// QUERY NO NATIVA QUE FUNCIONA
//        @Query("SELECT t FROM Turno t " +
//            "JOIN t.servicioPodo s " +
//            "JOIN t.usuario u " +
//            "JOIN t.dia d " +
//            "WHERE (:servicio IS NULL OR s.nombre LIKE %:servicio%) " +
//            "AND (:nombre IS NULL OR u.nombre LIKE %:nombre%) " +
//            "AND (:fecha IS NULL OR d.fecha = :fecha)")
//    List<Turno> searchByCriteria(@Param("nombre") String nombre,
//                                 @Param("servicio") String servicio,
//                                 @Param("fecha") LocalDate fecha);
// QUERY NATIVA



        @Query(value = "SELECT t.* FROM turno t " +
                "JOIN servicio_podo s ON t.servicio_podo_id = s.id " +
                "JOIN usuario u ON t.usuario_id = u.id " +
                "JOIN dia d ON t.dia_id = d.id " +
                "WHERE (:servicio IS NULL OR s.nombre LIKE CONCAT('%', :servicio, '%')) " +
                "AND (:nombre IS NULL OR u.nombre LIKE CONCAT('%', :nombre, '%')) " +
                "AND (:fecha IS NULL OR d.fecha = :fecha)",
                nativeQuery = true)
        List<Turno> searchByCriteria(@Param("nombre") String nombre,
                                     @Param("servicio") String servicio,
                                     @Param("fecha") LocalDate fecha);


//    @Query("SELECT t FROM Turno t " +
//            "JOIN t.servicioPodo s " +
//            "JOIN t.usuario u " +
//            "JOIN t.dia d " +
//            "WHERE (:servicio IS NULL OR s.nombre LIKE %:servicio%) " +
//            "AND (:fecha IS NULL OR d.fecha = :fecha)" +
//            "AND (:name IS NULL OR u.nombre LIKE %:name%) ")
//    List<Turno> searchByCriteria(@Param("name") String name,
//                                 @Param("servicio") String servicio,
//                                 @Param("fecha") java.sql.Date fecha);
    

}










