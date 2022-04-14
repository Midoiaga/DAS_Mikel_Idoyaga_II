package com.example.das_mikel_idoyaga_ii;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }
    public void onAqui(View view){
        //Método que te lleva a la actividad de registrarse pulsando el botón Aquí
        Intent i = new Intent (this, RegisterActivity.class);
        startActivity(i);
    }
}