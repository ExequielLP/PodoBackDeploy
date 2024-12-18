package Podogonnet.App.controller;

import Podogonnet.App.dto.TurnoDto;
import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.Feriado;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.servis.DiaServicio;
import Podogonnet.App.servis.ImagenServicio;
import Podogonnet.App.servis.PodoServicio;
import Podogonnet.App.servis.TurnoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/adminController")
public class AdminController {
    @Autowired
    private PodoServicio podoServicio;
    @Autowired
    private ImagenServicio imagenServicio;

    @Autowired
    private TurnoServicio turnoServicio;

    @Autowired
    private DiaServicio diaServicio;

    @PostMapping("/crearServicio")
    public ResponseEntity<ServicioPodo> crearServicioPodo(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("costo") Double costo,
            @RequestParam("file") MultipartFile file) {

        try {
            return ResponseEntity.ok(podoServicio.crearServicioPodo(nombre, descripcion, costo, file));
        } catch (Exception exception) {
            exception.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);

        }

    }

    // Chekear pq no cambian los paramentros
    @GetMapping("/listaTurnoAdmin")
    public ResponseEntity<Page<TurnosUsuario>> listaTurno(@RequestParam(value = "page", defaultValue = "0") int page,
                                                          @RequestParam(value = "size", defaultValue = "10") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            // Log para verificar los valores de los parámetros
            System.out.println("-------------------------");
            System.out.println("Page: " + page + ", Size: " + size);
            System.out.println("-------------------------");

            Page<TurnosUsuario> turnos = turnoServicio.getTurnosAdmin(pageable);
            return ResponseEntity.ok(turnos);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PutMapping("/AltaBaja/{id}")
    public void editarTurno(@PathVariable String id) {
        try {
            turnoServicio.AltaBaja(id);
        } catch (Exception e) {
            e.getStackTrace();
        }

    }

    @GetMapping("/listaServiciosAdmin")
    public ResponseEntity<List<ServicioPodo>> ListaServicios() {
        try {
            return ResponseEntity.ok(podoServicio.findAll());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PutMapping("AltaBajaServicio/{id}")
    public void AltaBajaServicio(@PathVariable String id) {
        podoServicio.AltaBaja(id);
    }

    @PutMapping("/ModificarServicio")
    public void modificarServicio(@RequestParam("id") String id,
                                  @RequestParam("nombre") String nombre,
                                  @RequestParam("descripcion") String descripcion,
                                  @RequestParam("costo") Double costo,
                                  @RequestParam("file") MultipartFile file) throws IOException {
        podoServicio.modificarServicio(id, nombre, descripcion, costo, file);
    }

    @PutMapping("/suspendeTurnoAdmin/{turnoId}")
    public void suspenderTurnoAdmin(@PathVariable String turnoId) {
        try {
            turnoServicio.suspenderTurno(turnoId);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    @PostMapping("/agregarFeriadoAdmin")
    public void feriadoDate(@RequestBody Feriado feriado) {
        try {
            System.out.println("entro");
            diaServicio.agregarFeriado(feriado);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
// usar este enpot que ya esta en TurnosControlador
//
//    @GetMapping("turnoDelDia/{date}")
//    public ResponseEntity<List<TurnosUsuario>> listaDeTurnos(@PathVariable String date) throws Exception {
//        LocalDate localDate = LocalDate.parse(date);
//        List<TurnosUsuario> dia = diaServicio.turnosDelDia(localDate);
//        return ResponseEntity.ok(dia);
//    }

    @GetMapping("/listaTurnoDelMesAdmin/{date}")
    public ResponseEntity<List<TurnoDto>> turnosDelMesAdmin(@PathVariable String date) {

        try {

            LocalDate localDate = LocalDate.parse(date);

            List<TurnoDto> listaTurno = turnoServicio.turnosDelMes(localDate);

            return ResponseEntity.ok(listaTurno);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/filtrarTurnoPor")
    public ResponseEntity<List<TurnoDto>> filtrarTurnoPor(@RequestParam(required = false) String nombre, @RequestParam(required = false) String servicio, @RequestParam(required = false) String fecha) {

        try {
//            if ( nombre!=null) {
//                System.out.println("nombreeeeeeeeee");
//                List<TurnoDto> turnoDtos = turnoServicio.filtrarTurnoPorNombre(nombre);
//                return ResponseEntity.ok(turnoDtos);
//
//            } else if (servicio!=null) {
//                System.out.println("seriviciooooooooooo");
//                List<TurnoDto> turnoDtos = turnoServicio.filtrarTurnoPorServicio(servicio);
//                return ResponseEntity.ok(turnoDtos);
//
//            } else if (date!=null){
//                System.out.println("holi");
//                List<TurnoDto> turnoDtos = turnoServicio.filtrarTurnoPorDate(date);
//                return ResponseEntity.ok(turnoDtos);

            List<TurnoDto> turnoDtos = turnoServicio.filtrarMultifiltro(nombre, servicio, fecha);
            return ResponseEntity.ok(turnoDtos);


        } catch (Exception e) {
            throw new RuntimeException("Problemas al filtrar turno" + e.getMessage());
        }

    }

}