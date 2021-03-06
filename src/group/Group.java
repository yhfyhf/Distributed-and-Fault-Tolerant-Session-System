package group;

import RPC.Conf;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Created by yhf on 4/9/16.
 */
public class Group {
    private List<Server> servers = new ArrayList<>();
    private Map<String, Server> serverTable = new HashMap();

    public static Group group;
    private Server localServer;

    static {
        try {
            group = new Group();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public Group() throws UnknownHostException {
        List<Integer> fAndN = Utils.readNAndF();
        if (fAndN.size() != 0) {
            Conf.N = fAndN.get(0);
            Conf.F = fAndN.get(1);
            Conf.W = Conf.N;
            Conf.WQ = Conf.W - Conf.F;
            Conf.R = Conf.WQ;
        }

        List<String> serverData = Utils.readServerData();
        String serverId = serverData.get(0).split(",")[0].trim();
        String serverIP = serverData.get(0).split(",")[1].trim();
        String serverDomain = serverData.get(0).split(",")[2].trim();
        localServer = new Server(serverId, InetAddress.getByName(serverIP), Conf.PORT_PROJ1B_RPC, serverDomain);
        localServer.setRebootNum(Utils.readRebootNum());

        for (int i = 1; i < serverData.size(); i++) {
            serverId = serverData.get(i).split(",")[0];
            serverIP = serverData.get(i).split(",")[1];
            serverDomain = serverData.get(i).split(",")[2];
            Server server = new Server(serverId, InetAddress.getByName(serverIP), Conf.PORT_PROJ1B_RPC, serverDomain);
            servers.add(server);
            serverTable.put(serverId, server);
        }
    }

    public List<Server> getServers() {
        return servers;
    }

    public List<Server> getRandomServers(int num) {
        List<Server> copy = new ArrayList<>(servers);
        Collections.shuffle(copy);
        System.out.println("copy size:" +  copy.size());
        System.out.println("Server" +  servers);

        System.out.println("num:" +  num);
        return copy.subList(0, num);
    }

    public Server getlocalServer() {
        return localServer;
    }

    public Map<String, Server> getServerTable() {
        return serverTable;
    }
}
