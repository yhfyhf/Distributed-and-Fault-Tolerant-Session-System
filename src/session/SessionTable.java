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
public class SessionTable extends ConcurrentHashMap<String, Session> {

    /**
     * Returns the session specified by sessionId. If the session exists, returns that session;
     * else, returns a new session.
     */
    public Session getOrDefault(String sessionId, Session defaultSession) {
        if (containsKey(sessionId)) {
            Session session = get(sessionId);
            session.update();
            return session;
        } else {
            return defaultSession;
        }
    }

    public void removeExpired() {
        Iterator<Entry<String, Session>> iter = this.entrySet().iterator();
        while (iter.hasNext()) {
            if (iter.next().getValue().getExpireAt().before(new Date())) {
                iter.remove();
            }
        }
    }
}
