package Podogonnet.App.controller;

import Podogonnet.App.dto.TurnosUsuario;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.servis.ImagenServicio;
import Podogonnet.App.servis.PodoServicio;
import Podogonnet.App.servis.TurnoServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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

    @GetMapping("/listaTurnoAdmin")
    public ResponseEntity<Page<TurnosUsuario>>listaTurno(Pageable pageable) {
        try {
            Page<TurnosUsuario> page=turnoServicio.findAll(pageable);
            return ResponseEntity.ok(page);
        }catch (Exception e){
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

}