package Podogonnet.App.controller;

import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.auth.AuthenticationResponse;
import Podogonnet.App.servis.auth.AutheticateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AutheticationController {
    @Autowired
    private AutheticateService autheticateService;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> Authetication(@RequestBody AutheticationRequest authen) {
        AuthenticationResponse auth = autheticateService.login(authen);

        return ResponseEntity.ok(auth);
    }

    @GetMapping("validate")
    public boolean validate(@RequestParam String jwt) {

        boolean isValidate = autheticateService.validateToken(jwt);
        return isValidate;


    }

    @GetMapping("/validateGetProfile")
    public ResponseEntity<AuthenticationResponse> validateGetProfile(@RequestParam String jwt) {
        AuthenticationResponse isValidate = autheticateService.validateGetProfile(jwt);
        return ResponseEntity.ok(isValidate);


    }

    @GetMapping("profiles")
    public ResponseEntity<Usuario> MyProfils() {
        Usuario user = autheticateService.findLogginInUser();
        return ResponseEntity.ok(user);


    }

}
