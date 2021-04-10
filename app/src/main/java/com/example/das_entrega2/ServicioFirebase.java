package com.example.das_entrega2;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class ServicioFirebase extends FirebaseMessagingService {

    private String precio;

    public ServicioFirebase() {
    }



    public void onMessageReceived(RemoteMessage remoteMessage) {
        if (remoteMessage.getData().size() > 0) {
            // si el mensaje lleva datos
            precio = remoteMessage.getData().get("precio");
            System.out.println("Precio recibido desde remoto: " + precio);

        }
        if (remoteMessage.getNotification() != null) {
            //si el mensaje es una notificacion

            NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal", NotificationManager.IMPORTANCE_DEFAULT);
                elManager.createNotificationChannel(elCanal);


                elCanal.setDescription("Descripción del canal");
                elCanal.enableLights(true);
                elCanal.setLightColor(Color.RED);
                elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
                elCanal.enableVibration(true);
            }


            if (remoteMessage.getNotification().getClickAction().equals("AVISO")) {
                //configurar notificacion
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("Precio del último pedido")
                        .setContentText("Precio del último pedido: " + precio + " €")
                        .setSubText("Ristorante Endika")
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true); //cancelar la notificacion al dar click;
            }
            /*
            else if (remoteMessage.getNotification().getClickAction().equals("MENSAJE")) {
                Intent i = new Intent(this, Actividad2.class);
                i.putExtra("texto", mensaje);
                PendingIntent intentenNot = PendingIntent.getActivity(this,0,i,PendingIntent.FLAG_UPDATE_CURRENT);


                //configurar notificacion
                elBuilder.setSmallIcon(android.R.drawable.stat_sys_warning)
                        .setContentTitle("Notificacion de mensaje enviado")
                        .setContentText("Se ha enviado un mensaje a todos los tokens")
                        .setSubText("ejer1_tema15")
                        .setVibrate(new long[]{0, 1000, 500, 1000})
                        .setAutoCancel(true)
                        .setContentIntent(intentenNot); //cancelar la notificacion al dar click;
            }

             */


            //lanzar notificacion
            elManager.notify(1, elBuilder.build());

        }

    }





}