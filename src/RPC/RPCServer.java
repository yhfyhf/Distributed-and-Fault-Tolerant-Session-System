package RPC;

import session.Session;
import session.SessionTable;

import java.io.IOException;
import java.net.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yhf on 4/9/16.
 */
public class RPCServer implements Runnable {

    public void run() {
        while (true) {
            DatagramSocket rpcSocket;
            try {
                rpcSocket = new DatagramSocket(Conf.PORT_PROJ1B_RPC);
            } catch (SocketException e) {
                System.out.println("Server SocketException: " + e);
                continue;
            }

            try {
                byte[] inBuf = new byte[Conf.MAX_PACKET_SIZE];
                DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
                rpcSocket.receive(recvPkt);
                InetAddress returnAddr = recvPkt.getAddress();
                int returnPort = recvPkt.getPort();

                String inStr = (new String(inBuf)).trim();

                System.out.println("!!! Server receives inStr: " + inStr);

                String[] tokens = inStr.split(";");
                String callID = tokens[0];
                int operationCode = Integer.parseInt(tokens[1]);
                String sessionId = tokens[2];
                String versionNumber = tokens[3];

                System.out.println("!!! Server receives operationCode: " + operationCode);

                String outStr = "";

                switch (operationCode) {
                    case Conf.SESSION_READ:
                        // callID;operationCode;sessionID;versionNumber
                        System.out.println("!!! Server receives a ReadSession operation.");
                        Session session = SessionTable.sessionTable.get(sessionId + "#" + versionNumber);
                        if (session != null) {
                            outStr = callID + ";" + session.getMessage();  // TODO: What if message contains ';'
                            System.out.println("!!! Server receives outStr: " + outStr);
                        } else {
                            outStr = "NotExists;";
                            System.out.println("!!! Server this session not exists.");
                        }
                        break;

                    case Conf.SESSION_WRITE:
                        // callID;operationCode;sessionId;versionNumber;message;dicardTime
                        String message = tokens[4];
                        String discardTimeStr = tokens[5];

                        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        Date discardTime = format.parse(discardTimeStr);

                        System.out.println("!!! Server writes: " + message + ";" + discardTime);
                        SessionTable.sessionTable.updateSession(sessionId, versionNumber, message, discardTime);
                        outStr = callID + ";";
                        System.out.println("!!! Not sure write successfully, outStr: " + outStr);
                        System.out.println("[Server] Now locally the session table is:");
                        for (String sessionKey : SessionTable.sessionTable.keySet()) {
                            System.out.println(sessionKey + "  " + SessionTable.sessionTable.get(sessionKey).getMessage());
                        }
                }

                byte[] outBuf = outStr.getBytes();
                DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, returnAddr, returnPort);
                rpcSocket.send(sendPkt);
            } catch (ParseException e) {
                // e.printStackTrace();
                System.out.println("!!! Server ParseException: " + e);
            } catch (UnknownHostException e) {
                // e.printStackTrace();
                System.out.println("!!! Server UnknownHostException: " + e);
            } catch (IOException e) {
                // e.printStackTrace();
                System.out.println("!!! Server IOException: " + e);
            } catch (IndexOutOfBoundsException e) {
                System.out.println("!!! Server IndexOutOfBoundsException: " + e);
            } finally {
                rpcSocket.close();
            }
        }
    }

    public static void main(String args[]) {
        (new Thread(new RPCServer())).start();
    }

}
