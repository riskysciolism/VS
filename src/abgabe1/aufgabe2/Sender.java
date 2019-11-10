package abgabe1.aufgabe2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private void startInput() {
        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(">> ");
                String msg = stdin.readLine();
                if ("q".equals(msg)) {
                    break;
                }
                send(msg);
                System.out.println("Send: '" + msg + "'");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startScheduler() {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        final Runnable timer = () -> {
            try {
                String msg = formatter.format(new Date(System.currentTimeMillis()));
                send(msg);
                System.out.println("Send: '" + msg + "'");
            } catch (IOException e) {
                e.printStackTrace();
            }
        };
        scheduler.scheduleAtFixedRate(timer, 0, 20, TimeUnit.SECONDS);
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

    public static void main(String[] args) {
        Sender sender = new Sender(SOCKET_PORT);
        System.out.println("Starte Timer[0] oder eigene Eingabe[1]?");
        System.out.print(">> ");
        try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
            String msg = stdin.readLine();
            switch (msg){
                case "1":
                    System.out.println("Starte eigene Eingabe...");
                    sender.startInput();
                    break;
                default:
                    System.out.println("Falsche Eingabe.");
                case "0":
                    System.out.println("Starte Timer...");
                    sender.startScheduler();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
