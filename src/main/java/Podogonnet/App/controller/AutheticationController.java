package Podogonnet.App.controller;

import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.auth.AuthenticationResponse;
import Podogonnet.App.servis.auth.AutheticateGoogle;
import Podogonnet.App.servis.auth.AutheticateService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import net.minidev.json.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AutheticationController {
    @Autowired
    private AutheticateService autheticateService;

    @Autowired
    private AutheticateGoogle autheticateGoogle;

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

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body) throws GeneralSecurityException, IOException {
        String token = body.get("token");
        try {
            System.out.println("----------------------------------------------------------------------------------------------");
       AuthenticationResponse authenticationResponse=autheticateGoogle.login(token);
            System.out.println("----------------------------------------------------------------------------------------------");
            System.out.println(authenticationResponse);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e){
            System.out.println("----------------------------------------------------------------------ERRORRR------------------------");
            System.out.println(e.getMessage());


            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }
}
