/*
    EditPetActivity permite ao usuário editar os dados de um pet já cadastrado,
    como nome, raça, idade, peso, altura, cor e imagem.
    Os dados são carregados do Firebase Firestore e atualizados após validação.
*/

package com.example.healthpet3.ui.pet;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.healthpet3.R;
import com.example.healthpet3.adapters.CustomSpinnerAdapter;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.repositories.TutorRepository;
import com.example.healthpet3.utils.ImageUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EditPetActivity extends AppCompatActivity {
    private Spinner petSpinner, petGender;
    private EditText petName, petBreed, petAge, petSpecies, petObservations, petWeight, petHeight, petColor;
    private ShapeableImageView petImageView;
    private View petImageButton;
    private AppCompatButton btnSavePet;
    private Uri petImageUri;
    private String imageBase64;
    private PetRepository petRepository;
    private TutorRepository tutorRepository;
    private String tutorEmail;
    private List<Pet> petsList = new ArrayList<>();
    private List<String> petNamesList = new ArrayList<>();
    private Pet selectedPet;
    private String clinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pet);

        petSpinner = findViewById(R.id.petSpinner);
        petName = findViewById(R.id.petName);
        petBreed = findViewById(R.id.petBreed);
        petAge = findViewById(R.id.petAge);
        petSpecies = findViewById(R.id.petSpecies);
        petGender = findViewById(R.id.petGender);
        petObservations = findViewById(R.id.petObservations);
        petWeight = findViewById(R.id.petWeight);
        petHeight = findViewById(R.id.petHeight);
        petColor = findViewById(R.id.petColor);
        petImageView = findViewById(R.id.petImageView);
        petImageButton = findViewById(R.id.petImageButton);
        btnSavePet = findViewById(R.id.btnSavePet);

        petRepository = new PetRepository();
        tutorRepository = new TutorRepository();
        tutorEmail = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : null;

        setupGenderSpinner();
        loadTutorClinic();
        loadPets();

        petImageButton.setOnClickListener(v -> selectImage());
        btnSavePet.setOnClickListener(v -> savePet());
    }

    private void setupGenderSpinner() {
        List<String> genderList = java.util.Arrays.asList("Select Gender", "Male", "Female");
        CustomSpinnerAdapter genderAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, genderList);
        petGender.setAdapter(genderAdapter);
    }

    private void loadTutorClinic() {
        tutorRepository.getTutorById(FirebaseAuth.getInstance().getCurrentUser().getUid(), new TutorRepository.TutorCallback() {
            @Override
            public void onSuccess(com.example.healthpet3.models.Tutor tutor) {
                if (tutor != null) {
                    clinic = tutor.getClinic();
                }
            }
            @Override
            public void onFailure(Exception e) {
                clinic = "";
            }
        });
    }

    private void loadPets() {
        if (tutorEmail == null) return;
        petRepository.getPetsByTutor(tutorEmail, pets -> {
            petsList = pets;
            petNamesList.clear();
            petNamesList.add("Selecione seu pet");
            for (Pet pet : petsList) {
                petNamesList.add(pet.getName());
            }
            CustomSpinnerAdapter petAdapter = new CustomSpinnerAdapter(EditPetActivity.this, R.layout.spinner_item, petNamesList);
            petSpinner.setAdapter(petAdapter);
            petSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (position > 0 && (position - 1) < petsList.size()) {
                        loadPetData(petsList.get(position - 1));
                    } else {
                        clearPetFields();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {}
            });

            clearPetFields();
        });
    }

    private void loadPetData(Pet pet) {
        selectedPet = pet;
        petName.setText(pet.getName());
        petBreed.setText(pet.getBreed());
        petAge.setText(pet.getAge());
        petSpecies.setText(pet.getSpecies());
        petObservations.setText(pet.getObservations());
        petWeight.setText(pet.getWeight());
        petHeight.setText(pet.getHeight());
        petColor.setText(pet.getColor());
        imageBase64 = pet.getImageBase64();
        if (imageBase64 != null && !imageBase64.isEmpty()) {
            ImageUtils.setBase64Image(petImageView, imageBase64, true);
        } else {
            petImageView.setImageResource(R.drawable.peticon4);
        }
        // Seleciona o gênero correto
        if (pet.getGender() != null) {
            String gender = pet.getGender();
            for (int i = 0; i < petGender.getCount(); i++) {
                if (petGender.getItemAtPosition(i).toString().equalsIgnoreCase(gender)) {
                    petGender.setSelection(i);
                    break;
                }
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 3001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3001 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            petImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), petImageUri);
                petImageView.setImageBitmap(bitmap);
                String base64 = encodeImageToBase64(bitmap);
                imageBase64 = base64;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        return android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT);
    }

    private void savePet() {
        if (selectedPet == null) return;
        String name = petName.getText().toString().trim();
        String breed = petBreed.getText().toString().trim();
        String age = petAge.getText().toString().trim();
        String species = petSpecies.getText().toString().trim();
        String gender = petGender.getSelectedItem() != null ? petGender.getSelectedItem().toString() : "";
        String observations = petObservations.getText().toString().trim();
        String weight = petWeight.getText().toString().trim();
        String height = petHeight.getText().toString().trim();
        String color = petColor.getText().toString().trim();
        // Só salva se algum campo mudou
        if ((TextUtils.isEmpty(name) || name.equals(selectedPet.getName())) &&
                (TextUtils.isEmpty(breed) || breed.equals(selectedPet.getBreed())) &&
                (TextUtils.isEmpty(age) || age.equals(selectedPet.getAge())) &&
                (TextUtils.isEmpty(species) || species.equals(selectedPet.getSpecies())) &&
                (TextUtils.isEmpty(gender) || gender.equals(selectedPet.getGender())) &&
                (TextUtils.isEmpty(observations) || observations.equals(selectedPet.getObservations())) &&
                (TextUtils.isEmpty(weight) || weight.equals(selectedPet.getWeight())) &&
                (TextUtils.isEmpty(height) || height.equals(selectedPet.getHeight())) &&
                (TextUtils.isEmpty(color) || color.equals(selectedPet.getColor())) &&
                (TextUtils.isEmpty(imageBase64) || imageBase64.equals(selectedPet.getImageBase64()))) {
            Toast.makeText(this, "Altere pelo menos um campo", Toast.LENGTH_SHORT).show();
            return;
        }
        Pet pet = new Pet(
                !TextUtils.isEmpty(name) ? name : selectedPet.getName(),
                !TextUtils.isEmpty(breed) ? breed : selectedPet.getBreed(),
                !TextUtils.isEmpty(age) ? age : selectedPet.getAge(),
                !TextUtils.isEmpty(species) ? species : selectedPet.getSpecies(),
                !TextUtils.isEmpty(gender) ? gender : selectedPet.getGender(),
                !TextUtils.isEmpty(observations) ? observations : selectedPet.getObservations(),
                selectedPet.getTutorEmail(),
                !TextUtils.isEmpty(weight) ? weight : selectedPet.getWeight(),
                !TextUtils.isEmpty(height) ? height : selectedPet.getHeight(),
                !TextUtils.isEmpty(color) ? color : selectedPet.getColor()
        );
        pet.setId(selectedPet.getId());
        pet.setImageBase64(!TextUtils.isEmpty(imageBase64) ? imageBase64 : selectedPet.getImageBase64());
        pet.setImageUrl(!TextUtils.isEmpty(imageBase64) ? imageBase64 : selectedPet.getImageBase64()); // Salva em imageUrl
        pet.setClinic(clinic); // Atualiza clínica do tutor
        petRepository.updatePet(pet, new PetRepository.UpdatePetsEmailCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(EditPetActivity.this, "Pet atualizado com sucesso!", Toast.LENGTH_SHORT).show();
                finish();
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditPetActivity.this, "Erro ao atualizar pet: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void clearPetFields() {
        petName.setText("");
        petBreed.setText("");
        petAge.setText("");
        petSpecies.setText("");
        petObservations.setText("");
        petWeight.setText("");
        petHeight.setText("");
        petColor.setText("");
        petGender.setSelection(0);
        petImageView.setImageResource(R.drawable.peticon4);
        imageBase64 = "";
        selectedPet = null;
    }
}
