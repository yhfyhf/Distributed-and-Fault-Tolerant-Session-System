package session;

import javax.servlet.http.Cookie;
import java.net.UnknownHostException;
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

    public static final int maxAge = 300;
    public static final int delta = 100;

    private String sessionId;
    private String versionNumber;
    private String message;
    private Date createAt;
    private Date expireAt;
    private List<String> locationMetadata;

    public Session() throws UnknownHostException {
        this(Utils.generateSessionId(), "1");
    }

    public Session(String sessionId, String versionNumber) throws UnknownHostException {
        this.sessionId = sessionId;
        this.versionNumber = versionNumber;
        this.message = "Hello, User";
        this.locationMetadata = new ArrayList<>();

        Calendar now = Calendar.getInstance();
        this.createAt = now.getTime();
        now.add(Calendar.SECOND, maxAge);
        this.expireAt = now.getTime();
    }

    /**
     * Update session's expireAt.
     */
    public void setExpireAt() {
        Calendar now = Calendar.getInstance();
        now.add(Calendar.SECOND, maxAge);
        this.expireAt = now.getTime();
    }

    public void setExpireAt(Date expireAt) {
        this.expireAt = expireAt;
    }

    /**
     * Update session's version number.
     */
    public void updateVersionNumber() {
        this.versionNumber = String.valueOf(Integer.valueOf(this.versionNumber) + 1);
    }

    /**
     * Update session by updating its version number and expireAt. It is used when refreshing.
     */
    public void update() {
        updateVersionNumber();
        setExpireAt();
    }

    /**
     * Update session's message.
     * Note this is an atomic operation. Version number and expireAt should not be updated here.
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    public Cookie generateCookie() {
        String locationMetadataStr = locationMetadata.toString();
        return new SessionCookie(sessionId, versionNumber, locationMetadataStr.substring(1, locationMetadataStr.length() - 1)); // remove "[" and "]"
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getVersionNumber() {
        return versionNumber;
    }

    public void setVersionNumber(String versionNumber) {
        this.versionNumber = versionNumber;
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

    public List<String> getLocationMetadata() {
        return locationMetadata;
    }

    public void resetLocationMetada() {
        locationMetadata = new ArrayList<>();
    }

    public void addLocation(String serverId) {
        locationMetadata.add(serverId);
    }

    public void addLocations(List<String> serverIds) {
        for (String serverId : serverIds) {
            addLocation(serverId);
        }
    }

    public String toString() {
        return sessionId + "#" + versionNumber;
    }
}
