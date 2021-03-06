package com.example.das_entrega2.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.BackoffPolicy;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.das_entrega2.PasswordAuth;
import com.example.das_entrega2.workers.ConexionBDComprobarToken;
import com.example.das_entrega2.workers.ConexionBDComprobarUsuario;
import com.example.das_entrega2.workers.ConexionBDGetTokens;
import com.example.das_entrega2.workers.ConexionBDInsertarToken;
import com.example.das_entrega2.workers.ConexionEnviarNotificacion;
import com.example.das_entrega2.R;
import com.example.das_entrega2.miBD;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ActivityPedido extends AppCompatActivity {

    TextView tvPrecio;
    private String[] partesPlato;
    private double precio;
    ArrayAdapter eladaptador;
    miBD gestorDB;
    ListView lv2;
    ArrayList<Double> precios = new ArrayList<>();

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

        setContentView(R.layout.activity_pedido);


        //OBTENER EL TOKEN DESDE FIREBASE
        //Al volver a la carta se lanzara una notificacion por mensajeria FCM a todos los tokens que tengan registrada la app
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {

                            return;
                        }
                        //Conocer el token que tiene asignado un dispositivo
                        String token = task.getResult().getToken();
                        System.out.println("TOKEN FCM: " + token);

                        //En este m??todo se comprueba si el token esta registrado en la BD remota 'tokens'con un Worker
                        //Si no esta registrado lo insertar?? mediante otro Worker
                        comprobarInsertarToken(token);

                    }
                });



        //Definir los platos que estan en mas de 1 idioma con getString. De esta manera mirara en strings.xml.
        String p2 = getString(R.string.pizza2);
        String p4 = getString(R.string.pizza4);
        String p5 = getString(R.string.pizza5);
        String p6 = getString(R.string.pizza6);
        String p8 = getString(R.string.pizza8);

        String sad1 = getString(R.string.ensalada1);
        String sad2 = getString(R.string.ensalada2);
        String sad3 = getString(R.string.ensalada3);
        String sad4 = getString(R.string.ensalada4);
        String sad5 = getString(R.string.ensalada5);
        String sad6 = getString(R.string.ensalada6);

        String ar1 = getString(R.string.arroz1);
        String ar2 = getString(R.string.arroz2);
        String ar3 = getString(R.string.arroz3);

        String espa1 = getString(R.string.espagueti1);
        String espa2 = getString(R.string.espagueti2);
        String espa3 = getString(R.string.espagueti3);
        String espa4 = getString(R.string.espagueti4);
        String espa5 = getString(R.string.espagueti5);

        String spc1 = getString(R.string.especialidad1);
        String spc2 = getString(R.string.especialidad2);
        String spc4 = getString(R.string.especialidad4);
        String spc5 = getString(R.string.especialidad5);

        String pst2 = getString(R.string.postre2);
        String pst3 = getString(R.string.postre3);


        lv2 = findViewById(R.id.lv2);
        tvPrecio = (TextView) findViewById(R.id.textViewPrecio);

        //para hacer el select en la BD Pedidos
        gestorDB = new miBD (this, "Pedidos", null, 1);

        Bundle extras= getIntent().getExtras();
        if (extras != null){
            //para cerrar la notificacion al pulsar ver pedido mediante el id
            int id=extras.getInt("id");
            NotificationManager elManager = (NotificationManager)
                    getSystemService(Context.NOTIFICATION_SERVICE);
            elManager.cancel(id);

            String elementos = extras.getString("elementos");
            //partimos el string en cada plato
            partesPlato=elementos.split(", ");

            for (int i=0; i<partesPlato.length;i++){
                System.out.println("Plato:" + partesPlato[i]);
                //si el plato se escribe de la misma manera para los 3 para los 3 no hay problema
                //solo cogemos el precio
                if (partesPlato[i].equals("Pizza Margarita")){ precio= 10; }
                else if (partesPlato[i].equals("Pizza Bolo??esa")){
                    partesPlato[i]=p2; //guardara ese plato depende en el idioma que estemos
                    precio=13.50; }
                else if (partesPlato[i].equals("Pizza Carbonara")){ precio=13.50; }
                else if (partesPlato[i].equals("Pizza 4 Quesos")){
                    partesPlato[i]=p4;
                    precio=12.50; }
                else if (partesPlato[i].equals("Pizza Napolitana")){
                    partesPlato[i]=p5;
                    precio=11.50; }
                else if (partesPlato[i].equals("Pizza Atun")){
                    partesPlato[i]=p6;
                    precio=12.50; }
                else if (partesPlato[i].equals("Pizza Sorrento")){ precio=13; }
                else if (partesPlato[i].equals("Pizza Tropical")){
                    partesPlato[i]=p8;
                    precio=13.50; }

                else if (partesPlato[i].equals("Ensalada Mixta")){
                    partesPlato[i]=sad1;
                    precio=7; }
                else if (partesPlato[i].equals("Ensalada Tropical")){
                    partesPlato[i]=sad2;
                    precio=9; }
                else if (partesPlato[i].equals("Ensalada de Pasta")){
                    partesPlato[i]=sad3;
                    precio=9; }
                else if (partesPlato[i].equals("Ensalada Campera")){
                    partesPlato[i]=sad4;
                    precio=10; }
                else if (partesPlato[i].equals("Ensalada Capresse")){
                    partesPlato[i]=sad5;
                    precio=9; }
                else if (partesPlato[i].equals("Ensalada Fruti di mare")){
                    partesPlato[i]=sad6;
                    precio=9.5; }

                else if (partesPlato[i].equals("Risotto de Setas")){
                    partesPlato[i]=ar1;
                    precio=10; }
                else if (partesPlato[i].equals("Risotto Marinero")){
                    partesPlato[i]=ar2;
                    precio=10; }
                else if (partesPlato[i].equals("Risotto 4 Quesos")){
                    partesPlato[i]=ar3;
                    precio=11; }

                else if (partesPlato[i].equals("Espagueti al Pesto")){
                    partesPlato[i]=espa1;
                    precio=9; }
                else if (partesPlato[i].equals("Espagueti Bolo??esa")){
                    partesPlato[i]=espa2;
                    precio=9; }
                else if (partesPlato[i].equals("Espagueti Carbonara")){
                    partesPlato[i]=espa3;
                    precio=9; }
                else if (partesPlato[i].equals("Espagueti Siciliana")){
                    partesPlato[i]=espa4;
                    precio=10; }
                else if (partesPlato[i].equals("Espagueti con Gambas")){
                    partesPlato[i]=espa5;
                    precio=11; }

                else if (partesPlato[i].equals("Lasagna de Carne")){
                    partesPlato[i]=spc1;
                    precio=10.5; }
                else if (partesPlato[i].equals("Ravioli de Setas")){
                    partesPlato[i]=spc2;
                    precio=11; }
                else if (partesPlato[i].equals("Tagliatelle al Andrea")){ precio=10; }
                else if (partesPlato[i].equals("Carpaccio de Carne")){
                    partesPlato[i]=spc4;
                    precio=12; }
                else if (partesPlato[i].equals("Provolone a la Plancha")){
                    partesPlato[i]=spc5;
                    precio=9; }

                else if (partesPlato[i].equals("Profiteroles")){ precio= 5; }
                else if (partesPlato[i].equals("Tarta de queso")){
                    partesPlato[i]=pst2;
                    precio=4; }
                else if (partesPlato[i].equals("Tiramis??")){
                    partesPlato[i]=pst3;
                    precio=6.50; }
                else if (partesPlato[i].equals("Panna cotta")){ precio=6;}

                //a??adira al ArrayList<Double> precios el precio de ese plato
                precios.add(precio);
            }


            //Se genera un adaptador y se le indican qu?? datos debe mostrar (partesPlato) y c??mo debe mostrarlos (simple_list_item_2)
            //Creamos el ArrayAdapter con la posibilidad de visualizar 2 textView en una fila --> simple_list_item_2
            eladaptador= new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2,android.R.id.text1,partesPlato){
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    View vista= super.getView(position, convertView, parent);
                    TextView lineaprincipal=(TextView) vista.findViewById(android.R.id.text1);
                    TextView lineasecundaria=(TextView) vista.findViewById(android.R.id.text2);
                    //linea principal: poner el plato
                    lineaprincipal.setText(partesPlato[position]);
                    //linea secundaria: poner el precio
                    String pr = getString(R.string.precio);
                    lineasecundaria.setText(pr + String.valueOf(precios.get(position)) + "???");

                    return vista;
                }
            };
            lv2.setAdapter(eladaptador);

            //Text View del precioTotal
            double precioT = extras.getDouble("precio");
            tvPrecio.setText(tvPrecio.getText()+": " + precioT + "???");
        }
    }


    //Este m??todo comprueba si el token que recibe esta registrado en la BD remota 'tokens' mediante un Worker
    //Si no esta registrado lo insertar?? mediante otro Worker
    public void comprobarInsertarToken(String token){
        //Al worker ConexionBDComprobarToken se le pasan como datos el token generado por Firebase
        Data datos = new Data.Builder()
                .putString("token", token)
                .build();

        //El worker ConexionBDComprobarToken comprobar?? si el token que se le pasa esta en la BD remota tokens
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDComprobarToken.class)
                .setInputData(datos)
                .build();

        //En ActivityPedido, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
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
                                //SELECT * FROM tokens WHERE token='$token'
                                //Por lo tanto el resultado ser?? un JSONObject que contendr?? un solo objeto con su token
                                json = (JSONObject) parser.parse(result);

                                //si el resultado de la consulta devuelve una l??nea, es decir, si el
                                //token est?? registrado en la BD remota de phpmyAdmin tokens.
                                if (json != null) {
                                    //se obtiene el token
                                    String token = (String) json.get("token");
                                    System.out.println("DESDE EL PHP (token): " + token);

                                }
                                else{ //Si no devuelve nada
                                    // El token no existe --> se a??ade a la BD remota mediante el m??todo insertarToken
                                    System.out.println("El token se va a??adir a la BD remota porque no existe");
                                    insertarToken(token);

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);

    }



    //este m??todo insertara el token en la BD remota tokens utilizando un Worker.
    public void insertarToken(String token){
        System.out.println("INSERTAR TOKEN EN LA BD REMOTA");
        //Al worker ConexionBDInsertarToken se le pasan como datos el token
        Data datos = new Data.Builder()
                .putString("token", token)
                .build();

        //El worker ConexionBDInsertarToken insertar?? un nuevo token en la BD remota tokens.
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDInsertarToken.class)
                .setInputData(datos)
                .build();

        //En ActivityPedido, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
        //Para insertar el token se utiliza la siguiente consulta en el php:
        //INSERT INTO tokens VALUES ('$token')
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if (workInfo != null && workInfo.getState().isFinished()) {
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }



    //este metodo conseguir?? todos los tokens que hay en la BD remota y
    //enviar?? una notificaci??n con mensajer??a FCM a todos esos tokens.
    public void conseguirTokensYenviarNotificacion(){
        //CONSEGUIR TODOS LOS TOKENS
        //El worker ConexionBDGetTokens conseguir?? la lista de tokens de la BD remota tokens.
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDGetTokens.class)
                .build();

        //En ActivityPedido, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            //recogemos el resultado que viene desde el worker al dar SUCESS
                            String result = workInfo.getOutputData().getString("resultado");

                            //La consulta que se realiza en el php es la siguiente -->
                            //SELECT * FROM tokens
                            //Por lo tanto el resultado ser?? un JSONArray que contendr?? varios elementos con la clave 'token'
                            JSONArray jsonArray = null;
                            //crear el ArrayList donde se van a ir a??adiendo los tokens
                            ArrayList<String> tokensAL = new ArrayList<>();
                            try {

                                jsonArray = new JSONArray(result);

                                for(int i = 0; i < jsonArray.length(); i++)
                                {
                                    //'token' es la clave de los datos en el JSON
                                    String token = jsonArray.getJSONObject(i).getString("token");
                                    //se a??ade el token al ArrayList
                                    tokensAL.add(token);
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            String resultadoStringTokens = tokensAL.toString();
                            System.out.println("TOKENS: " + resultadoStringTokens);


                            //Convertir el ArrayList<String> a String[]
                            //GUARDAR LOS TOKENS EN UNA ARRAY DE STRINGS
                            String[] tokens = tokensAL.toArray(new String[tokensAL.size()]);

                            for (int i=0; i<tokens.length;i++){
                                System.out.println("TOKEN " + i + ": " + tokens[i]);
                            }


                            //este m??todo enviar?? una notificaci??n a todos los tokens
                            //diciendo que se a realizado un nuevo pedido
                            enviarNotificacion(tokens);


                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);

    }


    //Este m??todo enviar?? una notificaci??n a todos los tokens que recibe desde el Worker ConexionBDGetTokens
    public void enviarNotificacion(String[] tokens){
        //conseguimos el precio total del ??ltimo pedido --> llega desde ActivityPostre
        Bundle extras= getIntent().getExtras();
        if (extras != null) {
            double precioT = extras.getDouble("precio");
            //convertimos el double a String
            String preciostr = String.valueOf(precioT);
            System.out.println("Precio Total (FCM): " + preciostr);

            //Al worker ConexionEnviarNotificacion se le pasan como datos el precio del ??ltimo pedido
            //y todos los tokens a los cuales se les va a enviar la notificaci??n
            Data datos = new Data.Builder()
                    .putString("precio", preciostr)
                    .putStringArray("tokens",tokens)
                    .build();

            //El worker ConexionEnviarNotificacion enviar?? la notificaci??n usando el ServicioFirebase a todos los tokens registrados.
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionEnviarNotificacion.class)
                    .setInputData(datos)
                    .build();

            //En ActivityPedido, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);

        }

    }



    //cuando se clicke en Volver a la Carta ser?? el momento en el que se envi?? la notificaci??n
    //a todos los tokens
    public void onClickIrCarta(View v){
        //CONSEGUIR LOS TOKENS
        //ENVIAR LA NOTIFICACION A TODOS LOS TOKENS
        conseguirTokensYenviarNotificacion();

        //cuando se pulse volver a la carta se podr?? realizar otro pedido
        Intent intentVolverCarta = new Intent(ActivityPedido.this, MainActivity.class);
        //Guardar lo que habia en MainActivity con el flag FLAG_ACTIVITY_REORDER_TO_FRONT
        intentVolverCarta.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intentVolverCarta);
    }

    //al pulsar el boton de atras la actividad se minimiza
    //Asi evitaremos incongruencias en la pila de actividades
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }



}