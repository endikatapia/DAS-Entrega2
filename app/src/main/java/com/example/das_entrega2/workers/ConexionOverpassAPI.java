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


        //CONSEGUIR EL BBOX
        String bbox= getInputData().getString("bbox");

        System.out.println("BBOX QUERY");



        String direccion = "https://www.overpass-api.de/api/interpreter?data=[out:json];node[amenity=restaurant]"+bbox+";out%20meta;";
        //String query=" node["amenity"="restaurant"]
        //(50.6,7.0,50.8,7.3);
        //out;"

        //http://www.overpass-api.de/api/xapi?node[rcn_ref=*][bbox=5.170799818181818,51.363934891891894,5.534436181818181,51.580151108108105]

        //String overpass_query = """[out:json]
        //(node["amenity"="restaurant"];
        //bbox;
        //out ;
        //""";


        //String direccion = "http://overpass-api.de/api/interpreter?data=query";
        HttpURLConnection urlConnection = null;


        Data resultados = null;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(60000);
            urlConnection.setReadTimeout(60000);

            /*
            Uri.Builder builder = new Uri.Builder()
                    .appendQueryParameter("bbox", bbox);
            String parametros = builder.build().getEncodedQuery();

            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();

             */


            int statusCode = urlConnection.getResponseCode();

            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                resultados = new Data.Builder()
                        .putString("resultado",result)
                        .build();
                inputStream.close();
                System.out.println("Resultado OVERPASS API: " + result);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.success(resultados);
    }


}
