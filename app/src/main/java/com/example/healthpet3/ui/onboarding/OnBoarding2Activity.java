/*
    Segunda tela do fluxo de onboarding do aplicativo.
    Permite ao usuário:
    - Pular o onboarding e ir diretamente para a tela de login.
    - Avançar para a próxima tela de login.
 */

package com.example.healthpet3.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.R;
import com.example.healthpet3.ui.login.LoginActivity;

public class OnBoarding2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);

        TextView skipText = findViewById(R.id.skipText);
        LinearLayout nextButtonContainer = findViewById(R.id.nextButtonContainer);

        skipText.setOnClickListener(v -> goToLogin());
        nextButtonContainer.setOnClickListener(v -> goToNext());
    }

    private void goToLogin() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void goToNext() {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}