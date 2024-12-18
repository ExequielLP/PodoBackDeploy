package Podogonnet.App.controller;

import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.servis.DiaServicio;
import Podogonnet.App.servis.TurnoServicio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/Turnos")
public class TurnoControlador {

    @Autowired
    private TurnoServicio turnoServicio;
    @Autowired
    private DiaServicio diaServicio;

    @PostConstruct
    public String generarTurnos() {
        LocalDate inicio = LocalDate.now();
        LocalDate fin = inicio.plus(1, ChronoUnit.YEARS);
        turnoServicio.generarTurnos(inicio, fin);
        return "Turnos generados exitosamente";
    }

    @GetMapping("turnoDelDia/{date}")
    public ResponseEntity<List<TurnosUsuario>> listaDeTurnos(@PathVariable String date) throws Exception {
        try {
            LocalDate localDate = LocalDate.parse(date);
            List<TurnosUsuario> dia = diaServicio.turnosDelDia(localDate);
            return ResponseEntity.ok(dia);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.emptyList()); // Manejo genérico de excepciones
        }
    }

    @PostMapping("/reservarTurno/{turnoId}/{servicioId}/{usuarioid}")
    public ResponseEntity<Turno> bookAppointment(@PathVariable String turnoId, @PathVariable String servicioId, @PathVariable String usuarioid) {
        System.out.println("ahi viene");
        System.out.println(usuarioid);
        return ResponseEntity.ok(turnoServicio.reservarTurno(turnoId, servicioId, usuarioid));
    }

    @GetMapping("/listaTurnos/{id}")
    public ResponseEntity<List<TurnosUsuario>> listaTurno(@PathVariable String id) {

        return ResponseEntity.ok(turnoServicio.listaDeTurnosId(id));

    }

    @GetMapping("/cancelarTurno/{id}")
    public ResponseEntity<Turno> cancelarTurno(@PathVariable String id) {

        return ResponseEntity.ok(turnoServicio.cancelarTurno(id));
    }
}
