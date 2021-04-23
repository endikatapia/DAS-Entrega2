package com.example.das_entrega2.workers;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionOverpassAPI extends Worker {


    public ConexionOverpassAPI(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //CONSEGUIR EL BBOX que viene desde el worker
        String bbox= getInputData().getString("bbox");


        //La dirección a la que le queremos hacer la petición tendrá el siguiente formato:
        //"http://overpass-api.de/api/interpreter?data=query";
        //La query en este caso es la siguiente:
        //data=[out:json];node[amenity=restaurant]"+bbox+";out%20meta;"
        //donde le indicamos que la respuesta debe ser JSON --> [out:json]
        //Los nodos que debe buscar son de amenity=restaurant y los debe buscar en el bbox calculado en la actividad, es decir, en un radio de 350 metros.
        //El problema que puede ocurrir en el worker es que si por ejemplo buscamos restaurantes en un areá muy abarrotada
        //de restaurantes, el worker no podrá devolver tanta cantidad de datos y habrá un fallo en la ejecución.
        //Por ese motivo se reduce a 350 metros la búsqueda de los restaurantes, para que el worker no tenga que cargar muchos datos
        String direccion = "https://www.overpass-api.de/api/interpreter?data=[out:json];node[amenity=restaurant]"+bbox+";out%20meta;";
        HttpURLConnection urlConnection = null;
        Data resultados = null;
        try {
            //Se genera un objeto HttpURLConnection con la configuración correspondiente
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);

            //Se mira el código de vuelta (debe ser 200), y se procesa el resultado
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                //Vamos generando en la variable result el resultado final
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                //Pares clave, valor
                resultados = new Data.Builder()
                        .putString("resultado",result)
                        .build();
                inputStream.close();
                System.out.println("Resultado OVERPASS API: " + result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(resultados); //Devolver los resultados
    }


}
