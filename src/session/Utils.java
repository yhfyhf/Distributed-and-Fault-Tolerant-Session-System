package session;

import java.util.UUID;

/**
 * Created by yhf on 3/17/16.
 */

public class Utils {

    public static String generateSessionId() {
        return UUID.randomUUID().toString();
    }
}
