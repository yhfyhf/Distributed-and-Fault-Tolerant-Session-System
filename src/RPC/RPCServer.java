package RPC;

import session.Session;
import session.SessionTable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
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
        try {
            System.out.println("Server thread running...");

            DatagramSocket rpcSocket = new DatagramSocket(Conf.PORT_PROJ1B_RPC);

            while (true) {
                byte[] inBuf = new byte[Conf.MAX_PACKET_SIZE];
                DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
                rpcSocket.receive(recvPkt);
                InetAddress returnAddr = recvPkt.getAddress();
                int returnPort = recvPkt.getPort();

                String inStr = new String(inBuf);

                System.out.println("Server receives inStr: " + inStr);

                String[] tokens = inStr.split(";");
                String callID = tokens[0];
                int operationCode = Integer.parseInt(tokens[1]);
                String sessionId = tokens[2];
                String versionNumber = tokens[3];

                String outStr = "";

                switch (operationCode) {
                    case Conf.SESSION_READ:
                        // callID;operationCode;sessionID;versionNumber
                        Session session = SessionTable.sessionTable.get(sessionId + ";" + versionNumber);
                        if (session != null) {
                            outStr = callID + ";" + session.getMessage();  // TODO: What if message contains ';'
                        } else {
                            outStr = "NotExists;";
                        }
                        break;

                    case Conf.SESSION_WRITE:
                        // callID;operationCode;sessionId;versionNumber;message;dicardTime
                        String message = tokens[4];
                        String discardTimeStr = tokens[5];

                        DateFormat format = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH);
                        Date discardTime = format.parse(discardTimeStr);

                        SessionTable.sessionTable.updateSession(sessionId, versionNumber, message, discardTime);
                        outStr = callID;
                }

                byte[] outBuf = outStr.getBytes();
                DatagramPacket sendPkt = new DatagramPacket(outBuf, outBuf.length, returnAddr, returnPort);
                rpcSocket.send(sendPkt);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        (new Thread(new RPCServer())).start();
    }

}
