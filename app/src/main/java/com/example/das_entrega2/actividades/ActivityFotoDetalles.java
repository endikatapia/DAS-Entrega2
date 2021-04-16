package com.example.das_entrega2.actividades;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.das_entrega2.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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
        setContentView(R.layout.activity_foto_detalles);

        imageViewReceta = findViewById(R.id.imageViewReceta);
        tvtitulo = findViewById(R.id.tvtitulo);
        tvdesc = findViewById(R.id.tvdesc);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id = extras.getString("id");
            titulo = extras.getString("titulo");
            descripcion = extras.getString("descripcion");

            //poner la imagen de Firebase
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference();
            StorageReference pathReference = storageRef.child(id);
            pathReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri).into(imageViewReceta);
                }
            });

            tvtitulo.setText(titulo);

            tvdesc.setText(descripcion);




        }





    }
}