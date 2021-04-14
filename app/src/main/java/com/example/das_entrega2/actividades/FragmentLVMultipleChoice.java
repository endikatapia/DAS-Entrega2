package com.example.das_entrega2.actividades;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.das_entrega2.R;

public class FragmentLVMultipleChoice extends Fragment {


    ListView lv;

    //listenerDelFragment se define en el fragment con todos los métodos que necesitemos
    public interface listenerDelFragment{
        void ponerLista();

    }
    private listenerDelFragment elListener;


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Hay que sobreescribir este metodo para enlazar FragmentLVMultipleChoice con su correspondiente XML --> fragmentlvmultiplechoice.xml
        //Este layout consta unicamente de la listView con checkBox multipleChoice.
        View v= inflater.inflate(R.layout.fragmentlvmultiplechoice,container,false);
        lv=v.findViewById(R.id.lv);
        return v;
    }


    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        //se ejecuta cuando se ha creado la actividad relacionada con ese fragment
        super.onActivityCreated(savedInstanceState);
        //La comunicación se hace mediante listeners definidos en los fragments e implementados en la Actividad
        //El metodo ponerLista esta implementado en ActivityComida y ActivityPostre
        elListener.ponerLista();
    }


    //Hay que unir el listener con los métodos implementados en la actividad
    //onAttach --> Momento que la actividad “se enlaza” con el fragment (con el contexto)
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            elListener=(listenerDelFragment) context;
        }
        catch (ClassCastException e){
            throw new ClassCastException("La clase " +context.toString()
                    + "debe implementar listenerDelFragment");
        }
    }
}
