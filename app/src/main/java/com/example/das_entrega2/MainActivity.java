package com.example.das_entrega2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements  DialogoLogin.ListenerdelDialogo  {


    EditText usernameet;
    EditText passwordet;
    private String hashedpassword;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        usernameet = (EditText) findViewById(R.id.usernameet);
        passwordet = (EditText) findViewById(R.id.passwordet);

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
            Toast.makeText(MainActivity.this,"El usuario o la contraseña deben contener al menos un caracter",Toast.LENGTH_SHORT).show();

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
                                            Toast.makeText(MainActivity.this, "La contraseñas coinciden", Toast.LENGTH_SHORT).show();
                                            System.out.println("Las contraseñas coinciden");
                                        }
                                        //sino coinciden Toast con contraseña incorrecta
                                        else{
                                            System.out.println("Las contraseñas no coinciden");
                                            Toast.makeText(MainActivity.this, "La contraseña del usuario " + username + " incorrecta", Toast.LENGTH_SHORT).show();
                                        }

                                        System.out.println("DESDE EL PHP (nom): " + nom);
                                        System.out.println("DESDE EL PHP (con): " + con);

                                    }
                                    //si no existe el usuario preguntarle si desea registrarse en la BD remota
                                    else {
                                        System.out.println("El usuario no existe en la BD");
                                        Toast.makeText(MainActivity.this, "El usuario " + username + " no existe", Toast.LENGTH_SHORT).show();
                                        //preguntar si quiere insertarlo con un dialog
                                        DialogFragment df = new DialogoLogin(username);
                                        df.show(getSupportFragmentManager(), "login");


                                        //insertarUsuario(username, hashedpassword);
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


    public void insertarUsuario(String username, String password){
        System.out.println("INSERTAR USUARIO");
       /*
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        */

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






    public void onClickInsertar(View v){
        System.out.println("INSERTAR USUARIO");
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("password", password)
                .build();


        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDInsertarUsuario.class)
                .setInputData(datos)
                .build();

        if (username.matches("") || password.matches("") ) {
            System.out.println("La Contraseña o el Usuario debe tener al menos 1 caracter");
            Toast.makeText(MainActivity.this,"El usuario o la contraseña deben contener al menos un caracter",Toast.LENGTH_SHORT).show();

        }
        else{ //si ha tecleado algo en los 2 campos --> DialogoLogin con contructor (String user)
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



    }



    public void onClickComprobar(View v){
        System.out.println("COMPROBAR USUARIO");
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("password", password)
                .build();


        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarUsuario.class)
                .setInputData(datos)
                .build();

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            //TextView textViewResult = findViewById(R.id.textoResultado);
                            try {
                                String result = workInfo.getOutputData().getString("resultado");
                                //JSONArray jsonArray = null;
                                JSONParser parser = new JSONParser();
                                JSONObject json = null;

                                json = (JSONObject) parser.parse(result);

                                if (json!=null) {
                                    String nom = (String) json.get("nombre");
                                    String con = (String) json.get("contraseña");

                                    System.out.println("DESDE EL PHP (nom): " + nom);
                                    System.out.println("DESDE EL PHP (con): " + con);
                                }
                                else{
                                    System.out.println("El usuario no existe en la BD");
                                }
                            }catch (ParseException e) {
                                e.printStackTrace();
                            }

                            //textViewResult.setText(resultadoString);
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

    }
}