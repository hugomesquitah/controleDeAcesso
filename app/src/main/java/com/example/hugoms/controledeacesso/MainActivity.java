package com.example.hugoms.controledeacesso;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {

    MqttAndroidClient client;
    private EditText payload;
    private Button publishMessage, subscribe, unsubscribe, onButton, offButton;
    private TextView payloadArrived, statusLed;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        payload = (EditText) findViewById(R.id.payload);
        publishMessage = (Button) findViewById(R.id.publishMessage);
        subscribe = (Button) findViewById(R.id.subscribe);
        unsubscribe = (Button) findViewById(R.id.unsubscribe);
        onButton = (Button) findViewById(R.id.onButton);
        offButton = (Button) findViewById(R.id.offButton);
        payloadArrived = (TextView) findViewById(R.id.textView2);
        statusLed = (TextView) findViewById(R.id.statusLed);
        statusLed.setBackgroundColor(Color.DKGRAY);

        String clientId = MqttClient.generateClientId();
        final MqttAndroidClient client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://192.168.1.56:1883",
                        clientId);

        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "Conectado", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "Não Conectado", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }


        publishMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "MQTTHugoTesteAcceptionRecebe";
                String message = payload.getText().toString().trim();
                if(!message.isEmpty()){
                    try {
                        client.publish(topic,message.getBytes(),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        onButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "MQTTHugoTesteAcceptionRecebe";
                String message = "L";
                if(!message.isEmpty()){
                    try {
                        client.publish(topic,message.getBytes(),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "MQTTHugoTesteAcceptionRecebe";
                String message = "D";
                if(!message.isEmpty()){
                    try {
                        client.publish(topic,message.getBytes(),0,false);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
            }
        });


        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String topic = "MQTTHugoTesteAcceptionEnvia";
                int qos = 0;
                if(client.isConnected()) {
                    try {
                        IMqttToken subToken = client.subscribe(topic, qos);
                        subToken.setActionCallback(new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
                                // The message was published
                                Toast.makeText(MainActivity.this, "Inscrito no Tópico", Toast.LENGTH_SHORT).show();

                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken,
                                                  Throwable exception) {
                                // The subscription could not be performed, maybe the user was not
                                // authorized to subscribe on the specified topic e.g. using wildcards
                                Toast.makeText(MainActivity.this, "Não Inscrito no Tópico", Toast.LENGTH_SHORT).show();
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

            }
        });


        unsubscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String topic = "MQTTHugoTesteAcceptionEnvia";
                try {
                    IMqttToken unsubToken = client.unsubscribe(topic);
                    unsubToken.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // The subscription could successfully be removed from the client
                            Toast.makeText(MainActivity.this, "Não Inscrito no Tópico", Toast.LENGTH_SHORT).show();
                            statusLed.setBackgroundColor(Color.DKGRAY);
                            statusLed.setText("-------");
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken,
                                              Throwable exception) {
                            // some error occurred, this is very unlikely as even if the client
                            // did not had a subscription to the topic the unsubscribe action
                            // will be successfully
                            Toast.makeText(MainActivity.this, "Inscrito no Tópico", Toast.LENGTH_SHORT).show();
                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });


        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
                payloadArrived.setText(new String (message.getPayload()));
                String currentPayload = new String(message.getPayload());
                if(currentPayload.equals("L")) {
                    statusLed.setText("LIGADO");
                    statusLed.setBackgroundColor(Color.YELLOW);
                } else {
                    statusLed.setText("DESLIGADO");
                    statusLed.setBackgroundColor(Color.GRAY);
                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });













    }


}