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
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/guardarImagen.php";
        HttpURLConnection urlConnection = null;

        String id = getInputData().getString("id");
        String titulo = getInputData().getString("titulo");
        String descripcion = getInputData().getString("descripcion");

        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);


            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("id", id)
                    .appendQueryParameter("titulo", titulo)
                    .appendQueryParameter("descripcion", descripcion);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

            /*
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("nombre", nombre);
            parametrosJSON.put("contraseña", contraseña);


            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();

             */

            //EN PHP
            //INSERT INTO `usuarios` (`nombre`, `contraseña`) VALUES ('Pruebadesdephpmyadmin', 'nohash1')


            //String parametros = "nombre=" + nombre + "&contraseña="+contraseña;






            int statusCode = urlConnection.getResponseCode();
            if (statusCode==200){
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
        return Result.failure();
    }

}
