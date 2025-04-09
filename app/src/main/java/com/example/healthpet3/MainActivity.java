package com.example.healthpet3;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redireciona para a tela de OnBoarding
        Intent intent = new Intent(this, OnBoarding1Activity.class);
        startActivity(intent);
        finish(); // Encerra MainActivity para não voltar ao clicar em "voltar"
    }
}
