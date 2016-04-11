package session;

import RPC.RPCClient;
import group.Server;

import javax.servlet.http.Cookie;
import java.io.IOException;
import java.net.InetAddress;
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

    /**
     * Send RPC writeSession request, and check if it successes.
     */
    public static Session writeSessionAndCheckSuccess(RPCClient rpcClient, Session session) throws IOException {
        String rpcResponseStr = rpcClient.writeSession(session.getSessionId(), session.getVersionNumber(), session.getMessage(), session.getExpireAt());
        String[] rpcResponse = rpcResponseStr.split(";");
        if (rpcResponse[0].equals("true")) {
            String[] serversStr = rpcResponse[1].split(",");
            System.out.println("Servlet receives locations: " + serversStr);
            for (String serverStr : serversStr) {
                String ipStr = serverStr.split(":")[0].substring(1);;
                String portStr = serverStr.split(":")[1];
                System.out.println("ipStr: " + ipStr);
                session.addLocation(new Server(InetAddress.getByName(ipStr), Integer.parseInt(portStr)));
            }
            return session;
        } else {
            System.out.println("Did not write successfully");
            return null;
        }
    }
}
