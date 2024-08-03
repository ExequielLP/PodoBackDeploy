package Podogonnet.App.servis;

import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.repository.TurnoRepository;
import Podogonnet.App.repository.UsuarioRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.List;
import java.util.Optional;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Service
public class TurnoServicio {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private PodoServicio podoServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public List<Turno> turnosDelDia(LocalDate date) {
        // Inicio del día a las 9:00 AM
        LocalDateTime startOfDay = date.atTime(9, 0);
        // Fin del día a las 6:00 PM (18:00)
        LocalDateTime endOfDay = date.atTime(18, 0);
        return turnoRepository.findByStartTimeBetweenAndEstadoFalse(startOfDay, endOfDay);
    }

    @Transactional
    public Turno reservarTurno(String id, String idServicio, String usuarioid) {
        try {
            // Buscar el turno por ID
            Turno turno = turnoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Turno no encontrado"));

            // Buscar el usuario por ID
            Usuario usuario = usuarioRepositorio.findById(usuarioid)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Verificar si el turno ya tiene un usuario asignado (está ocupado)
            if (turno.getUsuario() != null) {
                throw new RuntimeException("El turno ya está ocupado, lo siento");
            }


            // Obtener el servicio podal y asignar los detalles al turno
            ServicioPodo servicioPodo = podoServicio.findById(idServicio);
            turno.setServicioPodo(servicioPodo);
            turno.setEstado(!turno.isEstado());
            turno.setUsuario(usuario);
            turnoRepository.save(turno);

            return turno;
        } catch (Exception e) {
            throw new RuntimeException("Error al reservar el turno: " + e.getMessage(), e);
        }
    }


    // public void createDailyAppointmentsForAWeek() {
    // // Verifica si no hay turnos creados
    // if (turnoRepository.count() == 0) {
    // LocalDate today = LocalDate.now();
    // for (int day = 0; day < 7; day++) {
    // LocalDate date = today.plusDays(day);
    // LocalDateTime startOfDay = date.atTime(9, 0);
    // for (int i = 0; i < 5; i++) { // Cambiado a 5 turnos
    // LocalDateTime startTime = startOfDay.plusHours(i * 2);
    // LocalDateTime endTime = startTime.plusHours(2);
    // Turno turno = new Turno();
    // turno.setStartTime(startTime);
    // turno.setEndTime(endTime);
    // turno.setEstado(false);
    // turnoRepository.save(turno);
    // }
    // }
    // }
    // }

    public List<Turno> listaDeTurnosId(String id) {

        Usuario usuario = usuarioRepositorio.findById(id).orElseThrow();

        return turnoRepository.findByUsuario(usuario);
    }

    public Turno cancelarTurno(String id) {
        try {
            Optional<Turno> turno = turnoRepository.findById(id);
            if (turno.isPresent()) {
                Turno turnoNew = turno.get();
                turnoNew.setEstado(false);
                turnoRepository.save(turnoNew);
                return turnoNew;
            } else {
                throw new RuntimeException("Turno con id " + id + " no encontrado");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al cancelar el turno con id " + id, e);
        }
    }

    public List<Turno> findAll() {
        return turnoRepository.findAll();
    }

    public void AltaBaja(String id) {
        Optional<Turno> turnoOptional = turnoRepository.findById(id);
        if (turnoOptional.isPresent()) {
            Turno turno = turnoOptional.get();
            turno.setEstado(!turno.isEstado());
            turnoRepository.save(turno);
        }
    }

    private static final Set<LocalDate> FERIADOS = new HashSet<>(Arrays.asList(
            LocalDate.of(2024, Month.JANUARY, 1),
            LocalDate.of(2024, Month.JULY, 9)
    // Añadir más feriados según corresponda
    ));

    public boolean esDiaLaboral(LocalDate date) {
        return !FERIADOS.contains(date) &&
                date.getDayOfWeek() != DayOfWeek.SATURDAY &&
                date.getDayOfWeek() != DayOfWeek.SUNDAY;
    }

    public void generarTurnos(LocalDate inicio, LocalDate fin) {
        while (inicio.isBefore(fin)) {
            if (esDiaLaboral(inicio)) {
                List<LocalTime> horarios = Arrays.asList(
                        LocalTime.of(9, 0),
                        LocalTime.of(10, 30),
                        LocalTime.of(14, 0),
                        LocalTime.of(15, 30),
                        LocalTime.of(17, 30));

                for (LocalTime hora : horarios) {
                    LocalDateTime startTime = LocalDateTime.of(inicio, hora);
                    LocalDateTime endTime = startTime.plusHours(1);

                    // Verificar si el turno ya existe
                    boolean exists = turnoRepository.existsByStartTimeAndEndTime(startTime, endTime);
                    if (!exists) {
                        Turno turno = new Turno();
                        turno.setStartTime(startTime);
                        turno.setEndTime(endTime);
                        turno.setEstado(false); // Asumiendo que 'true' significa activo
                        turnoRepository.save(turno);
                    }
                }
            }
            inicio = inicio.plusDays(1);
        }
    }
}
