package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;
import android.annotation.TargetApi;
import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import java.io.DataOutputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    WebView mWebview ;
    private EditText ip, port;
    private Button connect;
    private ObjectInputStream in;
    private ObjectOutputStream outx;
    OutputStream outputStream ;
    DataOutputStream dataOutputStream;
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
    Button buttonMrug;
    TextView num1View;
    TextView num2View;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JoystickView joystick = new JoystickView(this);
        setContentView(R.layout.startup);
        int SDK_INT = android.os.Build.VERSION.SDK_INT;
        if (SDK_INT > 8)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                    .permitAll().build();
            StrictMode.setThreadPolicy(policy);
            //your codes here

        }
        text = (TextView) findViewById(R.id.textView);
        num1View = (TextView) findViewById(R.id.textView7);
        num2View = (TextView) findViewById(R.id.textView6);
        pAdress = (EditText) findViewById(R.id.ip_adress);
        pPort = (EditText) findViewById(R.id.port_adress);
        buttonConnect = (Button) findViewById(R.id.connectButton);
        buttonMrug = (Button) findViewById(R.id.buttonMrug);
        pAdress.setText("192.168.0.9");
        pPort.setText("5560");
        buttonMrug.setText("OPEN EYES");
        mWebview  = new WebView(this);

        mWebview = (WebView)findViewById(R.id.webview);
        mWebview.getSettings().setJavaScriptEnabled(true);

        buttonConnect.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (buttonConnect.getText().toString().equalsIgnoreCase("Connect")) {
                    try {
                        num2View.setText("testujemy");
                        num1View.setText("tutaj");
                        ipaddress = pAdress.getText().toString();
                        portnum = Integer.parseInt(pPort.getText().toString());
                        Client client = new Client(ipaddress, portnum);
                        client.start();
                        Thread.sleep(1000);
                        if(socket != null) {
                            mWebview.loadUrl("http://192.168.0.9:5000");
                        }

                    } catch (NumberFormatException | InterruptedException e) {
                        text.setText("Wyjebalo");
                    }
                } else {
                    buttonConnect.setText("Connect");
                    mWebview.loadUrl("about:blank");
                    //changeSwitchesSatte(false);
                    closeConnection();
                }
            }
        });

        buttonMrug.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                if (buttonMrug.getText().toString().equalsIgnoreCase("CLOSE EYES")) {
                    try {
                        if(socket != null) {
                            try {
                                String strng = "4,"+"000,"+"000" ;
                                dataOutputStream.writeUTF(strng);
                                dataOutputStream.flush(); // send the message
                                Thread.sleep(40);
                                buttonMrug.setText("OPEN EYES");
                            } catch (IOException | InterruptedException e) {

                            }
                        }
                    } catch (NumberFormatException e) {
                        text.setText("Wyjebalo");
                    }
                } else {
                    if(socket != null) {
                        try {
                            String strng = "5,"+"000,"+"000" ;
                            dataOutputStream.writeUTF(strng);
                            dataOutputStream.flush(); // send the message
                            Thread.sleep(40);
                            buttonMrug.setText("CLOSE EYES");
                        } catch (IOException | InterruptedException e) {

                        }
                    }
                }
            }
        });

    }



    @SuppressLint("SetTextI18n")
    private void closeConnection() {
        try {
            outputStream = socket.getOutputStream();
            dataOutputStream = new DataOutputStream(outputStream);
            dataOutputStream.writeUTF("2,Bye!");
            dataOutputStream.flush(); // send the message
            dataOutputStream.close(); // close the output stream when we're done.
            //out.writeObject("close");
            //out.close();
            // działaj kurw
            //in.close();
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
                outputStream = socket.getOutputStream();
                dataOutputStream = new DataOutputStream(outputStream);
                dataOutputStream.writeUTF("0,Hello from the other side!");
                dataOutputStream.flush(); // send the message
                buttonConnect.setText("Disconnect");
                //out = new ObjectOutputStream(socket.getOutputStream());
                //out.flush();
                //out.writeObject("opened");
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



    public void onJoystickMoved(float xPercent, float yPercent, int id) throws IOException {
                System.out.println(xPercent);
                String mytext = Float.toString(xPercent);
                String mytext2 = Float.toString(yPercent);
                num2View.setText(mytext);
                num1View.setText(mytext2);
                if(socket != null) {
                    try {
                        String strng = "1," + xPercent + "," + yPercent;
                        dataOutputStream.writeUTF(strng);
                        dataOutputStream.flush(); // send the message
                        Thread.sleep(40);
                    } catch (IOException | InterruptedException e) {

                    }
                }


    }
    }

