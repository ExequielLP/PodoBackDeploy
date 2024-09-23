package Podogonnet.App.servis.auth;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.*;


@Service
public class AutheticateGoogle {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
  private String CLIENT_ID;


    @Autowired
    private UsuarioRepositorio usuarioRepositorio;
    @Autowired
    private JwtService jwtService;

    @Autowired
    private AuthenticationManager authenticationManager;

    public AuthenticationResponse login(String token) throws GeneralSecurityException, IOException {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            Optional usuario = usuarioRepositorio.findByEmail((String)payload.get("email"));


            Usuario usuarioDB;
            if (usuario.isEmpty()) {
                 usuarioDB = new Usuario();
                usuarioDB.setNombre((String) payload.get("given_name"));
                usuarioDB.setUserName((String) payload.get("given_name"));
                usuarioDB.setEmail((String)payload.getEmail());
                usuarioDB.setPassword(null);
                usuarioDB.setRol(Rol.USER);
                                usuarioRepositorio.save(usuarioDB);
                authenticationResponse.setId(usuarioDB.getId());
                authenticationResponse.setRol(String.valueOf(Rol.USER));
                authenticationResponse.setUserName(usuarioDB.getUsername());

            }else{
                usuarioDB = (Usuario) usuario.get();

            }

           ;
            // Crear un UserDetails falso con la información del usuario
            UserDetails fakeUserDetails = User.withUsername(((String) payload.get("given_name")))
                    .password("")  // No se necesita la contraseña para OAuth2
                    .authorities(usuarioDB.getAuthorities())
                    .build();


            Authentication authentication = new UsernamePasswordAuthenticationToken(fakeUserDetails, null, fakeUserDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Aquí generas el JWT usando el UserDetails falso y el token de acceso como un reclamo adicional
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("accessToken", token);
            extraClaims.put("userName", payload.get("given_name"));
            extraClaims.put("name", payload.get("given_name"));
            extraClaims.put("email", payload.get("email"));
            extraClaims.put("role", "USER");
// completar authenticationResponse
            authenticationResponse.setId(usuarioDB.getId());
            authenticationResponse.setUserName(usuarioDB.getUsername());
            authenticationResponse.setRol(String.valueOf(usuarioDB.getRol()));
            String jwt = jwtService.generateToken(fakeUserDetails, extraClaims);
            authenticationResponse.setJwt(jwt);

            return authenticationResponse;

        } else {
            return null;
        }

    }
}
