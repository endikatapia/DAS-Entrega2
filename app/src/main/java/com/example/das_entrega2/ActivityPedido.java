package com.example.das_entrega2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Locale;

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
                else if (partesPlato[i].equals("Pizza Boloñesa")){
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
                else if (partesPlato[i].equals("Espagueti Boloñesa")){
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
                else if (partesPlato[i].equals("Tiramisú")){
                    partesPlato[i]=pst3;
                    precio=6.50; }
                else if (partesPlato[i].equals("Panna cotta")){ precio=6;}

                //añadira al ArrayList<Double> precios el precio de ese plato
                precios.add(precio);
            }


            //Se genera un adaptador y se le indican qué datos debe mostrar (partesPlato) y cómo debe mostrarlos (simple_list_item_2)
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
                    lineasecundaria.setText(pr + String.valueOf(precios.get(position)) + "€");

                    return vista;
                }
            };
            lv2.setAdapter(eladaptador);

            //Text View del precioTotal
            double precioT = extras.getDouble("precio");
            tvPrecio.setText(tvPrecio.getText()+": " + precioT + "€");
        }
    }

    public void onClickIrCarta(View v){
        //cuando se pulse volver a la carta se podra realizar otro pedido
        Intent intentVolverCarta = new Intent(ActivityPedido.this,MainActivity.class);
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