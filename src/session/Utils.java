package session;

import RPC.Conf;
import RPC.RPCClient;
import group.Group;
import group.Server;

import javax.servlet.http.Cookie;
import java.io.*;

/**
 * Created by yhf on 3/17/16.
 */

public class Utils {

    public static String generateSessionId() {
        Server localServer = Group.group.getlocalServer();
        int sessionNum = readSessionNum();
        String sessionId = localServer.getServerId() + "$" + localServer.getRebootNum() + "$" + readSessionNum();
        writeSessionNum(sessionNum + 1);
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
        String rpcResponseStr = rpcClient.writeSession(session.getSessionId(), session.getVersionNumber(), session.getMessage(), session.getExpireAt());
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

    public static int readSessionNum() {
        String fileName = Conf.SESSIONNUMFILE;
        String line;
        int sessionNum = 0;

        try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            while ((line = bufferedReader.readLine()) != null) {
                sessionNum = Integer.valueOf(line.trim());
            }
            bufferedReader.close();
        } catch (FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");
        } catch (IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
        return sessionNum;
    }

    public static void writeSessionNum(int sessionNum) {
        try {
            File file = new File(Conf.SESSIONNUMFILE);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(String.valueOf(sessionNum));
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        String ip = " /192.168.1.102:5300";
        System.out.println(trimIP(ip));
    }
}
