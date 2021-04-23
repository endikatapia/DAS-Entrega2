package com.example.das_entrega2;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class ServicioMusica extends Service {

    private final IBinder elBinder= new miBinder();
    private MediaPlayer mp;
    private int length;


    //onStartCommand pondrá el servicio de la música en marcha y lanzará una notificación
    //advirtiendo que el servicio se está ejecutando
    @Override
    public int onStartCommand(Intent intent, int flags, int startID){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager elmanager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel canalservicio = new NotificationChannel("IdCanal",
                    "NombreCanal",NotificationManager.IMPORTANCE_DEFAULT);
            elmanager.createNotificationChannel(canalservicio);
            Notification.Builder builder = new Notification.Builder(this, "IdCanal")
                    .setContentTitle(getString(R.string.app_name))
                    .setAutoCancel(false);
            Notification notification = builder.build();
            startForeground(1, notification);
        }

        //crear el MediaPlayer con la canción encontrada en raw/energy.mp3
        mp = MediaPlayer.create(this,R.raw.energy);
        //empezar el reproductor
        mp.start();
        //loopear la canción
        mp.setLooping(true);

        //Si el servicio se detiene, no se reinicia
        return START_NOT_STICKY;
    }


    //Hay que crear en el servicio una variable de tipo IBinder (Interfaz
    //de Binder), que contenga la instancia del servicio y devolver esa
    //variable en el método onBind
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return elBinder;
    }


    //Hay que crear en el servicio una clase que extienda de la clase
    //Binder y que tenga un método que devuelva la instancia del servicio
    public class miBinder extends Binder {
        public ServicioMusica obtenServicio(){
            return ServicioMusica.this;
        }

    }

    //cuando se haga onDestroy si el reproductor esta en marcha se detendrá
    @Override
    public void onDestroy() {
        if (mp!=null) {
            mp.stop();
            mp.release();
        }
    }

    //pausar el reproductor cuando recibamos una llamada
    public void pausa(){
        mp.pause();
        length=mp.getCurrentPosition();
    }

    //reanudar el reproductor después de atender la llamada
    public void reanudar() {
        mp.seekTo(length);
        mp.start();
    }


}
