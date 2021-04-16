package com.example.das_entrega2.actividades;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.das_entrega2.R;
import com.example.das_entrega2.workers.ConexionBDGetFotos;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class ActivityFotos extends AppCompatActivity {


    private ListView lvfotos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fotos);

        lvfotos = findViewById(R.id.lvfotos);



        System.out.println("Cargar la imagen desde Firebase y BDremota");

        //ir a una nueva actividad


        //conseguir las fotos de la BD remota con un worker

        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDGetFotos.class)
                .setConstraints(restricciones)
                .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){

                            String result = workInfo.getOutputData().getString("resultado");
                            JSONArray jsonArray = null;
                            ArrayList<String> listaIDS = new ArrayList<>();
                            ArrayList<String> listaTitulos = new ArrayList<>();
                            ArrayList<String> listaDesc = new ArrayList<>();
                            try {
                                //JSONObject foto = new JSONObject(result);

                                //id = foto.getString("id");
                                //titulo = foto.getString("titulo");
                                //descripcion = foto.getString("descripcion");


                                jsonArray = new JSONArray(result);

                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    String id = jsonArray.getJSONObject(i).getString("id");
                                    listaIDS.add(id);
                                    String titulo = jsonArray.getJSONObject(i).getString("titulo");
                                    listaTitulos.add(titulo);
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

                            //poner los titulos en un list view y al clickar en uno
                            //se abrira la informacion acerca de ella (titulo+desc) y la foto en grande
                            ArrayAdapter eladaptador =
                                    new ArrayAdapter<String>(ActivityFotos.this, android.R.layout.simple_list_item_1,listaTitulos);

                            lvfotos.setAdapter(eladaptador);

                            lvfotos.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                                    //seleccionarElemento(listaIDS.get(position),listaTitulos.get(position),listaDesc.get(position));
                                    //intent a actividad y se carga la foto
                                    Intent intentfotodetalles = new Intent(ActivityFotos.this,ActivityFotoDetalles.class);
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

        //return tokens;




        // Extraer información de la foto de la base de datos
        /*
        Data datos = new Data.Builder()
                .putString("username", usuario)
                .putString("imagen", fotoID)
                .build();
        Constraints restricciones = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetFotoWorker.class)
                .setConstraints(restricciones)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, status -> {
                    if (status != null && status.getState().isFinished()) {
                        String result = status.getOutputData().getString("datos");
                        try {
                            JSONObject foto = new JSONObject(result);

                            titulo = foto.getString("titulo");
                            descripcion = foto.getString("descripcion");
                            fecha = foto.getString("fecha");
                            latitud = foto.getString("latitud");
                            longitud = foto.getString("longitud");

                            editTextTitulo.setText(titulo);
                            editTextDescripcion.setText(descripcion);
                            textViewFecha.setText(fecha);

                            FirebaseStorage storage = FirebaseStorage.getInstance();
                            StorageReference storageRef = storage.getReference();
                            StorageReference pathReference = storageRef.child(fotoID);
                            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Glide.with(InfoFotoActivity.this).load(uri).into(imageViewFoto);
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });

        WorkManager.getInstance(this).enqueue(otwr);

         */








        //poner esas fotos junto al titulo en una listViewPersonalizado

        //cuando se clique en una foto se abrira la informacion acerca de ella (titulo+desc) y la foto en grande

        /*
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        StorageReference pathReference = storageRef.child("IMG_20210416_125326_8552512348616060076.jpg");
        pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri).into(imageViewFoto);
            }
        });

         */











    }
}