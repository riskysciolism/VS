package abgabe1.aufgabe3;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Receive broadcast message as UPD and send message as TCP
 */
public class UdpReceiver {

    DatagramSocket socket;
    static final Integer SOCKET_PORT = 9000;
    static final Integer BUFFER_SIZE = 256;

    private UdpReceiver(Integer port) {
        try {
            this.socket = new DatagramSocket(port);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    private void listenUDP(TcpServer tcpSender) {
        byte[] buffer = new byte[BUFFER_SIZE];
        DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
        try {
            while (true) {
                this.socket.receive(dp);
                tcpSender.sendMessage(new String(dp.getData(), StandardCharsets.UTF_8));
                System.out.println("UDP Receiver: " + new String(dp.getData(), StandardCharsets.UTF_8));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //TCP
        int port = Integer.parseInt(args[0]);
        int backlog = 50;
        if (args.length == 2) {
            backlog = Integer.parseInt(args[1]);
        }

        TcpServer tcp = new TcpServer(port, backlog);

        //UDP
        UdpReceiver receiver = new UdpReceiver(SOCKET_PORT);
        receiver.listenUDP(tcp);
    }
}


class TcpServer {
    private int port;
    private int backlog;
    private PrintWriter outWriter = null;
    private SocketAddress socketAddress = null;

    TcpServer(int port, int backlog) {
        this.port = port;
        this.backlog = backlog;

        start();
    }

    private void start() {
        try (ServerSocket serverSocket = new ServerSocket(port, backlog)) {
            System.out.println("EchoServer (iterativ) auf " + serverSocket.getLocalSocketAddress() + " gestartet ...");
            while (true) {
                handleClient(serverSocket);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    private void handleClient(ServerSocket server) {
        try (Socket socket = server.accept();
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {
            outWriter = out;
            socketAddress = socket.getRemoteSocketAddress();
            System.out.println("Verbindung zu " + socketAddress + " aufgebaut");
            outWriter.println("Server ist bereit ...");
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            System.out.println("Verbindung zu " + socketAddress + " abgebaut");
        }
    }

    void sendMessage(String output) {
        System.out.println(socketAddress + ">> [" + output + "]");
        outWriter.println("echo: " + output);
    }
}
