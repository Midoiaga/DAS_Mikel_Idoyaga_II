package com.example.das_mikel_idoyaga_ii;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {
    String token;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityManager am= (ActivityManager) this.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (am.isBackgroundRestricted()==true){
                hacerToast("Norecibiras ningun mensaje FCM");
            }
        }
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            hacerToast("Fallo en el token");
                            return;
                        }
                        token = task.getResult();
                        Log.d("token",token);
                    }
                });
        setContentView(R.layout.activity_login);
    }
    public void onAqui(View view){
        //Método que te lleva a la actividad de registrarse pulsando el botón Aquí
        Intent i = new Intent (this, RegisterActivity.class);
        startActivity(i);
    }
    public void onEntrar(View view){
        EditText etUsuario = findViewById(R.id.etUsuario);
        EditText epContraseña = findViewById(R.id.epContraseña);
        String nombre = etUsuario.getText().toString();
        String contraseña = epContraseña.getText().toString();
        Data datos = new Data.Builder().
                putString("nombre",nombre)
                .putString("contraseña",contraseña)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(ExistWorker.class).setInputData(datos).build();
        WorkManager.getInstance(this).getWorkInfoByIdLiveData(otwr.getId())
                .observe(this, new Observer<WorkInfo>() {
                    @Override
                    public void onChanged(WorkInfo workInfo) {
                        if(workInfo != null && workInfo.getState().isFinished()){
                            Log.d("da",workInfo.getOutputData().getString("datos"));
                            if(!workInfo.getOutputData().getString("datos").equalsIgnoreCase("[]")){
                                FCM();
                                Intent i = new Intent (getApplicationContext(), ImagenActivity.class);
                                startActivity(i);
                            }else{
                                Toast toast= Toast.makeText(getApplicationContext(),"Usuario o contraseña incorrecta",Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    }
                });
        WorkManager.getInstance(this).enqueue(otwr);
    }
    private void hacerToast(String s){
        //Metodo de apoyo para hacer notificaciones del tipo toast
        Toast toast= Toast.makeText(getApplicationContext(),s,Toast.LENGTH_SHORT);
        toast.show();
    }
    public void FCM(){
        EditText etUsuario = findViewById(R.id.etUsuario);
        String nombre = etUsuario.getText().toString();
        Data datos = new Data.Builder()
                .putString("nombre",nombre)
                .putString("token",token)
                .build();
        OneTimeWorkRequest otwr = new OneTimeWorkRequest.Builder(MensajeFCM.class).setInputData(datos).build();
        WorkManager.getInstance(this).enqueue(otwr);
    }
}