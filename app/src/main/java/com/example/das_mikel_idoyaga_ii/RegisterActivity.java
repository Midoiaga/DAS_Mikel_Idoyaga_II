package com.example.das_mikel_idoyaga_ii;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {
    private String usuario="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }
    public void onRegistrar(View view){
        EditText etUsuario = findViewById(R.id.etUsuarioRegis);
        EditText etContraseña = findViewById(R.id.epContraseñaRegis);
        EditText etContraseñaConfir = findViewById(R.id.epContraseñaConfir);
        EditText etLatitud = findViewById(R.id.etLatitud);
        EditText etLongitud = findViewById(R.id.etLongitud);
        String nombre = etUsuario.getText().toString();
        String contraseña = etContraseña.getText().toString();
        String contraseñaConfir = etContraseñaConfir.getText().toString();
        Double latitud = 0.0;
        Double longitud = 0.0;
        if(!etLatitud.getText().toString().equalsIgnoreCase("") && !etLongitud.getText().toString().equalsIgnoreCase("")){
            latitud = Double.parseDouble(etLatitud.getText().toString());
            longitud = Double.parseDouble(etLongitud.getText().toString());
            if(!nombre.equalsIgnoreCase("") && !contraseña.equalsIgnoreCase("")){
                if(contraseña.equalsIgnoreCase(contraseñaConfir)){
                    Data datos = new Data.Builder().putString("nombre",nombre).build();
                    OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(GetWorker.class).setInputData(datos).build();
                    Double finalLongitud = longitud;
                    Double finalLatitud = latitud;
                    WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                            .observe(this, new Observer<WorkInfo>() {
                                @Override
                                public void onChanged(WorkInfo workInfo) {
                                    if(workInfo != null && workInfo.getState().isFinished()){
                                        usuario = workInfo.getOutputData().getString("datos");
                                        if(usuario.equalsIgnoreCase("[]")){
                                            Data datos = new Data.Builder()
                                                    .putString("nombre",nombre)
                                                    .putString("contraseña",contraseña)
                                                    .putDouble("latitud", finalLatitud)
                                                    .putDouble("longitud", finalLongitud)
                                                    .build();
                                            OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(InsertWorker.class).setInputData(datos).build();
                                            WorkManager.getInstance(getApplicationContext()).enqueue(otwr);
                                            finish();
                                            Intent i = new Intent (getApplicationContext(), ImagenActivity.class);
                                            startActivity(i);
                                        }else{
                                            Toast toast= Toast.makeText(getApplicationContext(),"Usuario existente",Toast.LENGTH_SHORT);
                                            toast.show();
                                        }
                                    }
                                }
                            });
                    WorkManager.getInstance(this).enqueue(otwr);

                }else {
                    this.hacerToast("Contraseñas diferentes");
                }
            }else{
                this.hacerToast("Elementos vacios");
            }
        }else {
            this.hacerToast("Elementos vacios");
        }



    }

    private void hacerToast(String s){
        //Metodo de apoyo para hacer notificaciones del tipo toast
        Toast toast= Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }

}
