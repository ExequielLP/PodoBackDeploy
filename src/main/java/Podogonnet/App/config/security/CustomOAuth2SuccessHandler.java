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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.client.OAuth2AuthorizationSuccessHandler;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2UserAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    public CustomOAuth2SuccessHandler(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            OAuth2User oauth2User = authToken.getPrincipal();

            // Obtén el cliente autorizado (contiene el access token)
            OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                    authToken.getAuthorizedClientRegistrationId(),
                    oauth2User.getName()
            );

            String accessToken = authorizedClient.getAccessToken().getTokenValue();

            // Crear un UserDetails falso con el nombre del usuario (puedes adaptar según sea necesario)
            UserDetails fakeUserDetails = User.withUsername(oauth2User.getName())
                    .password("")  // No se necesita la contraseña para OAuth2
                    .authorities(oauth2User.getAuthorities())
                    .build();

            // Aquí generas el JWT usando el UserDetails falso y el token de acceso como un reclamo adicional
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("accessToken", accessToken);

            String jwt = jwtService.generateToken(fakeUserDetails, extraClaims);
            System.out.println(jwt);
            System.out.println("---------------------------------------");

            // Guarda el JWT en la sesión o lo devuelve en la respuesta
            response.setHeader("Authorization", "Bearer " + jwt);

            String redirectUrl = "http://localhost:5173/login?token=" + jwt;
            response.sendRedirect(redirectUrl);
        } else {
            // Maneja el caso en que el token no es de tipo OAuth2AuthenticationToken
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }

    }
}