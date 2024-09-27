package Podogonnet.App.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CookieUtil {
    public static void createCookie( HttpServletResponse httpResponse, String name, String value, String domein, Integer MaxAge) {
//        ResponseCookie cookie = ResponseCookie.from(name, value)
//                .domain(domein)
//                .path("/")
//                .maxAge(MaxAge)  // Establece el tiempo de expiración en segundos
//                .secure(true)    // Configura la cookie como segura
//                .httpOnly(true)  // Solo accesible mediante HTTP (no JavaScript)
//                .sameSite("None") // Define la política de SameSite
//                .build();
//        httpResponse.addHeader("Set-Cookie", cookie.toString());
        // Añade la cookie a la respuesta como un encabezado
        Cookie cookie = new Cookie(name, value);
       cookie.setSecure(true);
       cookie.setHttpOnly(true);
       cookie.setDomain(domein);
       cookie.setMaxAge(MaxAge);
       cookie.setPath("/");
       httpResponse.addCookie(cookie);
    }
    public static void clearCookie(HttpServletResponse httpServletResponse,String name){
        Cookie cookie=new Cookie(name,null);
        cookie.setPath("/");
        cookie.setDomain("podobackdeploy.onrender.com");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(1);
        httpServletResponse.addCookie(cookie);
    }
}
