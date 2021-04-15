package com.example.das_entrega2.workers;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConexionEnviarNotificacion extends Worker {

    public ConexionEnviarNotificacion(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/enviarNotificacionPrecio.php";
        HttpURLConnection urlConnection = null;
        String precio = getInputData().getString("precio");
        String[] tokens = getInputData().getStringArray("tokens");


        for (int i=0; i<tokens.length;i++){

            //System.out.println("TOKEN EN ENVIAR AL PHP " + i + ": " + tokens[i]);


        }
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);




            /*
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("precio", precio)
                    .appendQueryParameter("tokens", tokens);
            String parametros = builder.build().getEncodedQuery();

             */

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            //urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");



            JSONArray tokensJSON = new JSONArray();
            for (String token : tokens){
                tokensJSON.put(token);
            }

            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("precio", precio);
            parametrosJSON.put("tokens", tokensJSON);



            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();
            /*
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

             */


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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return Result.failure();
    }


}