package com.example.das_entrega2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ElAdaptadorRecycler extends RecyclerView.Adapter<ElViewHolder> {
    //Se crea una clase que extiende a la clase genérica RecyclerView.Adapter
    //Se usa la clase que extiende a ViewHolder
    private String[] losnombres;
    private int[] lasimagenes;
    private boolean[] seleccionados;


    public ElAdaptadorRecycler (String[] nombres, int[] imagenes)
    {
        //En el constructor se reciben los datos que se quieren mostrar en la lista
        //Se los asignamos a los atributos de la clase
        losnombres=nombres;
        lasimagenes=imagenes;
        seleccionados=new boolean[nombres.length];
    }

    @NonNull
    @Override
    public ElViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Infla el layout item_layout.xml definido para cada elemento. Crea y devuelve una instancia de
        //la clase que extiende a ViewHolder
        View elLayoutDeCadaItem= LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout,null);
        ElViewHolder evh = new ElViewHolder(elLayoutDeCadaItem);
        evh.seleccion = seleccionados;
        return evh;
    }

    @Override
    public void onBindViewHolder(@NonNull ElViewHolder holder, int position) {
        //Asigna a los atributos del ViewHolder los valores a mostrar para una posición concreta
        //En cada posicion asignara una imagen y un textView con el nombre de la categoria
        holder.eltexto.setText(losnombres[position]);
        holder.laimagen.setImageResource(lasimagenes[position]);

    }

    //Devuelve la cantidad de elementos mostrar
    @Override
    public int getItemCount() {
        return losnombres.length;
    }
}
