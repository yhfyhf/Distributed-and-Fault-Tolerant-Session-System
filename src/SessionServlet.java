import RPC.RPCClient;
import RPC.RPCServer;
import group.Server;
import session.Session;
import session.SessionCookie;
import session.SessionTable;
import session.Utils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by yhf on 3/17/16.
 */

/**
 * Servlet that handles the session logics.
 */
@WebServlet(name = "SessionServlet", urlPatterns = {"/"})
public class SessionServlet extends HttpServlet {

    private RPCClient rpcClient;
    private RPCServer rpcServer;

    /**
     * Start daemon thread to remove expired sessions.
     */
    public void init() {
        rpcClient = new RPCClient();
        rpcServer = new RPCServer();
        removeExpiredSessionsDaemon();
        runRPCServerDaemon();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieValue = "";
        String sessionKey;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            cookieValue = Utils.findCookie(SessionCookie.getCookieName(), cookies);
        }

        Session session;
        if (cookieValue.isEmpty()) {     // first visit, no cookie
            session = new Session();
        } else {                        // cookie passed to server, try to get session from session.SessionTable
            String sessionId = cookieValue.split("__")[0];
            String versionNumber = cookieValue.split("__")[1];
            String locationMetadataStr = cookieValue.split("__")[2];
            locationMetadataStr = locationMetadataStr.substring(1, locationMetadataStr.length() - 1);  // remove "[" and "]"
            sessionKey = sessionId + "#" + versionNumber;

            session = SessionTable.sessionTable.get(sessionKey);

            if (session == null) {
                List<Server> servers = new ArrayList<>();
                System.out.println("locationMetadata: " + locationMetadataStr);
                for (String serverStr : locationMetadataStr.split(",")) {
                    String ipStr = serverStr.split(":")[0].substring(1);
                    String portStr = serverStr.split(":")[1];
                    System.out.println("ipStr: " + ipStr);
                    servers.add(new Server(InetAddress.getByName(ipStr), Integer.parseInt(portStr)));
                }

                String rpcResponseStr = rpcClient.readSession(sessionId, versionNumber, servers);
                System.out.println("rpcResponseStr: " + rpcResponseStr);
                String[] rpcResponse = rpcResponseStr.split(";");
                if (rpcResponse[0].equals("true")) {
                    String message = rpcResponse[1];
                } else {  // starts with "false"
                    if (rpcResponse[1].equals("SocketTimeout")) {
                        System.out.println("Socket Timeout!!!");
                    } else {
                        // other error message
                        System.out.println("Error Message " + rpcResponse[1]);
                    }
                    // TODO: render an error page
                }
            }
        }

        String sessionId = session.getSessionId();
        String versionNumber = session.getVersionNumber();
        sessionKey = sessionId + "#" + versionNumber;
        SessionTable.sessionTable.put(sessionKey, session);

        Cookie cookie = session.generateCookie();
        response.addCookie(cookie);

        request.setAttribute("sessionId", session.getSessionId());
        request.setAttribute("versionNumber", session.getVersionNumber());
        request.setAttribute("curTime", new Date());
        request.setAttribute("expireAt", session.getExpireAt());
        request.setAttribute("message", session.getMessage());
        request.setAttribute("serialCookie", cookie.getValue());
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionKey = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            sessionKey = Utils.findCookie(SessionCookie.getCookieName(), cookies);
        }

        if (request.getParameter("replace") != null) {                              /* Replace message. */
            String message = request.getParameter("message");

            Session session;
            if (sessionKey.isEmpty()) {
                session = new Session();
            } else {
                if (SessionTable.sessionTable.containsKey(sessionKey)) {
                    session = SessionTable.sessionTable.get(sessionKey);
                    session.setMessage(message);
                    session.update();
                } else {
                    session = new Session();
                }
            }

            String sessionId = session.getSessionId();
            String versionNumber = session.getVersionNumber();
            sessionKey = sessionId + "#" + versionNumber;
            SessionTable.sessionTable.put(sessionKey, session);

            Cookie cookie = session.generateCookie();
            response.addCookie(cookie);

            request.setAttribute("sessionId", session.getSessionId());
            request.setAttribute("versionNumber", session.getVersionNumber());
            request.setAttribute("curTime", new Date());
            request.setAttribute("expireAt", session.getExpireAt());
            request.setAttribute("message", session.getMessage());
            request.setAttribute("serialCookie", cookie.getValue());
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } else if (request.getParameter("refresh") != null) {                       /* Refresh */
            doGet(request, response);
        } else if (request.getParameter("logout") != null) {
            SessionTable.sessionTable.remove(sessionKey);
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } else {
            doGet(request, response);
        }
    }

    private void removeExpiredSessionsDaemon() {
        Thread removeExpiredDaemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    SessionTable.sessionTable.removeExpired();
                }
            }
        });
        removeExpiredDaemonThread.setDaemon(true);
        removeExpiredDaemonThread.start();
    }

    private void runRPCServerDaemon() {
        Thread RPCServerDaemonThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    rpcServer.run();
                }
            }
        });
        RPCServerDaemonThread.setDaemon(true);
        RPCServerDaemonThread.start();
    }
}
