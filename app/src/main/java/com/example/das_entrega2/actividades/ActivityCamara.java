package com.example.das_entrega2.actividades;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.das_entrega2.R;
import com.example.das_entrega2.workers.ConexionBDGetFotos;
import com.example.das_entrega2.workers.ConexionBDGetTokens;
import com.example.das_entrega2.workers.ConexionBDGuardarImagen;
import com.example.das_entrega2.workers.ConexionBDInsertarUsuario;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ActivityCamara extends AppCompatActivity {

    private ImageView imageViewFoto;

    private final int CODIGO_FOTO_ARCHIVO=8;

    private final int CODIGO_GALERIA=20;

    private Uri uriimagen;

    private StorageReference storageRef;

    private EditText etextTitulo;
    private EditText etextDescripcion;

    File fichImg;

    private String imageName;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String idioma = prefs.getString("idiomapref", "es");

        Locale nlocale = new Locale(idioma);
        Locale.setDefault(nlocale);
        Configuration configuration = getBaseContext().getResources().getConfiguration();
        configuration.setLocale(nlocale);
        configuration.setLayoutDirection(nlocale);

        Context context = getBaseContext().createConfigurationContext(configuration);
        getBaseContext().getResources().updateConfiguration(configuration, context.getResources().getDisplayMetrics());

        setContentView(R.layout.activity_camara);

        imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);
        etextTitulo = (EditText) findViewById(R.id.etextTitulo);
        etextDescripcion = (EditText) findViewById(R.id.eTextDescripcion);


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    0);

        } else {
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD

        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);

        } else {
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD

        }



        //cuando se rote mantener la imagen en el ImageView y el nombre de la imagen
        if(savedInstanceState != null) {
            uriimagen = savedInstanceState.getParcelable("image");
            imageName = savedInstanceState.getString("nommbreImagen");
            imageViewFoto.setImageURI(uriimagen);
        }




        /*
        String [] permisos = new String[]{
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO ESTÁ CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)){
                // MOSTRAR AL USUARIO UNA EXPLICACIÓN DE POR QUÉ ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO ESTÁ CONCEDIDO TODAVÍA O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR Permisos
            ActivityCompat.requestPermissions(this, permisos,
                    0);

        } else {
            //EL PERMISO ESTÁ CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
            return true;
        }
        return false;

         */
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // Si la petición se cancela, granResults estará vacío
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                } else {
                    // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA
                }
                return;
            }
        }
    }



    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", uriimagen);
        outState.putString("nommbreImagen",imageName);
    }




    public void onClickSacarFoto(View v){
        System.out.println("Sacar Foto --> Se abrira la camara mediante un intent");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        File directorio=this.getFilesDir();
        //File fichImg = null;
        fichImg = null;
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



    public void onClickGaleria(View v) {
        System.out.println("Se abrira la GALERIA");
        Intent elIntentGal = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(elIntentGal, CODIGO_GALERIA);


    }


    public Bitmap imagenEscalada() throws IOException {
        Bitmap bitmapFoto= MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(),uriimagen);

        int anchoDestino = imageViewFoto.getWidth();
        int altoDestino = imageViewFoto.getHeight();
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
        return bitmapredimensionado;
    }




    //Poner la foto en la imageView
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK) {

            uriimagen = data.getData();
            imageName = new File(uriimagen.getPath()).getName();
            System.out.println("Nombre de la imagen de la GALERIA: " + imageName);


            imageViewFoto.setImageURI(uriimagen);
        }

        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {


            imageName = new File(uriimagen.getPath()).getName();
            //imageName = uriimagen.toString().split("/")[uriimagen.toString().split("/").length-1];
            System.out.println("Nombre de la imagen de SACAR FOTO: " + imageName);

            Bitmap imagenEscalada = null;
            try {
                imagenEscalada = imagenEscalada();
            } catch (IOException e) {
                e.printStackTrace();
            }
            imageViewFoto.setImageBitmap(imagenEscalada);
                //imageViewFoto.setImageURI(uriimagen);







            }





        }




    public void onClickGuardarFirebase(View v){
        System.out.println("Guardar la imagen en FIREBASE y en la BDremota");

        String titu = etextTitulo.getText().toString();

        String desc = etextDescripcion.getText().toString();

        if (uriimagen==null) {
            System.out.println("Debes sacar una foto o elegir una foto de la galeria");
            String eligefoto = getString(R.string.eligefoto);

            //TOAST PERSONALIZADO con layout_toast.xml
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText(eligefoto); // le indicamos el texto

            Toast toast = new Toast(this);
            toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
            toast.setView(layout); //le establecemos el layout al Toast
            toast.show(); //lo enseñamos
        }
        else{

            if (titu.matches("")) {

                System.out.println("El titulo debe tener al menos un caracter");
                String titalmenos1caracter = getString(R.string.titalmenos1caracter);

                //TOAST PERSONALIZADO con layout_toast.xml
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText(titalmenos1caracter); // le indicamos el texto

                Toast toast = new Toast(this);
                toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
                toast.setView(layout); //le establecemos el layout al Toast
                toast.show(); //lo enseñamos


            } else {
                //guardar en firebase
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageRef = storage.getReference();
                StorageReference spaceRef = storageRef.child(imageName);
                spaceRef.putFile(uriimagen);

                System.out.println("Imagen subida a Firebase");
                String subidaafirebase = getString(R.string.subidaafirebase);

                //TOAST PERSONALIZADO con layout_toast.xml
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.layout_toast, (ViewGroup) findViewById(R.id.toast_layout_root)); //inflamos la vista con el layout

                TextView text = (TextView) layout.findViewById(R.id.text);
                text.setText(subidaafirebase); // le indicamos el texto

                Toast toast = new Toast(this);
                toast.setDuration(Toast.LENGTH_SHORT); //duracion corta
                toast.setView(layout); //le establecemos el layout al Toast
                toast.show(); //lo enseñamos


                //guardar en la BD remota imagenes
                Data datos = new Data.Builder()
                        .putString("id", imageName)
                        .putString("titulo", titu)
                        .putString("descripcion", desc)
                        .build();


                Constraints restricciones = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDGuardarImagen.class)
                        .setConstraints(restricciones)
                        .setInputData(datos)
                        .build();



                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {
                                    try {
                                        Thread.sleep(3000); // Delay para que se suba la foto a Firebase y se pueda cargar correctamente

                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwr);



                //Despues de subir la imagen que se borre del almacenamiento local para que no ocupe espacio
                //PARA BORRAR EL FICHERO DESPUES DE SACAR LA IMAGEN Y NO OCUPE ESPACIO EN DEVICE EXPLORER

                boolean deleted = false;
                if (fichImg!=null) {
                    try {
                        deleted = fichImg.delete();
                    } catch (SecurityException e) {
                    }

                    if (!deleted) {
                        fichImg.deleteOnExit();
                    }
                }


            }


        }





    }

    public void onClickCargarDesdeFirebase(View v){
        System.out.println("Ir a actividad ActivityFotos");
        Intent intentaFotos= new Intent(ActivityCamara.this,ActivityFotos.class);
        startActivity(intentaFotos);

        //ir a una nueva actividad


        //conseguir las fotos de la BD remota con un worker




    }












}