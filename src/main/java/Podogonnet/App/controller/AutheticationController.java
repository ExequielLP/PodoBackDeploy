package Podogonnet.App.controller;

import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.auth.AuthenticationResponse;
import Podogonnet.App.servis.auth.AutheticateGoogle;
import Podogonnet.App.servis.auth.AutheticateService;
import Podogonnet.App.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1/auth")
public class AutheticationController {
    @Autowired
    private AutheticateService autheticateService;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Autowired
    private AutheticateGoogle autheticateGoogle;

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> Authetication(@RequestBody AutheticationRequest authen,
            HttpServletResponse httpServletResponse) {
        AuthenticationResponse auth = autheticateService.login(authen,httpServletResponse);
        return ResponseEntity.ok(auth);
    }

    @GetMapping("validate")
    public boolean validate(HttpServletRequest httpServletRequest)  {
        boolean isValidate = autheticateService.validateToken(httpServletRequest);
        return isValidate;
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

            AuthenticationResponse authenticationResponse = autheticateGoogle.login(token);
            CookieUtil.createCookie(httpServletResponse, cookieName, authenticationResponse.getJwt(), "localhost",
                    false, 8000);
            return ResponseEntity.ok(authenticationResponse);
        } catch (Exception e) {
            System.out.println(
                    "----------------------------------------------------------------------ERRORRR------------------------");
            System.out.println(e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutAndRemoveCookie(HttpServletResponse httpServletResponse) {
        CookieUtil.clearCookie(httpServletResponse, cookieName);
        return ResponseEntity.ok("Logout successful and cookie removed");
    }

}
