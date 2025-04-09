package com.example.healthpet3;

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

public class OnBoarding1Activity extends AppCompatActivity {

    private TextView skipText;
    private LinearLayout nextButtonContainer;
    private TextView descriptionText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding1);

        skipText = findViewById(R.id.skipText);
        nextButtonContainer = findViewById(R.id.nextButtonContainer);
        descriptionText = findViewById(R.id.descriptionText);

        // Skip → Login
        skipText.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoarding1Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        // Next → OnBoarding2
        nextButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoarding1Activity.this, OnBoarding2Activity.class);
            startActivity(intent);
            finish();
        });

        // Texto estilizado
        String fullText = "Helping you\nkeep your bestie\nstay healthy!";
        SpannableString styledText = new SpannableString(fullText);

        int start = fullText.indexOf("keep your bestie");
        int end = start + "keep your bestie".length();

        styledText.setSpan(new StyleSpan(Typeface.BOLD), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        styledText.setSpan(new ForegroundColorSpan(Color.BLACK), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        descriptionText.setText(styledText);
    }
}
