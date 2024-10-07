package Podogonnet.App.controller;

import Podogonnet.App.dto.EmailDto;
import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.EmailService;
import Podogonnet.App.servis.RecoverPasswordServicio;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.servis.auth.AuthenticationResponse;
import Podogonnet.App.servis.auth.AutheticateGoogle;
import Podogonnet.App.servis.auth.AutheticateService;
import Podogonnet.App.servis.auth.JwtService;
import Podogonnet.App.util.CookieUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AutheticationController {
    @Autowired
    private AutheticateService autheticateService;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Autowired
    private AutheticateGoogle autheticateGoogle;

    @Autowired
    private EmailService emailService;

    @Autowired
    private RecoverPasswordServicio recoverPasswordServicio;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> Authetication(@RequestBody AutheticationRequest authen,
                                                                HttpServletResponse httpServletResponse) {
        AuthenticationResponse auth = autheticateService.login(authen, httpServletResponse);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("validate")
    public ResponseEntity<Boolean> validate(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        boolean isValidate = autheticateService.validateToken(httpServletRequest);
        return ResponseEntity.ok(isValidate);
    }

    @GetMapping("/validateGetProfile")
    public ResponseEntity<AuthenticationResponse> validateGetProfile(HttpServletRequest httpServletRequest) {
        AuthenticationResponse userIsValid = autheticateService.validateGetProfile(httpServletRequest);
        return ResponseEntity.ok(userIsValid);

    }

    @GetMapping("profiles")
    public ResponseEntity<Usuario> MyProfils() {
        Usuario user = autheticateService.findLogginInUser();
        return ResponseEntity.ok(user);

    }

    @PostMapping("/google")
    public ResponseEntity<?> googleLogin(@RequestBody Map<String, String> body, HttpServletResponse httpServletResponse)
            throws GeneralSecurityException, IOException {

        String token = body.get("token");
        try {

            AuthenticationResponse authenticationResponse = autheticateGoogle.login(token, httpServletResponse);

            // CookieUtil.createCookie(httpServletResponse, cookieName,
            // authenticationResponse.getJwt(), "localhost",
            // 8000);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            System.out.println(
                    "----------------------------ERRORRR------------------------------------");
            System.out.println(e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutAndRemoveCookie(HttpServletResponse httpServletResponse) {
        CookieUtil.clearCookie(httpServletResponse, cookieName);
        return ResponseEntity.ok("Logout successful and cookie removed");
    }

    @PostMapping("/send-email")
    public ResponseEntity<String> forgetPassword(@RequestParam String email) throws MessagingException {
        emailService.sendMail(email);
        return ResponseEntity.ok("ok");
    }

    @GetMapping("/is-token-valid")
    public ResponseEntity<AuthenticationResponse> validateTokenAccess(@RequestParam String jwt, HttpServletResponse httpServletResponse) {
        try {
            AuthenticationResponse authenticationResponse = recoverPasswordServicio.recoverPassword(jwt, httpServletResponse);

            return ResponseEntity.ok(authenticationResponse);

        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PutMapping("/recovery-password")
    public ResponseEntity<?> resetPassword(@RequestBody AutheticationRequest autheticationRequest,HttpServletResponse httpServletResponse) throws Throwable {
        try {
            System.out.println(autheticationRequest);
            recoverPasswordServicio.resetPassword(autheticationRequest,httpServletResponse);
            return ResponseEntity.ok("Contraseña correcta");
        } catch (Exception e) {
            e.getMessage();
            return ResponseEntity.badRequest().build();
        }
    }

}
