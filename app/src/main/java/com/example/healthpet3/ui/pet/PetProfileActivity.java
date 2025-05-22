
package com.example.healthpet3.ui.pet;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthpet3.R;
import com.example.healthpet3.adapters.StatusAdapter;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.ui.tutor.TutorProfileActivity;
import com.example.healthpet3.utils.ImageUtils;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PetProfileActivity extends AppCompatActivity {

    private ImageView petImage, petGenderIcon;
    private TextView petNameText, petBreedText, petAgeText, petWeightText, petHeightText, petColorText;
    private RecyclerView statusRecyclerView;
    private TextView aboutTitle, statusTitle;
    private CircleImageView tutorProfileImage;
    private StatusAdapter statusAdapter;
    private PetProfileViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_profile);

        initializeViews();
        setupViewModel();
        setupRecyclerView();
        setupClickListeners();

        String petId = getIntent().getStringExtra("petId");
        if (petId == null || petId.isEmpty()) {
            Toast.makeText(this, "Erro: ID do pet não foi fornecido.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        viewModel.loadPetData(petId);
    }

    private void initializeViews() {
        petImage = findViewById(R.id.petImageView);
        petNameText = findViewById(R.id.petNameTextView);
        petBreedText = findViewById(R.id.petBreedTextView);
        petAgeText = findViewById(R.id.petAgeTextView);
        petWeightText = findViewById(R.id.petWeightTextView);
        petHeightText = findViewById(R.id.petHeightTextView);
        petColorText = findViewById(R.id.petColorTextView);
        statusRecyclerView = findViewById(R.id.statusRecyclerView);
        petGenderIcon = findViewById(R.id.petGenderIcon);
        aboutTitle = findViewById(R.id.aboutPetName);
        statusTitle = findViewById(R.id.statusTitle);
        tutorProfileImage = findViewById(R.id.tutorProfileImage);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(PetProfileViewModel.class);

        viewModel.getPetData().observe(this, this::updatePetInfo);
        viewModel.getStatusList().observe(this, statuses -> {
            statusAdapter.setStatusList(statuses);
        });
        viewModel.getErrorMessage().observe(this, message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
            }
        });
        viewModel.getTutorImage().observe(this, base64Image -> {
            ImageUtils.setBase64Image(tutorProfileImage, base64Image, true);
        });
    }

    private void setupRecyclerView() {
        statusAdapter = new StatusAdapter(this, new ArrayList<>());
        statusRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        statusRecyclerView.setAdapter(statusAdapter);
    }

    private void setupClickListeners() {
        tutorProfileImage.setOnClickListener(v -> {
            Intent intent = new Intent(PetProfileActivity.this, TutorProfileActivity.class);
            startActivity(intent);
        });

        ImageButton buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(v -> finish());
    }

    private void updatePetInfo(Pet pet) {
        if (pet != null) {
            petNameText.setText(pet.getName());
            petBreedText.setText(pet.getBreed());
            petColorText.setText(pet.getColor());
            petWeightText.setText(pet.getWeight() + " kg");
            petHeightText.setText(pet.getHeight() + " cm");
            petAgeText.setText(viewModel.calculateAge(pet.getAge()));
            aboutTitle.setText("About " + pet.getName());
            statusTitle.setText(pet.getName() + "'s status");

            ImageUtils.setBase64Image(petImage, pet.getImageUrl(), false);

            updateGenderIcon(pet.getGender());
        }
    }

    private void updateGenderIcon(String gender) {
        if (gender != null) {
            if (gender.equalsIgnoreCase("male")) {
                petGenderIcon.setBackgroundResource(R.drawable.gender_male_background);
                petGenderIcon.setImageResource(R.drawable.ic_male_symbol);
            } else if (gender.equalsIgnoreCase("female")) {
                petGenderIcon.setBackgroundResource(R.drawable.gender_female_background);
                petGenderIcon.setImageResource(R.drawable.ic_female_symbol);
            } else {
                petGenderIcon.setBackgroundResource(R.drawable.gender_background_placeholder);
                petGenderIcon.setImageResource(R.drawable.ic_gender_symbol);
            }
        } else {
            petGenderIcon.setBackgroundResource(R.drawable.gender_background_placeholder);
            petGenderIcon.setImageResource(R.drawable.ic_gender_symbol);
        }
    }
}