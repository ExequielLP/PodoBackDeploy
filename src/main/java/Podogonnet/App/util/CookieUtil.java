package Podogonnet.App.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {
    public static void createCookie( HttpServletResponse httpResponse, String name, String value, String domein, Integer MaxAge) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .domain(domein)
                .path("/")
                .maxAge(MaxAge)  // Establece el tiempo de expiración en segundos
                .secure(true)    // Configura la cookie como segura
                .httpOnly(true)  // Solo accesible mediante HTTP (no JavaScript)
                .sameSite("None") // Define la política de SameSite
                .build();
        httpResponse.addHeader("Set-Cookie", cookie.toString());

//        Cookie cookie = new Cookie(name, value);
//       cookie.setSecure(true);
//       cookie.setHttpOnly(true);
//       cookie.setDomain(domein);
//       cookie.setMaxAge(MaxAge);
//       cookie.setPath("/");
//       httpResponse.addCookie(cookie);
    }
    public static void clearCookie(HttpServletResponse httpServletResponse, String name) {
        String environment = System.getenv("ENTORNO");
        String domain;

        // Configura el dominio según el entorno
        if ("localDBlocal".equalsIgnoreCase(environment)) {
            domain = "localhost";
        } else {
            domain = ".onrender.com"; // Incluye el punto para que sea válido para subdominios
        }

        // Construcción de la cookie para eliminarla
        ResponseCookie cookie = ResponseCookie.from(name, null) // Valor nulo para eliminarla
                .domain(domain)        // Dominio de la cookie
                .path("/")             // Válido para todas las rutas
                .maxAge(0)             // Expira inmediatamente
                .secure(true)          // Solo se envía en conexiones HTTPS
                .httpOnly(true)        // No accesible desde JavaScript
                .sameSite("None")      // Permite solicitudes entre dominios
                .build();

        // Agrega la cookie al encabezado de la respuesta
        httpServletResponse.addHeader("Set-Cookie", cookie.toString());
    }

}
