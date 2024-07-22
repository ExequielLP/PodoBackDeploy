package Podogonnet.App.controller;


import Podogonnet.App.dto.RegisterUser;
import Podogonnet.App.dto.SaveUser;
import Podogonnet.App.entity.ServicioPodo;
import Podogonnet.App.entity.Turno;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.PodoServicio;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.servis.auth.AutheticateService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class UsuarioControlador {
    @Autowired
    private PodoServicio podoServicio;

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private AutheticateService autheticateService;

    @GetMapping("/user")
    public ResponseEntity<Page<Usuario>> findAllUser(Pageable pegeable) {
        Page<Usuario> listaUsuarios = usuarioServicio.listaDeUser(pegeable);
        return ResponseEntity.ok(listaUsuarios);
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUser> register(@RequestBody SaveUser newUser) throws Exception {
        System.out.println("qqqqqqqqqqqqqqqqqqqqqqq");
        RegisterUser registerUser = autheticateService.registerOneCostumer(newUser);
        return ResponseEntity.status(HttpStatus.CREATED).body(registerUser);
        /* return ResponseEntity.ok(RegisterUser registerUser= autheticateService.regusterOneCustumer(newUser);); */
    }

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioPodo>> listaDeServicios() {
        List<ServicioPodo> listaDeServicios = podoServicio.findAll();
        return ResponseEntity.ok(listaDeServicios);
    }







}
