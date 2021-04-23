package com.example.das_entrega2.actividades;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_entrega2.R;
import com.example.das_entrega2.workers.ConexionBDGetFotos;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Locale;

public class ActivityFotos extends AppCompatActivity {


    private ListView lvfotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //establecer idioma seleccionado en las preferencias (por defecto: castellano)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());


        setContentView(R.layout.activity_fotos);


        //toast diciendo que las imagenes pueden tardar en cargar desde firebase
        //TOAST PERSONALIZADO con layout_toast.xml
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

        String cargalistaFirebase = getString(R.string.cargalistaFirebase);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(cargalistaFirebase); // le indicamos el texto

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
        toast.setView(layout); //le establecemos el layout al Toast
        toast.show(); //lo enseñamos


        //ListView donde irán colocados los titulos de las recetas
        lvfotos = findViewById(R.id.lvfotos);

        System.out.println("Cargar la imagen desde Firebase y BDremota");

        //Debe tener conexion a la red
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        //El worker ConexionBDGetFotos conseguirá todas las imagenes de la BD remota imagenes
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDGetFotos.class)
                .setConstraints(restricciones)
                .build();

        //En ActivityFotos, se añade un Observer a la tarea antes de encolarla usando WorkManager
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            //recogemos el resultado que viene desde el worker al dar SUCESS
                            String result = workInfo.getOutputData().getString("resultado");
                            JSONArray jsonArray = null;
                            //La consulta que se realiza en el php es la siguiente -->
                            //SELECT * FROM imagenes
                            //Por lo tanto el resultado será un JSONArray que contendrá varios elementos. Cada uno con su id, titulo y descripción.

                            //Crear los 3 arraylist que contendrán cada uno de los elementos de la imagen
                            ArrayList<String> listaIDS = new ArrayList<>();
                            ArrayList<String> listaTitulos = new ArrayList<>();
                            ArrayList<String> listaDesc = new ArrayList<>();
                            try {


                                jsonArray = new JSONArray(result);

                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    //'id' es la primera clave de los datos en el JSON
                                    String id = jsonArray.getJSONObject(i).getString("id");
                                    listaIDS.add(id);
                                    //'titulo' es la segunda clave de los datos en el JSON
                                    String titulo = jsonArray.getJSONObject(i).getString("titulo");
                                    listaTitulos.add(titulo);
                                    //'descripcion' es la tercera clave de los datos en el JSON
                                    String descripcion = jsonArray.getJSONObject(i).getString("descripcion");
                                    listaDesc.add(descripcion);

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String resultadoStringIDS = listaIDS.toString();
                            System.out.println("IDS DE LAS FOTOS: " + resultadoStringIDS);

                            String resultadoStringTitulos = listaTitulos.toString();
                            System.out.println("TITULOS DE LAS FOTOS: " + resultadoStringTitulos);

                            String resultadoStringDesc = listaDesc.toString();
                            System.out.println("DESCRIPCION DE LAS FOTOS: " + resultadoStringDesc);

                            //poner los titulos en un list view mediante el adaptador
                            ArrayAdapter eladaptador =
                                    new ArrayAdapter<String>(ActivityFotos.this, android.R.layout.simple_list_item_1,listaTitulos);
                            lvfotos.setAdapter(eladaptador);

                            //Al clickar sobre un elemento se abrirá una actividad con informacion detallada del mismo (imagen+titulo+descripcion).
                            lvfotos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    //intent a ActivityFotoDetalles --> se carga la foto junto con el título y la descripción
                                    Intent intentfotodetalles = new Intent(ActivityFotos.this,ActivityFotoDetalles.class);
                                    //pasarle como extras los datos de la posición seleccionada
                                    intentfotodetalles.putExtra("id",listaIDS.get(position));
                                    intentfotodetalles.putExtra("titulo",listaTitulos.get(position));
                                    intentfotodetalles.putExtra("descripcion",listaDesc.get(position));
                                    startActivity(intentfotodetalles);


                                    System.out.println(listaIDS.get(position));
                                }
                            });



                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);


    }
}