package RPC;

import group.Group;
import group.Server;
import session.Session;
import session.SessionTable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.*;

/**
 * Created by yhf on 4/9/16.
 */
public class RPCClient {

    /**
     * Gets session from R servers chosen from WQ servers stored in location metadata,
     * and returns the first received packet.
     *
     * Send:  callID;Conf.SESSION_READ;sessionID;versionNumber
     * Return: true;CallID;message
     *         true;NotExists
     *         false;SocketTimeout
     *         false;errorMessage
     */
    public String readSession(String sessionID, String versionNumber, List<Server> servers) throws IOException {
        DatagramSocket rpcSocket = new DatagramSocket();
        rpcSocket.setSoTimeout(5000);

        String callID = UUID.randomUUID().toString();
        String outStr = callID + ";" + Conf.SESSION_READ + ";" + sessionID + ";" + versionNumber;
        byte[] outBuf = outStr.getBytes();

        System.out.println("Client starts to sending read operation...");

        while (Conf.R < servers.size()) {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(servers.size());
            servers.remove(index);
        }

        for (Server server : servers) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, server.getIp(), server.getPort());
            rpcSocket.send(sendPkt);
            System.out.println("Client sent to server: " + server);
        }

        System.out.println("Client waiting for response...");

        byte [] inBuf = new byte[Conf.MAX_PACKET_SIZE];
        DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);

        String ret = "";
        try {
            String inStr;
            do {
                recvPkt.setLength(inBuf.length);
                rpcSocket.receive(recvPkt);
                inStr = new String(inBuf);
            } while (inStr == null || inStr.equals("") || !inStr.split(";")[0].equals(callID));
            ret = "true;" + inStr;
        } catch (SocketTimeoutException e) {
            e.printStackTrace();
            ret = "false;" + "SocketTimeout";
        } catch (IOException e) {
            e.printStackTrace();
            ret = "false;" + e;
        } finally {
            rpcSocket.close();
            return ret;
        }
    }

    /**
     * Writes to W servers, waits for the first WQ successful responses,
     * and sets the new cookie metadata to the set of WQ bricks that responded.
     */
    public String writeSession(String sessionId, String versionNumber, String message, Date dicardTime)
            throws IOException {
        DatagramSocket rpcSocket = new DatagramSocket();
        rpcSocket.setSoTimeout(5000);

        String callID = UUID.randomUUID().toString();
        String outStr = callID + ";" + Conf.SESSION_WRITE + ";" + sessionId + ";"
                + versionNumber + ";" + message + ";" + dicardTime;
        byte[] outBuf = outStr.getBytes();

        System.out.println("Client starts to send write operation...");

        for (Server server : Group.getRandomServers(Conf.W)) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, server.getIp(), server.getPort());
            rpcSocket.send(sendPkt);
        }

        byte [] inBuf = new byte[Conf.MAX_PACKET_SIZE];
        DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);

        String sessionKey = sessionId + "#" + versionNumber;
        Session session = SessionTable.sessionTable.get(sessionKey);

        String ret = "";
        try {
            int numResponded = 0;
            String inStr;
            List<Server> locations = new ArrayList<>();
            do {
                recvPkt.setLength(inBuf.length);
                rpcSocket.receive(recvPkt);
                inStr = new String(inBuf);

                if (inStr.split(";")[0].equals(callID)) {
                    numResponded++;
                    locations.add(new Server(recvPkt.getAddress(), recvPkt.getPort()));
                }
            } while (numResponded < Conf.WQ);
            session.addLocations(locations);
            ret = "true;";
        } catch (SocketTimeoutException e) {
            ret = "false;" + "SocketTimeout";
        } finally {
            rpcSocket.close();
            return ret;
        }
    }
}
