package aufgabe2;

import java.io.IOException;
import java.net.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;


public class Sender {
    private DatagramSocket socket;
    private Integer port;
    private InetAddress broadcastAddr;
    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);
    private static final Integer SOCKET_PORT = 50000;

    public Sender(Integer port) {
        this(port, getBroadcastAddress());
    }

    public Sender(Integer port, InetAddress broadcastAddr) {
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

    private void send(String message) throws IOException {
        byte[] byteMessage = message.getBytes();
        this.socket.send(new DatagramPacket(byteMessage, byteMessage.length, this.broadcastAddr, this.port));
    }

    public void startScheduler() {
        SimpleDateFormat formatter= new SimpleDateFormat("HH:mm:ss");
        final Runnable timer = () -> {
            try {
                send(formatter.format(new Date(System.currentTimeMillis())));
                System.out.println("Send message..");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        final ScheduledFuture<?> timerHandler =
                scheduler.scheduleAtFixedRate(timer, 0, 20, TimeUnit.SECONDS);
    }

    public static InetAddress getBroadcastAddress() {
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

    public static void main(String[] args) {
        Sender sender = new Sender(SOCKET_PORT);
        sender.startScheduler();
    }
}
