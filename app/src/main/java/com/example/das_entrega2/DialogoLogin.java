package com.example.das_entrega2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoLogin extends DialogFragment {


    ListenerdelDialogo miListener;
    private String usuario;

    //constructor con el usuario
    public DialogoLogin(String user) {
        usuario=user;
    }

    public interface ListenerdelDialogo {
        void alpulsarSi();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        miListener =(ListenerdelDialogo) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //recogemos el usuario que nos viene de parametro desde el constructor
        //para establecer los strings segun el idioma seleccionado en preferencias se usa: getString(R.string.usudialogo);
        builder.setTitle("El usuario " + usuario + " no existe");
        builder.setMessage("¿Deseas Registrarte?");
        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lo gestiona la actividad ActivityLogin mediante miListener
                miListener.alpulsarSi();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Al pulsar NO no hace nada
            }
        });

        //Al pulsar fuera o al presionar el boton back no se cancela el dialogo
        setCancelable(false);
        //Cuando rotemos el dialogo setRetainInstance(true) para funcionamiento correcto
        setRetainInstance(true);
        return builder.create();
    }
}

