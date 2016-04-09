import session.SessionCookie;
import session.SessionData;
import session.SessionTable;

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
    private SessionTable sessionTable = new SessionTable();

    /**
     * Start daemon thread to remove expired sessions.
     */
    public SessionServlet() {
        super();
        removeExpiredSessionsDaemon();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(SessionCookie.getCookieName())) {
                    String cookieValue = cookie.getValue();
                    sessionId = SessionCookie.getSessionId(cookieValue);
                    break;
                }
            }
        }

        SessionData sessionData;
        if (sessionId == null) {        // first visit, no cookie
            sessionData = new SessionData();
        } else {                        // cookie passed to server, try to get session from session.SessionTable
            sessionData = sessionTable.getOrDefault(sessionId, new SessionData());  // session may exist or be expired
        }
        sessionId = sessionData.getSessionId();
        sessionTable.put(sessionId, sessionData);

        Cookie cookie = sessionData.generateCookie();
        response.addCookie(cookie);

        request.setAttribute("sessionData", sessionTable.get(sessionId));
        request.setAttribute("serialCookie", cookie.getValue());
        request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String sessionId = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(SessionCookie.getCookieName())) {
                String cookieValue = cookie.getValue();
                sessionId = SessionCookie.getSessionId(cookieValue);
                break;
            }
        }

        if (request.getParameter("replace") != null) {                              /* Replace message. */
            String message = request.getParameter("message");

            SessionData sessionData;
            if (sessionId == null) {
                sessionData = new SessionData();
            } else {
                if (sessionTable.containsKey(sessionId)) {
                    sessionData = sessionTable.get(sessionId);
                    sessionData.updateMessage(message);
                    sessionData.update();
                } else {
                    sessionData = new SessionData();
                }
            }

            sessionId = sessionData.getSessionId();
            sessionTable.put(sessionId, sessionData);

            Cookie cookie = sessionData.generateCookie();
            response.addCookie(cookie);

            request.setAttribute("sessionData", sessionTable.get(sessionId));
            request.setAttribute("serialCookie", cookie.getValue());
            request.getRequestDispatcher("/WEB-INF/index.jsp").forward(request, response);
        } else if (request.getParameter("refresh") != null) {                       /* Refresh */
            doGet(request, response);
        } else if (request.getParameter("logout") != null) {
            sessionTable.remove(sessionId);
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
                    sessionTable.removeExpired();
                }
            }
        });
        removeExpiredDaemonThread.setDaemon(true);
        removeExpiredDaemonThread.start();
    }
}
