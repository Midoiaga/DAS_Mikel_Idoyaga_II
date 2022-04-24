package com.example.das_mikel_idoyaga_ii;


import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;


import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class UploadWorker extends Worker {
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Método que hara una llamada al php para subir una foto de un usuario a la base de datos

        String fotoen64 ="";
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/midoyaga002/WEB/subirfoto.php";
        HttpURLConnection urlConnection;
        String nombre = getInputData().getString("nombre");
        String imagen = getInputData().getString("foto");
        Uri uriImagen = Uri.parse(imagen);
        Bitmap bitmapFoto = null;
        try {
            bitmapFoto = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Redimensionar la imagen para que no ocupe tanto espacio aunque se pierda un poco de calidad
        int anchoDestino = 200;
        int altoDestino = 200;
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
        //Codificarlo para guardar la foto en formato String
        Bitmap bitmapredimensionado = Bitmap.createScaledBitmap(bitmapFoto,anchoFinal,altoFinal,true);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        fotoen64 = Base64.encodeToString(fototransformada,Base64.DEFAULT);

        try {
            JSONObject parametrosJSON = new JSONObject();
            parametrosJSON.put("param1", fotoen64);
            parametrosJSON.put("param2", nombre);
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type","application/json");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametrosJSON.toString());
            out.close();
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                return Result.success();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
