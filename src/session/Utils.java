package session;

import RPC.RPCClient;
import group.Group;
import group.Server;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by yhf on 3/17/16.
 */

public class Utils {

    public static String generateSessionId() {
        Server localServer = Group.group.getlocalServer();
        String sessionId = localServer.getServerId() + "$" + localServer.getRebootNum() + "$" + SessionTable.sessionTable.size();
        return sessionId;
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

    /**
     * Send RPC writeSession request, add locations to session, and check if it successes.
     */
    public static Session writeSessionAndCheckSuccess(RPCClient rpcClient, Session session) throws IOException {
        Date discardTime = session.getExpireAt();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(discardTime);
        calendar.add(Calendar.MILLISECOND, Session.delta);
        discardTime = calendar.getTime();
        String rpcResponseStr = rpcClient.writeSession(session.getSessionId(), session.getVersionNumber(),
                session.getMessage(), discardTime);
        String[] rpcResponse = rpcResponseStr.split(";");
        if (rpcResponse[0].equals("true")) {
            String[] serversStr = rpcResponse[1].split(",");
            System.out.println("Servlet receives write response locations: " + serversStr);
            session.resetLocationMetada();
            for (String serverStr : serversStr) {
                String idStr = serverStr.split(":")[0];
                System.out.println("idStr: " + idStr);
                if (Group.group.getServerTable().containsKey(idStr)) {
                    session.addLocation(idStr);
                }
            }
            return session;
        } else {
            System.out.println("Did not write successfully");
            return null;
        }
    }

    /**
     * Trim IP.
     * IP string may have leading space or slash, so use this function to remove this characters.
     */
    public static String trimIP(String ip) {
        return ip.trim().replaceAll("/", "");
    }

    public static void main(String[] args) {
        String ip = " /192.168.1.102:5300";
        System.out.println(trimIP(ip));
    }
}
