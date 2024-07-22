package Podogonnet.App.controller;

import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.PodoServicio;
import Podogonnet.App.servis.UsuarioServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalControlador {

    @Autowired
    private PodoServicio podoServicio;

    @GetMapping("/listaSerivicios")
    public ResponseEntity<List<ServicioPodo>>listaServicios(){
        List<ServicioPodo>listaServicios=podoServicio.listaServicios();
        return ResponseEntity.ok(listaServicios);
    }
    @GetMapping("/servicioPodo/{id}")
    public ResponseEntity<ServicioPodo>findByOne(@PathVariable("id") String id){

        return ResponseEntity.ok(podoServicio.findById(id));

    }
}
