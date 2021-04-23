package com.example.das_entrega2.actividades;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.das_entrega2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Locale;

public class ActivityFotoDetalles extends AppCompatActivity {


    private ImageView imageViewReceta;
    private TextView tvtitulo;
    private TextView tvdesc;

    private String id;
    private String titulo;
    private String descripcion;



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

        setContentView(R.layout.activity_foto_detalles);

        //toast diciendo que la imagen se tiene que cargar desde Firebase
        //TOAST PERSONALIZADO con layout_toast.xml
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

        String cargaFirebase = getString(R.string.cargaFirebase);

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(cargaFirebase); // le indicamos el texto

        Toast toast = new Toast(this);
        toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
        toast.setView(layout); //le establecemos el layout al Toast
        toast.show(); //lo enseñamos


        //conseguir los 3 elementos del layout
        imageViewReceta = findViewById(R.id.imageViewReceta);
        tvtitulo = findViewById(R.id.tvtitulo);
        tvdesc = findViewById(R.id.tvdesc);

        //recibir los 3 datos desde el Intent de ActivityFotos
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            titulo = extras.getString("titulo");
            descripcion = extras.getString("descripcion");

            //poner la imagen de Firebase en el imageView (puede tardar unos segundos en cargar).
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child(id);
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(imageViewReceta);
                }
            });

            String tit = getString(R.string.titulo);
            //poner el titulo de la imagen correspondiente
            tvtitulo.setText(tit + titulo);

            String desc = getString(R.string.descripcion);
            //poner la descripción de la imagen correspondiente
            tvdesc.setText(desc + descripcion);


        }


    }
}