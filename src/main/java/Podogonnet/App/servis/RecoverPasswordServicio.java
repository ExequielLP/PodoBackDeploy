package Podogonnet.App.servis;

import Podogonnet.App.dto.auth.AutheticationRequest;
import Podogonnet.App.entity.Usuario;
import Podogonnet.App.repository.UsuarioRepositorio;
import Podogonnet.App.servis.auth.AuthenticationResponse;
import Podogonnet.App.servis.auth.JwtService;
import Podogonnet.App.util.CookieUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RecoverPasswordServicio {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UsuarioServicio usuarioServicio;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    public AuthenticationResponse recoverPassword(String jwt, HttpServletResponse httpServletResponse) throws Throwable {


        String email = jwtService.extracEmail(jwt);
        Usuario usuario = usuarioServicio.findByEmail(email);
        AuthenticationResponse authenticationResponse = new AuthenticationResponse();
        authenticationResponse.setEmail(usuario.getEmail());
        String environment = System.getenv("ENTORNO");
        String domein = "";
        if ("localDBlocal".equalsIgnoreCase(environment)) {
            domein = "localhost";
            System.out.println(domein);
        } else {
            domein = "podobackdeploy.onrender.com";
            System.out.println(domein);
        }

        CookieUtil.createCookie(httpServletResponse, cookieName, jwt, domein, 8000);
        return authenticationResponse;
    }

    public Usuario resetPassword(AutheticationRequest autheticationRequest,HttpServletResponse httpServletResponse) throws Throwable {
       try {
           Usuario usuario=usuarioServicio.findByEmail(autheticationRequest.getEmail());
           usuario.setPassword(autheticationRequest.getPassword());
           usuarioRepositorio.save(usuario);
            CookieUtil.clearCookie(httpServletResponse,cookieName);
           return usuario;
       }catch (Exception e){
           throw new RuntimeException("PROBLEMAS AL CAMBIO CONTRASEÑA DE USUARIO");
       }
    }
}
