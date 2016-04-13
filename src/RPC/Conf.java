package RPC;

/**
 * Created by yhf on 4/9/16.
 */
public class Conf {
    public static final int SESSION_READ = 1;
    public static final int SESSION_WRITE = 2;

    public static final int W = 1;   // sends write requests to W distinct servers
    public static final int WQ = 1;  // waits for at least WQ responses
    public static final int R = 1;   // sends read requests to R servers randomly chosen from WQ servers

    public static final int MAX_PACKET_SIZE = 4096;
    public static final int PORT_PROJ1B_RPC = 5300;

    public static final String DATAFILE = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/data.txt";
    public static final String REBOOTNUMFILE = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/rebootnum.txt";
    public static final String SESSIONNUMFILE = "/Users/Christina/DropBox/Courses/CS5300/project/pro1b/src/sessionnum.txt";
}
