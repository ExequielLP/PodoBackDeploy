/*
package Podogonnet.App.config.security;

import Podogonnet.App.entity.Usuario;


import Podogonnet.App.enums.Rol;
import Podogonnet.App.repository.UsuarioRepositorio;
import Podogonnet.App.servis.auth.JwtService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private OAuth2AuthorizedClientService authorizedClientService;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    UsuarioRepositorio usuarioRepositorio;


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
            String userInfoEndpointUri = "https://www.googleapis.com/oauth2/v3/userinfo";

            // Obtener los datos del usuario
            Map<String, Object> userInfo = getUserInfo(accessToken, userInfoEndpointUri);
            // Guardo el usuario si no esta registrado
            Optional usuario = usuarioRepositorio.findByEmail((String)userInfo.get("email"));
            if (usuario.isEmpty()) {
                Usuario usuarioDB = new Usuario();
                usuarioDB.setNombre((String) oauth2User.getAttributes().get("name"));
                usuarioDB.setUserName((String) oauth2User.getAttributes().get("name"));
                usuarioDB.setEmail((String) userInfo.get("email"));
                usuarioDB.setPassword(null);
                usuarioDB.setRol(Rol.USER);
                usuarioRepositorio.save(usuarioDB);
            }


            // Crear un UserDetails falso con la información del usuario
            UserDetails fakeUserDetails = User.withUsername(oauth2User.getName())
                    .password("")  // No se necesita la contraseña para OAuth2
                    .authorities(oauth2User.getAuthorities())
                    .build();

            // Aquí generas el JWT usando el UserDetails falso y el token de acceso como un reclamo adicional
            Map<String, Object> extraClaims = new HashMap<>();
            extraClaims.put("accessToken", accessToken);
            extraClaims.put("userName", oauth2User.getAttributes().get("given_name"));
            extraClaims.put("name", oauth2User.getAttributes().get("given_name"));
            System.out.println("------------------------------------------------");
            System.out.println(oauth2User.getAttributes().get("given_name"));
            extraClaims.put("email", userInfo.get("email"));
            extraClaims.put("role", "USER"); // Puedes cambiarlo según sea necesario
            extraClaims.put("authorities", oauth2User.getAuthorities());
            String jwt = jwtService.generateToken(fakeUserDetails, extraClaims);
            System.out.println("------------------------------------------------");
            System.out.println(jwt);

            // Guarda el JWT en la sesión o lo devuelve en la respuesta
            response.setHeader("Authorization", "Bearer " + jwt);
            String redirectUrl = "http://localhost:5173/?token=" + jwt;
            response.sendRedirect(redirectUrl);
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Authentication failed");
        }
    }

    private Map<String, Object> getUserInfo(String accessToken, String userInfoEndpointUri) throws IOException {
        URL url = new URL(userInfoEndpointUri);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + accessToken);

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }

                // Procesar la respuesta JSON para extraer los datos del usuario
                JsonNode jsonNode = objectMapper.readTree(response.toString());
                Map<String, Object> userInfo = new HashMap<>();
                userInfo.put("name", jsonNode.get("name").asText());
                userInfo.put("email", jsonNode.get("email").asText());
                return userInfo;
            }
        } else {
            throw new IOException("Failed to get user info from Google: HTTP " + responseCode);
        }
    }
}*/
