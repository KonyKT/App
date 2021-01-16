package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    private EditText ip, port;
    private Button connect;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Socket socket;
    private String ipaddress;
    private int portnum;
    private TextView link;
    private Pattern pattern;
    private Matcher matcher;
    private Handler handler;
    TextView text;
    EditText pAdress, pPort;
    Button buttonConnect;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.startup);

        text = (TextView) findViewById(R.id.textView);
        pAdress = (EditText) findViewById(R.id.ip_adress);
        pPort = (EditText) findViewById(R.id.port_adress);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        pAdress.setText("192.168.0.9");
        pPort.setText("5560");

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (buttonConnect.getText().toString().equalsIgnoreCase("Connect")) {
                    try {
                        text.setText("Gównojebane");
                        ipaddress = pAdress.getText().toString();
                        portnum = Integer.parseInt(pPort.getText().toString());
                        Client client = new Client(ipaddress, portnum);
                        client.start();
                        buttonConnect.setText("Disconnect");
                    } catch (NumberFormatException e) {
                        text.setText("Wyjebalo");
                    }
                } else {
                    buttonConnect.setText("Connect");
                    //changeSwitchesSatte(false);
                    closeConnection();
                }
            }
        });

    }


    private void closeConnection() {
        try {
            out.writeObject("close");
            out.close();
            // działaj kurwa
            in.close();
            socket.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }//end of closeConnection

    @Override
    protected void onStop() {
        super.onStop();
        closeConnection();
    }


    private class Client extends Thread {
        private String ipaddress;
        private int portnum;

        public Client(String ipaddress, int portnum) {
            this.ipaddress = ipaddress;
            this.portnum = portnum;
        }

        @Override
        public void run() {
            super.run();
            connectToServer(ipaddress, portnum);

        }


        @SuppressLint("SetTextI18n")
        public void connectToServer(String ip, int port) {

            try {
                socket = new Socket(InetAddress.getByName(ip), port);
                out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                out.writeObject("opened");
                in = new ObjectInputStream(socket.getInputStream());
                for (int i = 0; i < 1; i++) {
                    text.setText((String) in.readObject() + "\n");
                }
                handler.post(new Runnable() {
                    public void run() {
                        connect.setText("Close");
                    }
                });
            } catch (IOException e) {

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }

        }

    }//end of client class


    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        switch (id) {
            case R.id.joystickView6:
                System.out.println(xPercent);
                TextView num1View = (TextView) findViewById(R.id.textView7);
                TextView num2View = (TextView) findViewById(R.id.textView6);
                String mytext = Float.toString(xPercent);
                String mytext2 = Float.toString(yPercent);
                num2View.setText(mytext);
                num1View.setText(mytext2);

                break;
        }
    }

}