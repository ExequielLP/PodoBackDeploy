package Podogonnet.App.controller;

import Podogonnet.App.entity.Turno;
import Podogonnet.App.servis.TurnoServicio;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/Turnos")
public class TurnoControlador {

    @Autowired
    private TurnoServicio turnoServicio;

    //creo turnos para 1 semana
    @PostConstruct
    public void createDailyAppointmentsAtStartup() {
        turnoServicio.createDailyAppointmentsForAWeek();
    }

    @GetMapping("turnoDelDia/{date}")
    public ResponseEntity<List<Turno>> listaDeTurnos(@PathVariable String date) {
        LocalDate localDate = LocalDate.parse(date);
        List<Turno> listaTurdenosDelDia = turnoServicio.turnosDelDia(localDate);
        return ResponseEntity.ok(listaTurdenosDelDia);
    }

    @PostMapping("/reservarTurno/{turnoId}/{servicioId}/{usuarioid}")
    public ResponseEntity<Turno> bookAppointment(@PathVariable String turnoId, @PathVariable String servicioId ,@PathVariable String usuarioid ) {
        System.out.println("ahi viene");
        System.out.println(usuarioid);
        return ResponseEntity.ok(turnoServicio.reservarTurno(turnoId,servicioId,usuarioid));
    }

    @GetMapping("/listaTurnos/{id}")
    public ResponseEntity<List<Turno>>listaTurno(@PathVariable String id) {

        return ResponseEntity.ok(turnoServicio.listaDeTurnosId(id));


    }
    @GetMapping("/cancelarTurno/{id}")
    public ResponseEntity<Turno> cancelarTurno(@PathVariable String id){

        return ResponseEntity.ok( turnoServicio.cancelarTurno(id));
    }
}
