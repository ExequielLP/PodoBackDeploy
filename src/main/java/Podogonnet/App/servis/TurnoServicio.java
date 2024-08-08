package Podogonnet.App.servis;

import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;

import Podogonnet.App.repository.DiaRepositorio;
import Podogonnet.App.repository.TurnoRepository;

import Podogonnet.App.repository.UsuarioRepositorio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.*;
import java.util.*;

@Service
public class TurnoServicio {

    @Autowired
    private TurnoRepository turnoRepository;

    @Autowired
    private PodoServicio podoServicio;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Autowired
    private DiaRepositorio diaRepositorio;

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


    public List<TurnosUsuario> listaDeTurnosId(String id) {

      try {
          Usuario usuario = usuarioRepositorio.findById(id).orElseThrow();
          List<TurnosUsuario> ListDto = new ArrayList<>();
          List<Turno> turnos = turnoRepository.findByUsuario(usuario);
          for (Turno turDB : turnos){
              TurnosUsuario turnoDtp=new TurnosUsuario();
              turnoDtp.setId(turDB.getId());
              turnoDtp.setNombreServicio(turDB.getServicioPodo().getNombre());
              turnoDtp.setCosto(turDB.getServicioPodo().getCosto());
              turnoDtp.setStartTime(turDB.getStartTime());
              turnoDtp.setEndTime(turDB.getEndTime());
              turnoDtp.setEstado(turDB.isEstado());
              ListDto.add(turnoDtp);
          }
          return ListDto;
      }catch (Exception e){
          throw new  RuntimeException("Error con traer los turnos del usuario"+e.getMessage());
      }

    }

    public Turno cancelarTurno(String id) {
        try {
            Optional<Turno> turno = turnoRepository.findById(id);
            if (turno.isPresent()) {
                Turno turnoNew = turno.get();
                turnoNew.setEstado(false);
                turnoNew.setId(null);
                turnoNew.setServicioPodo(null);
                turnoNew.setUsuario(null);
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
            turno.setEstado(false);
            turno.setId(null);
            turno.setServicioPodo(null);
            turno.setUsuario(null);
            turno.setEstado(!turno.isEstado());
            turnoRepository.save(turno);
        }
    }

    public void generarTurnos(LocalDate startDate, LocalDate endDate) {
        List<LocalTime[]> horarios = new ArrayList<>();
        horarios.add(new LocalTime[]{LocalTime.of(9, 0), LocalTime.of(10, 0)});
        horarios.add(new LocalTime[]{LocalTime.of(10, 30), LocalTime.of(11, 30)});
        horarios.add(new LocalTime[]{LocalTime.of(14, 0), LocalTime.of(15, 0)});
        horarios.add(new LocalTime[]{LocalTime.of(15, 30), LocalTime.of(16, 30)});
        horarios.add(new LocalTime[]{LocalTime.of(16, 30), LocalTime.of(17, 30)});

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (esDiaLaboral(date)) {
                // Verificar si ya existe un día con esta fecha
                Optional<Dia> diaExistente = diaRepositorio.findByFecha(date);

                if (diaExistente.isPresent()) {
                    // Si ya existe un día con turnos para esta fecha, continuar con el siguiente día
                    continue;
                }

                Dia dia = new Dia();
                dia.setFecha(date);
                dia.setFeriado(false); // Asume que el día no es festivo
                dia.setCompleto(false);

                List<Turno> turnosDelDia = new ArrayList<>();
                for (LocalTime[] horario : horarios) {
                    LocalDateTime startDateTime = LocalDateTime.of(date, horario[0]);
                    LocalDateTime endDateTime = LocalDateTime.of(date, horario[1]);

                    Turno turno = new Turno();
                    turno.setDia(dia);
                    turno.setStartTime(startDateTime);
                    turno.setEndTime(endDateTime);
                    turno.setEstado(false); // Asume que el turno está disponible
                    turnosDelDia.add(turno);
                }

                // Asociar los turnos con el día
                dia.setTurnos(turnosDelDia);
                // Persistir el día, lo que también guarda los turnos debido al CascadeType.ALL
                diaRepositorio.save(dia);
            }
        }
    }

    private boolean esDiaLaboral(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY;
    }

}
