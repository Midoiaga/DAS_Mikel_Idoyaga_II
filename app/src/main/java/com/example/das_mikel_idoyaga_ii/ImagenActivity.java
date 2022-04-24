package com.example.das_mikel_idoyaga_ii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Base64;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.Toast;

import java.io.BufferedReader;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import java.util.stream.Collectors;

public class ImagenActivity extends AppCompatActivity {

    private String nombre;
    private Uri uriimagen;
    private File fichImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Al crear la actividad se vincula con  el layout que contiene lo relacionado con las fotos e imagenes.
        //Se encarga de mantener el nombre del usuario
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombre = (String) extras.get("nombre");

        }
        if (savedInstanceState!= null)
        {
            nombre = savedInstanceState.getString("nombre");

        }
        //Inicializamos la alarma para que salte la actividad más o menos cada 10 segundos
        Intent i= new Intent(this,AlarmaActivity.class);
        i.putExtra("nombre",nombre);
        PendingIntent i2= PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager gestor= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        gestor.setRepeating(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime(),10000,i2);
    }
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        //Guardar el bundle para que no se pierdan los atributos necesarios para el desarrollo de la actividad.
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("nombre",nombre);

    }

    public void onFoto(View view){
        //Método que al pulsar el botón foto nos abrira la camara de fotos y una vez hecha la foto lo guardara en el dispositivo
        String nombrefich = nombre;
        File directorio=this.getFilesDir();
        fichImg = null;
        try {
            fichImg = File.createTempFile(nombrefich, ".jpg",directorio);
            uriimagen = FileProvider.getUriForFile(this, "com.example.das_mikel_idoyaga_ii.provider", fichImg);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, 1);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Método que una vez hecha la foto pondra la foto en pantalla y llamara al metodo para enviar la foto
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = findViewById(R.id.img);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            image.setImageURI(uriimagen);
            enviarFoto();
        }
    }

    public void enviarFoto(){
        //Método encargado de llamar al worker para subir la foto añadiendole el nombre del propietario de la foto y la ubicación de la foto en el dospositivo
        Data datos = new Data.Builder()
                .putString("nombre",nombre)
                .putString("foto",uriimagen.toString())
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(UploadWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
    }

    private void hacerToast(String s){
        //Metodo de apoyo para hacer notificaciones del tipo toast
        Toast toast= Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }

    public void onBuscar(View view){
        //Método encargado en llamar al worker para descargar la foto en base al nombre proporcionado y una vez recibida se proyectara en la pantalla
        EditText etNombreF = findViewById(R.id.etNombreF);
        String nombreF = etNombreF.getText().toString();
        if(!nombreF.equalsIgnoreCase("")) {
            Data datos = new Data.Builder()
                    .putString("nombre", nombreF)
                    .build();
            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(DownloadWorker.class).setInputData(datos).build();
            WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                    .observe(this, new Observer<WorkInfo>() {
                        @Override
                        public void onChanged(WorkInfo workInfo) {
                            if (workInfo != null && workInfo.getState().isFinished()) {
                                try {
                                    //Leemos el fichero donde esta guardado el base64 de la foto que ha sido escrita por la clase DownloadWorker con anterioridad.
                                    BufferedReader ficherointerno = new BufferedReader(new InputStreamReader(
                                            openFileInput("foto.txt")));
                                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                        //Decodificar la foto de string para poder proyectar la imagen por pantalla
                                        String  Linea =  ficherointerno.lines().collect(Collectors.joining());
                                        ficherointerno.close();
                                        byte[] bytes = Base64.decode(Linea, Base64.DEFAULT);
                                        Bitmap elBitMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                        ImageView img  = findViewById(R.id.img);
                                        img.setImageBitmap(elBitMap);
                                    }


                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }else{
            hacerToast("Necesitas escribir un nombre de un usuario");
        }

    }
    public void onMapa(View view){
        //Método que al pulsar el botón MAPA te abrira la actividad del mapa
        Intent i = new Intent (getApplicationContext(), ActividadMapa.class);
        startActivity(i);
    }
}