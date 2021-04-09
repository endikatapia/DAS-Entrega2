package com.example.das_entrega2;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Locale;

public class ActivityComida extends AppCompatActivity implements FragmentLVMultipleChoice.listenerDelFragment{

    ListView listView;
    ArrayAdapter eladaptador;
    ArrayList<String> comidas = new ArrayList<>();
    private double precio;
    private String[] datosPizza;
    private String[] ingredientesPizza;
    private String[] datosEnsalada;
    private String[] ingredientesEnsalada;
    private String[] datosArroz;
    private String[] ingredientesArroz;
    private String[] datosEspagueti;
    private String[] ingredientesEspagueti;
    private String[] datosEspecialidad;
    private String[] ingredientesEspecialidad;


    //DATOS PIZZA(CATEGORIA 0)
    int[] comidaPizza={R.drawable.pizzamargarita,R.drawable.pizzabolognesa,R.drawable.pizzacarbonara,R.drawable.pizza4quesos,R.drawable.pizzanapolitana,R.drawable.pizzaatun,R.drawable.pizzasorrento,R.drawable.pizzatropical};
    double[] preciosPizza = {10,13.50,13.50,12.50,11.50,12.50,13,13.50};

    //DATOS ENSALADA(CATEGORIA 1)
    int[] comidaEnsalada={R.drawable.ensaladamixta,R.drawable.ensaladatropical,R.drawable.ensaladapasta,R.drawable.ensaladacampera,R.drawable.ensaladacapresse,R.drawable.ensaladafdmare};
    double[] preciosEnsalada = {7,9,9,10,9,9.5};

    //DATOS ARROZ(CATEGORIA 2)
    int[] comidaArroz={R.drawable.arrozsetas,R.drawable.risottomarinero,R.drawable.risotto4quesos};
    double[] preciosArroz = {10,10,11};

    //DATOS ESPAGUETI(CATEGORIA 3)
    int[] comidaEspagueti={R.drawable.espaguetipesto,R.drawable.espaguetibolonesa,R.drawable.espagueticarbonara,R.drawable.espaguetisicilia,R.drawable.espaguetigambas};
    double[] preciosEspagueti = {9,9,9,10,11};

    //DATOS ESPECIALIDAD(CATEGORIA 4)
    int[] comidaEspecialidad={R.drawable.lasagnacarne,R.drawable.raviolisetas,R.drawable.tagliatele,R.drawable.carpaccocarne,R.drawable.provolone};
    double[] preciosEspecialidad = {10.50,11,10,12,9};


    OutputStreamWriter fichero;
    TextView userr;


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


        //Si el idioma es castellano (es) cargar los fragments con los datos en castellano
        if (idioma.equals("es")) {
            datosPizza = new String[]{"Pizza Margarita", "Pizza Boloñesa", "Pizza Carbonara", "Pizza 4 Quesos", "Pizza Napolitana", "Pizza Atun", "Pizza Sorrento", "Pizza Tropical"};
            ingredientesPizza = new String[]{"mozzarella, tomate", "mozzarella, orégano, salsa bolognesa", "mozzarella, tomate, beicon, huevo, cebolla", "tomate, orégano, 4 quesos", "mozzarella, tomate, orégano, anchoas, aceitunas", "mozzarella, tomate, orégano, atún", "jamón serrano, tomate, rúcula, láminas de parmesano", "mozzarella, tomate, orégano, jamón, piña"};

            datosEnsalada= new String[]{"Ensalada Mixta", "Ensalada Tropical", "Ensalada de Pasta", "Ensalada Campera", "Ensalada Capresse", "Ensalada Fruti di mare"};
            ingredientesEnsalada= new String[]{"atún, espárragos, cebolla, tomate, lechuga", "piña, manzana, maíz, tomate, lechuga, jamón, salsa rosa", "pasta, atún, maíz, aceitunas, cebolla, salsa rosa", "patata, maíz, nueces, lechuga, salsa rosa", "mozzarella de búfala, tomate, orégano, aceitunas, alcaparras", "mejillones, gambas, palitos de cangrejo, tomate, lechuga y salsa rosa"};

            datosArroz= new String[]{"Risotto de Setas", "Risotto Marinero", "Risotto 4 Quesos"};
            ingredientesArroz= new String[]{"caldo de pollo, arroz carnaroli, setas funghi, cebolla, rucula", "arroz arborio, cebolla, caldo de pescado, mejillones, calamar, cáscara de limón", "arroz bomba, gorgonzola, gouda, chedar, parmesano"};

            datosEspagueti= new String[]{"Espagueti al Pesto", "Espagueti Boloñesa", "Espagueti Carbonara", "Espagueti Siciliana", "Espagueti con Gambas"};
            ingredientesEspagueti= new String[]{"espagueti, albahaca, parmesano, piñones", "salsa bolognesa, parmesano", "nata, bacon, parmesano, huevo", "anchoas, alcaparras, aceitunas, salsa de tomate", "gambas, ajo, picante"};

            datosEspecialidad= new String[]{"Lasagna de Carne", "Ravioli de Setas", "Tagliatelle al Andrea", "Carpaccio de Carne", "Provolone a la Plancha"};
            ingredientesEspecialidad= new String[]{"carne picada, placas de canelón, salsa de tomate, cebolla, queso en lonchas, leche", "harina de trigo, huevos, setas frescas, cebolla", "nata, jamón serrano, espinacas, queso ricotta", "solomillo de ternera, alcaparras, queso Parmesano, mostaza en grano", "queso provolone, tomate, orégano seco, albahaca fresca"};

            //Si el idioma es ingles (en) cargar los fragments con los datos en ingles
        } else if (idioma.equals("en")){
            datosPizza = new String[]{"Pizza Margarita", "Pizza Bolognese", "Pizza Carbonara", "Pizza 4 Cheese", "Pizza Neapolitan", "Pizza Tuna", "Pizza Sorrento", "Pizza Tropical"};
            ingredientesPizza = new String[]{"mozzarella, tomato", "mozzarella, oregano, bolognese sauce", "mozzarella, tomato, bacon, egg, onion", "tomato, oregano, 4 cheeses", "mozzarella, tomato, oregano, anchovies, olives", "mozzarella, tomato, oregano, tuna","serrano ham, tomato, arugula, parmesan slices","mozzarella, tomato, oregano, ham, pineapple"};

            datosEnsalada= new String[]{"Mixed Salad", "Tropical Salad", "Pasta Salad", "Country Salad", "Capresse Salad", "Fruti di mare Salad"};
            ingredientesEnsalada= new String[]{"tuna, asparagus, onion, tomato, lettuce", "pineapple, apple, corn, tomato, lettuce, ham, pink sauce", "pasta, tuna, corn, olives, onion, pink sauce", "potato, corn, walnuts , lettuce, pink sauce","buffalo mozzarella, tomato, oregano, olives, capers","mussels, prawns, crab sticks, tomato, lettuce and pink sauce"};

            datosArroz= new String[]{"Mushroom Risotto", "Sailor Risotto", "4 Cheese Risotto"};
            ingredientesArroz= new String[]{"chicken broth, carnaroli rice, funghi mushrooms, onion, rocket", "arborio rice, onion, fish broth, mussels, squid, lemon peel", "bomba rice, gorgonzola, gouda, chedar, parmesan"};

            datosEspagueti= new String[]{"Spaghetti with Pesto", "Spaghetti Bolognese", "Spaghetti Carbonara", "Sicilian Spaghetti", "Spaghetti with Prawns"};
            ingredientesEspagueti= new String[]{"spaghetti, basil, parmesan, pine nuts", "bolognese sauce, parmesan", "cream, bacon, parmesan, egg", "anchovies, capers, olives, tomato sauce", "prawns, garlic, spicy"};

            datosEspecialidad= new String[]{"Meat Lasagna", "Mushroom Ravioli", "Tagliatelle al Andrea", "Meat Carpaccio", "Grilled Provolone"};
            ingredientesEspecialidad= new String[]{"minced meat, cannelloni plates, tomato sauce, onion, sliced cheese, milk", "wheat flour, eggs, fresh mushrooms, onion", "cream, serrano ham, spinach, ricotta cheese", "beef tenderloin, capers, Parmesan cheese, grain mustard","provolone cheese, tomato, dried oregano, fresh basil"};

            //Si el idioma es italiano (it) cargar los fragments con los datos en italiano
        } else if (idioma.equals("it")){
            datosPizza = new String[]{"Pizza Margarita", "Pizza Bolognese", "Pizza Carbonara", "Pizza 4 Formaggi", "Pizza Napoletana", "Pizza Tonno", "Pizza Sorrento", "Pizza Tropicale"};
            ingredientesPizza = new String[]{"mozzarella, pomodoro", "mozzarella, origano, ragù alla bolognese", "mozzarella, pomodoro, pancetta, uovo, cipolla", "pomodoro, origano, 4 formaggi", "mozzarella, pomodoro, origano, acciughe, olive", "mozzarella, pomodoro, origano, tonno","prosciutto serrano, pomodoro, rucola, scaglie di parmigiano","mozzarella, pomodoro, origano, prosciutto, ananas"};

            datosEnsalada= new String[]{"Insalata Mista", "Insalata Tropicale", "Insalata di Pasta", "Insalata Country", "Insalata Capresse", "Insalata Fruti di Mare"};
            ingredientesEnsalada= new String[]{"tonno, asparagi, cipolla, pomodoro, lattuga", "ananas, mela, mais, pomodoro, lattuga, prosciutto, salsa rosa", "pasta, tonno, mais, olive, cipolla, salsa rosa", "patata, mais, noci , lattuga, salsa rosa ","mozzarella di bufala, pomodoro, origano, olive, capperi","cozze, gamberi, bastoncini di granchio, pomodoro, lattuga e salsa rosa"};

            datosArroz= new String[]{"Risotto ai funghi", "Risotto alla marinara", "Risotto ai 4 formaggi"};
            ingredientesArroz= new String[]{"brodo di pollo, riso carnaroli, funghi funghi, cipolla, rucola", "riso arborio, cipolla, brodo di pesce, cozze, calamari, scorza di limone", "riso bomba, gorgonzola, gouda, chedar, parmigiano"};

            datosEspagueti= new String[]{"Spaghetti al Pesto", "Spaghetti alla Bolognese", "Spaghetti alla Carbonara", "Spaghetti Siciliani", "Spaghetti ai Gamberi"};
            ingredientesEspagueti= new String[]{"spaghetti, basilico, parmigiano, pinoli", "ragù alla bolognese, parmigiano", "panna, pancetta, parmigiano, uovo", "acciughe, capperi, olive, salsa di pomodoro", "gamberi, aglio, piccante"};

            datosEspecialidad= new String[]{"Lasagna di carne", "Ravioli ai funghi", "Tagliatelle al Andrea", "Carpaccio di carne", "Provolone alla griglia"};
            ingredientesEspecialidad= new String[]{"carne macinata, cannelloni, salsa di pomodoro, cipolla, formaggio a fette, latte", "farina di frumento, uova, funghi freschi, cipolla", "panna, prosciutto serrano, spinaci, ricotta", "filetto di manzo, capperi, Parmigiano, senape di grano","provola, pomodoro, origano secco, basilico fresco"};

        }

        setContentView(R.layout.activity_comida);
        listView=findViewById(R.id.lv);
        userr=findViewById(R.id.userr);

    } //final onCreate


    //este metodo sera el encargado de poner la lista segun la categoria que reciba de ElViewHolder mediante el intent.
    //Si el usuario clicka en el cardView de las pizzas (categoria 0) se cargara el fragment con los datos de la pizza.
    public void ponerLista(){
        //recogemos la informacion que viene desde ElViewHolder
        Bundle extras= getIntent().getExtras();
        if (extras != null){
            int categoria = extras.getInt("categoria");
            if (categoria==0){ //Si la categoria recibida es 0 --> PIZZA
                //Se genera un adaptador y se le indican qué datos debe mostrar (datosPizza)
                //y cómo debe mostrarlos (simple_list_item_multiple_choice)
                //Creamos el ArrayAdapter con la posibilidad de elegir mas de un item --> simple_list_item_multiple_choice
                eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosPizza);
                listView.setAdapter(eladaptador);

                //Se añade un listener para indicar qué hacer cuando se seleccione algún elemento del ListView:
                //cuando clickamos en un item se selecciona el checkbox y nos sale informacion acerca del plato
                //se gestiona en la función seleccionarElemento de esta misma clase
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        seleccionarElemento(datosPizza[position],comidaPizza[position],ingredientesPizza[position],preciosPizza[position]);
                        System.out.println(datosPizza[position]);
                    }
                });


            }

            else if (categoria==1){
                //Si la categoria recibida es 1 --> ENSALADA
                eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosEnsalada);
                listView.setAdapter(eladaptador);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        seleccionarElemento(datosEnsalada[position],comidaEnsalada[position],ingredientesEnsalada[position],preciosEnsalada[position]);
                        System.out.println(datosEnsalada[position]);
                    }
                });
            }

            else if (categoria==2){
                //Si la categoria recibida es 2 --> ARROZ
                eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosArroz);
                listView.setAdapter(eladaptador);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        seleccionarElemento(datosArroz[position],comidaArroz[position],ingredientesArroz[position],preciosArroz[position]);
                        System.out.println(datosArroz[position]);
                    }
                });
            }

            else if (categoria==3){
                //Si la categoria recibida es 3 --> ESPAGUETI
                eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosEspagueti);
                listView.setAdapter(eladaptador);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        seleccionarElemento(datosEspagueti[position],comidaEspagueti[position],ingredientesEspagueti[position],preciosEspagueti[position]);
                        System.out.println(datosEspagueti[position]);
                    }
                });
            }


            else if (categoria==4){
                //Si la categoria recibida es 4 --> ESPECIALIDAD
                eladaptador = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,datosEspecialidad);
                listView.setAdapter(eladaptador);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id){
                        seleccionarElemento(datosEspecialidad[position],comidaEspecialidad[position],ingredientesEspecialidad[position],preciosEspecialidad[position]);
                        System.out.println(datosEspecialidad[position]);

                    }
                });
            }


        }
    } //final ponerLista



    public void seleccionarElemento(String nombreComida, int imagen, String ingredientes,double precio){
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE){ //Si el teléfono está en apaisado: actualizar FragmentDetalles
            //EL OTRO FRAGMENT EXISTE --> por lo tanto si la orientacion es horizontal se cargara el land/activity_comida.xml
            //enseñando en la parte derecha el fragment con la listView multiple_choice, en el medio los detalles acerca de
            //los platos que seleccionemos y en la izquierda el boton para añadir al pedido los platos seleccionados.
            System.out.println("-------------HORIZONTAL---------");
            FragmentDetalles elotro=(FragmentDetalles) getSupportFragmentManager().findFragmentById(R.id.fragmentFotoIndv);
            //FragmentDetalles se encargara de cargar y visualizar los datos mediante el metodo setDatos.
            //Como parametro le pasaremos el nombre del plato, la imagen, los ingredientes y el precio
            elotro.setDatos(nombreComida,imagen,ingredientes,precio);
        }
        else{
            //EL OTRO FRAGMENT NO EXISTE, HAY QUE LANZAR LA ACTIVIDAD QUE LO CONTIENE -->
            //estando en layout/activity_comida.xml(vertical) se lanza la actividad ActivityDetalles.
            //enseñara los detalles en esa actividad mediante el Fragment fragmentdetalles.xml
            System.out.println("-------------VERTICAL---------");

            //Cuando se cree esa actividad guardamos en el intent los 4 parametros necesarios
            //para enseñar toda la informacion del plato
            Intent i= new Intent(ActivityComida.this,ActivityDetalles.class);
            i.putExtra("nombre",nombreComida);
            i.putExtra("imagen",imagen);
            i.putExtra("ingredientes",ingredientes);
            i.putExtra("precio",precio);
            startActivity(i);
        }

    }


    //Cuando se pulse el Boton Añadir y Volver a la carta
    @RequiresApi(api = Build.VERSION_CODES.O) //para hacer String.join es necesario esta linea
    public void onClickAnadirYvolver(View v){
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
                    if (item.equals("Pizza Bolognese")){ item="Pizza Boloñesa"; }
                    else if (item.equals("Pizza 4 Cheese") || item.equals("Pizza 4 Formaggi")){ item="Pizza 4 Quesos"; }
                    else if (item.equals("Pizza Neapolitan") || item.equals("Pizza Napoletana")){ item="Pizza Napolitana"; }
                    else if (item.equals("Pizza Tuna") || item.equals("Pizza Tonno")){ item="Pizza Atun"; }
                    else if (item.equals("Pizza Tropicale")){ item="Pizza Tropical"; }
                    else if (item.equals("Mixed Salad") || item.equals("Insalata Mista")){ item="Ensalada Mixta"; }
                    else if (item.equals("Tropical Salad") || item.equals("Insalata Tropicale")){ item="Ensalada Tropical"; }
                    else if (item.equals("Pasta Salad") || item.equals("Insalata di Pasta")){ item="Ensalada de Pasta"; }
                    else if (item.equals("Country Salad") || item.equals("Insalata Country")){ item="Ensalada Campera"; }
                    else if (item.equals("Capresse Salad") || item.equals("Insalata Capresse")){ item="Ensalada Capresse"; }
                    else if (item.equals("Fruti di mare Salad") || item.equals("Insalata Fruti di Mare")){ item="Ensalada Fruti di mare"; }
                    else if (item.equals("Mushroom Risotto") || item.equals("Risotto ai funghi")){ item="Risotto de Setas"; }
                    else if (item.equals("Sailor Risotto") || item.equals("Risotto alla marinara")){ item="Risotto Marinero"; }
                    else if (item.equals("4 Cheese Risotto") || item.equals("Risotto ai 4 formaggi")){ item="Risotto 4 Quesos"; }
                    else if (item.equals("Spaghetti with Pesto") || item.equals("Spaghetti al Pesto")){ item="Espagueti al Pesto"; }
                    else if (item.equals("Spaghetti Bolognese") || item.equals("Spaghetti alla Bolognese")){ item="Espagueti Boloñesa"; }
                    else if (item.equals("Spaghetti Carbonara") || item.equals("Spaghetti alla Carbonara")){ item="Espagueti Carbonara"; }
                    else if (item.equals("Sicilian Spaghetti") || item.equals("Spaghetti Siciliani")){ item="Espagueti Siciliana"; }
                    else if (item.equals("Spaghetti with Prawns") || item.equals("Spaghetti ai Gamberi")){ item="Espagueti con Gambas"; }
                    else if (item.equals("Meat Lasagna") || item.equals("Lasagna di carne")){ item="Lasagna de Carne"; }
                    else if (item.equals("Mushroom Ravioli") || item.equals("Ravioli ai funghi")){ item="Ravioli de Setas"; }
                    else if (item.equals("Meat Carpaccio") || item.equals("Carpaccio di carne")){ item="Carpaccio de Carne"; }
                    else if (item.equals("Grilled Provolone") || item.equals("Provolone alla griglia")){ item="Provolone a la Plancha"; }

                    //Se añaden los item seleccionados a el ArrayList<String> comidas
                    comidas.add(item);
                }
            }
        }

        //Ver los platos seleccionados del arraylist
        for (int z=0;z<comidas.size();z++) {
            System.out.println("PLATO: " + comidas.get(z));
        }
        //Guardar en un unico string las comidas del ArrayList. P.e --> Pizza Boloñesa, Rissoto 4 Quesos, Lasagna de Carne
        //mediante el metodo de String "join".
        String platos = String.join(", ", comidas);

        //si el string contiene al menos una letra, es decir, que se haya pedido un plato
        //Se enseña un toast con los platos añadidos al pedido
        if ( ! platos.isEmpty()) {
            String aped = getString(R.string.añaped);

            //TOAST PERSONALIZADO
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(aped+platos);

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }else{
            //Si no ha pedido nada y le da al boton se enseña un toast diciendo que no ha pedido nada
            String naped = getString(R.string.noañaped);

            //TOAST PERSONALIZADO
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root));

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(naped);

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT);
            toast.setView(layout);
            toast.show();
        }


        //SE VA GUARDANDO EN UN FIHERO LO DEL ARRAYLIST<STRING> comidas PARA SABER QUE ES LO QUE VA PIDIENDO EL CLIENTE
        //Para añadir sin borrar lo de antes se usa el MODE_APPEND
        try {
            fichero = new OutputStreamWriter(openFileOutput("ficheroPedido.txt", Context.MODE_APPEND));
            for (int z=0;z<comidas.size();z++) {
                //guardamos el precio y el idioma en castellano para luego leerlo mas facilmente
                if (comidas.get(z).equals("Pizza Margarita")){ precio= 10; }
                else if (comidas.get(z).equals("Pizza Boloñesa")){ precio=13.50; }
                else if (comidas.get(z).equals("Pizza Carbonara")){ precio=13.50; }
                else if (comidas.get(z).equals("Pizza 4 Quesos")){ precio=12.50; }
                else if (comidas.get(z).equals("Pizza Napolitana")){ precio=11.50; }
                else if (comidas.get(z).equals("Pizza Atun")){ precio=12.50; }
                else if (comidas.get(z).equals("Pizza Sorrento")){ precio=13; }
                else if (comidas.get(z).equals("Pizza Tropical")){ precio=13.50; }

                else if (comidas.get(z).equals("Ensalada Mixta")){ precio=7; }
                else if (comidas.get(z).equals("Ensalada Tropical")){ precio=9; }
                else if (comidas.get(z).equals("Ensalada de Pasta")){ precio=9; }
                else if (comidas.get(z).equals("Ensalada Campera")){ precio=10; }
                else if (comidas.get(z).equals("Ensalada Capresse")){ precio=9; }
                else if (comidas.get(z).equals("Ensalada Fruti di mare")){ precio=9.5; }

                else if (comidas.get(z).equals("Risotto de Setas")){ precio=10; }
                else if (comidas.get(z).equals("Risotto Marinero")){ precio=10; }
                else if (comidas.get(z).equals("Risotto 4 Quesos")){ precio=11; }

                else if (comidas.get(z).equals("Espagueti al Pesto")){ precio=9; }
                else if (comidas.get(z).equals("Espagueti Boloñesa")){ precio=9; }
                else if (comidas.get(z).equals("Espagueti Carbonara")){ precio=9; }
                else if (comidas.get(z).equals("Espagueti Siciliana")){ precio=10; }
                else if (comidas.get(z).equals("Espagueti con Gambas")){ precio=11; }

                else if (comidas.get(z).equals("Lasagna de Carne")){ precio=10.5; }
                else if (comidas.get(z).equals("Ravioli de Setas")){ precio=11; }
                else if (comidas.get(z).equals("Tagliatelle al Andrea")){ precio=10; }
                else if (comidas.get(z).equals("Carpaccio de Carne")){ precio=12; }
                else if (comidas.get(z).equals("Provolone a la Plancha")){ precio=9; }

                //se escribe en el fichero ficheroPedido.txt de esta manera:
                //Pizza Margarita; Precio: 10
                //Espagueti al Pesto; Precio: 9
                fichero.write(comidas.get(z)+"; Precio: "+ precio +System.lineSeparator());
                //System.lineSeparator() para salto de linea
            }
            //se cierra el fichero
            fichero.close();
        } catch (IOException e) {
            System.out.println("Error escribiendo el fichero");
        }


        //Volver a MainActivity para seguir con el pedido
        Intent i2 = new Intent(ActivityComida.this,MainActivity.class);
        //Guardar el estado anterior de MainActivity con el flag FLAG_ACTIVITY_REORDER_TO_FRONT
        i2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(i2);


    } //fin onClick



}