package com.example.das_entrega2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;
import androidx.preference.PreferenceManager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;

public class ActivityPostre extends AppCompatActivity implements FragmentLVMultipleChoice.listenerDelFragment,DialogoFinal.ListenerdelDialogo {

    ListView listView;
    ArrayAdapter eladaptador;
    ArrayList<String> postres = new ArrayList<>();
    OutputStreamWriter fichero;
    BufferedReader ficherointerno;
    private String[] partesPlato;
    double precio;
    miBD gestorDB;
    Intent intentVerPedido;
    Button botonFinalizar;
    private String[] datosPostre;
    private String[] ingredientesPostre;

    //POSTRES
    int[] comidaPostre={R.drawable.profiteroles,R.drawable.tartaqueso,R.drawable.tiramisu,R.drawable.pannacotta};
    double[] preciosPostre = {5,4,6.5,6};

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

        //Si el idioma es castellano (es) cargar los fragments con los datos de los postres en castellano
        if (idioma.equals("es")) {
            datosPostre= new String[]{"Profiteroles", "Tarta de queso", "Tiramisú", "Panna cotta"};
            ingredientesPostre= new String[]{"leche, mantequilla, harina, huevos, limón, canela, azúcar", "galletas, nata, azucar, queso, mermelada", "queso mascarpone, yemas, azúcar glass, cacao en polvo, café fuerte", "nata, azúcar, gelatina, vainilla, canela"};

            //Si el idioma es ingles (en) cargar los fragments con los datos de los postres en ingles
        } else if (idioma.equals("en")) {
            datosPostre= new String[]{"Profiteroles", "Cheesecake", "Tiramisu", "Panna cotta"};
            ingredientesPostre= new String[]{"milk, butter, flour, eggs, lemon, cinnamon, sugar", "cookies, cream, sugar, cheese, jam", "mascarpone cheese, yolks, icing sugar, cocoa powder, strong coffee", "cream, sugar, gelatin, vanilla, cinnamon"};

            //Si el idioma es italiano (it) cargar los fragments con los datos de los postres en italiano
        } else if (idioma.equals("it")) {
            datosPostre= new String[]{"Profiteroles", "Cheesecake", "Tiramisu", "Panna cotta"};
            ingredientesPostre= new String[]{"latte, burro, farina, uova, limone, cannella, zucchero", "biscotti, panna, zucchero, formaggio, marmellata", "mascarpone, tuorli, zucchero a velo, cacao in polvere, caffè forte", "panna, zucchero, gelatina , vaniglia, cannella"};

        }

        setContentView(R.layout.activity_postre);
        listView=findViewById(R.id.lv);

        botonFinalizar = (Button) findViewById(R.id.buttonFinalizar);
        //para guardar en la BD el pedido realizado
        gestorDB = new miBD (this, "Pedidos", null, 1);
    }



    public void seleccionarElemento(String nombreComida, int imagen, String ingredientes,double precio){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){
            //EL OTRO FRAGMENT EXISTE --> por lo tanto si la orientacion es horizontal se cargara el land/activity_postre.xml
            //enseñando en la parte derecha el fragment con la listView multiple_choice, en el medio los detalles acerca de
            //los postres que seleccionemos y en la izquierda el boton para añadir al pedido los postres seleccionados.
            System.out.println("-------------HORIZONTAL---------");
            //FragmentDetalles se encargara de cargar y visualizar los datos mediante el metodo setDatos.
            //Como parametro le pasaremos el nombre del plato, la imagen, los ingredientes y el precio
            FragmentDetalles elotro=(FragmentDetalles) getSupportFragmentManager().findFragmentById(R.id.fragmentFotoIndv);
            elotro.setDatos(nombreComida,imagen,ingredientes,precio);
        }
        else{
            //EL OTRO FRAGMENT NO EXISTE, HAY QUE LANZAR LA ACTIVIDAD QUE LO CONTIENE -->
            //estando en layout/activity_postre.xml(vertical) se lanza la actividad ActivityDetalles.
            //enseñara los detalles en esa actividad mediante el Fragment fragmentdetalles.xml
            System.out.println("-------------VERTICAL---------");

            Intent i= new Intent(ActivityPostre.this,ActivityDetalles.class);
            i.putExtra("nombre",nombreComida);
            i.putExtra("imagen",imagen);
            i.putExtra("ingredientes",ingredientes);
            i.putExtra("precio",precio);
            startActivity(i);
        }


    }


    //este metodo sera el encargado de poner la lista de los postres
    @Override
    public void ponerLista() {
        //Se genera un adaptador y se le indican qué datos debe mostrar (datosPostre)
        //y cómo debe mostrarlos (simple_list_item_multiple_choice)
        //Creamos el ArrayAdapter con la posibilidad de elegir mas de un item --> simple_list_item_multiple_choice
        eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosPostre);
        listView.setAdapter(eladaptador);

        //Se añade un listener para indicar qué hacer cuando se seleccione algún elemento del ListView:
        //cuando clickamos en un item se selecciona el checkbox y nos sale informacion acerca del postre
        //se gestiona en la función seleccionarElemento de esta misma clase
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                seleccionarElemento(datosPostre[position],comidaPostre[position],ingredientesPostre[position],preciosPostre[position]);
                System.out.println(datosPostre[position]);
            }
        });
    }


    //Cuando se pulse el Boton Finalizar --> Mostrara DialogoFinal
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onClickFinalizar(View v){
        System.out.println("Pulsado FINALIZAR PEDIDO");
        DialogFragment df = new DialogoFinal();
        df.show(getSupportFragmentManager(),"final");

    }


    //Cuando se pulse el Finalizar en el Dialog DialogoFinal
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void alpulsarFinalizar() {
        //DESABILITAR BOTON, tanto horizontal como vertical para que no deje volver a pedir
        botonFinalizar.setClickable(false);

        //PRIMERO GUARDAR EN EL FICHERO LOS POSTRES QUE ESTAN SELECCIONADOS
        //Coger los valores que se han seleccionado de las listView
        this.guardarEnElFichero();

        //GUARDAR EN LA DB Pedido INT PK AUTOINCREMENTO idPedido, STRING elementosPedidos, REAL PrecioTotal
        this.guardarEnLaBBDD();


        //LANZAR LA NOTIFICACION DE QUE HA ACABADO EL PEDIDO Y DAR OPCION DE VER EL PEDIDO
        NotificationManager elManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder elBuilder = new NotificationCompat.Builder(this, "IdCanal");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel elCanal = new NotificationChannel("IdCanal", "NombreCanal",
                    NotificationManager.IMPORTANCE_DEFAULT);
            elManager.createNotificationChannel(elCanal);


            elCanal.setDescription("Descripción del canal");
            elCanal.enableLights(true);
            elCanal.setLightColor(Color.RED);
            elCanal.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            elCanal.enableVibration(true);
        }

        try {
            //al darle a finalizar el fichero se vacia para poder realizar un nuevo pedido
            fichero = new OutputStreamWriter(openFileOutput("ficheroPedido.txt", Context.MODE_PRIVATE));
            fichero.close();
        }
        catch  (IOException e) {
            System.out.println("Error");
        }


        //INTENT PARA VER EL PEDIDO
        //Opcion para ver el ultimo pedido --> mostralo en ActivityPedido
        PendingIntent intentEnNot2 = PendingIntent.getActivity(this, 1, intentVerPedido, PendingIntent.FLAG_UPDATE_CURRENT);


        String fp = getString(R.string.hasfin);
        String pf = getString(R.string.pedfinn);
        String vtp = getString(R.string.verpedi);
        //configurar notificacion
        elBuilder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.logorestaurante))
                .setSmallIcon(android.R.drawable.stat_sys_warning)
                .setContentTitle("Ristorante Endika")
                .setContentText(fp)
                .setSubText(pf)
                .setVibrate(new long[]{0, 1000, 500, 1000})
                .addAction(android.R.drawable.ic_menu_view,vtp, intentEnNot2)
                .setAutoCancel(true); //cancelar la notificacion al dar click


        //lanzar notificacion
        elManager.notify(1, elBuilder.build());

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void guardarEnLaBBDD() {
        double precioTotal=0;
        ArrayList<String> elementosPedidosConRepetidos = new ArrayList<>();

        try {
            //abrimos el fichero para leerlo con BufferedReader
            ficherointerno = new BufferedReader(new InputStreamReader(openFileInput("ficheroPedido.txt")));

            String linea;
            while ((linea = ficherointerno.readLine()) != null) {

                //leemos las lineas del fichero --> Panna cotta; Precio: 6.0
                System.out.println("LINEA EN EL FICHERO: " + linea);
                //partimos la linea en 2 cachos partiendo de ;
                partesPlato=linea.split(";");
                elementosPedidosConRepetidos.add(partesPlato[0]);
                //nos quedamos con la parte de Precio: 6.0 y la volvemos a partir en el " ".
                String[] precioArray = partesPlato[1].split(" ");
                //nos quedamos con la parte del numero (6.0)
                String precioInd = precioArray[2];
                System.out.println("precio parte numero: " + precioInd);
                //lo parseamos a Double
                double precioIndv = Double.parseDouble(precioInd);
                //se va sumando el precio total
                precioTotal = precioTotal+precioIndv;
            }


            //guardar elementosPedidos sin repetidos y precio total en la BD Pedidos
            //Para que no aparezcan repetidos usar LinkedHashSet, ya que, solo nos interesa
            //guardar en la BD los platos diferentes que ha pedido sin importar la cantidad.
            //En el precio total si que se sumaran todos los elementos (aunque esten repetidos)
            Set<String> s = new LinkedHashSet<String>(elementosPedidosConRepetidos);
            String elementosPedidosSinRepeticion = String.join(", ", s);;

            System.out.println("Elementos pedidos:" + elementosPedidosSinRepeticion);
            System.out.println("PRECIO TOTAL: " +precioTotal);

            //le pasamos a la actividad ActivityPedido los elementos pedidos sin repeticion y el precio total
            //El id sera 1. Este numero lo usaremos para cerrar la notificacion cuando clickemos en la opcion Ver Tu Pedido
            intentVerPedido = new Intent(ActivityPostre.this,ActivityPedido.class);
            intentVerPedido.putExtra("elementos",elementosPedidosSinRepeticion);
            intentVerPedido.putExtra("precio",precioTotal);
            intentVerPedido.putExtra("id",1);

            //guardarlos en la BD Pedidos
            //este metodo esta implementado en miBD Y se le pasan como parametros
            //los elementosPedidosSinRepeticion y el precioTotal
            gestorDB.guardarPedido(elementosPedidosSinRepeticion,precioTotal);

        }
        catch (IOException e) {
            e.printStackTrace();
        }

    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void guardarEnElFichero(){
        //https://stackoverflow.com/questions/3996938/why-is-listview-getcheckeditempositions-not-returning-correct-values
        //Coger los valores que se han seleccionado de la listView con multiple choice
        //Para ello en el layout hay que poner la opcion android:choiceMode="multipleChoice"
        SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
        if (checkedItems != null) { //Si hay algun elemento seleccionado
            for (int i=0; i<checkedItems.size(); i++) { //los recorremos
                if (checkedItems.valueAt(i)) { //si esta seleccionado el elemento de esta posicion
                    String item = listView.getAdapter().getItem(checkedItems.keyAt(i)).toString(); //guardar el nombre del plato (item)
                    Log.i("TAG",item + " was selected");
                    //guardamos los valores(string) en castellano para facilitar las operaciones con el fichero
                    //Por lo tanto si el valor que lee esta en ingles o en italiano guardara es item en castellano
                    if (item.equals("Cheesecake")){ item="Tarta de queso"; }
                    else if (item.equals("Tiramisu")){ item="Tiramisú"; }

                    //Se añaden los item seleccionados a el ArrayList<String> postres
                    postres.add(item);
                }
            }
        }

        //ver los postres seleccionados del arraylist
        for (int z=0;z<postres.size();z++) {
            System.out.println("POSTRE: " + postres.get(z));
        }
        //Guardar en un unico string los postres del ArrayList. P.e --> Panna Cotta, Tarta de queso
        //mediante el metodo de String "join".
        String postres_s = String.join(", ", postres);

        //si el string contiene al menos una letra, es decir, que se haya pedido un postre
        //Se enseña un toast con los postres añadidos al pedido
        if ( ! postres_s.isEmpty()) {
            String aped = getString(R.string.añaped);
            //Toast.makeText(this,aped+postres_s,Toast.LENGTH_SHORT).show();

            //TOAST PERSONALIZADO
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(aped+postres_s);

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }else{
            //Si no ha pedido ningun postre y le da al boton se enseña un toast diciendo que no ha pedido nada
            String npostre = getString(R.string.nopostre);
            //Toast.makeText(this,npostre,Toast.LENGTH_SHORT).show();

            //TOAST PERSONALIZADO
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(npostre);

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }

        //SE VA GUARDANDO EN UN FIHERO LO DEL ARRAYLIST<STRING> postres PARA SABER QUE ES LO QUE VA PIDIENDO EL CLIENTE
        //Para añadir sin borrar lo de antes se usa el MODE_APPEND
        try {
            fichero = new OutputStreamWriter(openFileOutput("ficheroPedido.txt", Context.MODE_APPEND));
            for (int z=0;z<postres.size();z++) {

                if (postres.get(z).equals("Profiteroles")){ precio= 5; }
                else if (postres.get(z).equals("Tarta de queso")){ precio=4; }
                else if (postres.get(z).equals("Tiramisú")){ precio=6.50; }
                else if (postres.get(z).equals("Panna cotta")){ precio=6; }


                //se escribe en el fichero ficheroPedido.txt de esta manera:
                //Panna Cotta; Precio: 6
                //Tarta de queso; Precio: 4
                fichero.write(postres.get(z)+"; Precio: "+ precio +System.lineSeparator());
                //System.lineSeparator() para salto de linea
            }
            //se cierra el fichero
            fichero.close();
        } catch (IOException e) {
            System.out.println("Error escribiendo el fichero");
        }

    }


    //al pulsar el boton de atras la actividad se minimiza
    //Asi evitaremos incongruencias en la pila de actividades
    @Override
    public void onBackPressed() {
        this.moveTaskToBack(true);
    }




}