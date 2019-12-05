package abgabe2.aufgabe1;

import javax.jms.*;
import javax.naming.*;
import java.util.Date;

public class JmsPublisher {
    private Connection connection;
    private Session session;
    private MessageProducer producer;

    JmsPublisher(String sendDest) throws NamingException, JMSException {
        Context ctx = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
        this.connection = factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destOut = (Destination) ctx.lookup(sendDest);
        this.producer = this.session.createProducer(destOut);
    }

    void publish() {
        try {
            while (true) {
                System.out.println("Sending message..");
                this.producer.send(this.session.createTextMessage(new Date().toString()));
                Thread.sleep(100);
            }
        } catch (JMSException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            JmsPublisher node = new JmsPublisher("1");
            node.publish();
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
    }


}
