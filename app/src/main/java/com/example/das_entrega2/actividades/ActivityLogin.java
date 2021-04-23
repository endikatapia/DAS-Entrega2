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
    }


    //Al clickar en el boton LOGIN
    public void onClickLogin(View v) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println("LOGIN USUARIO");
        //recoger el usuario introducido en el primer EditText
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        //recoger la contraseña introducida en el segundo EditText
        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        //si el usuario o la contraseña están vacías lanzar toast diciendo que
        //la Contraseña o el Usuario deben tener al menos 1 caracter
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

        } else { //si los datos tienen al menos un caracter

            //sacamos el hash de la contraseña introducida ccon el método generateStrongPasswordHash
            //de la clase PasswordAuth. Este método se encargara de generar un fuerte hash con salt trás hacer varias iteraciones
            //El hash tendrá el siguiente formato:
            // 1000:2c2e10c36e728a45f810d3a74f3b207b:16fb6503b10480d59ba35a14190d997891aca8c649c81478b3473ed5e308e65a08864a264
            hashedpassword = PasswordAuth.generateStrongPasswordHash(password);
            System.out.println("HASHED PASSWORD: " + hashedpassword);

            //Al worker ConexionBDComprobarUsuario se le pasan como datos el usuario y la contraseña hasheada.
            Data datos = new Data.Builder()
                    .putString("username", username)
                    .putString("password", hashedpassword)
                    .build();

            //El worker ConexionBDComprobarUsuario comprobara si el usuario que se le pasa esta en la BD remota usuarios
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarUsuario.class)
                    .setInputData(datos)
                    .build();

            //En ActivityLogin, se añade un Observer a la tarea antes de encolarla usando WorkManager
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                try {
                                    //recogemos el resultado que viene desde el worker al dar SUCESS
                                    String result = workInfo.getOutputData().getString("resultado");
                                    JSONParser parser = new JSONParser();
                                    JSONObject json = null;

                                    //La consulta que se realiza en el php es la siguiente -->
                                    //SELECT * FROM usuarios WHERE nombre='$nombre'
                                    //Por lo tanto el resultado será un JSONObject que contendrá un solo objeto con su nombre y contraseña hasheada.
                                    json = (JSONObject) parser.parse(result);

                                    //si el resultado de la consulta devuelve una línea, es decir, si el
                                    //usuario está registrado en la BD remota de phpmyAdmin usuarios.
                                    if (json != null) {
                                        //si existe el usuario --> comprobar si la contraseña es correcta
                                        String nom = (String) json.get("nombre");
                                        String con = (String) json.get("contraseña");

                                        //Obtenemos 'nom': nombre de usuario y 'con': contraseña que viene con el hash.
                                        //El metodo validatePassword comprobará si la contraseña introducida en el EditText coincide
                                        //con la contraseña hasheada que viene desde el php.
                                        boolean lasContrasenasCoinciden = PasswordAuth.validatePassword(password,con);
                                        //si la contraseña introducida coincide con su hash --> Intent a MainActivity
                                        if (lasContrasenasCoinciden){
                                            //INTENT A MAIN ACTIVITY
                                            System.out.println("El usuario "+username+ " existe Y la contraseña es correcta --> MAIN ACTIVITY");
                                            Intent intentMainActivity = new Intent(ActivityLogin.this, MainActivity.class);
                                            //guardamos el usuario para que salga en MainActivity
                                            intentMainActivity.putExtra("usuario",username);
                                            startActivity(intentMainActivity);
                                        }

                                        //si no coinciden se muestra un Toast con mensaje --> contraseña incorrecta
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
                                            toast.show();
                                        }

                                        //datos recibidos desde el PHP
                                        System.out.println("DESDE EL PHP (nombre): " + nom);
                                        System.out.println("DESDE EL PHP (contraseñaHash): " + con);

                                    }

                                    //si el resultado de la consulta devuelve null, es decir, si el
                                    //usuario NO está registrado en la BD remota de phpmyAdmin usuarios.
                                    else{
                                        //Toast diciendole que el usuario no esta registrado --> para registrarse pulsar Registrate
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
                                        toast.show();

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


    }//fin onClickLogin




    //Al clickar en el boton REGISTRARSE
    public void onClickRegistrate(View v) throws InvalidKeySpecException, NoSuchAlgorithmException {
        System.out.println("REGISTRAR USUARIO");
        //recoger el usuario introducido en el primer EditText
        String username = usernameet.getText().toString();
        System.out.println("Username: " + username);

        //recoger la contraseña introducida en el segundo EditText
        String password = passwordet.getText().toString();
        System.out.println("Password: " + password);

        //si el usuario o la contraseña están vacías lanzar toast diciendo que
        //la Contraseña o el Usuario deben tener al menos 1 caracter
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
        } else {//si los datos tienen al menos un caracter

            //sacamos el hash de la contraseña introducida ccon el método generateStrongPasswordHash
            //de la clase PasswordAuth. Este método se encargara de generar un fuerte hash con salt trás hacer varias iteraciones
            //El hash tendrá el siguiente formato:
            // 1000:2c2e10c36e728a45f810d3a74f3b207b:16fb6503b10480d59ba35a14190d997891aca8c649c81478b3473ed5e308e65a08864a264
            hashedpassword = PasswordAuth.generateStrongPasswordHash(password);
            System.out.println("HASHED PASSWORD: " + hashedpassword);

            //Al worker ConexionBDComprobarUsuario se le pasan como datos el usuario y la contraseña hasheada.
            Data datos = new Data.Builder()
                    .putString("username", username)
                    .putString("password", hashedpassword)
                    .build();


            //El worker ConexionBDComprobarUsuario comprobara si el usuario que se le pasa esta en la BD remota usuarios
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarUsuario.class)
                    .setInputData(datos)
                    .build();

            //En ActivityLogin, se añade un Observer a la tarea antes de encolarla usando WorkManager
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                try {
                                    //recogemos el resultado que viene desde el worker al dar SUCESS
                                    String result = workInfo.getOutputData().getString("resultado");
                                    JSONParser parser = new JSONParser();
                                    JSONObject json = null;

                                    //La consulta que se realiza en el php es la siguiente -->
                                    //SELECT * FROM usuarios WHERE nombre='$nombre'
                                    //Por lo tanto el resultado será un JSONObject que contendrá un solo objeto con su nombre y contraseña hasheada.
                                    json = (JSONObject) parser.parse(result);

                                    if (json != null) {
                                        //si existe el usuario en la BD remota usuarios --> TOAST AVISANDOLE
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

                                        System.out.println("DESDE EL PHP (nom): " + nom);
                                        System.out.println("DESDE EL PHP (con): " + con);

                                    }
                                    //si no existe el usuario preguntarle si desea registrarse en la BD remota mediante el
                                    //dialogo DialogoLogin
                                    else {
                                        System.out.println("El usuario no existe en la BD");
                                        //preguntar si quiere insertarlo mediante el Dialogo, se le pasa de parametro al constructor
                                        //el nombre del usuario introducido en el EditText
                                        DialogFragment df = new DialogoLogin(username);
                                        df.show(getSupportFragmentManager(), "login");
                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }

    }//fin onClickRegistrate


    //este método insertara al Usuario en la BD remota usuarios utilizando un Worker.
    public void insertarUsuario(String username, String password){
        System.out.println("INSERTAR USUARIO");

        //Al worker ConexionBDInsertarUsuario se le pasan como datos el usuario y la contraseña hasheada
        //Estos datos se reciben cuando el usuario pulse SI en el DialogoLogin.
        Data datos = new Data.Builder()
                .putString("username", username)
                .putString("password", password)
                .build();

        //El worker ConexionBDComprobarUsuario insertará un nuevo usuario en la BD remota usuarios.
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDInsertarUsuario.class)
                .setInputData(datos)
                .build();

        //En ActivityLogin, se añade un Observer a la tarea antes de encolarla usando WorkManager
        //Para insertar al usuario se utiliza la siguiente consulta en el php:
        //INSERT INTO usuarios VALUES ('$nombre','$contraseña')
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }



    //Al pulsar si en el dialogo, es decir, si quiere registrarse en la BD remota
    @Override
    public void alpulsarSi() {
        //se le llama al método insertarUsuario y se le pasan de parametros el usuario
        //y la contraseña con su respectivo hash.
        insertarUsuario(usernameet.getText().toString(),hashedpassword);
        String añad = getString(R.string.aña);

        //TOAST PERSONALIZADO con layout_toast.xml --> usuario añadido
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