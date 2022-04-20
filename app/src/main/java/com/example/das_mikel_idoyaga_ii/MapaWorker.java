package com.example.das_mikel_idoyaga_ii;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MapaWorker extends Worker {
    public MapaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String direccion = "http://ec2-52-56-170-196.eu-west-2.compute.amazonaws.com/midoyaga002/WEB/getmapa.php";
        HttpURLConnection urlConnection;
        try {
            URL destino = new URL(direccion);
            urlConnection = (HttpURLConnection) destino.openConnection();
            urlConnection.setConnectTimeout(5000);
            urlConnection.setReadTimeout(5000);
            int statusCode = urlConnection.getResponseCode();
            if (statusCode == 200) {
                BufferedInputStream inputStream = new BufferedInputStream(urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                String line, result = "";
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }
                Log.d("qqq",result);
                result = result.replace("[", "");
                result = result.replace("]", "");
                result = "["+result+"]";
                Log.d("qqq",result);
                JSONArray jsonArray = new JSONArray(result);
                ArrayList<String> listaN = new ArrayList<>();
                ArrayList<String> listaLat = new ArrayList<>();
                ArrayList<String> listaLon = new ArrayList<>();

                for(int i = 0; i < jsonArray.length(); i++) {
                    String nombre = jsonArray.getJSONObject(i).getString("Nombre");
                    String latitud = jsonArray.getJSONObject(i).getString("Latitud");
                    String longitud = jsonArray.getJSONObject(i).getString("Longitud");
                    listaN.add(nombre);
                    listaLat.add(latitud);
                    listaLon.add(longitud);
                }
                inputStream.close();

                Data resultados = new Data.Builder()
                        .putString("datosN",listaN.toString())
                        .putString("datosLat",listaLat.toString())
                        .putString("datosLon",listaLon.toString())
                        .build();
                return Result.success(resultados);
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
