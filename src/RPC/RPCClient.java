package RPC;

import group.Group;
import group.Server;
import session.Session;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by yhf on 4/9/16.
 */
public class RPCClient {

    /**
     * Gets session from R servers chosen from WQ servers stored in location metadata,
     * and returns the first received packet.
     */
    public static Session readSession(Session session) throws IOException {
        DatagramSocket rpcSocket = new DatagramSocket();
        rpcSocket.setSoTimeout(5000);

        String callID = UUID.randomUUID().toString();
        String outStr = callID + ";" + Conf.SESSION_READ + ";" +
                session.getSessionId() + ";" + session.getVersionNumber();
        byte[] outBuf = Utils.stringToByteArray(outStr);

        List<Server> servers = session.getLocationMetadata();
        while (Conf.R < servers.size()) {
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(servers.size());
            servers.remove(index);
        }

        for (Server server : servers) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, server.getIp(), server.getPort());
            rpcSocket.send(sendPkt);
        }

        byte [] inBuf = new byte[Conf.MAX_PACKET_SIZE];
        DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);

        try {
            String inStr;
            do {
                recvPkt.setLength(inBuf.length);
                rpcSocket.receive(recvPkt);
                inStr = Utils.byteArrayToString(inBuf);
            } while (inStr == null || inStr.equals("") || !inStr.split(";")[0].equals(callID));
        } catch (ClassNotFoundException e) {
            session = null;
            e.printStackTrace();
        } catch (SocketTimeoutException e) {
            session = null;
            e.printStackTrace();
        } catch (IOException e) {
            session = null;
            e.printStackTrace();
        } finally {
            rpcSocket.close();
            return session;
        }
    }

    /**
     * Writes to W servers, waits for the first WQ successful responses,
     * and sets the new cookie metadata to the set of WQ bricks that responded.
     */
    public static Session writeSession(Session session) throws IOException {
        DatagramSocket rpcSocket = new DatagramSocket();
        rpcSocket.setSoTimeout(5000);

        String callID = UUID.randomUUID().toString();
        String outStr = callID + ";" + Conf.SESSION_WRITE + ";" +
                session.getSessionId() + ";" + session.getVersionNumber() + ";" +
                session.getMessage();

        byte[] outBuf = Utils.stringToByteArray(outStr);

        for (Server server : Group.getRandomServers(Conf.W)) {
            DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, server.getIp(), server.getPort());
            rpcSocket.send(sendPkt);
        }

        byte [] inBuf = new byte[Conf.MAX_PACKET_SIZE];
        DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);

        session.resetLocationMetada();

        try {
            int numResponded = 0;
            String inStr;
            do {
                recvPkt.setLength(inBuf.length);
                rpcSocket.receive(recvPkt);
                inStr = Utils.byteArrayToString(inBuf);

                if (inStr.split(";")[0].equals(callID)) {
                    numResponded++;
                    session.addLocation(new Server(recvPkt.getAddress(), recvPkt.getPort()));
                }
            } while (numResponded < Conf.WQ);
        } catch (ClassNotFoundException e) {
            session = null;
            e.printStackTrace();
        } finally {
            rpcSocket.close();
            return session;
        }
    }
}
