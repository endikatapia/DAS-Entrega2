package com.example.das_entrega2.workers;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConexionBDInsertarUsuario extends Worker {


    public ConexionBDInsertarUsuario(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //En el servidor se encuentrá el PHP --> insertarUsuario.php
        //Este PHP insertará un nuevo usuario en la BD remota.
        //La BD remota usuarios se compone de estos elementos:
        //nombre: (PK) varchar(255), contraseña: varchar(255)
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/insertarUsuario.php";
        HttpURLConnection urlConnection = null;

        //recibir los datos desde WorkManager: usuario y contraseña hasheada
        String nombre = getInputData().getString("username");
        String contraseña = getInputData().getString("password");
        try {
            //Se genera un objeto HttpURLConnection con la configuración correspondiente
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);

            //Para enviar parámetros al fichero PHP, se utiliza Uri.Builder
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("nombre", nombre)
                    .appendQueryParameter("contraseña", contraseña);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST"); //Formato de envío
            urlConnection.setDoOutput(true); //Necesario si se usa POST o PUT
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded"); //Formato de los parámetros

            //Para incluir los parámetros en la llamada se usa un objeto PrintWriter
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            //Se mira el código de vuelta (debe ser 200), y se procesa el resultado
            int statusCode = urlConnection.getResponseCode();
            System.out.println(statusCode);
            if (statusCode==200){
                //si ha ido bien devolver Sucess
                return Result.success();
            }


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
