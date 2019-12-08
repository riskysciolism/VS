package abgabe2.aufgabe1;

import org.eclipse.paho.client.mqttv3.*;

public class MqttReceiver {
    MqttReceiver() throws MqttException {
        MqttClient client = new MqttClient("tcp://localhost:1883", MqttClient.generateClientId());
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {}

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {}

            @Override
            public void messageArrived (String topic, MqttMessage m) throws Exception {
                System.out.println("Topic: " + topic + ",  Message: " + m.toString());
            }
        });
        client.connect();
        client.subscribe("JmsToMqtt");
    }

    public static void main(String[] args) {
        try {
            MqttReceiver mqttReceiver = new MqttReceiver();
        } catch (MqttException e) {
            e.printStackTrace();
        }
        while (true){}
    }
}
