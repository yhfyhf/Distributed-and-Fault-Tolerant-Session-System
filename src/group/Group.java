package group;

import RPC.Conf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yhf on 4/9/16.
 */
public class Group {
    private List<Server> servers = new ArrayList<Server>();

    public static Group group;

    static {
        try {
            group = new Group();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Group() throws UnknownHostException {
        List<String> serverData = Utils.getServerData();
        for (String item : serverData) {
            String serverIP = item.split(",")[1];
            servers.add(new Server(InetAddress.getByName(serverIP), Conf.PORT_PROJ1B_RPC));
        }
//        servers.add(new Server(InetAddress.getByName("192.168.1.102"), Conf.PORT_PROJ1B_RPC));
//        servers.add(new Server(InetAddress.getByName("192.168.1.108"), Conf.PORT_PROJ1B_RPC));
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Server> getRandomServers(int num) {
        List<Server> copy = new ArrayList<>(servers);
        Collections.shuffle(copy);
        return copy.subList(0, num);
    }
}
