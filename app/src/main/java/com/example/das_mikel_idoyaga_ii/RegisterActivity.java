package com.example.das_mikel_idoyaga_ii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class RegisterActivity extends AppCompatActivity {
    private String usuario = "";
    private double latitudA;
    private double longitudA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Método encargado de vincular esta actividad con el layout de registro.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Comprueba si tienes permisos para la geolocalización y si no los tienes te los pide
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            hacerToast("Necesitas otorgar el permiso de la ubicacion");
        } else {
            FusedLocationProviderClient proveedordelocalizacion = LocationServices.getFusedLocationProviderClient(this);
            proveedordelocalizacion.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            //Consigue tu latitud y longitud actual
                            latitudA = location.getLatitude();
                            longitudA = location.getLongitude();
                        } else {
                            hacerToast("Fallo con la ubicacion location");
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        hacerToast("Fallo con la ubicacion");
                    }
                });

        }

    }

    private boolean permisonCheck(){
        //Metodo que comprueba si tienes los permisos de geolocalización
        boolean resultado;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED) {
            hacerToast("Necesitas otorgar el permiso de la ubicacion");
            resultado = false;
        }
        else {
            resultado = true;
        }

        return resultado;
    }

    public void onRegistrar(View view){
        //Método que al pulsar en el botón REGISTRAR y cumplir todos los requisitos llamara al worker para inserta un nuevo usuario con los parrametros te tu usuario
        EditText etUsuario = findViewById(R.id.etUsuarioRegis);
        EditText etContraseña = findViewById(R.id.epContraseñaRegis);
        EditText etContraseñaConfir = findViewById(R.id.epContraseñaConfir);
        String nombre = etUsuario.getText().toString();
        String contraseña = etContraseña.getText().toString();
        String contraseñaConfir = etContraseñaConfir.getText().toString();

        if(permisonCheck()) {
            if (!nombre.equalsIgnoreCase("") && !contraseña.equalsIgnoreCase("")) {
                if (contraseña.equalsIgnoreCase(contraseñaConfir)) {
                    Data datos = new Data.Builder().putString("nombre", nombre).build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
                    Double finalLongitud = longitudA;
                    Double finalLatitud = latitudA;
                    WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                            .observe(this, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    if (workInfo != null && workInfo.getState().isFinished()) {
                                        usuario = workInfo.getOutputData().getString("datos");
                                        if (usuario.equalsIgnoreCase("[]")) {
                                            Data datos = new Data.Builder()
                                                    .putString("nombre", nombre)
                                                    .putString("contraseña", contraseña)
                                                    .putDouble("latitud", finalLatitud)
                                                    .putDouble("longitud", finalLongitud)
                                                    .build();
                                            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos).build();
                                            WorkManager.getInstance(getApplicationContext()).enqueue(otwr);
                                            finish();
                                            Intent i = new Intent(getApplicationContext(), ImagenActivity.class);
                                            i.putExtra("nombre", nombre);
                                            startActivity(i);
                                        } else {
                                            Toast toast = Toast.makeText(getApplicationContext(), "Usuario existente", Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }
                            });
                    WorkManager.getInstance(this).enqueue(otwr);

                } else {
                    this.hacerToast("Contraseñas diferentes");
                }
            } else {
                this.hacerToast("Elementos vacios");
            }
        } else{
            finish();
        }
    }





    private void hacerToast(String s){
        //Metodo de apoyo para hacer notificaciones del tipo toast
        Toast toast= Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }

}
