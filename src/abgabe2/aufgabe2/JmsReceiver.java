package abgabe2.aufgabe2;

import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class JmsReceiver implements MessageListener{
    private Connection connection;
    private Session session;
    private MessageConsumer consumer;

    JmsReceiver(String recDest) throws NamingException, JMSException {
        Context ctx = new InitialContext();
        ConnectionFactory factory = (ConnectionFactory) ctx.lookup("ConnectionFactory");
        this.connection = factory.createConnection();
        this.session = this.connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
        Destination destIn = (Destination) ctx.lookup(recDest);
        this.consumer = this.session.createConsumer(destIn);
        this.consumer.setMessageListener((MessageListener) this);
        this.connection.start();
    }

    /**
     * asynchronous message consumption
     *
     * @see javax.jms.MessageListener
     */
    @Override
    public void onMessage(Message message) {
        if (message instanceof TextMessage) {
            TextMessage textMessage = (TextMessage) message;
            try {
                System.out.println(textMessage.getText());
            } catch (JMSException e) {
                System.err.println(e);
            }
        } else {
            System.out.println(message);
        }
    }

    public static void main(String[] args) {
        try {
            JmsReceiver receiver = new JmsReceiver("2");
        } catch (NamingException | JMSException e) {
            e.printStackTrace();
        }
        while(true) {}
    }

}
