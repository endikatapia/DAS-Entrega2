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
        //En el servidor se encuentrá el PHP --> enviarNotificacionPrecio.php
        //Este PHP enviará una notificación usando el ServicioFirebase a todos los tokens de la BD remota.
        //Si la aplicación está en primer plano lanzará una notificación al usuario indicandole el precio de su último pedido
        //A los demás usuarios que tengan la app en segundo plano les llegará una notificación avisandoles de que se ha realizado un nuevo pedido.
        String direccion = "http://ec2-54-167-31-169.compute-1.amazonaws.com/etapia008/WEB/enviarNotificacionPrecio.php";
        HttpURLConnection urlConnection = null;

        //recibir los datos desde WorkManager: precio y String[] tokens
        String precio = getInputData().getString("precio");
        String[] tokens = getInputData().getStringArray("tokens");
        try {
            //Se genera un objeto HttpURLConnection con la configuración correspondiente
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);



            urlConnection.setRequestMethod("POST"); //Formato de envío
            urlConnection.setDoOutput(true); //Necesario si se usa POST o PUT
            urlConnection.setRequestProperty("Content-Type","application/json"); //Formato de los parámetros --> JSON en este caso
            //en el PHP los datos JSON se reciben así: $parametros = json_decode( file_get_contents( 'php://input' ), true );

            //convertimos el String[] en un JSONArray para no tener problemas al tratar los datos en el PHP.
            JSONArray tokensJSON = new JSONArray();
            for (String token : tokens){
                tokensJSON.put(token);
            }

            //Se crea el objeto JSON Y se le pasan los parámetros
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("precio", precio);
            parametrosJSON.put("tokens", tokensJSON);

            //Para incluir los parámetros en la llamada se usa un objeto PrintWriter
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString()); //Al enviarlo, transformarlo a String
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
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //si algo ha fallado en la ejecución devolver Failure
        return Result.failure();
    }


}
