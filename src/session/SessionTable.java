package session;

import java.net.UnknownHostException;
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

    public static SessionTable sessionTable = new SessionTable();

    /**
     * Returns the session specified by sessionId. If the session exists, returns that session;
     * else, returns a new session.
     */
    public Session getOrDefault(String sessionKey, Session defaultSession) {
        if (containsKey(sessionKey)) {
            Session session = get(sessionKey);
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

    public void updateSession(String sessionId, String versionNumber, String message, Date discardTime) throws UnknownHostException {
        Session session = getOrDefault(sessionId, new Session(sessionId));
        session.setVersionNumber(versionNumber);
        session.setMessage(message);
        session.setExpireAt(discardTime);
        sessionTable.put(sessionId+"#"+versionNumber, session);
    }
}
