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

        setContentView(R.layout.activity_camara);

        //buscar elementos del layout
        imageViewFoto = (ImageView) findViewById(R.id.imageViewFoto);
        etextTitulo = (EditText) findViewById(R.id.etextTitulo);
        etextDescripcion = (EditText) findViewById(R.id.eTextDescripcion);


        //Pedir permisos para la Camara al hacer onCreate
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO EST?? CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                // MOSTRAR AL USUARIO UNA EXPLICACI??N DE POR QU?? ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO EST?? CONCEDIDO TODAV??A O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    0);

        } else {
            //EL PERMISO EST?? CONCEDIDO, EJECUTAR LA FUNCIONALIDAD

        }

        //Pedir permisos para WRITE_EXTERNAL_STORAGE al hacer onCreate
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //EL PERMISO NO EST?? CONCEDIDO, PEDIRLO
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // MOSTRAR AL USUARIO UNA EXPLICACI??N DE POR QU?? ES NECESARIO EL PERMISO

            } else {
                //EL PERMISO NO EST?? CONCEDIDO TODAV??A O EL USUARIO HA INDICADO
                //QUE NO QUIERE QUE SE LE VUELVA A SOLICITAR
            }
            //PEDIR EL PERMISO
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);

        } else {
            //EL PERMISO EST?? CONCEDIDO, EJECUTAR LA FUNCIONALIDAD

        }



        //cuando se rote el dispositivo mantener la imagen en el ImageView y el nombre de la imagen
        if(savedInstanceState != null) {
            uriimagen = savedInstanceState.getParcelable("image");
            imageName = savedInstanceState.getString("nommbreImagen");
            imageViewFoto.setImageURI(uriimagen);
        }


    } //fin onCreate


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 0: {
                // Si la petici??n se cancela, granResults estar?? vac??o
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // PERMISO CONCEDIDO, EJECUTAR LA FUNCIONALIDAD
                } else {
                    // PERMISO DENEGADO, DESHABILITAR LA FUNCIONALIDAD O EJECUTAR ALTERNATIVA
                }
                return;
            }
        }
    }



    //cuando se rote el dispositivo guardar la uri de la imagen y su nombre
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("image", uriimagen);
        outState.putString("nommbreImagen",imageName);
    }



    //Opcion 1: El usuario clicka en SACAR FOTO
    public void onClickSacarFoto(View v){
        System.out.println("Sacar Foto --> Se abrira la camara mediante un intent");

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String nombrefich = "IMG_" + timeStamp + "_";
        //El directorio donde almacenar la imagen. Debe ser compatible con lo definido en el FileProvider
        File directorio=this.getFilesDir();

        //Si queremos la imagen en tama??o completo, hay que indicar d??nde se va a almacenar.
        //Para poder indicarle a la aplicaci??n de fotograf??a d??nde debe almacenar la imagen
        //hay que usar un Uri generado a trav??s de un FileProvider.
        fichImg = null;
        uriimagen = null;
        try {
            //se crea el fichero File para la imagen
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            //El nombre ??nico definido en el atributo authorities del manifiesto: com.example.das_entrega2
            //uriimagen es la Uri del fichero para almacenar la imagen
            uriimagen = FileProvider.getUriForFile(this, "com.example.das_entrega2", fichImg);
        } catch (IOException e) {

        }
        //se abre la camara de nuestro dispositivo mediante un Intent
        //y se le pasa como c??digo 8 para distinguir la llamada
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, CODIGO_FOTO_ARCHIVO);


    }


    //Opcion 2: El usuario clicka en GALERIA
    public void onClickGaleria(View v) {
        System.out.println("Se abrira la GALERIA");
        //Se abrir?? la Galeria mediante un Intent y se le pasa como c??digo 20 para distinguir la llamada
        Intent elIntentGal = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(elIntentGal, CODIGO_GALERIA);
    }


    //este m??todo escalar?? la imagen al tama??o que se va a mostrar, pero mantendr?? su aspecto (el ratio).
    /*
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

     */




    //Poner la foto en la imageView dependiendo el c??digo que reciba desde el Intent
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_GALERIA && resultCode == RESULT_OK) {
            //Si la elijo desde la galer??a lo que recibo es un Uri
            uriimagen = data.getData();
            //conseguimos el nombre de la imagen y lo guardamos en la variable imageName
            imageName = new File(uriimagen.getPath()).getName();
            System.out.println("Nombre de la imagen de la GALERIA: " + imageName);

            //establecer a el imageViewFoto la Uri de la imagen
            imageViewFoto.setImageURI(uriimagen);
        }

        if (requestCode == CODIGO_FOTO_ARCHIVO && resultCode == RESULT_OK) {
            //si el c??digo recibido es el de sacar una foto con la camara
            //conseguimos el nombre de la imagen y lo guardamos en la variable imageName
            imageName = new File(uriimagen.getPath()).getName();
            //imageName = uriimagen.toString().split("/")[uriimagen.toString().split("/").length-1];
            System.out.println("Nombre de la imagen de SACAR FOTO: " + imageName);

            //escalamos la imagen
            /*
            Bitmap imagenEscalada = null;
            try {
                imagenEscalada = imagenEscalada();
            } catch (IOException e) {
                e.printStackTrace();
            }

             */
            //establecer a el imageViewFoto la imagen escalada
            imageViewFoto.setImageURI(uriimagen);

            }

        }


    //cuando el usuario clicke en Subir Imagen
    public void onClickGuardarFirebase(View v){
        System.out.println("Guardar la imagen en FIREBASE y en la BDremota");

        //recoger el titulo introducido en el primer EditText
        String titu = etextTitulo.getText().toString();

        //recoger la descripcion introducida en el segundo EditText
        String desc = etextDescripcion.getText().toString();

        //si el usuario clicka en subir imagen sin haber una imagen seleccionada,
        //es decir, si la Uri de la imagen es null
        if (uriimagen==null) {
            //Toast avisandole de que tiene que elegir una foto de la Galeria o sacarla con la camara
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
            toast.show(); //lo ense??amos
        }
        else{
            //si el titulo introducido esta vac??o
            if (titu.matches("")) {
                //Toast avisandole de que tiene que introducir al menos un caracter en el t??tulo
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
                toast.show(); //lo ense??amos


            } else {
                //si el t??tulo tiene al menos un caracter y tenemos la imagen elegida --> guardar en firebase
                FirebaseStorage storage = FirebaseStorage.getInstance();
                storageRef = storage.getReference();
                //El nombre del fichero o path a usar en Firebase Storage ser?? el nombre de la imagen sacado anteriormente
                StorageReference spaceRef = storageRef.child(imageName);
                spaceRef.putFile(uriimagen);

                //Toast avisandole de que la imagen se ha subido a Firebase
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
                toast.show(); //lo ense??amos


                //Tambi??n se debe guardar en la BD remota imagenes, para luego poder cargarla

                //Al worker ConexionBDGuardarImagen se le pasan como datos el id(nombre de la imagen), el titulo y la descripci??n.
                Data datos = new Data.Builder()
                        .putString("id", imageName)
                        .putString("titulo", titu)
                        .putString("descripcion", desc)
                        .build();

                //Debe tener conexion a la red
                Constraints restricciones = new Constraints.Builder()
                        .setRequiredNetworkType(NetworkType.CONNECTED)
                        .build();

                //El worker ConexionBDGuardarImagen guardar?? la imagen la BD remota imagenes
                OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ConexionBDGuardarImagen.class)
                        .setConstraints(restricciones)
                        .setInputData(datos)
                        .build();

                //En ActivityCamara, se a??ade un Observer a la tarea antes de encolarla usando WorkManager
                WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                        .observe(this, new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo workInfo) {
                                if (workInfo != null && workInfo.getState().isFinished()) {

                                }
                            }
                        });
                WorkManager.getInstance(this).enqueue(otwr);


                //Despues de subir la imagen a Firebase, se borra del almacenamiento local
                //De esta manera se evita que ocupe espacio en Device File
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

    } //fin onClickSubirFirebase


    //Cuando el usuario pulse en la opci??n 'Ver Recetas'
    public void onClickCargarDesdeFirebase(View v){
        //ir a una nueva actividad que conseguir?? las fotos de la BD remota imagenes mediante un worker
        //y colocar?? el titulo de las mismas en un ListView. Cuando pulse sobre alguna de ellas, se abrir??
        //otra actividad con m??s detalles acerca de esa imagen(receta).
        System.out.println("Ir a actividad ActivityFotos");
        Intent intentaFotos= new Intent(ActivityCamara.this,ActivityFotos.class);
        startActivity(intentaFotos);


    }



}