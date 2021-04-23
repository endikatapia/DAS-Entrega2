package com.example.das_entrega2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class LlamadasReceiver extends BroadcastReceiver {

    TelephonyManager telManager;
    ServicioMusica elservicio;




    @Override
    public void onReceive(Context context, Intent intent) {

        telManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        telManager.listen(phoneListener, PhoneStateListener.LISTEN_CALL_STATE);
        //Para poder acceder a un servicio desde un BroadcastReceiver se usa el método
        //peekService(...), que sólo se puede usar si el servicio está previamente asociado (bind) a
        //una actividad de la aplicación.
        ServicioMusica.miBinder binder = (ServicioMusica.miBinder) peekService(context, new Intent(context, ServicioMusica.class));
        elservicio = binder.obtenServicio();
        //ya puedo llamar a los métodos del servicio
    }




    private final PhoneStateListener phoneListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            try {
                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING: {
                        //Teléfono sonando
                        System.out.println("Telefono sonando");
                        //pausar la musica
                        elservicio.pausa();
                        break;
                    }
                    case TelephonyManager.CALL_STATE_OFFHOOK: {
                        //Teléfono descolgado
                        System.out.println("Telefono descolgado");
                        break;
                    }
                    case TelephonyManager.CALL_STATE_IDLE: {
                        //Teléfono inactivo
                        System.out.println("Telefono inactivo");
                        //reanudar la musica
                        elservicio.reanudar();
                        break;
                    }
                    default: { }
                }
            } catch (Exception ex) {

            }
        }
    };



}
