package Podogonnet.App.config.security.filter;

import Podogonnet.App.entity.Usuario;
import Podogonnet.App.servis.UsuarioServicio;
import Podogonnet.App.servis.auth.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.hibernate.ObjectNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@Component
public class JwtAutheticateFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService;

    @Autowired
    private UsuarioServicio usuarioServicio;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
/*agarro en header y busco las autorizaciones*/
        String authorizationHeaders=request.getHeader("Authorization");
        if (!StringUtils.hasText(authorizationHeaders) || !authorizationHeaders.startsWith("Bearer")){
            filterChain.doFilter(request,response);
            return;

        }

        /* agarro el jwt */

        String jwt=authorizationHeaders.split(" ")[1];

        /* extraer el subjet/username  usando el jwtService*/
        String userName=jwtService.extracUsername(jwt);

        /* setear objete autentificado dentro de ContextHolder*/
        Usuario user=usuarioServicio.findOneByUsername(userName);
        /* creo el token de authetificacion para el securyt context holder*/

        UsernamePasswordAuthenticationToken autheToken=new UsernamePasswordAuthenticationToken(userName,null,user.getAuthorities());

        /*agrego detalles para sumar info a la authentificaicon*/

        autheToken.setDetails(new WebAuthenticationDetails(request));

        /*seteo el secunrity context holder*/

        SecurityContextHolder.getContext().setAuthentication(autheToken);

        filterChain.doFilter(request,response);







    }
}
