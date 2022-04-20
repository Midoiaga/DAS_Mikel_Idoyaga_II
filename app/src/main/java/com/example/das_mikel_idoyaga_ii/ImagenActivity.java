package com.example.das_mikel_idoyaga_ii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

public class ImagenActivity extends AppCompatActivity {

    private String nombre;
    private Uri uriimagen;
    private File fichImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagen);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            nombre = (String) extras.get("nombre");
        }
    }

    public void onFoto(View view){

        String nombrefich = nombre;
        File directorio=this.getFilesDir();
        fichImg = null;
        try {
            fichImg = File.createTempFile(nombrefich, ".png",directorio);
            uriimagen = FileProvider.getUriForFile(this, "com.example.das_mikel_idoyaga_ii.provider", fichImg);
            Log.d("aaaa",fichImg.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("aaaa",uriimagen.toString());
        Intent elIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        elIntent.putExtra(MediaStore.EXTRA_OUTPUT, uriimagen);
        startActivityForResult(elIntent, 1);

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ImageView image = findViewById(R.id.img);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            image.setImageURI(uriimagen);
            //Bundle extras = data.getExtras();
            //Bitmap laminiatura = (Bitmap) extras.get("data");
            enviarFoto();
        }
    }
    public void enviarFoto(){

//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        foto.compress(Bitmap.CompressFormat.PNG, 50, stream);
//        byte[] fototransformada = stream.toByteArray();
//        String fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);
//        Log.d("d",fichImg.toString());
        Data datos = new Data.Builder()
                .putString("nombre",nombre)
                .putString("foto",fichImg.toString())
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
                                String foto64 = workInfo.getOutputData().getString("datos");
                                byte[] bytes = Base64.decode(foto64, Base64.DEFAULT);
                                Bitmap elBitMap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                ImageView img  = findViewById(R.id.img);
                                img.setImageBitmap(elBitMap);

                            }
                        }
                    });
            WorkManager.getInstance(this).enqueue(otwr);
        }else{
            hacerToast("Necesitas escribir un nombre de un usuario");
        }
    }
}