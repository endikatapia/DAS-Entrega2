package com.example.das_entrega2.workers;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConexionBDGuardarImagen extends Worker {


    public ConexionBDGuardarImagen(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //En el servidor se encuentrá el PHP --> guardarImagen.php
        //Este PHP insertará una nueva imagen en la BD remota.
        //La BD remota imagenes se compone de estos elementos:
        //id: (PK) varchar(255), titulo: varchar(255), descripcion: varchar(255)
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/guardarImagen.php";
        HttpURLConnection urlConnection = null;

        //recibir los datos desde WorkManager: id, título y descripcion
        String id = getInputData().getString("id");
        String titulo = getInputData().getString("titulo");
        String descripcion = getInputData().getString("descripcion");

        try {
            //Se genera un objeto HttpURLConnection con la configuración correspondiente
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //Para enviar parámetros al fichero PHP, se utiliza Uri.Builder
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id", id)
                    .appendQueryParameter("titulo", titulo)
                    .appendQueryParameter("descripcion", descripcion);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            //Para incluir los parámetros en la llamada se usa un objeto PrintWriter
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Se mira el código de vuelta (debe ser 200), y se procesa el resultado
            int statusCode = urlConnection.getResponseCode();
            if (statusCode==200){
                //si ha ido bien devolver Sucess
                return Result.success();
            }

            System.out.println(statusCode);


        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //si algo ha fallado en la ejecución devolver Failure
        return Result.failure();
    }

}
