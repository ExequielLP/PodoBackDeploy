package Podogonnet.App.config.security;

import Podogonnet.App.config.security.filter.JwtAutheticateFilter;
import Podogonnet.App.enums.Rol;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class HttpSecurityConfig {

    @Autowired
    private AuthenticationProvider daoAuthenticationProvider;

    @Autowired
    private JwtAutheticateFilter jwtAutheticateFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        SecurityFilterChain filterChain = http
                .cors(Customizer.withDefaults())
                .csrf(csrfConfig -> csrfConfig.disable())
                .sessionManagement(sessionMagConfigCon -> sessionMagConfigCon
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthenticationProvider)
                .addFilterAfter(jwtAutheticateFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(authRequestConfig -> {


                    /*por que asi anda*/
                    authRequestConfig.requestMatchers(HttpMethod.GET,"/api/v1/user").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.POST,"/adminController/crearServicio").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.GET,"/adminController/listaTurnoAdmin").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT,"/adminController/AltaBaja/{id}").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.GET,"/adminController/listaServiciosAdmin").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT,"/adminController/AltaBajaServicio/{id}").hasRole(Rol.ADMIN.name());
                    authRequestConfig.requestMatchers(HttpMethod.PUT,"/adminController/ModificarServicio").hasRole(Rol.ADMIN.name());



                                                            /* PERO ASI NO!  LPM */
                    /*Usuario linea 44 ahi tenes que configurar exe */
                    /*  authRequestConfig.requestMatchers(HttpMethod.GET,"/api/v1/user").hasRol(Rol.ADMIN.name());*/

                    /*EJEMPLO DE EXPRESION REGULAR
                    * authRequestConfig.requestMatchers(RegexRequestMatcher.regexMatcher(HttpMethod.GET,"/api/v1/user/[0-9]*"));
                    * */




                    /*EndPoint Publicos*/

                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/register").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.POST, "/api/v1/auth/authenticate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/auth/validate").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/api/v1/servicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/listaSerivicios").permitAll();
                    authRequestConfig.requestMatchers(HttpMethod.GET, "/portal/servicioPodo/{id}").permitAll();

                    // esta seria para admin,poner!
                    authRequestConfig
                            .requestMatchers(HttpMethod.POST, "/adminController/listaTurnos/{idTurno}/{idServicio}")
                            .permitAll();
                    authRequestConfig.anyRequest().authenticated();

                }).build();
        return filterChain;
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173/"));
        configuration.setAllowedMethods(Arrays.asList("*"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
