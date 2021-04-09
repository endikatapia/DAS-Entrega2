package com.example.das_entrega2;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class DialogoPostre extends DialogFragment {


    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo {
        void alpulsarSi();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        miListener =(ListenerdelDialogo) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //para establecer los strings segun el idioma seleccionado en preferencias se usa: getString(R.string.usudialogo);
        String cp = getString(R.string.continuarPedido);
        String poc = getString(R.string.postreOcarta);
        String pos = getString(R.string.postres);
        String vc = getString(R.string.volverCarta);
        builder.setTitle(cp);
        builder.setMessage(poc);
        builder.setPositiveButton(pos, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lo gestiona la actividad MainActivity mediante miListener
                miListener.alpulsarSi();

            }
        });

        builder.setNegativeButton(vc, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Al pulsar volver a la carta NO hace nada
            }
        });

        //Al pulsar fuera o al dar al boton de atras no se cancela el dialogo
        setCancelable(false);
        return builder.create();
    }
}
