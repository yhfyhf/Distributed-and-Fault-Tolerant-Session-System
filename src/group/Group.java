package group;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by yhf on 4/9/16.
 */
public class Group {
    private static List<Server> servers = new ArrayList<Server>();

    public static List<Server> getServers() {
        return servers;
    }

    public static List<Server> getRandomServers(int num) {
        List<Server> copy = new ArrayList<>(servers);
        Collections.shuffle(copy);
        return copy.subList(0, num);
    }
}
