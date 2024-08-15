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

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.Arrays;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private JwtAutheticateFilter jwtAutheticateFilter;

    @Configuration
    @EnableWebSecurity
    public class SecurityConfig {

        @Autowired
        private AuthenticationProvider daoAuthenticationProvider;

        @Autowired
        private JwtAutheticateFilter jwtAutheticateFilter;

        @Bean
        @Order(1)
        public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) throws Exception {
            OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
            http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                    .oidc(Customizer.withDefaults());

            http
                    .exceptionHandling((exceptionConfig) -> {
                        exceptionConfig.authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login"));
                    });
            http
                    .oauth2ResourceServer(oauthResourceConfig -> {
                        oauthResourceConfig.jwt(Customizer.withDefaults());
                    });


            return http.build();
        }

        @Bean
        @Order(2)
        public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {

            http.authorizeHttpRequests(authConfig -> {

                        authConfig.requestMatchers("/login").permitAll();

                        authConfig.anyRequest().authenticated();
                    })
//                    .oauth2Login(Customizer.withDefaults())
                    .formLogin(Customizer.withDefaults());



            return http.build();
        }


        @Bean
        @Order(3)
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .cors(Customizer.withDefaults())
                    .csrf(csrfConfig -> csrfConfig.disable())
                    .sessionManagement(sessionMagConfigCon -> sessionMagConfigCon
                            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .authenticationProvider(daoAuthenticationProvider)
                    .addFilterAfter(jwtAutheticateFilter, UsernamePasswordAuthenticationFilter.class)
                    .authorizeHttpRequests(authRequestConfig -> {
                                // Configuraciones de autorización para diferentes roles y endpoints
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/user").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.POST, "/adminController/crearServicio").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/listaTurnoAdmin").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/AltaBaja/{id}").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/adminController/listaServiciosAdmin").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/AltaBajaServicio/{id}").hasRole(Rol.ADMIN.name());
                                authRequestConfig.requestMatchers(HttpMethod.PUT, "/adminController/ModificarServicio").hasRole(Rol.ADMIN.name());

                                // Endpoints públicos
                                authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll();
                                authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll();
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll();
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/servicios").permitAll();
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/listaSerivicios").permitAll();
                                authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/servicioPodo/{id}").permitAll();

                                // Endpoint público para admin
                                authRequestConfig
                                        .requestMatchers(HttpMethod.POST, "/adminController/listaTurnos/{idTurno}/{idServicio}")
                                        .permitAll();


                            }
                    );


            return http.build();
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

        @Bean
        public AuthorizationServerSettings authorizationServerSettings() {
            return AuthorizationServerSettings.builder()
                    .issuer("http://localhost:8080").build();
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(Arrays.asList("https://podogonnet.netlify.app", "https://podofrontdeploy.onrender.com", "http://localhost:5173/"));
            configuration.setAllowedMethods(Arrays.asList("*"));
            configuration.setAllowedHeaders(Arrays.asList("*"));
            configuration.setAllowCredentials(true);
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

    }
}
