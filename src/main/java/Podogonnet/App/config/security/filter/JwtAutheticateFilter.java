package Podogonnet.App.config.security.filter;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.servis.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

import java.io.IOException;


@Component
public class JwtAutheticateFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Value("${jwt.accessTokenCookieName}")
    private String cookieName;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        /*agarro en header y busco las autorizaciones*/


        String jwt = getToken(request);
        if (jwt == null || jwt.isEmpty()) {
            filterChain.doFilter(request, response);
            return;
        }
        try {
            /* extraer el subjet/username  usando el jwtService*/
            String userName = jwtService.extracEmail(jwt);

            /* setear objete autentificado dentro de ContextHolder*/
            Usuario user = null;

            user = usuarioServicio.findByEmail(userName);

            /* creo el token de authetificacion para el securyt context holder*/

            UsernamePasswordAuthenticationToken autheToken = new UsernamePasswordAuthenticationToken(userName, null, user.getAuthorities());

            /*agrego detalles para sumar info a la authentificaicon*/

            autheToken.setDetails(new WebAuthenticationDetails(request));

            /*seteo el secunrity context holder*/

            SecurityContextHolder.getContext().setAuthentication(autheToken);

            filterChain.doFilter(request, response);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }

    }

    public String getToken(HttpServletRequest httpServletRequest) {
        Cookie cookie = WebUtils.getCookie(httpServletRequest, cookieName);
        return cookie != null ? cookie.getValue() : null;
    }
}
