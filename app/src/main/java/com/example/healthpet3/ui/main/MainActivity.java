/*
    Classe inicial da aplicação.
    Responsável por redirecionar o usuário para a tela de OnBoarding ao abrir o app.
    Essa activity é usada como ponto de entrada do aplicativo e é encerrada após o redirecionamento,
    garantindo que o usuário não possa voltar a ela ao pressionar o botão "voltar".
 */

package com.example.healthpet3.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.ui.onboarding.OnBoarding1Activity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redireciona para a tela de OnBoarding
        Intent intent = new Intent(this, OnBoarding1Activity.class);
        startActivity(intent);
        finish();
    }
}
