package com.example.das_entrega2.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.das_entrega2.R;

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
        String usudialogo = getString(R.string.usudialogo);
        String noexiste = getString(R.string.noexiste);
        String registro = getString(R.string.registrar);
        String si = getString(R.string.si);
        String no = getString(R.string.no);
        builder.setTitle(usudialogo + usuario + noexiste);
        builder.setMessage(registro);
        builder.setPositiveButton(si, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lo gestiona la actividad ActivityLogin mediante miListener
                miListener.alpulsarSi();
            }
        });

        builder.setNegativeButton(no, new DialogInterface.OnClickListener() {
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

