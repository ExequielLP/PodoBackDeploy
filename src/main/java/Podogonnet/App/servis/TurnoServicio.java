package Podogonnet.App.servis;

import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.repository.TurnoRepository;
import Podogonnet.App.repository.UsuarioRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
        return turnoRepository.findByStartTimeBetween(startOfDay, endOfDay);
    }

    public Turno reservarTurno(String id, String idServicio, String usuarioid) {
        Turno turno = turnoRepository.findById(id).orElseThrow(() -> new RuntimeException("Turno no encontrado"));
        Usuario usuario = usuarioRepositorio.findById(usuarioid).orElseThrow(() -> new RuntimeException("Usario no encontrado"));

        ServicioPodo servicioPodo = podoServicio.findById(idServicio);
        turno.setServicioPodo(servicioPodo);
        turno.setEstado(!turno.isEstado());
        turno.setUsuario(usuario);
        turnoRepository.save(turno);
        System.out.println("devoolviendoooooooing");
        return turno;
    }


    public void createDailyAppointmentsForAWeek() {
        // Verifica si no hay turnos creados
        if (turnoRepository.count() == 0) {
            LocalDate today = LocalDate.now();
            for (int day = 0; day < 7; day++) {
                LocalDate date = today.plusDays(day);
                LocalDateTime startOfDay = date.atTime(9, 0);
                for (int i = 0; i < 5; i++) { // Cambiado a 5 turnos
                    LocalDateTime startTime = startOfDay.plusHours(i * 2);
                    LocalDateTime endTime = startTime.plusHours(2);
                    Turno turno = new Turno();
                    turno.setStartTime(startTime);
                    turno.setEndTime(endTime);
                    turno.setEstado(false);
                    turnoRepository.save(turno);
                }
            }
        }
    }

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
       Optional<Turno>turnoOptional=turnoRepository.findById(id);
       if (turnoOptional.isPresent()){
           Turno turno=turnoOptional.get();
           turno.setEstado(!turno.isEstado());
           turnoRepository.save(turno);
       }
    }
}







