package session;

import javax.servlet.http.Cookie;
import java.util.UUID;

/**
 * Created by yhf on 3/17/16.
 */

public class Utils {

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }

    /**
     * Find the cookie with given cookieName, and returns the cookieValue.
     */
    public static String findCookie(String cookieName, Cookie[] cookies) {
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SessionCookie.getCookieName())) {
                String cookieValue = cookie.getValue();
                return cookieValue;
            }
        }
        return "";
    }
}
