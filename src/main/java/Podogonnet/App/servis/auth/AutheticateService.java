package Podogonnet.App.servis.auth;

import Podogonnet.App.dto.RegisterUser;
import Podogonnet.App.dto.SaveUser;
import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.util.CookieUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.util.WebUtils;

import java.util.HashMap;
import java.util.IllegalFormatCodePointException;
import java.util.Map;

@Service
public class AutheticateService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Autowired
    private UsuarioServicio usuarioServicio;
    @Autowired
    private JwtService jwtService;

    public RegisterUser registerOneCostumer(SaveUser newUser) throws Exception {

        Usuario user = usuarioServicio.registerOneCostumer(newUser);
        RegisterUser userDto = new RegisterUser();
        userDto.setName(user.getNombre());
        userDto.setUserName(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRol().name());
        userDto.setId(user.getId());

        String jwt = jwtService.generateToken(user, generateExtraClaims(user));
        userDto.setJwt(jwt);

        return userDto;
    }

    private Map<String, Object> generateExtraClaims(Usuario user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("name", user.getNombre());
        extraClaims.put("role", user.getRol().name());
        extraClaims.put("authorizaties", user.getAuthorities());
        extraClaims.put("userName", user.getUsername());
        return extraClaims;

    }

    public AuthenticationResponse login(AutheticationRequest authen,HttpServletResponse httpServletResponse) {
        Authentication authentication = new UsernamePasswordAuthenticationToken(authen.getEmail(), authen.getPassword());


        authenticationManager.authenticate(authentication);

        UserDetails user = usuarioServicio.findOneByEmail(authen.getEmail());

        String jwt = jwtService.generateToken(user, generateExtraClaims((Usuario) user));

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUserName(((Usuario) user).getNombre());
        authenticationResponse.setRol(((Usuario) user).getRol().toString());
        authenticationResponse.setId(((Usuario) user).getId());
        authenticationResponse.setEmail(((Usuario) user).getEmail());

        String environment = System.getenv("ENTORNO");
        String domein="";
        if ("localDBlocal".equalsIgnoreCase(environment)){
            domein="localhost";
            System.out.println(domein);
        }else {domein="podobackdeploy.onrender.com";
            System.out.println("------------------+++++++--------------++++++++++---------------+++++++++");
            System.out.println(domein);
        }

        CookieUtil.createCookie(httpServletResponse, cookieName, jwt, domein, 8000);



        return authenticationResponse;

    }

    public boolean validateToken(HttpServletRequest request) {

        try {
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            jwtService.extracEmail(cookie.getValue());

            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("validateTkoken()----> cookies del usuario no validad");
            return false;

        }


    }


    public Usuario findLogginInUser() {
        /*aca basicamente tengo que obtener el usuarui del securutyContextHolder*/
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String user = (String) auth.getPrincipal();
        return usuarioServicio.findOneByEmail(user);

    }

    public AuthenticationResponse validateGetProfile(HttpServletRequest httpServletRequest) {
        try {
            Cookie cookie=WebUtils.getCookie(httpServletRequest,cookieName);
            String jwt=cookie.getValue();
            String username = jwtService.extracEmail(jwt);
            Usuario usuario = usuarioServicio.findByEmail(username);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setId(usuario.getId());
            authenticationResponse.setUserName(usuario.getNombre());
            authenticationResponse.setRol(String.valueOf(usuario.getRol()));
            authenticationResponse.setEmail(usuario.getEmail());
            authenticationResponse.setJwt(jwt);
            return authenticationResponse;


        } catch (Throwable e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

