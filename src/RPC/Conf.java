package RPC;

/**
 * Created by yhf on 4/9/16.
 */
public class Conf {
    public static final int SESSION_READ = 1;
    public static final int SESSION_WRITE = 2;

    public static final int W = 1;   // sends write requests to W distinct servers
    public static final int R = 1;   // sends read requests to R servers randomly chosen from WQ servers
    public static int WQ = W;  // waits for at least WQ responses
    public static int F = 1;
    public static int N = 3;

    public static final int MAX_PACKET_SIZE = 4096;

    public static final int PORT_PROJ1B_RPC = 5300;

    public static final String who = "xyd";

    public static String SERVERS_INFO_FILEPATH = null;
    public static String REBOOTNUM_FILEPATH = null;
    public static String FANDN_FILEPATH = null;
    static {
        if (who.equals("yhf")) {
            SERVERS_INFO_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/servers.txt";
            REBOOTNUM_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/rebootnum.txt";
            FANDN_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/fandn.txt";
        } else if (who.equals("xyd")) {
            SERVERS_INFO_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/servers.txt";
            REBOOTNUM_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/rebootnum.txt";
            FANDN_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/fandn.txt";
        } else {   // AWS EC2
            SERVERS_INFO_FILEPATH = "/servers.txt";
            FANDN_FILEPATH = "/fandn.txt";
        }
    }
}
