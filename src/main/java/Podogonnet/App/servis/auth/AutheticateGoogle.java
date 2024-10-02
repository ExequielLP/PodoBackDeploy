package Podogonnet.App.servis.auth;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;
import Podogonnet.App.util.CookieUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import jakarta.servlet.http.HttpServletResponse;
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


    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    public AuthenticationResponse login(String token, HttpServletResponse httpServletResponse) throws GeneralSecurityException, IOException {
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(CLIENT_ID))
                .build();

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            System.out.println("------------*-*-*-*-------------");
            System.out.println(payload.getEmail());
            Optional usuario = usuarioRepositorio.findByEmail((String)payload.get("email"));


            Usuario usuarioDB;
            if (usuario.isEmpty()) {
                 usuarioDB = new Usuario();
                usuarioDB.setNombre((String) payload.get("given_name"));
                usuarioDB.setUserName((String) payload.get("given_name"));
                usuarioDB.setEmail((String)payload.get("email"));
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
            UserDetails fakeUserDetails = User.withUsername(((String) payload.get("email")))
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

            String environment = System.getenv("ENTORNO");
            String domein="";
            if ("localDBlocal".equalsIgnoreCase(environment)){
                domein="localhost";
                System.out.println(domein);
            }else {domein="podobackdeploy.onrender.com";
                System.out.println(domein);
            }

            CookieUtil.createCookie(httpServletResponse, cookieName, jwt, domein, 8000);


            return authenticationResponse;

        } else {
            return null;
        }

    }
}
