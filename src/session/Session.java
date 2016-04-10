package session;

import group.Server;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by yhf on 3/17/16.
 */

/**
 * Implementation of Session that encapsulates all the fields that a session holds.
 */
public class Session {

    private static int maxAge = 60;

    private String sessionId;
    private String versionNumber;
    private String message;
    private Date createAt;
    private Date expireAt;
//    private String locationMetadata;
    private List<Server> locationMetadata;   // 可以把改为List<String>

    public Session() {
        this.sessionId = Utils.generateSessionId();
        this.versionNumber = "1";
        this.message = "Hello, User";
//        this.locationMetadata = "default_location_metadata";
        this.locationMetadata = new ArrayList<Server>();

        Calendar now = Calendar.getInstance();
        this.createAt = now.getTime();
        now.add(Calendar.SECOND, maxAge);
        this.expireAt = now.getTime();
    }

    /**
     * Update session's expireAt.
     */
    private void updateExpireAt() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, maxAge);
        this.expireAt = now.getTime();
    }

    /**
     * Update session's version number.
     */
    private void updateVersionNumber() {
        this.versionNumber = String.valueOf(Integer.valueOf(this.versionNumber) + 1);
    }

    /**
     * Update session by updating its version number and expireAt. It is used when refreshing.
     */
    public void update() {
        updateVersionNumber();
        updateExpireAt();
    }

    /**
     * Update session's message.
     * Note this is an atomic operation. Version number and expireAt should not be updated here.
     * @param message
     */
    public void updateMessage(String message) {
        this.message = message;
    }

    public Cookie generateCookie() {
        return new SessionCookie(sessionId, versionNumber, locationMetadata.toString());
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public String getMessage() {
        return message;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public Date getExpireAt() {
        return expireAt;
    }

    public List<Server> getLocationMetadata() {
        return locationMetadata;
    }

    public void resetLocationMetada() {
        locationMetadata = new ArrayList<>();
    }

    public void addLocation(Server server) {
        locationMetadata.add(server);
    }
}
