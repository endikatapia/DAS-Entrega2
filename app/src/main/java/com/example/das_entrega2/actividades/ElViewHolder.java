package com.example.das_entrega2.actividades;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.das_entrega2.R;
import com.example.das_entrega2.actividades.ActivityComida;

public class ElViewHolder extends RecyclerView.ViewHolder {
    public TextView eltexto;
    public ImageView laimagen;
    public boolean[] seleccion;

    public ElViewHolder(@NonNull View itemView) {
        super(itemView);
        //En el constructor de la clase se hace la asociación entre los campos de la clase y los elementos gráficos del layout
        //En esta aplicacion cada CardView consta de la imagen de la comida y el nombre de la misma.
        eltexto = itemView.findViewById(R.id.texto);
        laimagen = itemView.findViewById(R.id.foto);

        //Se define un listener para cada itemView y se gestiona qué se quiere hacer al seleccionar cada elemento
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //creamos el intent para ir a la actividad que mostrara la lista de los platos de la categoria seleccionada
                Intent i1 = new Intent(itemView.getContext(), ActivityComida.class);
                //Cuando se haga click en un cardView se guardara la posicion seleccionada de esa categoria
                //mas tarde se usara ese numero para establecer la lista de platos de esa categoria
                //mediante el metodo ponerLista() de ActivityComida
                if(getAdapterPosition()==0) {
                    i1.putExtra("categoria",0);
                    itemView.getContext().startActivity(i1);
                }else if (getAdapterPosition()==1) {
                    i1.putExtra("categoria",1);
                    itemView.getContext().startActivity(i1);
                }
                else if (getAdapterPosition()==2) {
                    i1.putExtra("categoria",2);
                    itemView.getContext().startActivity(i1);
                }
                else if (getAdapterPosition()==3) {
                    i1.putExtra("categoria",3);
                    itemView.getContext().startActivity(i1);
                }
                else if (getAdapterPosition()==4) {
                    i1.putExtra("categoria",4);
                    itemView.getContext().startActivity(i1);
                }


            }
        });




    }

}

