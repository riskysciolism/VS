# Aufgabe 3
Prüfen und vergleichen Sie die Performanz von zwei verschiedenen Message-Brokern
bei unterschiedlichen Quality of Service (QoS) Levels. Verwenden Sie dazu die beiden öffentlich zugänglichen gehosteten MQTT-Broker mqtt.eclipse.org:1883 und
broker.hivemq.com:1883.
Entwickeln Sie eine Java-Klasse, die unter Verwendung der Paho-Library Nachrichten
veröffentlicht und empfängt. Messen Sie die Zeit, die zwischen dem Beginn des Sendens
und dem Ende des Empfangs einer bestimmten Nachricht vergeht. Ein Experimentdurchlauf soll aus 10 Wiederholungen dieses Ablaufs (Senden + Empfangen) bestehen.
Aus den zehn Zeitdauern sollen Durchschnitt und Standardabweichung der Zeitdauern
berechnet werden.
1. Geben Sie den Java-Quelltext für die Klasse in Moodle ab. 
2. Führen Sie die Messung für mqtt.eclipse.org:1883 mit QoS Level 0 durch. 
Geben Sie ein kurzes zusammenfassendes Protokoll Ihres Experiments über Moodle als
Text ab. Es sollen folgende Informationen enthalten sein:
    * Datum/Uhrzeit Beginn und Ende des Experiments
    * Beschreibung des Netzwerkes und Anschlusses des Rechners, auf dem Ihr Programm
ausgeführt wird.
    * Beschreibung der Nachricht (Format, Größe, Inhalt)
    * durchschnittliche Zeitdauer, Standardabweichung der Zeitdauern
3. Führen Sie die Messung für mqtt.eclipse.org:1883 mit QoS Level 2 durch und geben Sie das Protokoll über Moodle ab.
4. Führen Sie die Messung für broker.hivemq.com:1883 mit QoS Level 0 durch und geben Sie das Protokoll über Moodle ab.
5. Führen Sie die Messung für broker.hivemq.com:1883 mit QoS Level 2 durch und geben Sie das Protokoll über Moodle ab.