package abgabe1.aufgabe1;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * Analyses the package loss and corrupted data between a sender and receiver
 * using the UDP protocol.
 *
 * The server awaits a connection from the client. He then counts and validates
 * incoming packets until the client signals to end the test (this signalling
 * message includes the number of packets send from the client). Afterwards the
 * server prints the results.
 */
public class Loss {
    public static void main(String[] args) {
        System.out.println("Selection (\"client\" / \"server\")");
        System.out.print(">> ");
        String selection = new Scanner(System.in).nextLine();

        if (selection.equals("server")) {
            new LossServer().start();
        } else if (selection.equals("client")) {
            new LossClient().start();
        }
    }
}

class LossClient {
    private static final String IP_SERVER = "192.168.0.38";
    private static final int PORT_SERVER  = 8080;
    private static final int PORT_CLIENT  = 8081;
    private static final int BUFSIZE      = 508;

    public void start() {
        System.out.print("How long should the test run (seconds): ");
        long testDurationMs = Integer.parseInt(new Scanner(System.in).nextLine()) * 1000;

        try (DatagramSocket socket = new DatagramSocket(PORT_CLIENT)) {
            InetAddress addressServer = InetAddress.getByName(IP_SERVER);
            DatagramPacket packetOut;

            System.out.println("Client starting ...");
            int packetCount = 0;
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() < startTime + testDurationMs) {
                packetOut = new DatagramPacket(new byte[BUFSIZE], BUFSIZE, addressServer, PORT_SERVER);
                socket.send(packetOut);
                packetCount += 1;

                if (packetCount % 100 == 0) System.out.println("Packages send: " + packetCount);

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            byte[] data = Integer.toString(packetCount).getBytes();
            packetOut = new DatagramPacket(data, data.length, addressServer, PORT_SERVER);
            socket.send(packetOut);
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
            socket.setSoTimeout(0);
            // The value we expect to receive from the client - all zeros - otherwise the packet got corrupted
            final byte[] byteArrayAllZeros = new byte[508];

            System.out.println("Server starting ...");
            int packetsReceived = 0;
            int packetsCorrupted = 0;

            while (true) {
                DatagramPacket packetIn = new DatagramPacket(new byte[BUFSIZE], BUFSIZE);
                socket.receive(packetIn);
                packetsReceived += 1;

                boolean messageReceivedTestFinished = packetIn.getLength() != BUFSIZE;
                if (messageReceivedTestFinished) {
                    int packetCount = Integer.parseInt(new String(packetIn.getData(), 0, packetIn.getLength()));
                    System.out.println("Send / received: " + packetsReceived + " / " + packetCount);
                    System.out.println("Packages corrupted: " + packetsCorrupted);
                } else if (!Arrays.equals(packetIn.getData(), byteArrayAllZeros)) {
                    packetsCorrupted += 1;
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
