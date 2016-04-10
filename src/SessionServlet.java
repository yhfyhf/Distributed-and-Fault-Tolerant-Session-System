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

/**
 * Created by yhf on 3/17/16.
 */

/**
 * Servlet that handles the session logics.
 */
@WebServlet(name = "SessionServlet", urlPatterns = {"/"})
public class SessionServlet extends HttpServlet {

    /**
     * Start daemon thread to remove expired sessions.
     */
    public SessionServlet() {
        super();
        removeExpiredSessionsDaemon();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionKey = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            sessionKey = Utils.findCookie(SessionCookie.getCookieName(), cookies);
        }

        Session session;
        if (sessionKey.isEmpty()) {     // first visit, no cookie
            session = new Session();
        } else {                        // cookie passed to server, try to get session from session.SessionTable
            session = SessionTable.sessionTable.getOrDefault(sessionKey, new Session());  // session may exist or be expired
        }

        String sessionId = session.getSessionId();
        String versionNumber = session.getVersionNumber();
        sessionKey = sessionId + ";" + versionNumber;
        SessionTable.sessionTable.put(sessionKey, session);

        Cookie cookie = session.generateCookie();
        response.addCookie(cookie);

        request.setAttribute("session", session);
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
            sessionKey = sessionId + ";" + versionNumber;
            SessionTable.sessionTable.put(sessionKey, session);

            Cookie cookie = session.generateCookie();
            response.addCookie(cookie);

            request.setAttribute("session", SessionTable.sessionTable.get(sessionKey));
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

    private void removeExpiredSessionsDaemon(){
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
}
