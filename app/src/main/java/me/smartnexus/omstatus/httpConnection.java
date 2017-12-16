package me.smartnexus.omstatus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



public class httpConnection extends Service {

    Handler handler = new Handler();
    boolean checking = true;
    NotificationManager nm;

    @Override
    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("me.smartnexus.omstatus.ANDROID", "ANDROID CHANNEL", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);

        nm.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    while(true) {
                        if (!checkConnection()) {
                            alert();
                        }
                        sleep(5000);
                        handler.post(this);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

        Toast.makeText(this,"Servicio en funcionamiento "+ idArranque, Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Servicio detenido", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intencion) {
        return null;
    }

    private Boolean checkConnection() {
        StringBuilder result = new StringBuilder();
        URL url;
        HttpURLConnection conn;
        String line;
        try {
            url = new URL("http://vps487097.ovh.net/api/status");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }



        return result.toString().equals("true");
    }

    public void alert() {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this,"me.smartnexus.omstatus.ANDROID").setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("Orquesta Manager is Down!").setContentText("Something is wrong with the application");
        nm.notify(1, nb.build());
        checking = false;
    }
}
