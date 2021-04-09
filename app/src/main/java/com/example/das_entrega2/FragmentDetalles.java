package com.example.das_entrega2;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FragmentDetalles extends Fragment {



    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Hay que sobreescribir este metodo para enlazar FragmentDetalles con su correspondiente XML --> fragmentdetalles.xml
        View v= inflater.inflate(R.layout.fragmentdetalles,container,false);
        return v;
    }

    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }


    public void setDatos(String elemento, int imageCode, String ingredientes, double precio) {
        //este metodo se encargara de actualizar los datos recibidos desde ActivityDetalles y
        //visualizara para cada plato su nombre, imagen, ingredientes y precio.
        TextView nombre= getView().findViewById(R.id.nombre);
        nombre.setText(elemento);

        ImageView imagen = getView().findViewById(R.id.imagenV);
        imagen.setImageResource(imageCode);

        TextView ing= getView().findViewById(R.id.ingredientes);
        String in = getString(R.string.ingredientes);
        ing.setText(in + ingredientes);

        TextView precios= getView().findViewById(R.id.precios);
        String pr = getString(R.string.precio);
        precios.setText(pr + precio + "€");

    }




}
