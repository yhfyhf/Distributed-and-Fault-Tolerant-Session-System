package group;

import java.net.InetAddress;

/**
 * Created by yhf on 4/9/16.
 */
public class Server {
    private InetAddress ip;
    private int port;

    public Server(InetAddress ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String toString() {
        return ip + ":" + port;
    }
}
