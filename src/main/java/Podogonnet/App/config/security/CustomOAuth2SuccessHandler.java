package Podogonnet.App.config.security;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.servis.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioRepositorio usuarioRepositorio;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        // Obtén los detalles del usuario autenticado desde OAuth2
        OAuth2AuthenticationToken oauth2Token = (OAuth2AuthenticationToken) authentication;
        System.out.println("--------------------------------------------------------");
        // Obtén el email del usuario desde los atributos proporcionados por Google
        String email = oauth2Token.getPrincipal().getAttribute("email");

        // Obtén otros atributos si es necesario
        String name = oauth2Token.getPrincipal().getAttribute("name");


        // Crear un mapa o una clase para incluir en el JWT (opcional, si necesitas más información en el token)
        Map<String, Object> claims = new HashMap<>();
        claims.put("email", email);

        // Buscar usuario por email
        Optional<Usuario> usuarioOpt = usuarioRepositorio.findByUserName(name);

        Usuario usuario;
        if (usuarioOpt.isPresent()) {
            usuario = usuarioOpt.get();
        } else {
            // Si el usuario no existe, crear uno nuevo con los datos proporcionados por Google
            usuario = new Usuario();
            usuario.setEmail(email);
            usuario.setNombre(name);
            usuario.setRol(Rol.USER);
            usuario.setPassword(null);
            // Guardar el nuevo usuario en la base de datos
            usuarioRepositorio.save(usuario);
        }

        // Generar el JWT utilizando el servicio JWT y los detalles obtenidos de Google
        String jwt = jwtService.generateToken(usuario, claims);
        System.out.println("HOlaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        System.out.println(usuario);
        // Configurar la respuesta con el JWT
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + jwt + "\"}");
        response.getWriter().flush();
    }

}
