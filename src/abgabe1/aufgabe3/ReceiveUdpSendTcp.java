package abgabe1.aufgabe3;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * configure UDP_SOCKET_PORT, BUFFER_SIZE, TCP_SOCKET_PORT and TCP_HOST
 * Start UdpSender first
 * Receive broadcast message as UPD and send message as TCP
 */
class UdpReceiver {

    private DatagramSocket udp_socket;
    private static final Integer UDP_SOCKET_PORT = 50000;
    private static final Integer BUFFER_SIZE = 1024;
    private static final Integer TCP_SOCKET_PORT = 8000;
    private static final String TCP_HOST = "0.0.0.0";

    private UdpReceiver(Integer port) {
        try {
            this.udp_socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void listenUDP(TcpClient tcpSender) {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        try {
            System.out.println("Listening..");
            while (true) {
                this.udp_socket.receive(dp);
                String msg = new String(dp.getData(), 0, dp.getLength());
                System.out.println("UDP listener: " + msg);
                tcpSender.sendMessage(msg);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //TCP
        TcpClient tcp = new TcpClient(TCP_HOST,TCP_SOCKET_PORT);

        //UDP
        UdpReceiver receiver = new UdpReceiver(UDP_SOCKET_PORT);
        receiver.listenUDP(tcp);
    }
}


class TcpClient {
    private PrintWriter outWriter = null;

    TcpClient(String host,int port) {
        try {
            Socket socket = new Socket(host, port);
            this.outWriter =  new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void sendMessage(String output) {
        System.out.println(">> [" + output + "]");
        this.outWriter.write(output.length());
        this.outWriter.println(output);
        this.outWriter.flush();
    }
}
