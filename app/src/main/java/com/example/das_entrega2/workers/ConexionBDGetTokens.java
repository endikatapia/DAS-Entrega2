package com.example.das_entrega2.workers;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConexionBDGetTokens extends Worker {


    public ConexionBDGetTokens(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //En el servidor se encuentrá el PHP --> getTokens.php
        //Este PHP conseguirá todos los tokens de la BD remota.
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/getTokens.php";
        HttpURLConnection urlConnection = null;
        Data resultados = null;
        try {
            //Se genera un objeto HttpURLConnection con la configuración correspondiente
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            //En este caso no se le envía ningún parámetro al PHP, ya que solo queremos conseguir los tokens
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
                System.out.println("Resultado TODOS LOS TOKENS: " + result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(resultados); //Devolver los resultados
    }


}
