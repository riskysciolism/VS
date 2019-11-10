package abgabe1.aufgabe3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * configure UDP_SOCKET_PORT, TCP_SOCKET_PORT and TCP_BACKLOG
 * Receive message as TCP and broadcast it as UDP
 */
class UdpSender {
    private DatagramSocket socket;
    private Integer port;
    private InetAddress broadcastAddr;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private static final Integer UDP_SOCKET_PORT = 40000;
    private static final Integer TCP_SOCKET_PORT = 8000;
    private static final Integer TCP_BACKLOG = 8;

    private UdpSender(Integer port) {
        this(port, getBroadcastAddress());
    }

    private UdpSender(Integer port, InetAddress broadcastAddr) {
        try {
            this.socket = new DatagramSocket();
            this.port = port;
            this.broadcastAddr = broadcastAddr;
            if (this.broadcastAddr != null) {
                this.socket.connect(this.broadcastAddr, this.port);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    void send(String message) throws IOException {
        byte[] byteMessage = message.getBytes();
        this.socket.send(new DatagramPacket(byteMessage, byteMessage.length, this.broadcastAddr, this.port));
    }

    private static InetAddress getBroadcastAddress() {
        try {
            InetAddress localhost = Inet4Address.getLocalHost();
            return NetworkInterface.
                    getByInetAddress(localhost).
                    getInterfaceAddresses().
                    get(0).getBroadcast();

        } catch (UnknownHostException | SocketException e) {
            return null;
        }

    }

    /**
     * Start UDP Sender
     * Start TCP Receiver
     *
     * @param args
     */
    public static void main(String[] args) {
        //UDP
        UdpSender sender = new UdpSender(UDP_SOCKET_PORT);

        //TCP
        new TcpReceiver(TCP_SOCKET_PORT, TCP_BACKLOG, sender).start();
    }
}


class TcpReceiver {
    private int port;
    private int backlog;
    private UdpSender sender;

    public TcpReceiver(int port, int backlog, UdpSender sender) {
        this.port = port;
        this.backlog = backlog;
        this.sender = sender;
    }

    public void start() {
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
        SocketAddress socketAddress = null;
        try (Socket socket = server.accept();
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            socketAddress = socket.getRemoteSocketAddress();
            System.out.println("Verbindung zu " + socketAddress + " aufgebaut");
            int length;
            while ((length = in.read()) > 0) {
                String input = in.readLine().substring(0, length);
                System.out.println(socketAddress + ">> [" + input + "]");
                this.sender.send(input);
            }
        } catch (IOException e) {
            System.err.println(e);
        } finally {
            System.out.println("Verbindung zu " + socketAddress + " abgebaut");
        }
    }
}
