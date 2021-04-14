package com.example.das_entrega2.actividades;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.das_entrega2.R;
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

import java.util.ArrayList;

public class ActivityMapa extends FragmentActivity implements OnMapReadyCallback {

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

        elmapa.setMapType(GoogleMap.MAP_TYPE_SATELLITE);


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
                                System.out.println("LONG: " + location.getLatitude());

                                LatLng coordenadasActuales = new LatLng(location.getLatitude(), location.getLongitude());

                                elmapa.addMarker(new MarkerOptions()
                                        .position(coordenadasActuales)
                                        .title("El marcador"));

                                CameraUpdate actualizar = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
                                //CameraUpdateFactory.zoomTo(20);

                                elmapa.animateCamera(actualizar);



                                ArrayList<LatLng> coordenadas = new ArrayList<LatLng>();
                                coordenadas.add(coordenadasActuales);



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





    }




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

    }




}
