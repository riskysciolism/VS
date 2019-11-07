package abgabe1.aufgabe3;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Receive message as TCP and broadcast it as UDP
 */
public class UdpSender {
    UdpSender(){

    }

    public void sendMessage(String message){

    }

    /**
     * Start UDP Sender
     * Start TCP Receiver
     * @param args
     */
    public static void main(String[] args) {
        //UDP
        UdpSender udp = new UdpSender();

        //TCP
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        TcpReceiver tcp = new TcpReceiver(host, port, udp);
    }
}


class TcpReceiver {
    TcpReceiver(String host, int port, UdpSender sender) {
        start(host, port, sender);
    }

    private void start(String host, int port, UdpSender sender) {
        try (Socket socket = new Socket(host, port);
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
             BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {

            // Begrüßung vom Server empfangen und auf Konsole ausgeben
            String msg = in.readLine();
            sender.sendMessage(msg);

        } catch (Exception e) {
            System.err.println(e);
        }
    }
}
