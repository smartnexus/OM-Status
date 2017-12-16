package me.smartnexus.omstatus;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class httpConnection extends Service {

    Handler handler = new Handler();
    Runnable check;

    @Override
    public void onCreate() {
        Toast.makeText(this,"Servicio iniciado", Toast.LENGTH_SHORT).show();
        check = new Runnable() {
            @Override
            public void run() {
                if(!checkConnection()) {
                    NotificationManager mNotificationManager =
                            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    String id = "my_channel_01";
                    CharSequence name = "Orchesta Manager is Down!";
                    String description = "Something is going wrong with de application";
                    int importance = NotificationManager.IMPORTANCE_HIGH;
                    NotificationChannel mChannel = new NotificationChannel(id, name, importance);
                    mChannel.setDescription(description);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.enableVibration(true);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    mNotificationManager.createNotificationChannel(mChannel);
                }
            }
        };
    }

    @Override
    public int onStartCommand(Intent intenc, int flags, int idArranque) {
        Toast.makeText(this,"Servicio en funcionamiento "+ idArranque, Toast.LENGTH_SHORT).show();
        handler.postDelayed(check, 5000);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this,"Servicio detenido", Toast.LENGTH_SHORT).show();
        handler.removeCallbacks(check);
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

}
