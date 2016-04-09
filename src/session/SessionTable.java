package session;

import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by yhf on 3/17/16.
 */

/**
 * Session table that stores all sessions.
 */
public class SessionTable extends ConcurrentHashMap<String, SessionData> {

    /**
     * Returns the session specified by sessionId. If the session exists, returns that session;
     * else, returns a new session.
     */
    public SessionData getOrDefault(String sessionId, SessionData defaultSessionData) {
        if (containsKey(sessionId)) {
            SessionData sessionData = get(sessionId);
            sessionData.update();
            return sessionData;
        } else {
            return defaultSessionData;
        }
    }

    public void removeExpired() {
        Iterator<Entry<String, SessionData>> iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().getValue().getExpireAt().before(new Date())) {
                iter.remove();
            }
        }
    }
}
