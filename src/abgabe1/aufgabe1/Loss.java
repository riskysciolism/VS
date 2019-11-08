package one;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.sql.SQLOutput;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Analyses the package loss and corrupted data between a sender and receiver using the UDP protocol.
 */
public class Loss {
    public static void main(String[] args) {
        String selection = "";
        if (args.length == 0) {
            System.out.println("Selection (\"client\" / \"server\")");
            System.out.print(">> ");
            selection = new Scanner(System.in).nextLine();
        }

        if (selection.equals("server")) {
            new LossServer().start();
        } else if (selection.equals("client")) {
            new LossClient().start();
        }
    }
}

class LossClient {
    private static final int PORT_CLIENT = 8081;
    private static final int BUFSIZE = 508;
    private static final String IP_SERVER = "192.168.0.38";
    private static final int PORT_SERVER = 8080;

    public void start() {
        System.out.print("How long should the test run (seconds): ");
        long timeframe = Integer.parseInt(new Scanner(System.in).nextLine()) * 1000;

        try (DatagramSocket socket = new DatagramSocket(PORT_CLIENT)) {
            InetAddress addressServer = InetAddress.getByName(IP_SERVER);
            DatagramPacket packageOut;

            System.out.println("Client starting ...");
            int packagesSend = 0;
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() < startTime + timeframe) {
                packageOut = new DatagramPacket(new byte[BUFSIZE], BUFSIZE, addressServer, PORT_SERVER);
                socket.send(packageOut);
                packagesSend += 1;

                if (packagesSend % 100 == 0) System.out.println("Packages send: " + packagesSend);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            byte[] data = Integer.toString(packagesSend).getBytes();
            packageOut = new DatagramPacket(data, data.length, addressServer, PORT_SERVER);
            socket.send(packageOut);
        } catch (final IOException e) {
            System.err.println(e);
        }

        System.out.println("Client shutting down ...");
    }
}

class LossServer {
    private static final int PORT = 8080;
    private static final int BUFSIZE = 508;

    public void start() {
        try (DatagramSocket socket = new DatagramSocket(PORT)) {
            // TODO Useful value
            socket.setSoTimeout(0);

            System.out.println("Server starting ...");
            byte[] emptyPackage = new byte[508];
            int packagesReceived = 0;
            int packagesCorrupted = 0;

            while (true) {
                DatagramPacket packetIn = new DatagramPacket(new byte[BUFSIZE], BUFSIZE);
                socket.receive(packetIn);
                packagesReceived += 1;

                if (packetIn.getLength() != BUFSIZE) {
                    int packagesSend = Integer.parseInt(new String(packetIn.getData(), 0, packetIn.getLength()));
                    System.out.println("Send / received: " + packagesReceived + " / " + packagesSend);
                    System.out.println("Packages corrupted: " + packagesCorrupted);
                } else if (!Arrays.equals(packetIn.getData(), emptyPackage)) {
                    packagesCorrupted += 1;
                }
            }
        } catch (SocketTimeoutException e) {
            System.err.println("Timeout: " + e.getMessage());
        } catch (Exception e) {
            System.err.println(e);
        }
        System.out.println("Server shutting down ...");
    }
}