package com.example.das_entrega2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import java.util.Locale;

public class ActivityDetalles extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //establecer idioma seleccionado en las preferencias (por defecto: castellano)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());
        setContentView(R.layout.activity_detalles);

        //ActivityDetalles contiene el otro fragment (fragmentFotoInd) definido en activity_detalles.xml,
        //Recogemos los datos del intent, enlazamos el fragment y ejecutamos su método de actualización setDatos
        //Esos datos se actualizan en FragmentDetalles
        FragmentDetalles elotro = (FragmentDetalles) getSupportFragmentManager().findFragmentById(R.id.fragmentFotoIndv);
        String plato=getIntent().getStringExtra("nombre");
        int imagen = getIntent().getIntExtra("imagen",0);
        String ingredientes = getIntent().getStringExtra("ingredientes");
        double precio = getIntent().getDoubleExtra("precio",0);
        elotro.setDatos(plato,imagen,ingredientes,precio);
    }
}