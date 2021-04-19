package com.example.das_entrega2.actividades;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
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


public class ActivityMapa extends FragmentActivity implements OnMapReadyCallback {

    //private static final OutputFormat JSON = null;
    private int i=0;
    private Double distancia;
    Double distTotalRuta = 0.0 ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);


        SupportMapFragment elfragmento = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentoMapa);
        elfragmento.getMapAsync(this);


    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // Si la petición se cancela, granResults estará vacío
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

        elmapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);


        //cosas que se pueden hacer con la camara
        /*
        CameraUpdateFactory.zoomIn();

        CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(43.26, -2.95),9);

        elmapa.moveCamera(actualizar);

        LatLng nuevascoordenadas= new LatLng(43.26,-2.95);

        CameraPosition Poscam = new CameraPosition.Builder()
                .target(nuevascoordenadas)
                .zoom(6)
                .bearing(54)
                .tilt(5)
                .build();
        CameraUpdate otravista = CameraUpdateFactory.newCameraPosition(Poscam);

        elmapa.animateCamera(otravista);

         */

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);

        } else {




            //sacar posicion actual mediante geolocalizacion
            FusedLocationProviderClient proveedordelocalizacion =
                    LocationServices.getFusedLocationProviderClient(this);


            LocationRequest peticion = LocationRequest.create();
            peticion.setInterval(1000);
            peticion.setFastestInterval(5000);
            peticion.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


            proveedordelocalizacion.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {



                                System.out.println("LAT: " + location.getLatitude());
                                System.out.println("LONG: " + location.getLongitude());

                                LatLng coordenadasActuales = new LatLng(location.getLatitude(), location.getLongitude());



                                double lat = location.getLatitude();
                                double lon = location.getLongitude();

                                double R = 6371;  // earth radius in km

                                double radius = 0.75; // km

                                double x1 = lon - Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat)));//oeste

                                double x2 = lon + Math.toDegrees(radius/R/Math.cos(Math.toRadians(lat))); //este

                                double y1 = lat + Math.toDegrees(radius/R); //norte

                                double y2 = lat - Math.toDegrees(radius/R); //sur

                                //el orden --> (sur, oeste, norte y este)

                                System.out.println("BBOX: (sur): " + y2 + " (oeste): " + x1 + " (norte): " + y1 + " (este): " + x2);

                                String bbox=  "(" + y2 + "," + x1 + "," + y1 + "," + x2 + ")";

                                System.out.println("BBOX: " + bbox);




                                Data datos = new Data.Builder()
                                        .putString("bbox", bbox)
                                        .build();

                                //el worker
                                Constraints restricciones = new Constraints.Builder()
                                        .setRequiredNetworkType(NetworkType.CONNECTED)
                                        .build();

                                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionOverpassAPI.class)
                                        .setConstraints(restricciones)
                                        .setInputData(datos)
                                        .build();

                                WorkManager.getInstance(ActivityMapa.this).getWorkInfoByIdLiveData(otwr.getId())
                                        .observe(ActivityMapa.this, new Observer<WorkInfo>() {
                                            @RequiresApi(api = Build.VERSION_CODES.O)
                                            @Override
                                            public void onChanged(WorkInfo workInfo) {
                                                if(workInfo != null && workInfo.getState().isFinished()){


                                                    //LA respuesta es un JSONObject no un JSONArray
                                                    try {
                                                        String result = workInfo.getOutputData().getString("resultado");
                                                        JSONParser parser = new JSONParser();
                                                        JSONObject json = (JSONObject) parser.parse(result);


                                                        JSONArray elements = (JSONArray) json.get("elements");

                                                        //System.out.println("ELEMENTOS: " + jsonArray);
                                                        ArrayList<Double> lats = new ArrayList<>();
                                                        ArrayList<Double> longs = new ArrayList<>();
                                                        ArrayList<String> nombres = new ArrayList<>();



                                                        Iterator i = elements.iterator();

                                                        while (i.hasNext()) {

                                                            JSONObject restaurante = (JSONObject) i.next();
                                                            Double lat = (Double) restaurante.get("lat");
                                                            lats.add(lat);
                                                            Double lon = (Double) restaurante.get("lon");
                                                            longs.add(lon);


                                                            JSONObject tags = (JSONObject) restaurante.get("tags");
                                                            String name = "";
                                                            if (tags.containsKey("name")) {
                                                                name = (String) tags.get("name");
                                                                nombres.add(name);
                                                            }
                                                            else {
                                                                name = "";
                                                                nombres.add(name);
                                                            }

                                                            //System.out.println("lat: " + String.valueOf(lat));
                                                            //System.out.println("lon: " + String.valueOf(lon));
                                                        }

                                                        String latitudes = lats.toString();
                                                        System.out.println("LATS: " + latitudes);

                                                        String longitudes = longs.toString();
                                                        System.out.println("LONGS: " + longitudes);

                                                        String nombresss = nombres.toString();
                                                        System.out.println("NOMBRES: " + nombresss);


                                                        //añadirMarcadoresDeRestaurantes(lats,longs);

                                                        for (int j=0; j<lats.size();j++){
                                                            LatLng coordss = new LatLng(lats.get(j), longs.get(j));
                                                            elmapa.addMarker(new MarkerOptions()
                                                                    .position(coordss)
                                                                    .title(nombres.get(j)));
                                                        }



                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }



                                                }
                                            }
                                        });
                                WorkManager.getInstance(ActivityMapa.this).enqueue(otwr);






                                elmapa.addMarker(new MarkerOptions()
                                        .position(coordenadasActuales)
                                        .title("Ubicación actual"));

                                CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
                                //CameraUpdateFactory.zoomTo(20);

                                elmapa.animateCamera(actualizar);



                                ArrayList<LatLng> coordenadas = new ArrayList<LatLng>();
                                coordenadas.add(coordenadasActuales);



                                /*
                                //para poner marcadores
                                elmapa.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                                    @Override
                                    public void onMapClick(LatLng latLng) {
                                        elmapa.addMarker(new MarkerOptions()
                                                .position(latLng)
                                                .title("El marcador"));



                                        coordenadas.add(latLng);


                                        //https://www.geeksforgeeks.org/how-to-calculate-distance-between-two-locations-in-android/
                                        distancia = SphericalUtil.computeDistanceBetween(coordenadas.get(i), coordenadas.get(i+1));
                                        System.out.println("DISTANCIA ENTRE 2 PUNTOS: " + distancia + " METROS");


                                        distTotalRuta = distTotalRuta + distancia;

                                        System.out.println("DISTANCIA TOTAL RUTA: " +  distTotalRuta + " METROS");



                                        Polyline polyline1 = elmapa.addPolyline(new PolylineOptions()
                                                .clickable(true)
                                                .add(coordenadas.get(i),
                                                        coordenadas.get(i+1)
                                                ));
                                        // Store a data object with the polyline, used here to indicate an arbitrary type.
                                        polyline1.setTag("A");
                                        i++;
                                        stylePolyline(polyline1);





                                    }
                                });

                                */



                            } else {
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
















    //estilo de la linea
    private void stylePolyline(Polyline polyline) {
        String type = "";
        // Get the data object stored with the polyline.
        if (polyline.getTag() != null) {
            type = polyline.getTag().toString();
        }

        switch (type) {
            // If no type is given, allow the API to use the default.
            case "A":
                polyline.setEndCap(new RoundCap());
                polyline.setWidth(12);
                polyline.setColor(Color.RED);
                polyline.setJointType(JointType.ROUND);

                break;
            case "B":
                // Use a round cap at the start of the line.
                polyline.setStartCap(new RoundCap());
                break;
        }

    } //FIN POLYLINE






}
