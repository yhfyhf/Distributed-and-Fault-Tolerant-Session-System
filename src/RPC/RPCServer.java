package RPC;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * Created by yhf on 4/9/16.
 */
public class RPCServer implements Runnable {

    public void run() {
        try {
            DatagramSocket rpcSocket = new DatagramSocket(Conf.PORT_PROJ1B_RPC);

            while (true) {
                byte[] inBuf = new byte[Conf.MAX_PACKET_SIZE];
                DatagramPacket recvPkt = new DatagramPacket(inBuf, inBuf.length);
                rpcSocket.receive(recvPkt);
                InetAddress returnAddr = recvPkt.getAddress();
                int returnPort = recvPkt.getPort();
            }
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        (new Thread(new RPCServer())).start();
    }

}
