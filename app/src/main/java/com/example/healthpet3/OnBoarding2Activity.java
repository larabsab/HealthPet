package com.example.healthpet3;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class OnBoarding2Activity extends AppCompatActivity {

    private TextView skipText;
    private LinearLayout nextButtonContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding2);

        skipText = findViewById(R.id.skipText);
        nextButtonContainer = findViewById(R.id.nextButtonContainer);

        skipText.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoarding2Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        nextButtonContainer.setOnClickListener(v -> {
            Intent intent = new Intent(OnBoarding2Activity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
