package com.example.das_entrega2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActivityCamara extends AppCompatActivity {

    private ImageView imageViewFoto;

    private final int CODIGO_FOTO_ARCHIVO=8;

    private Uri uriimagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camara);

        imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);


    }



    public void onClickSacarFoto(View v){
        System.out.println("Sacar Foto --> Se abrira la camara mediante un intent");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio=this.getFilesDir();
        File fichImg = null;
        uriimagen = null;
        try {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            uriimagen = FileProvider.getUriForFile(this, "com.example.das_entrega2", fichImg);
        } catch (IOException e) {

        }
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, CODIGO_FOTO_ARCHIVO);

    }




    //Poner la foto en la imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {




            imageViewFoto.setImageURI(uriimagen);

            /*
            Bitmap bitmapFoto;
            try {
                bitmapFoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uriimagen);

                int anchoDestino = elImageView.getWidth();
                int altoDestino = elImageView.getHeight();
                int anchoImagen = bitmapFoto.getWidth();
                int altoImagen = bitmapFoto.getHeight();
                float ratioImagen = (float) anchoImagen / (float) altoImagen;
                float ratioDestino = (float) anchoDestino / (float) altoDestino;
                int anchoFinal = anchoDestino;
                int altoFinal = altoDestino;
                if (ratioDestino > ratioImagen) {
                    anchoFinal = (int) ((float)altoDestino * ratioImagen);
                } else {
                    altoFinal = (int) ((float)anchoDestino / ratioImagen);
                }
                Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);

                elImageView.setImageBitmap(bitmapredimensionado);
            } catch (IOException e) {
                e.printStackTrace();
            }

             */



        }

    }


    public void onClickGuardarServidor(View v){
        System.out.println("Guardar la imagen en la jerarquia de carpetas en el servidor");

    }

    public void onClickCargarDesdeServidor(View v){
        System.out.println("Cargar la imagen desde el servidor");

    }












}