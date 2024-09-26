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
        Authentication authentication = new UsernamePasswordAuthenticationToken(authen.getUserName(), authen.getPassword());


        authenticationManager.authenticate(authentication);

        UserDetails user = usuarioServicio.findOneByUsername(authen.getUserName());

        String jwt = jwtService.generateToken(user, generateExtraClaims((Usuario) user));

        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setJwt(jwt);
        authenticationResponse.setUserName(((Usuario) user).getNombre());
        authenticationResponse.setRol(((Usuario) user).getRol().toString());
        authenticationResponse.setId(((Usuario) user).getId());

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

    }

    public boolean validateToken(HttpServletRequest request) {

        try {
            Cookie cookie = WebUtils.getCookie(request, cookieName);
            System.out.println("el value de la coooikee esssssssssssssssssss");
            System.out.println(cookie.getValue());
            jwtService.extracUsername(cookie.getValue());
            return true;

        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println("validateTkoken(stringjwt---------------------------------------------------------------");
            return false;

        }


    }


    public Usuario findLogginInUser() {
        /*aca basicamente tengo que obtener el usuarui del securutyContextHolder*/
        UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        String user = (String) auth.getPrincipal();
        return usuarioServicio.findOneByUsername(user);

    }

    public AuthenticationResponse validateGetProfile(HttpServletRequest httpServletRequest) {
        try {
            Cookie cookie=WebUtils.getCookie(httpServletRequest,cookieName);
            String jwt=cookie.getValue();
            String username = jwtService.extracUsername(jwt);
            Usuario usuario = usuarioServicio.findOneByUsername(username);
            AuthenticationResponse authenticationResponse = new AuthenticationResponse();
            authenticationResponse.setId(usuario.getId());
            authenticationResponse.setUserName(usuario.getUsername());
            authenticationResponse.setRol(String.valueOf(usuario.getRol()));
            authenticationResponse.setJwt(jwt);
            return authenticationResponse;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

