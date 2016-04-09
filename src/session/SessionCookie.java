package session;

import javax.servlet.http.Cookie;

/**
 * Created by yhf on 3/17/16.
 */

/**
 * Inheritance from Cookie that holds more customized fields.
 */
public class SessionCookie extends Cookie {

    private static final String cookieName = "CS5300PROJ1SESSION";

    public SessionCookie(String sessionId, String versionNumber, String locationMetadata) {
        super(cookieName, sessionId + "__" + versionNumber + "__" + locationMetadata);
    }

    public static String getCookieName() {
        return cookieName;
    }

    public static String getSessionId(String cookieValue) {
        return cookieValue.split("__")[0];
    }

    public static String getVersionNumber(String cookieValue) {
        return cookieValue.split("__")[1];
    }

    public static String getLocationMetadata(String cookieValue) {
        return cookieValue.split("__")[2];
    }
}
