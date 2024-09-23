package Podogonnet.App.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    public static void createCookie( HttpServletResponse httpResponse, String name, String value, String domein, Integer MaxAge) {
//        String environment = System.getenv("ENTORNO");
//        boolean isProduction = "production".equalsIgnoreCase(environment);

        Cookie cookie = new Cookie(name, value);
//        if (isProduction) {
//
//        }
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setDomain(domein);
        cookie.setMaxAge(MaxAge);
        cookie.setPath("/");
        httpResponse.addCookie(cookie);
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
