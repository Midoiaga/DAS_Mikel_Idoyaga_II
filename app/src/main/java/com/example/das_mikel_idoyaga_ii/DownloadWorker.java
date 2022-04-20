package com.example.das_mikel_idoyaga_ii;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class DownloadWorker extends Worker {
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/midoyaga002/WEB/bajarfoto.php";
        HttpURLConnection urlConnection;
        String nombre = getInputData().getString("nombre");
        Log.d("d", nombre);
        String parametros = "nombre=" + nombre;

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

                Scanner s = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";

//                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
//                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
//                String line, result = "";
//                while ((line = bufferedReader.readLine()) != null) {
//                    result += line;
//                }
                Log.d("res",result);
                OutputStreamWriter fichero = new OutputStreamWriter(getApplicationContext().openFileOutput("foto.txt",
                        Context.MODE_PRIVATE));
                fichero.write(result);
                fichero.close();

////                Log.d("res",result);
////                Bitmap elBitmap = BitmapFactory.decodeStream(urlConnection.getInputStream());
////                Log.d("bi",elBitmap.toString());
////                Data resultados = new Data.Builder()
////                        .putString("datos", result)
////                        .build();
//                File path = this.getApplicationContext().getExternalFilesDir("/data/data/com.example.das_mikel_idoyaga_ii/files");
//                File f = new File(path.getAbsolutePath(), "foto.txt");
//                Log.i("FICH","PATH:"+path.getAbsolutePath());
//                OutputStreamWriter ficheroexterno = new OutputStreamWriter(new FileOutputStream(f));
//                ficheroexterno.write(result);
//                ficheroexterno.close();
//
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