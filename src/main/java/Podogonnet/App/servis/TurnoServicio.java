package Podogonnet.App.servis;

import Podogonnet.App.dto.TurnoDto;
import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.Dia;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.repository.DiaRepositorio;
import Podogonnet.App.repository.TurnoRepository;
import Podogonnet.App.repository.UsuarioRepositorio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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
            for (Turno turDB : turnos) {
                TurnosUsuario turnoDtp = new TurnosUsuario();
                turnoDtp.setId(turDB.getId());
                turnoDtp.setNombreServicio(turDB.getServicioPodo().getNombre());
                turnoDtp.setCosto(turDB.getServicioPodo().getCosto());
                turnoDtp.setStartTime(turDB.getStartTime());
                turnoDtp.setEndTime(turDB.getEndTime());
                turnoDtp.setEstado(turDB.isEstado());
                ListDto.add(turnoDtp);
            }
            return ListDto;
        } catch (Exception e) {
            throw new RuntimeException("Error con traer los turnos del usuario" + e.getMessage());
        }

    }

    public Turno cancelarTurno(String id) {
        try {
            Optional<Turno> turno = turnoRepository.findById(id);
            if (turno.isPresent()) {
                Turno turnoNew = turno.get();
                turnoNew.setEstado(false);
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

    public Page<TurnosUsuario> getTurnosAdmin(Pageable pageable) {
        try {
            Page<Turno> turnos = turnoRepository.findByEstadoTrue(pageable);
            System.out.println("-------------------------");
            System.out.println("Total Elements: " + turnos.getTotalElements());
            System.out.println("-------------------------");
            List<TurnosUsuario> turnosUsuarioList = new ArrayList<>();
            for (Turno turnoAux : turnos) {
                TurnosUsuario turnoDto = new TurnosUsuario();
                turnoDto.setId(turnoAux.getId());
                turnoDto.setNombreUsuario(turnoAux.getUsuario().getNombre());
                turnoDto.setNombreServicio(turnoAux.getServicioPodo().getNombre());
                turnoDto.setStartTime(turnoAux.getStartTime());
                turnoDto.setEndTime(turnoAux.getEndTime());
                turnoDto.setEstado(turnoAux.isEstado());
                turnoDto.setCosto(turnoAux.getServicioPodo().getCosto());
                turnosUsuarioList.add(turnoDto);

            }
            Page<TurnosUsuario> turnoDto = new PageImpl<>(turnosUsuarioList, pageable, turnos.getTotalElements());

            return turnoDto;
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
            return Page.empty(pageable);
        }
    }

    public void AltaBaja(String id) {
        Optional<Turno> turnoOptional = turnoRepository.findById(id);
        if (turnoOptional.isPresent()) {
            Turno turno = turnoOptional.get();
            turno.setEstado(false);
            turno.setServicioPodo(null);
            turno.setUsuario(null);
            turnoRepository.save(turno);
        }
    }

    public void generarTurnos(LocalDate startDate, LocalDate endDate) {
        List<LocalTime[]> horarios = new ArrayList<>();
        horarios.add(new LocalTime[] { LocalTime.of(9, 0), LocalTime.of(10, 0) });
        horarios.add(new LocalTime[] { LocalTime.of(10, 30), LocalTime.of(11, 30) });
        horarios.add(new LocalTime[] { LocalTime.of(14, 0), LocalTime.of(15, 0) });
        horarios.add(new LocalTime[] { LocalTime.of(15, 30), LocalTime.of(16, 30) });
        horarios.add(new LocalTime[] { LocalTime.of(16, 30), LocalTime.of(17, 30) });

        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            if (esDiaLaboral(date)) {
                // Verificar si ya existe un día con esta fecha
                Optional<Dia> diaExistente = diaRepositorio.findByFecha(date);

                if (diaExistente.isPresent()) {
                    // Si ya existe un día con turnos para esta fecha, continuar con el siguiente
                    // día
                    continue;
                }

                Dia dia = new Dia();
                dia.setFecha(date);
                dia.setFeriado(null); // Asume que el día no es festivo
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

    public void suspenderTurno(String turnoId) {
        Turno turno = new Turno();
        Optional<Turno> turnoDB = turnoRepository.findById(turnoId);
        if (turnoDB.isPresent()) {
            turno = turnoDB.get();
            turno.setTurnoSuspendible(true);
            turno.setEstado(true);
            turnoRepository.save(turno);
        }

    }

    public List<TurnoDto> turnosDelMes(LocalDate date) {
        try {
            YearMonth yearMonth = YearMonth.of(date.getYear(), date.getMonth());
            System.out.println("yearMonth " + yearMonth);
            LocalDateTime startOfMonth = yearMonth.atDay(1).atStartOfDay();
            System.out.println("startOfMonth " + startOfMonth);
            LocalDateTime endOfMonth = yearMonth.atEndOfMonth().atTime(23, 59, 59);
            System.out.println("endOfMonth " + endOfMonth);
            List<Turno> turnosDelMes = turnoRepository.findByStartTimeBetween(startOfMonth, endOfMonth);
            List<TurnoDto> turnosDtos = new ArrayList<>();
            int cont = 0;
            for (Turno aux : turnosDelMes) {
                TurnoDto turnoDto = new TurnoDto();
                turnoDto.setId(aux.getId());
                turnoDto.setStartTime(aux.getStartTime());
                turnoDto.setEndTime(aux.getEndTime());
                turnoDto.setTurnoSuspendible(aux.isTurnoSuspendible());
                turnoDto.setEstado(aux.isEstado());
                turnoDto.setFeriado(aux.isFeriado());
                turnoDto.setNombreServicio(aux.getServicioPodo() != null ? aux.getServicioPodo().getNombre() : null);
                turnosDtos.add(turnoDto);
                cont = cont + 1;
            }
            System.out.println(turnosDelMes);
            System.out.println("laaaaaaaaaaaaacon");
            System.out.println(cont);
            return turnosDtos;

        } catch (Exception e) {

            throw new RuntimeException("Error al obtener los turnos del mes" + e.getMessage());
        }

    }
}
