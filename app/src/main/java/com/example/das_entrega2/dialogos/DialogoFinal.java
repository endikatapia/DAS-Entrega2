package com.example.das_entrega2.dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.das_entrega2.R;

public class DialogoFinal extends DialogFragment {


    ListenerdelDialogo miListener;

    public interface ListenerdelDialogo {
        void alpulsarFinalizar();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        miListener =(ListenerdelDialogo) getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //para establecer los strings segun el idioma seleccionado en preferencias se usa: getString(R.string.finPedido);
        String fin = getString(R.string.finPedido);
        String pof = getString(R.string.finOpostre);
        String finzar = getString(R.string.finzar);
        String vps = getString(R.string.volverPostres);
        builder.setTitle(fin);
        builder.setMessage(pof);
        builder.setPositiveButton(finzar, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Lo gestiona la actividad ActivityPostre mediante miListener
                miListener.alpulsarFinalizar();

            }
        });

        builder.setNegativeButton(vps, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Al pulsar Volver a los postres no hace nada
            }
        });

        //Al pulsar fuera o al dar al boton de atras no se cancela el dialogo
        setCancelable(false);
        return builder.create();
    }
}
