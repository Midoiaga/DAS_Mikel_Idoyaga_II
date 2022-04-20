package com.example.das_mikel_idoyaga_ii;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class UploadWorker extends Worker {
    public UploadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String fotoen64 ="";
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/midoyaga002/WEB/subirfoto.php";
        HttpURLConnection urlConnection;
        String nombre = getInputData().getString("nombre");
        String imagen = getInputData().getString("foto");
        Log.d("d",imagen);
        Uri uriImagen = Uri.parse(imagen);
        Bitmap bitmapFoto = null;
        try {
            bitmapFoto = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uriImagen);
        } catch (IOException e) {
            e.printStackTrace();
        }
        int anchoDestino = 100;
        int altoDestino = 100;
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
        Log.d("d",nombre);
        Log.d("d","aqui");
        //Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmapredimensionado.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] fototransformada = stream.toByteArray();
        fotoen64 = Base64.encodeToString(fototransformada,Base64.NO_WRAP);
        String parametros = "nombre="+nombre+"&imagen="+fotoen64;

        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
            out.print(parametros);
            out.close();
            int statusCode = urlConnection.getResponseCode();
            Log.d("aaa", String.valueOf(statusCode));
            if (statusCode == 200) {
                return Result.success();
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return Result.failure();
    }
}
