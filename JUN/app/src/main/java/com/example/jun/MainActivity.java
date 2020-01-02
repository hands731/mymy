package com.example.jun;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.HandlerThread;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    TextView state;
    EditText uvText, temperatureText, humidyText, dustext;
    Button configBtn, F5Btn;
    Switch airCondition, airCleaner, humidy, cutton, onOff;


    Thread thread;
    Socket socket = null;
    int temperatureValue, humidyValue, uvValue =0;
    double dustValue = 0.0;
    int cuttonState = 0;
    int HumidyState = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //앱 기본 스타일 설정
        getSupportActionBar().setElevation(0);


        uvText = (EditText) findViewById(R.id.uvEdit);
        temperatureText = (EditText) findViewById(R.id.temperatureEdit);
        humidyText = (EditText) findViewById(R.id. humidyEdit);
        dustext = (EditText) findViewById(R.id.dustEdit);

        configBtn = (Button) findViewById(R.id.config);
        onOff = (Switch)findViewById(R.id.OnOff);
        F5Btn = (Button) findViewById(R.id.F5Button);
        airCondition = (Switch) findViewById(R.id.airCondition);
        airCleaner = (Switch) findViewById(R.id.airCleaner);
        state = (TextView) findViewById(R.id.state);
        humidy = (Switch) findViewById(R.id.humidy);
        cutton = (Switch) findViewById(R.id.cutton);



        configBtn.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        airCondition.setChecked(false);
                        airCleaner.setChecked(false);
                        humidy.setChecked(false);
                        cutton.setChecked(false);
                    }
                });

                if(!onOff.isChecked()){
                }else{
                temperatureValue=Integer.parseInt(temperatureText.getText().toString());
                humidyValue = Integer.parseInt(humidyText.getText().toString());
                uvValue = Integer.parseInt(uvText.getText().toString());
                dustValue = Double.parseDouble(dustext.getText().toString());


                System.out.println("손준손준손준손준 temp : "+temperatureValue+"  humidy : "+humidyValue+"  uvValue : "+uvValue+" dustValue : "+dustValue );
                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "loading...");
                myClientTask.execute();
                }

                //messageText.setText("");
            }
        });
        //connect 버튼 클릭
        onOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!onOff.isChecked()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            airCondition.setChecked(false);
                            airCleaner.setChecked(false);
                            humidy.setChecked(false);
                            cutton.setChecked(false);
                        }
                    });
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "off");
                    myClientTask.execute();


                }
                else{

                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346 , "loading...");
                    myClientTask.execute();
                }

                //messageText.setText("");
            }
        });

        airCleaner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!airCleaner.isChecked()) {
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airCleanerOff");
                    myClientTask.execute();
                }
                else{
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airCleanerOn");
                    myClientTask.execute();
                }

                //messageText.setText("");
            }
        });

        cutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!cutton.isChecked()) {
                    if(cuttonState ==1) {
                        cuttonState = 0;
                        MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "cuttonOff");
                        myClientTask.execute();

                    }
                    }
                else{
                    if(cuttonState==0) {
                        cuttonState = 1;
                        MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "cuttonOn");
                        myClientTask.execute();

                    }
                    }

                //messageText.setText("");
            }
        });

        humidy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!humidy.isChecked()) {
                    if(HumidyState==1){
                        HumidyState=0;
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "humidyOff");
                    myClientTask.execute();
                }
                }
                else{
                    if(HumidyState==0){
                    HumidyState=1;
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "humidyOn");
                    myClientTask.execute();
                }}

                //messageText.setText("");
            }
        });

        airCondition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!airCondition.isChecked()) {
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airConditionOff");
                    myClientTask.execute();
                }
                else{
                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airConditionOn");
                    myClientTask.execute();
                }

                //messageText.setText("");

            }

        });


        //F5 버튼 클릭
        F5Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                    MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "loading...");
                    myClientTask.execute();
                    //   recieveText.setText("");
                    //    messageText.setText("");

            }
        });
    }

    //
    public class MyClientTask extends AsyncTask<Void, Void, Void> {
        String dstAddress;
        int dstPort;
        String response = "";
        String myMessage = "";



        //constructor
        MyClientTask(String addr, int port, String message) {
            dstAddress = addr;
            dstPort = port;
            myMessage = message;
        }

        public void protocol(final String pro){
            String result = "";



                        String proto[];
                        String myProto[];
                        int temp, hum, uv = 0;
                        double dust = 0.0;



                        if (pro.contains("Temperature")&&!pro.contains("&")) {
                            proto = pro.split(":");
                            myProto = proto[4].split("'C");
                            System.out.println("너의 이름은 :" + myProto[0] + "이다");
                            temp = Integer.parseInt(myProto[0]);
                            myProto = proto[5].split("%");
                            hum = Integer.parseInt(myProto[0]);
                            myProto = proto[6].split("mg");
                            dust = Double.parseDouble(myProto[0]);
                            myProto = proto[8].split("Level");
                            System.out.println("uv :" + myProto[0] + "이다");
                            uv = Integer.parseInt(myProto[0]);
                            System.out.println("temp :" + temp + "이다");
                            System.out.println("humidy :" + hum + "이다");
                            System.out.println("dust :" + dust + "이다");
                            System.out.println("uv :" + uv + "이다");
                            if (temp >= temperatureValue) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {

                                            airCondition.setChecked(true);

                                    }
                                });

                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airConditionOn");
                                myClientTask.execute();

                        }
                            if(hum <=humidyValue){


                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        humidy.setChecked(true);
                                    }
                                });
                                HumidyState = 1;
                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "humidyOn");
                                myClientTask.execute();



                            }else{
                                if(HumidyState == 1){

                                    HumidyState = 0;
                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "humidyOff");

                                myClientTask.execute();}
                            }

                            if(dust>=dustValue){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        airCleaner.setChecked(true);
                                    }
                                });

                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airCleanerOn");
                                myClientTask.execute();
                            }else{
                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "airCleanerOff");
                                myClientTask.execute();
                            }

                            if(uv>=uvValue){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        cutton.setChecked(true);
                                    }
                                });

                                if(cuttonState == 0){
                                    cuttonState = 1;
                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "cuttonOn");
                                myClientTask.execute();

                            }}
                            else{
                                if(cuttonState ==1){
                                    cuttonState = 0;
                                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346, "cuttonOff");
                                myClientTask.execute();

                                }}




                    }




            if(pro.equals("loading...")){
                MyClientTask myClientTask = new MyClientTask("192.168.0.26", 12346 , "loading...");
                myClientTask.execute();
            }


            //return result;
        }


        @Override
        protected Void doInBackground(Void... arg0) {

            Socket socket = null;
            myMessage = myMessage.toString();
            try {
                socket = new Socket(dstAddress, dstPort);
                //송신
                OutputStream out = socket.getOutputStream();
                System.out.println();
                out.write(myMessage.getBytes());

                //수신
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int bytesRead;
                InputStream inputStream = socket.getInputStream();
                /*
                 * notice:
                 * inputStream.read() will block if no data return
                 */
                while ((bytesRead = inputStream.read(buffer)) != -1){
                    byteArrayOutputStream.write(buffer, 0, bytesRead);
                    response += byteArrayOutputStream.toString("UTF-8");

                  protocol(response);



                }
                System.out.println(response+"아 시발련아 떠라좀");



            } catch (UnknownHostException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "UnknownHostException: " + e.toString();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                response = "IOException: " + e.toString();
            }finally{
                if(socket != null){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }


        protected void onPostExecute(Void result) {
            String m[];
            m=response.split("&");

            state.setText(m[0]);
            super.onPostExecute(result);
        }
    }

}

