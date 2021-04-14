package com.example.das_entrega2.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;

import com.example.das_entrega2.R;

import java.util.Locale;

public class ActivityPreferencias extends AppCompatActivity {

    private String comidaPref;
    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //IDIOMA --> seleccionado en las preferencias (por defecto: castellano)
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");
        //establecer idioma
        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_preferencias);

        //Obtenemos las preferencias
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //por defecto la comida favorita sera la pizza
        comidaPref = prefs.getString("comidapref","Pizza");
    }


    public void onClickGuardar(View v){
        //Al clickar guardar se establecen las nuevas preferencias y vamos a MainActivity
        Intent i = new Intent(this, MainActivity.class);
        //recogemos el usuario desde MainActivity y se lo volvemos a pasar a MainActivity para que se mantenga
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String user = extras.getString("usuario");
            i.putExtra("usuario",user);
        }
        //tambien le pasamos en el intent la comida preferida seleccionada
        i.putExtra("comidaPref",comidaPref);
        startActivity(i);


    }

}