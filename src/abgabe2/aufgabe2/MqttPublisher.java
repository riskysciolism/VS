package abgabe2.aufgabe2;

import org.eclipse.paho.client.mqttv3.*;

import java.util.Date;

public class MqttPublisher {
    private MqttClient client;
    private MqttPublisher() throws MqttException {
        this.client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
    }

    void publish() throws MqttException {
        this.client.connect();
        MqttMessage message = new MqttMessage();

        while(true) {
            System.out.println("Sending message..");
            message.setPayload(new Date().toString().getBytes());
            this.client.publish("baboChannel", message);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        try {
            MqttPublisher mqttPublisher = new MqttPublisher();
            mqttPublisher.publish();
        } catch (MqttException e) {
            e.printStackTrace();
        }

    }
}
