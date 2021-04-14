package com.example.das_entrega2.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_entrega2.workers.ConexionBDComprobarUsuario;
import com.example.das_entrega2.workers.ConexionBDInsertarUsuario;
import com.example.das_entrega2.dialogos.DialogoLogin;
import com.example.das_entrega2.PasswordAuth;
import com.example.das_entrega2.R;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Locale;

public class ActivityLogin extends AppCompatActivity implements DialogoLogin.ListenerdelDialogo {

    ImageView logo;
    EditText usernameet;
    EditText passwordet;
    private String hashedpassword;
    OutputStreamWriter fichero;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //establecer el idioma que había guardado en las preferencias --> por defecto: castellano
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());


        setContentView(R.layout.activity_login);



        //se crea el fichero donde se van a ir guardando los platos pedidos en modo privado,
        //es decir, cada vez que el usuario entre se le creara el fichero de nuevo.
        try {
            fichero = new OutputStreamWriter(openFileOutput("ficheroPedido.txt", Context.MODE_PRIVATE));
            fichero.close();
        } catch (IOException e) {
            System.out.println("Error escribiendo el fichero");
        }


        //Buscar elementos del layout
        logo=(ImageView) findViewById(R.id.logo);
        usernameet = (EditText) findViewById(R.id.usuario);
        passwordet = (EditText) findViewById(R.id.contraseña);

        //establecer a la ImageView el logo del restaurante encontrado en R.drawable
        logo.setImageResource(R.drawable.logorestaurante);


        System.out.println("Hola mundo");
    }


    public void onClickLogin(View v) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println("LOGIN USUARIO");
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        if (username.matches("") || password.matches("") ) {
            System.out.println("La Contraseña o el Usuario debe tener al menos 1 caracter");
            String almenos1caracter = getString(R.string.almenos1caracter);

            //TOAST PERSONALIZADO con layout_toast.xml
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(almenos1caracter); // le indicamos el texto

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
            toast.setView(layout); //le establecemos el layout al Toast
            toast.show(); //lo enseñamos

        } else {

            hashedpassword = PasswordAuth.generateStrongPasswordHash(password);
            System.out.println("HASHED PASSWORD: " + hashedpassword);

            Data datos = new Data.Builder()
                    .putString("username", username)
                    .putString("password", hashedpassword)
                    .build();


            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarUsuario.class)
                    .setInputData(datos)
                    .build();

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                try {
                                    String result = workInfo.getOutputData().getString("resultado");
                                    //JSONArray jsonArray = null;
                                    JSONParser parser = new JSONParser();
                                    JSONObject json = null;

                                    json = (JSONObject) parser.parse(result);

                                    if (json != null) {
                                        //si existe el usuario --> comprobar si la contraseña es correcta
                                        String nom = (String) json.get("nombre");
                                        String con = (String) json.get("contraseña");

                                        //'con' viene con el hash
                                        //hacer el hash del password del editText y comparar si son iguales con el metodo validatePassword
                                        //este metodo compara si coindicen la contraseña del edittext y la que viene del php
                                        boolean lasContrasenasCoinciden = PasswordAuth.validatePassword(password,con);
                                        //si son iguales intent a mainActivity
                                        if (lasContrasenasCoinciden){
                                            //Toast.makeText(ActivityLogin.this, "Las contraseñas coinciden", Toast.LENGTH_SHORT).show();

                                            //INTENT A MAIN ACTIVITY
                                            System.out.println("El usuario "+username+ " existe Y la contraseña es correcta --> MAIN ACTIVITY");
                                            Intent intentMainActivity = new Intent(ActivityLogin.this, MainActivity.class);
                                            //guardamos el usuario para que salga en MainActivity
                                            intentMainActivity.putExtra("usuario",username);
                                            startActivity(intentMainActivity);



                                        }
                                        //sino coinciden Toast con contraseña incorrecta
                                        else{
                                            System.out.println("Las contraseñas no coinciden");
                                            String usudialogo = getString(R.string.usudialogo);
                                            String existePero = getString(R.string.existePero);

                                            //TOAST PERSONALIZADO con layout_toast.xml
                                            LayoutInflater inflater = getLayoutInflater();
                                            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                                            TextView text = (TextView) layout.findViewById(R.id.text);
                                            text.setText(usudialogo+ usernameet.getText().toString()+ existePero);

                                            Toast toast = new Toast(ActivityLogin.this);
                                            toast.setDuration(Toast.LENGTH_SHORT);
                                            toast.setView(layout);
                                            toast.show();;
                                        }

                                        System.out.println("DESDE EL PHP (nom): " + nom);
                                        System.out.println("DESDE EL PHP (con): " + con);

                                    }
                                    else{ //Toast diciendole que el usuario no esta registrado
                                        //TOAST PERSONALIZADO con layout_toast.xml
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

                                        String usudialogo = getString(R.string.usudialogo);
                                        String noestaregistrado = getString(R.string.noestareg);

                                        TextView text = (TextView) layout.findViewById(R.id.text);
                                        text.setText(usudialogo+ usernameet.getText().toString()+ noestaregistrado);

                                        Toast toast = new Toast(ActivityLogin.this);
                                        toast.setDuration(Toast.LENGTH_SHORT);
                                        toast.setView(layout);
                                        toast.show();;



                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                } catch (NoSuchAlgorithmException e) {
                                    e.printStackTrace();
                                } catch (InvalidKeySpecException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }


    }




    public void onClickRegistrate(View v) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println("REGISTRAR USUARIO");
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        if (username.matches("") || password.matches("") ) {
            System.out.println("La Contraseña o el Usuario debe tener al menos 1 caracter");
            String almenos1caracter = getString(R.string.almenos1caracter);

            //TOAST PERSONALIZADO con layout_toast.xml
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(almenos1caracter); // le indicamos el texto

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
            toast.setView(layout); //le establecemos el layout al Toast
            toast.show(); //lo enseñamos

        } else {

            hashedpassword = PasswordAuth.generateStrongPasswordHash(password);
            System.out.println("HASHED PASSWORD: " + hashedpassword);

            Data datos = new Data.Builder()
                    .putString("username", username)
                    .putString("password", hashedpassword)
                    .build();


            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarUsuario.class)
                    .setInputData(datos)
                    .build();

            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                try {
                                    String result = workInfo.getOutputData().getString("resultado");
                                    //JSONArray jsonArray = null;
                                    JSONParser parser = new JSONParser();
                                    JSONObject json = null;

                                    json = (JSONObject) parser.parse(result);

                                    if (json != null) {
                                        //si existe el usuario --> TOAST AVISANDOLE
                                        String nom = (String) json.get("nombre");
                                        String con = (String) json.get("contraseña");

                                        String yaexiste = getString(R.string.yaexiste);
                                        String usudialo = getString(R.string.usudialogo);

                                        //TOAST PERSONALIZADO con layout_toast.xml
                                        LayoutInflater inflater = getLayoutInflater();
                                        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

                                        TextView text = (TextView) layout.findViewById(R.id.text);
                                        text.setText(usudialo + nom + yaexiste); // le indicamos el texto

                                        Toast toast = new Toast(ActivityLogin.this);
                                        toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
                                        toast.setView(layout); //le establecemos el layout al Toast
                                        toast.show(); //lo enseñamos




                                        System.out.println("El usuario "+username+ " ya existe");

                                        //sino coinciden Toast con contraseña incorrecta

                                        System.out.println("DESDE EL PHP (nom): " + nom);
                                        System.out.println("DESDE EL PHP (con): " + con);

                                    }
                                    //si no existe el usuario preguntarle si desea registrarse en la BD remota
                                    else {
                                        System.out.println("El usuario no existe en la BD");
                                        //Toast.makeText(ActivityLogin.this, "El usuario " + username + " no existe", Toast.LENGTH_SHORT).show();
                                        //preguntar si quiere insertarlo con un dialog
                                        DialogFragment df = new DialogoLogin(username);
                                        df.show(getSupportFragmentManager(), "login");
                                        //insertarUsuario(username, hashedpassword);
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }



    }


    public void insertarUsuario(String username, String password){
        System.out.println("INSERTAR USUARIO");


        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("password", password)
                .build();


        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDInsertarUsuario.class)
                .setInputData(datos)
                .build();


        //si ha tecleado algo en los 2 campos --> DialogoLogin con contructor (String user)
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            //TextView textViewResult = findViewById(R.id.textoResultado);
                            //textViewResult.setText(workInfo.getOutputData().getString("datos"));
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);


    }



    //Al pulsar si en el dialogo
    @Override
    public void alpulsarSi() {
        //insertar al usuario
        insertarUsuario(usernameet.getText().toString(),hashedpassword);
        String añad = getString(R.string.aña);
        //Toast.makeText(ActivityLogin.this,usuario.getText().toString()+añad,Toast.LENGTH_SHORT).show();

        //TOAST PERSONALIZADO con layout_toast.xml
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(usernameet.getText().toString()+añad);

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }
}