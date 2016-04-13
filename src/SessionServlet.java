import RPC.RPCClient;
import RPC.RPCServer;
import group.Group;
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
    private static RPCServer rpcServer;

    public SessionServlet() {
        rpcClient = new RPCClient();
        rpcServer = new RPCServer();
        removeExpiredSessionsDaemon();
        (new Thread(rpcServer)).start();
    }

    /**
     * Get cookies, find the desire cookie.
     * If cookie not exists:
     *     New a session.
     * If cookie exists:
     *     Get sessionId__versionNumber__locationMetadata from the cookie.
     *     Get the session specified by sessionId#versionNumber from the local sessionTable.
     *     If session not exists:
     *         Send RPC readSession to other servers specified in the cookie.
     *         Get one response, and get all the data from the response.
     *         Stores in local server.
     *     If session exists:
     *         pass
     * Send RPC writeSession operation (write to W servers, waits for WQ response)
     *     * If write fails (does not receive WQ responses), displays failing page
     *     * If write successes, returns session data and cookie to browser.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieValue = "";
        String sessionKey;
        Cookie[] cookies = request.getCookies();
        String exutedServerId = Group.group.getlocalServer().getServerId();
        if (cookies != null) {
            cookieValue = Utils.findCookie(SessionCookie.getCookieName(), cookies);
        }

        Session session;
        if (cookieValue.isEmpty()) {     // first visit, no cookie
            session = new Session();
            System.out.println("[Servelet]: First visit, no cookie sent");
            session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
            if (session == null) {
                System.out.println("Servlet session is null. 1");
                renderErrorPage(request, response, "First visit, newed a session and tries to write to other servers but failed.");
                return;
            }
        } else {                         // Cookie value is not empty
            String sessionId = cookieValue.split("__")[0];
            String versionNumber = cookieValue.split("__")[1];
            String locationMetadataStr = cookieValue.split("__")[2];
            sessionKey = sessionId + "#" + versionNumber;
            session = SessionTable.sessionTable.get(sessionKey);

            if (session == null) {        // can not find session locally
                List<Server> servers = new ArrayList<>();
                System.out.println("[Servlet] Session not exists locally, requesting from locationMetadata: " + locationMetadataStr);
                for (String serverStr : locationMetadataStr.split(",")) {
                    String idStr = serverStr.split(":")[0].trim();
                    if (Group.group.getServerTable().containsKey(idStr)) {
                        servers.add(Group.group.getServerTable().get(idStr));
                    }
                }

                String rpcResponseStr = rpcClient.readSession(sessionId, versionNumber, servers);
                System.out.println("rpcResponseStr: " + rpcResponseStr);
                String[] rpcResponse = rpcResponseStr.split(";");
                if (rpcResponse[0].equals("true")) {
                    String message = rpcResponse[2];
                    exutedServerId = rpcResponse[3].split(":")[0];
                    session = new Session(sessionId, versionNumber);
                    session.setMessage(message);
                    session.update();
                    session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
                    if (session == null) {
                        renderErrorPage(request, response, "Read session remotely, and tries to write to other servers but failed");
                        return;
                    }
                } else {      // read operation returns starting with "false"
                    if (rpcResponse[1].equals("SocketTimeout")) {
                        System.out.println("Socket Timeout!!!");
                    } else {
                        // other error message
                        System.out.println("Error Message " + rpcResponse[1]);
                    }
                    renderErrorPage(request, response, "Read operation fails, please try deleting the local cookie.");
                    return;
                }
            } else {   // session exists locally
                session.update();
                session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
                if (session == null) {
                    renderErrorPage(request, response, "Update version locally, and tries to write to other servers but failed");
                    return;
                }
            }
        }

        System.out.println("[Servlet] Session: " + session);
        String sessionId = session.getSessionId();
        String versionNumber = session.getVersionNumber();
        sessionKey = sessionId + "#" + versionNumber;
        SessionTable.sessionTable.put(sessionKey, session);

        Cookie cookie = session.generateCookie();
        System.out.println("[Servlet] Cookie: " + cookie.getValue());
        cookie.setMaxAge(Session.maxAge);
        cookie.setDomain("localhost");
        response.addCookie(cookie);

        request.setAttribute("sessionId", session.getSessionId());
        request.setAttribute("versionNumber", session.getVersionNumber());
        request.setAttribute("curTime", new Date());
        request.setAttribute("expireAt", session.getExpireAt());
        request.setAttribute("message", session.getMessage());
        request.setAttribute("serialCookie", cookie.getValue());
        request.setAttribute("localServerId", Group.group.getlocalServer().getServerId());
        request.setAttribute("localServerRebootNum", Group.group.getlocalServer().getRebootNum());
        request.setAttribute("exutedServerId", exutedServerId);
        request.setAttribute("metadata", SessionCookie.getLocationMetadata(cookieValue));
        request.setAttribute("cookieDomain", cookie.getDomain());
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String cookieValue = "";
        String sessionKey = "";
        Cookie[] cookies = request.getCookies();
        String exutedServerId = Group.group.getlocalServer().getServerId();
        if (cookies != null) {
            cookieValue = Utils.findCookie(SessionCookie.getCookieName(), cookies);
        }

        if (request.getParameter("replace") != null) {       /* Replace message. */
            String replaceMessage = request.getParameter("message");

            Session session;
            if (cookieValue.isEmpty()) {
                session = new Session();   // first visit, should not set message
                System.out.println("[Servelet]: First visit, no cookie sent");
                session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
                if (session == null) {
                    System.out.println("Servlet session is null. 1");
                    renderErrorPage(request, response, "First visit, newed a session and tries to write to other servers but failed.");
                    return;
                }
            } else {   // Cookie value is not empty
                String sessionId = cookieValue.split("__")[0];
                String versionNumber = cookieValue.split("__")[1];
                String locationMetadataStr = cookieValue.split("__")[2];
                sessionKey = sessionId + "#" + versionNumber;
                session = SessionTable.sessionTable.get(sessionKey);

                if (session == null) {        // can not find session locally
                    List<Server> servers = new ArrayList<>();
                    System.out.println("[Servlet] Session not exists locally, requesting from locationMetadata: " + locationMetadataStr);
                    for (String serverStr : locationMetadataStr.split(",")) {
                        String idStr = serverStr.split(":")[0].trim();
                        if (Group.group.getServerTable().containsKey(idStr)) {
                            servers.add(Group.group.getServerTable().get(idStr));
                        }
                    }
                    String rpcResponseStr = rpcClient.readSession(sessionId, versionNumber, servers);
                    System.out.println("rpcResponseStr: " + rpcResponseStr);
                    String[] rpcResponse = rpcResponseStr.split(";");
                    if (rpcResponse[0].equals("true")) {
                        exutedServerId = rpcResponse[3].split(":")[0];
                        session = new Session(sessionId, versionNumber);
                        session.setMessage(replaceMessage);
                        session.update();
                        session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
                        if (session == null) {
                            renderErrorPage(request, response, "Read session remotely, and tries to write to other servers but failed");
                            return;
                        }
                    } else {      // read operation returns starting with "false"
                        if (rpcResponse[1].equals("SocketTimeout")) {
                            System.out.println("Socket Timeout!!!");
                        } else {
                            // other error message
                            System.out.println("Error Message " + rpcResponse[1]);
                        }
                        renderErrorPage(request, response, "Read operation fails");
                        return;
                    }
                } else {   // session exists locally
                    session.update();
                    session.setMessage(replaceMessage);
                    session = Utils.writeSessionAndCheckSuccess(rpcClient, session);
                    if (session == null) {
                        renderErrorPage(request, response, "Update version locally, and tries to write to other servers but failed");
                        return;
                    }
                }
            }

            String sessionId = session.getSessionId();
            String versionNumber = session.getVersionNumber();
            sessionKey = sessionId + "#" + versionNumber;
            SessionTable.sessionTable.put(sessionKey, session);

            Cookie cookie = session.generateCookie();
            cookie.setMaxAge(Session.maxAge);
            cookie.setDomain("localhost");
            response.addCookie(cookie);

            request.setAttribute("sessionId", session.getSessionId());
            request.setAttribute("versionNumber", session.getVersionNumber());
            request.setAttribute("curTime", new Date());
            request.setAttribute("expireAt", session.getExpireAt());
            request.setAttribute("message", session.getMessage());
            request.setAttribute("serialCookie", cookie.getValue());
            request.setAttribute("localServerId", Group.group.getlocalServer().getServerId());
            request.setAttribute("localServerRebootNum", Group.group.getlocalServer().getRebootNum());
            request.setAttribute("exutedServerId", exutedServerId);
            request.setAttribute("metadata", SessionCookie.getLocationMetadata(cookieValue));
            request.setAttribute("cookieDomain", cookie.getDomain());
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } else if (request.getParameter("refresh") != null) {                       /* Refresh */
            doGet(request, response);
        } else if (request.getParameter("logout") != null) {
            if (!cookieValue.isEmpty()) {
                String sessionId = cookieValue.split("__")[0];
                String versionNumber = cookieValue.split("__")[1];
                sessionKey = sessionId + "#" + versionNumber;
            }
            Session session = SessionTable.sessionTable.get(sessionKey);
            Cookie cookie = session.generateCookie();
            SessionTable.sessionTable.remove(sessionKey);
            cookie.setMaxAge(0);
            response.addCookie(cookie);
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

    private void renderErrorPage(HttpServletRequest request, HttpServletResponse response, String errorMessage)
            throws ServletException, IOException {
        request.setAttribute("error", errorMessage);
        request.getRequestDispatcher("/WEB-INF/error.jsp").forward(request, response);
    }
}
