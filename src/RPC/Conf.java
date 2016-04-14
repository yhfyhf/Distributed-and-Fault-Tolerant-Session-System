package RPC;

/**
 * Created by yhf on 4/9/16.
 */
public class Conf {
    public static final int SESSION_READ = 1;
    public static final int SESSION_WRITE = 2;

    public static int N = 3;         // number of servers in total
    public static int W = 3;         // sends write requests to W distinct servers
    public static int F = 1;
    public static int WQ = W - F;    // waits for at least WQ responses
    public static int R = 3;         // sends read requests to R servers randomly chosen from WQ servers

    public static final int MAX_PACKET_SIZE = 4096;

    public static final int PORT_PROJ1B_RPC = 5300;

    public static final String who = "aws";

    public static String SERVERS_INFO_FILEPATH = null;
    public static String REBOOTNUM_FILEPATH = null;
    public static String NF_FILEPATH = null;
    static {
        if (who.equals("yhf")) {
            SERVERS_INFO_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/servers.txt";
            REBOOTNUM_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/rebootnum.txt";
            NF_FILEPATH = "/Users/yhf/Dropbox/CS5300/project1b/src/NF.txt";
        } else if (who.equals("xyd")) {
            SERVERS_INFO_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/servers.txt";
            REBOOTNUM_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/rebootnum.txt";
            NF_FILEPATH = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/NF.txt";
        } else {   // AWS EC2
            SERVERS_INFO_FILEPATH = "/servers.txt";
            NF_FILEPATH = "/NF.txt";
        }
    }
}
