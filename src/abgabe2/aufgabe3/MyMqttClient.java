package abgabe2.aufgabe3;

import org.eclipse.paho.client.mqttv3.*;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MyMqttClient {
    private org.eclipse.paho.client.mqttv3.MqttClient client;
    // hivemq = tcp://broker.hivemq.com:1883
    // eclipse = tcp://mqtt.eclipse.org:1883
    private final static  String BROKER = "tcp://broker.hivemq.com:1883";
    private final static int QOS = 2;
    public final static String topic = "performanceTest";
    static List<Double> durations = new ArrayList<>();

    private MyMqttClient() throws MqttException {
        this.client = new org.eclipse.paho.client.mqttv3.MqttClient(BROKER, org.eclipse.paho.client.mqttv3.MqttClient.generateClientId());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}

            @Override
            public void messageArrived (String topic, MqttMessage m) throws Exception {
                long deltaTime = Duration.between(Instant.parse(m.toString()), Instant.now()).toMillis();
                //System.out.println("Topic: " + topic + ",  Message: " + m.toString() + ", deltaTime: " + deltaTime);
                durations.add((double) deltaTime);
            }
        });
        this.client.connect();
        this.client.subscribe(topic);
    }

    void publish(String message, Integer qos) throws MqttException {
        System.out.println("Sending message..");
        this.client.publish(topic, message.getBytes(), qos, false);
    }

    public static double mean(List<Double> a) {
        double sum = 0.0;
        for (Double aDouble : a) {
            sum = sum + aDouble;
        }
        return sum / a.size();
    }

    public static double var(List<Double> a) {
        double m = mean(a);
        double sum = 0.0;
        for (Double aDouble : a) {
            sum = sum + (aDouble - m) * (aDouble - m);
        }
        return sum / (a.size() - 1);
    }

    public static double standardDeviation(List<Double> a) {
        return Math.sqrt(var(a));
    }

    public static void main(String[] args) {
        int packageSize = 0;
        Date startTime, endTime;
        try {
            MyMqttClient myMqttClient = new MyMqttClient();

            startTime = new Date();
            for (int i = 0; i < 10; i++) {
                String message = Instant.now().toString();
                myMqttClient.publish(message, QOS);
                packageSize = message.getBytes().length;
                Thread.sleep(1000);
            }
            endTime =  new Date();

            System.out.println("-------------------------------------------------------");
            System.out.println("Start der Messung: " + startTime);
            System.out.println("Ende der Messung: " + endTime);
            System.out.println("Netzwerk: Heimnetzwerk");
            System.out.println("Art des Anschlusses: WLAN");
            System.out.println("Größe der Nachricht: " + packageSize + " Bytes");
            System.out.println("Inhalt der Nachricht: java.time.Instant.now()");
            System.out.println("Quality of service: " + QOS);
            System.out.println("Broker: " + BROKER);
            System.out.println("Durchschnittliche Empfangsdauer: " + mean(durations) + "ms");
            System.out.println("Standardabweichung: " + standardDeviation(durations) + "ms");
            System.out.println("-------------------------------------------------------");
        } catch (MqttException | InterruptedException e) {
            e.printStackTrace();
        }


    }

}
