package group;

import java.net.InetAddress;

/**
 * Created by yhf on 4/9/16.
 */
public class Server {
    private String serverId;
    private InetAddress ip;
    private int port;
    private String domain;
    private int rebootNum ;

    public Server(String id, InetAddress ip, int port, String domain) {
        this.serverId = id;
        this.ip = ip;
        this.port = port;
        this.domain = domain;
    }

    public void setRebootNum(int num) { this.rebootNum = num; }

    public int getRebootNum() { return rebootNum; }

    public String getServerId() {
        return serverId;
    }

    public InetAddress getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    public String getDomain() { return domain; }

    public String toString() {
        return serverId + ":" + ip + ":" + port + ":" + domain;
    }
}
