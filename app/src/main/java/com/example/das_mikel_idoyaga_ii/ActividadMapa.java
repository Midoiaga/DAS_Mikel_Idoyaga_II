package com.example.das_mikel_idoyaga_ii;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class ActividadMapa extends FragmentActivity implements OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Al crear la actividad se vincula con  el layout del mapa. Así, enseñando el mapa mundi
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        SupportMapFragment elfragmento =

                (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentoMapa);
        elfragmento.getMapAsync(this);
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        //Método que una vez que este cargado el mapa cargara todas las ubicaciones de todos los usuarios
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MapaWorker.class).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            String nombres = workInfo.getOutputData().getString("datosN");
                            nombres = nombres.substring(1, nombres.length()-1);
                            String[] arrN = nombres.split(", ");
                            String latitudes = workInfo.getOutputData().getString("datosLat");
                            latitudes = latitudes.substring(1, latitudes.length()-1);
                            String[] arrLat = latitudes.split(", ");
                            String longitudes = workInfo.getOutputData().getString("datosLon");
                            longitudes = longitudes.substring(1, longitudes.length()-1);
                            String[] arrLon = longitudes.split(", ");
                            for(int i =0; i<arrLat.length;i++){
                                googleMap.addMarker(new MarkerOptions()
                                        .position(new LatLng(Double.parseDouble(arrLat[i]), Double.parseDouble(arrLon[i])))
                                        .title(arrN[i]));
                            }

                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
}