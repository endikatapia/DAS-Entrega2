package com.example.das_entrega2.actividades;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.example.das_entrega2.R;
import com.example.das_entrega2.workers.ConexionBDGetFotos;
import com.example.das_entrega2.workers.ConexionOverpassAPI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.maps.android.SphericalUtil;


import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;


public class ActivityMapa extends FragmentActivity implements OnMapReadyCallback {

    Bitmap iconoRestaurante;
    String nosehanencont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //establecer el idioma que hab??a guardado en las preferencias --> por defecto: castellano
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_mapa);

        //toast avisandole que los restaurantes mas cercanos tienen que cargar desde el API OpenStreetMap (Overpass)
        //Tarda en torno a 5-10 segundos. EL API de Overpass nos permite hacer 2 peticiones en un minuto mas o menos
        // Si se hacen demasiadas peticiones seguidas el API Overpass no reponder?? correctamente
        //TOAST PERSONALIZADO con layout_toast.xml
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

        String cargaMapa = getString(R.string.cargaMapa);
        nosehanencont = getString(R.string.nosehanencont);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(cargaMapa); // le indicamos el texto

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_LONG); //duracion corta
        toast.setView(layout); //le establecemos el layout al Toast
        toast.show(); //lo ense??amos


        //Bitmap para establecer como icono el restaurante
        BitmapDrawable bitmapdraw=(BitmapDrawable)getResources().getDrawable(R.drawable.restauranteicono);
        Bitmap b=bitmapdraw.getBitmap();
        iconoRestaurante = Bitmap.createScaledBitmap(b, 84, 84, false);


        //Para poder trabajar con el mapa deberemos utilizar el identificador que le hayamos asignado al
        //Fragment donde se encuentra el mapa y llamar al m??todo getMapAsync(???).
        SupportMapFragment elfragmento = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentoMapa);
        elfragmento.getMapAsync(this);

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // Si la petici??n se cancela, granResults estar?? vac??o
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                } else {
                // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA

                }
                return;
            }
        }
    }



    @Override
    public void onMapReady(GoogleMap elmapa) {

        //el mapa ser?? de tipo HYBRID (satelite + localizaciones)
        elmapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        //Si no tiene permisos para leer la Ubicaci??n pedirlos
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO EST?? CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // MOSTRAR AL USUARIO UNA EXPLICACI??N DE POR QU?? ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO EST?? CONCEDIDO TODAV??A O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        } else { //si ya tiene los permisos


            //sacar posicion actual mediante geolocalizacion
            //Hay que instanciar el proveedor de posiciones
            FusedLocationProviderClient proveedordelocalizacion =
                    LocationServices.getFusedLocationProviderClient(this);


            LocationRequest peticion = LocationRequest.create();
            peticion.setInterval(1000); //cada cu??ntos milisegundos debe actualizarse
            peticion.setFastestInterval(5000); //cada cu??ntos milisegundos somos capaces de gestionar una actualizaci??n
            peticion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Precisi??n y tipo de localizaci??n que se desea


            //Llamar a getLastLocation() y a??adir listeners
            proveedordelocalizacion.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) { //si nos devuelve una localizacion

                                System.out.println("LAT: " + location.getLatitude());
                                System.out.println("LONG: " + location.getLongitude());

                                //sacamos las coordenadas actuales y las guardamos en una variable LatLng
                                LatLng coordenadasActuales = new LatLng(location.getLatitude(), location.getLongitude());


                                //Ahora tenemos que calcular el BoundigBox
                                //https://wiki.openstreetmap.org/wiki/Overpass_API/Language_Guide
                                //El BOUNDINGBOX es el ??rea (N,S,W,E) donde se van a buscar los restaurantes mas cercanos

                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                double R = 6371;  // earth radius in km

                                double radius = 0.35; // EL RADIO EN EL QUE REALIZAR LA BUSQUEDA (350 m)

                                //sacar las diferentes coordenadas  (N,S,W,E) dependiendo de nuestra localizaci??n actual
                                double x1 = lon - Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));//oeste

                                double x2 = lon + Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat))); //este

                                double y1 = lat + Math.toDegrees(radius/R); //norte

                                double y2 = lat - Math.toDegrees(radius/R); //sur

                                //el orden --> (sur, oeste, norte y este)
                                System.out.println("BBOX: (sur): " + y2 + " (oeste): " + x1 + " (norte): " + y1 + " (este): " + x2);

                                String bbox=  "(" + y2 + "," + x1 + "," + y1 + "," + x2 + ")";

                                System.out.println("BBOX: " + bbox);

                                //Al worker ConexionOverpassAPI se le pasan como datos el BoundingBox donde se realizar?? la busqueda.
                                Data datos = new Data.Builder()
                                        .putString("bbox", bbox)
                                        .build();

                                //el worker necesita conexion a internet
                                Constraints restricciones = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();

                                //El worker ConexionOverpassAPI mostrar?? los restaurantes m??s cercanos en un radio de 350 metros
                                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionOverpassAPI.class)
                                        .setConstraints(restricciones)
                                        .setInputData(datos)
                                        .build();

                                //En ActivityMapa, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
                                WorkManager.getInstance(ActivityMapa.this).getWorkInfoByIdLiveData(otwr.getId())
                                        .observe(ActivityMapa.this, new Observer<WorkInfo>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if(workInfo != null && workInfo.getState().isFinished()){
                                                    //LA respuesta es un JSONObject muy largo, no un JSONArray
                                                    try {
                                                        //recogemos el resultado que viene desde el worker al dar SUCESS
                                                        String result = workInfo.getOutputData().getString("resultado");
                                                        JSONParser parser = new JSONParser();
                                                        JSONObject json = (JSONObject) parser.parse(result);

                                                        //Del JSONObject sacamos un JSONArray que tiene como clave 'elements'.
                                                        //En este JSONArray encontraremos las latitudes, longitudes y nombres de los restaurantes encontrados
                                                        JSONArray elements = (JSONArray) json.get("elements");

                                                        //Crear los 3 arraylist para guardar las latitudes, longitudes y nombres de los restaurantes encontrados
                                                        ArrayList<Double> lats = new ArrayList<>();
                                                        ArrayList<Double> longs = new ArrayList<>();
                                                        ArrayList<String> nombres = new ArrayList<>();

                                                        //Usar el iterator para iterar sobre el JSONArray
                                                        Iterator i = elements.iterator();

                                                        //mientras exista elementos (restaurantes)
                                                        while (i.hasNext()) {
                                                            //obtenemos el JSONObject
                                                            JSONObject restaurante = (JSONObject) i.next();
                                                            //conseguimos su latitud
                                                            Double lat = (Double) restaurante.get("lat");
                                                            lats.add(lat);
                                                            //conseguimos su longitud
                                                            Double lon = (Double) restaurante.get("lon");
                                                            longs.add(lon);

                                                            //conseguimos su nombre, solo si est?? registrado en la API.
                                                            //Si no devolvemos un String vac??o
                                                            //Para acceder al nombre tenemos que lograr la clave 'tags' primero.
                                                            JSONObject tags = (JSONObject) restaurante.get("tags");
                                                            String name = "";
                                                            if (tags.containsKey("name")) {
                                                                //conseguimos el nombre
                                                                name = (String) tags.get("name");
                                                                nombres.add(name);
                                                            } else {
                                                                name = "";
                                                                nombres.add(name);
                                                            }

                                                        }




                                                        String latitudes = lats.toString();
                                                        System.out.println("LATS: " + latitudes);

                                                        String longitudes = longs.toString();
                                                        System.out.println("LONGS: " + longitudes);

                                                        String nombresss = nombres.toString();
                                                        System.out.println("NOMBRES: " + nombresss);


                                                        //si no ha encontrado ning??n restaurante
                                                        if (lats.size()==0) {
                                                            //toast diciendo que no se han encontrado restaurantes en ese radio
                                                            Toast.makeText(ActivityMapa.this, nosehanencont, Toast.LENGTH_SHORT).show();

                                                        }
                                                        //Para todos los restaurantes encontrados
                                                        for (int j = 0; j < lats.size(); j++) {
                                                            //conseguimos sus coordenas
                                                            LatLng coordss = new LatLng(lats.get(j), longs.get(j));
                                                            //ponemos un marcador en el mapa con el icono del restaurante en esas coordenadas y el nombre
                                                            //del marcador ser?? el nombre del restaurante
                                                            elmapa.addMarker(new MarkerOptions()
                                                                    .position(coordss)
                                                                    .icon(BitmapDescriptorFactory.fromBitmap(iconoRestaurante))
                                                                    .title(nombres.get(j)));
                                                        }


                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }

                                                }
                                            }
                                        });
                                WorkManager.getInstance(ActivityMapa.this).enqueue(otwr);



                                //a??adimos como marcador al mapa nuestra posici??n actual
                                elmapa.addMarker(new MarkerOptions()
                                        .position(coordenadasActuales)
                                        .title("Ubicaci??n actual"));

                                //Ubicacion actual con un zoom de 14
                                CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 14);
                                //poner una animaci??n a la camara mientra se acerca a la localizaci??n deseada.
                                elmapa.animateCamera(actualizar);



                            } else {
                                //si no devuelve una localizacion --> LAT y LONG desconocidas
                                System.out.println("LAT: (desconocida)");
                                System.out.println("LONG: (desconocida)");
                            }

                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

        }


    } //FIN ON MAP READY



}
