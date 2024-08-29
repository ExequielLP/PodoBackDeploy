package Podogonnet.App.config.security;

import Podogonnet.App.config.security.filter.JwtAutheticateFilter;
import Podogonnet.App.enums.Rol;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.UUID;

import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private JwtAutheticateFilter jwtAutheticateFilter;

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Configuración de OAuth2
        http.oauth2Login(oauth2Login -> oauth2Login
                .successHandler(customOAuth2SuccessHandler));
        ; // Manejador de éxito personalizado

        // Configuración de JWT
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sessionMagConfig -> sessionMagConfig
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAutheticateFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authRequestConfig -> {
                    // Rutas privadas
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/user").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/adminController/crearServicio")
                            .hasRole(Rol.ADMIN.name());

                    // Rutas públicas
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/servicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/listaSerivicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/servicioPodo/{id}").permitAll();
                    authRequestConfig.requestMatchers("/oauth2/**").permitAll();
                    authRequestConfig.requestMatchers("/login/oauth2/**").permitAll();
                    ;
                    authRequestConfig.anyRequest().authenticated();// Permitir acceso a todos los endpoints de OAuth2
                });

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("https://podogonnet.netlify.app",
                "https://podofrontdeploy.onrender.com", "http://localhost:5173", "https://accounts.google.com/"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
                .privateKey(privateKey)
                .keyID(UUID.randomUUID().toString())
                .build();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    private static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            keyPair = keyPairGenerator.generateKeyPair();
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

}
