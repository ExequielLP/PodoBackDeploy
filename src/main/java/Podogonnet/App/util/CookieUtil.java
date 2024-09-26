package Podogonnet.App.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {
    public static void createCookie( HttpServletResponse httpResponse, String name, String value, String domein, Integer MaxAge) {
//        String environment = System.getenv("ENTORNO");
//        boolean isProduction = "production".equalsIgnoreCase(environment);

//        Cookie cookie = new Cookie(name, value);
//        if (isProduction) {
//
//        }

        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/")
                .maxAge(MaxAge)  // Establece el tiempo de expiración en segundos
                .secure(true)    // Configura la cookie como segura
                .httpOnly(true)  // Solo accesible mediante HTTP (no JavaScript)
                .sameSite("None") // Define la política de SameSite
                .build();

        // Añade la cookie a la respuesta como un encabezado
        httpResponse.addHeader("Set-Cookie", cookie.toString());
//        cookie.setSecure(true);
//        cookie.setHttpOnly(true);
//        cookie.setDomain(domein);
//        cookie.setMaxAge(MaxAge);
//        cookie.setPath("/");
        System.out.println("en cookie util");
        System.out.println(cookie);
    }
    public static void clearCookie(HttpServletResponse httpServletResponse,String name){
        Cookie cookie=new Cookie(name,null);
        cookie.setPath("/");
        cookie.setDomain("");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(1);
        httpServletResponse.addCookie(cookie);
    }
}
