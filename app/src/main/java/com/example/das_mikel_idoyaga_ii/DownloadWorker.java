package com.example.das_mikel_idoyaga_ii;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;


public class DownloadWorker extends Worker {
    public DownloadWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        //Método que hara una llamada al php de descargar una foto usuario con el nombre que hayamos elegido anteriormente
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/midoyaga002/WEB/bajarfoto.php";
        HttpURLConnection urlConnection;
        String nombre = getInputData().getString("nombre");
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
            if (statusCode == 200) {
                //Aqui se lee la respuesta de la web y la escribimos en un fichero para mas adelante poder recogerlo, porque sino es demasiado grande para poder pasarlo como dato
                Scanner s = new Scanner(urlConnection.getInputStream()).useDelimiter("\\A");
                String result = s.hasNext() ? s.next() : "";
                OutputStreamWriter fichero = new OutputStreamWriter(getApplicationContext().openFileOutput("foto.txt", Context.MODE_PRIVATE));
                fichero.write(result);
                fichero.close();
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