package com.example.das_mikel_idoyaga_ii;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class AlarmaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Al crear la actividad se vincula con  el layout de la alarma que contendra un mensaje de aviso
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarma);

    }

}