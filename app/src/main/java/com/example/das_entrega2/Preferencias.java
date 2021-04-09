package com.example.das_entrega2;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;


public class Preferencias extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        //Android permite almacenar las preferencias del usuario en la aplicación a través de la clase SharedPreferences.
        //El fichero XML pref_config.xml contiene la configuración y la descripción de las preferencias--> se encuentra en res/xml
        //En esta aplicacion se utilizaran las preferencias para definir la comida favorita y el idioma preferido.
        addPreferencesFromResource(R.xml.pref_config);
    }
}
