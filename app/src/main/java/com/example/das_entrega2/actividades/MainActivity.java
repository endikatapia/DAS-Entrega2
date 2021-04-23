package com.example.das_entrega2.actividades;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.das_entrega2.R;
import com.example.das_entrega2.ServicioMusica;
import com.example.das_entrega2.dialogos.DialogoPostre;
import com.example.das_entrega2.workers.ConexionOverpassAPI;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DialogoPostre.ListenerdelDialogo {

    TextView bienvenido;
    RecyclerView lalista;
    ElAdaptadorRecycler eladaptador;
    int[] categorias;
    String comidaPref;
    String user;
    TextView userr;
    SharedPreferences prefs;


    private ServicioMusica elservicio;
    private static boolean mBound;
    private boolean reproduciendo=false;
    private ServiceConnection laconexion = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            elservicio = ((ServicioMusica.miBinder) service).obtenServicio();
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            elservicio = null;
        }
    };;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //establecer el idioma que había guardado en las preferencias --> por defecto: castellano
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_main);

        //poner la toolbar personalizada
        setSupportActionBar(findViewById(R.id.toolbar));



        //Navigation drawer --> menú lateral deslizante desde la parte izquierda del dispositivo
        final DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        NavigationView elnavigation = findViewById(R.id.elnavigationview);
        elnavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               switch (item.getItemId()){
                   //Primera opción --> captar imagenes con la camara y guardarlas y mostrarlas utilizando Firebase
                   case R.id.captarImagen:
                       System.out.println("Ir a la actividad de la camara");
                       //Intent a ActivityCamara
                       Intent intentcamara = new Intent(MainActivity.this, ActivityCamara.class);
                       startActivity(intentcamara);
                       break;

                   //Segunda opción --> Localiza los restaurantes en un radio de 350 metros desde la ubicación actual del usuario
                   case R.id.maps:
                       System.out.println("Ir a google Maps");
                       //Intent a ActivityMapa
                       Intent intentmapa = new Intent(MainActivity.this, ActivityMapa.class);
                       startActivity(intentmapa);
                       break;

                   //Tercera opción --> Reproductor de música (poner la música en marcha)
                   case R.id.musicaplay:
                       System.out.println("PLAY musica");
                       //mirar si los permisos para leer el estado del móvil estan concedidos; en caso afirmativo
                       if (permisoReadPhoneState()){
                           //la variable mBound se usa para el funcionamiento correcto del servicio cuando se rota el dispositivo
                           //o cuando la aplicación pasa a estar en segundo plano
                           if (!mBound) {
                               //Se une la actividad al servicio mediante bindService
                               Intent myService = new Intent(getApplicationContext(), ServicioMusica.class);
                               bindService(myService, laconexion, Context.BIND_AUTO_CREATE);
                               mBound = true;
                           }

                           if (!reproduciendo) {
                               //en caso de que la música no se este reproduciendo ponemos el servicio en marcha
                               Intent myService = new Intent(getApplicationContext(), ServicioMusica.class);

                               if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                   startForegroundService(myService);
                               } else {
                                   startService(myService);
                               }
                               bindService(myService, laconexion, Context.BIND_AUTO_CREATE);
                               //reproduciendo pasará a ser true
                               reproduciendo = true;
                           }

                       }

                       break;

                   //Cuarta opción --> Reproductor de música (detener la música)
                   case R.id.musicapause:
                       System.out.println("PAUSE musica");
                       System.out.println("ESTA EN MARCHA" + estaElServicioEnMarcha(ServicioMusica.class));

                       //parar el servicio
                       Intent intent = new Intent(getApplicationContext(), ServicioMusica.class);
                       stopService(intent);

                       //si el servicio está en marcha se desenlaza
                       //Un servicio enlazado se detendrá al destruir el último de los enlaces
                       if (estaElServicioEnMarcha(ServicioMusica.class)) {
                           unbindService(laconexion);
                           System.out.println("Servicio detenido");
                       }
                       //reproduciendo pasa a ser false
                       reproduciendo = false;

                       break;

               }
               elmenudesplegable.closeDrawers();
               return false;
           }
       });

        //icono del navigationDrawer (hamburger)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_hamburger);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        //TextViews de bienvenida al usuario
        bienvenido= findViewById(R.id.textViewBienvenido);
        userr = findViewById(R.id.userr);


        //cuando rotas, minimizas o en general, se hace onDestroy, se debe mantener el nombre del usuario
        //Para ello, lo recuperamos con savedInstanceState.
        if (savedInstanceState != null) {
            user = savedInstanceState.getString("usuario");
            System.out.println("Usuario: " + user);
            userr.setText(user);
            userr.setTypeface(null, Typeface.BOLD); //letra negrita para el nombre

        }
        //tratamos el nombre de usuario que viene de la actividad ActivityLogin
        //en este caso solo entrara aqui la primera vez que se cree esta actividad
        else{
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                user = extras.getString("usuario");
                userr.setText(user);
                userr.setTypeface(null, Typeface.BOLD); //letra negrita para el nombre
            }
        }
        //en ambos casos guardamos ese nombre y lo establecemos en el TextView userr

        //Se recoge el elemento de la interfaz gráfica
        //la lista (recycledView + cardView) que va a contener las categorias de comida
        lalista = findViewById(R.id.rv);


        //guardar en un array los nombres de las categorias
        String piz = getString(R.string.pizzas);
        String ens = getString(R.string.ensaladas);
        String ar = getString(R.string.arroces);
        String espa = getString(R.string.espaguetis);
        String espec = getString(R.string.especialidad);
        String[] nombres={piz, ens, ar, espa, espec};

        //Con las preferencias recuperamos la comida favorita del usuario
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Por defecto no tendra ninguna marcada, por lo que le asignaremos null
        if (prefs.contains("comidapref")) {
            comidaPref = prefs.getString("comidapref", null);
            //Se cargara laLista en funcion de la comida favorita del usuario
            //Se crea el adaptador con los datos a mostrar y se asigna al RecyclerView
            if (comidaPref.equals("Pizza")) {
                //si la preferencia del cliente son las pizzas la imagen del cardView pizza se pondra con fondo verde
                categorias = new int[]{R.drawable.pizzaprefs, R.drawable.ensalada, R.drawable.arrozz, R.drawable.esp, R.drawable.las};
                eladaptador = new ElAdaptadorRecycler(nombres, categorias);
                lalista.setAdapter(eladaptador);
            } else if (comidaPref.equals("Ensalada")) {
                //si la preferencia del cliente son las ensaladas la imagen del cardView ensalada se pondra con fondo verde
                categorias = new int[]{R.drawable.pizza, R.drawable.ensaladaprefs, R.drawable.arrozz, R.drawable.esp, R.drawable.las};
                eladaptador = new ElAdaptadorRecycler(nombres, categorias);
                lalista.setAdapter(eladaptador);
            } else if (comidaPref.equals("Arroz")) {
                //si la preferencia del cliente es el arroz la imagen del cardView arroz se pondra con fondo verde
                categorias = new int[]{R.drawable.pizza, R.drawable.ensalada, R.drawable.arrozprefs, R.drawable.esp, R.drawable.las};
                eladaptador = new ElAdaptadorRecycler(nombres, categorias);
                lalista.setAdapter(eladaptador);
            } else if (comidaPref.equals("Espagueti")) {
                //si la preferencia del cliente son las espagueti la imagen del cardView espagueti se pondra con fondo verde
                categorias = new int[]{R.drawable.pizza, R.drawable.ensalada, R.drawable.arrozz, R.drawable.espprefs, R.drawable.las};
                eladaptador = new ElAdaptadorRecycler(nombres, categorias);
                lalista.setAdapter(eladaptador);
            } else if (comidaPref.equals("Especialidad")) {
                //si la preferencia del cliente es la especialidad la imagen del cardView especialidad se pondra con fondo verde
                categorias = new int[]{R.drawable.pizza, R.drawable.ensalada, R.drawable.arrozz, R.drawable.esp, R.drawable.lasprefs};
                eladaptador = new ElAdaptadorRecycler(nombres, categorias);
                lalista.setAdapter(eladaptador);
            }
        }
        else {//if (comidaPref==null){ //Sin preferencias
            //Si no tiene ninguna preferencia se pondran todas con fondo normal
            categorias = new int[]{R.drawable.pizza, R.drawable.ensalada, R.drawable.arrozz, R.drawable.esp, R.drawable.las};
            eladaptador = new ElAdaptadorRecycler(nombres, categorias);
            lalista.setAdapter(eladaptador);

        }

        //se utiliza un LinearLayout horizontal con scroll donde los elementos se muestran de forma lineal horizontal.
        //setLayoutManager establece lalista con los cardViews
        LinearLayoutManager elLayoutLineal= new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        lalista.setLayoutManager(elLayoutLineal);


    } //final onCreate



    //permiso para leer el estado del telefono (cuando ocurre una llamada se gestiona mediante el Receiver LlamadasReceiver)
    public boolean permisoReadPhoneState(){
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_PHONE_STATE},
                    0);

        }
        else{
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            return  true;
        }
        return false;
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 0:{
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                }
                else {
                // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA

                }
                return;
            }

        }
    }



    //este método devuelve un boolean diciendo si el servicio está en marcha o no
    private boolean estaElServicioEnMarcha(Class<?> serviceClass){
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if(serviceClass.getName().equals(service.service.getClassName())){
                return true;
            }
        }
        return false;
    }


    //con este método se recupera al anterior de la música.
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        this.reproduciendo = savedInstanceState.getBoolean("reproduciendo");

    }

    //cuando se hace onStop se mirá si la música esta reproduciendo
    //en caso afimativo se desenlaza la conexión entre la actividad y el servicio.
    @Override
    public void onStop(){
        super.onStop();
        if (mBound && reproduciendo){
            unbindService(laconexion);
            mBound=false;

        }

    }





    //guardar el usuario en el Bundle cuando se haga onDestroy
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        user  = userr.getText().toString();
        outState.putString("usuario", user);

        //cuando se rote guardar el estado de la musica
        outState.putBoolean("reproduciendo", reproduciendo);

    }


    //AL HACER CLICK EN CONTINUAR PEDIDO (boton) --> enseñar Dialogo DiologoPostre
    public void onClickContinuar(View v){
        System.out.println("Pulsado CONTINUAR CON EL PEDIDO");
        DialogFragment df = new DialogoPostre();
        df.show(getSupportFragmentManager(),"postre");
    }

    //al pulsar si en el dialogo ir a DiologoPostre
    public void alpulsarSi(){
        Intent i2 = new Intent(MainActivity.this, ActivityPostre.class);
        startActivity(i2);
    }


    //MENU DE TOOLBAR CON PREFERENCIAS
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu); //inflar el menu definido en menu.xml
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            //primera opcion de ajustes, para establecer preferencias
            //entre ellas la comida favorita y el idioma preferido
            case R.id.opcion1: {
                System.out.println("Ajustes");
                Intent intentPreferencias = new Intent(MainActivity.this, ActivityPreferencias.class);
                intentPreferencias.putExtra("usuario",user); //le pasamos el usuario como extra a la actividad de las prefs
                startActivity(intentPreferencias);

                break;

            }
            //INTENT IMPLICITO --> ABRE EL NAVEGADOR para mas informacion sobre la comida italiana con Intent.ACTION_VIEW
            case R.id.opcion2: {
                System.out.println("Información sobre comida Italiana");
                Intent intentInfo = new Intent(Intent.ACTION_VIEW, Uri.parse("https://blog.thefork.com/es/gastronomia-italia/"));
                startActivity(intentInfo);

                break;

            }

            //cuando se pulse en el navigation drawer --> para que se abra
            case android.R.id.home: {
                final DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
                elmenudesplegable.openDrawer(GravityCompat.START);
                return true;
            }


        }
        return super.onOptionsItemSelected(item);
    }


    //al pulsar el boton de atras la actividad se minimiza
    //Asi evitaremos incongruencias en la pila de actividades
    @Override
    public void onBackPressed() {
        final DrawerLayout elmenudesplegable = findViewById(R.id.drawer_layout);
        //si el navigationDrawer esta abierto que se cierre
        if (elmenudesplegable.isDrawerOpen(GravityCompat.START)) {
            elmenudesplegable.closeDrawer(GravityCompat.START);
        } else {
            //sino poner la app en segundo plano
            //super.onBackPressed();
            this.moveTaskToBack(true);
        }
    }


}