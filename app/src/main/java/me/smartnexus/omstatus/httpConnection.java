package me.smartnexus.omstatus;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static me.smartnexus.omstatus.NotificationUtils.ANDROID_CHANNEL_ID;

public class httpConnection extends Service {

    boolean checking = true;
    NotificationManager nm;

    @Override
    public void onCreate() {
        nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel("me.smartnexus.omstatus.ANDROID", "ANDROID CHANNEL", NotificationManager.IMPORTANCE_HIGH);
        channel.enableLights(true);
        channel.enableVibration(true);
        channel.setLightColor(Color.GREEN);

        nm.createNotificationChannel(channel);
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        while (checking) {
            if (checkConnection()) {
                alert();
            }
        }

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
            return false;
        }

        Toast.makeText(this,"Comprobando conexión...",
                Toast.LENGTH_SHORT).show();

        return result.toString().equals("true");
    }

    public void alert() {
        NotificationCompat.Builder nb = new NotificationCompat.Builder(this,"me.smartnexus.omstatus.ANDROID").setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("Orquesta Manager is Down!").setContentText("Something is wrong with the application");
        nm.notify(1, nb.build());
        checking = false;
    }
}
