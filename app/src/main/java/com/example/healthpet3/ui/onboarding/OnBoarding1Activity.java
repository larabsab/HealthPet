/*
    Primeira tela de onboarding do app.
    Exibe uma mensagem estilizada e permite que o usuário:
    - Pule o onboarding e vá para a tela de login;
    - Avance para a próxima tela de onboarding.
 */

package com.example.healthpet3.ui.onboarding;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.R;
import com.example.healthpet3.ui.login.LoginActivity;

public class OnBoarding1Activity extends AppCompatActivity {

    private static final String TARGET_TEXT = "keep your bestie";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);

        TextView skipText = findViewById(R.id.skipText);
        LinearLayout nextButtonContainer = findViewById(R.id.nextButtonContainer);
        TextView descriptionText = findViewById(R.id.descriptionText);

        // Ao clicar em "Skip", ir para Login
        skipText.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });

        // Ao clicar em "Next", ir para próxima tela de onboarding
        nextButtonContainer.setOnClickListener(v -> {
            startActivity(new Intent(this, OnBoarding2Activity.class));
            finish();
        });

        // Aplica estilo ao texto principal da tela
        applyStyledText(descriptionText);
    }

    private void applyStyledText(TextView textView) {
        String fullText = "Helping you\nkeep your bestie\nstay healthy!";
        SpannableString styledText = new SpannableString(fullText);

        int start = fullText.indexOf(TARGET_TEXT);
        int end = start + TARGET_TEXT.length();

        styledText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        textView.setText(styledText);
    }
}