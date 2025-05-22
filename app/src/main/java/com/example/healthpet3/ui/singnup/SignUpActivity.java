/*
    Atividade responsável pelo cadastro de tutores e pets.
    Permite seleção do tipo de cadastro, entrada de dados, upload de imagem
    e criação de conta no Firebase Authentication (para tutor) e Firestore (para ambos).

    Esta classe é um controlador da interface (ViewController) e deve delegar a lógica
    de negócio para outras classes para seguir o príncipio da responsabilidade única (SRP).
 */

package com.example.healthpet3.ui.singnup;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.example.healthpet3.R;
import com.example.healthpet3.adapters.CustomSpinnerAdapter;
import com.example.healthpet3.data.FirebaseAuthService;
import com.example.healthpet3.data.FirebaseStorageService;
import com.example.healthpet3.data.AuthService;
import com.example.healthpet3.data.StorageService;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.ui.homepet.HomePetActivity;
import com.example.healthpet3.ui.pet.PetProfileActivity;
import android.provider.MediaStore;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.List;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

public class SignUpActivity extends AppCompatActivity implements SignUpPresenter.SignUpView {
    private LinearLayout tutorForm, petForm;
    private Spinner spinnerTipo, petClinic, tutorClinic, petGender;
    private Button btnTutorRegister, btnPetRegister;
    private EditText tutorName, tutorEmail, tutorPassword, tutorPhone;
    private EditText petName, petBreed, petAge, tutorEmailPet;
    private EditText petWeight, petHeight, petColor;
    private LinearLayout petImageButton, tutorImageButton;
    private ImageView petImageView, tutorImageView;
    private Uri petImageUri, tutorImageUri;
    private EditText petSpecies, petObservations;
    private ProgressDialog progressDialog;
    private SignUpPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializeViews();
        setupSpinners();
        setupClickListeners();
        setupPresenter();
    }

    private void initializeViews() {
        tutorForm = findViewById(R.id.tutorForm);
        petForm = findViewById(R.id.petForm);
        spinnerTipo = findViewById(R.id.spinnerTipo);

        // Tutor views
        tutorName = findViewById(R.id.tutorName);
        tutorEmail = findViewById(R.id.tutorEmail);
        tutorPassword = findViewById(R.id.tutorPassword);
        tutorPhone = findViewById(R.id.tutorPhone);
        tutorClinic = findViewById(R.id.tutorClinic);
        btnTutorRegister = findViewById(R.id.btnTutorRegister);
        tutorImageButton = findViewById(R.id.tutorImageButton);
        tutorImageView = findViewById(R.id.tutorImageView);

        // Pet views
        petName = findViewById(R.id.petName);
        petBreed = findViewById(R.id.petBreed);
        petAge = findViewById(R.id.petAge);
        tutorEmailPet = findViewById(R.id.tutorEmailPet);
        btnPetRegister = findViewById(R.id.btnPetRegister);
        petSpecies = findViewById(R.id.petSpecies);
        petGender = findViewById(R.id.petGender);
        petObservations = findViewById(R.id.petObservations);
        petWeight = findViewById(R.id.petWeight);
        petHeight = findViewById(R.id.petHeight);
        petColor = findViewById(R.id.petColor);
        petImageButton = findViewById(R.id.petImageButton);
        petImageView = findViewById(R.id.petImageView);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Processando...");
        progressDialog.setCancelable(false);
    }

    private void setupPresenter() {
        AuthService authService = new FirebaseAuthService();
        StorageService storageService = new FirebaseStorageService();
        presenter = new SignUpPresenter(this, authService, storageService);
        presenter.loadClinics();
    }

    private void setupSpinners() {
        // Type Spinner
        List<String> tipoOptions = java.util.Arrays.asList("Select Type", "Tutor", "Pet");
        CustomSpinnerAdapter tipoAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, tipoOptions);
        spinnerTipo.setAdapter(tipoAdapter);

        // Gender Spinner
        List<String> genderList = java.util.Arrays.asList("Select Gender", "Male", "Female");
        CustomSpinnerAdapter genderAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, genderList);
        petGender.setAdapter(genderAdapter);
    }

    private void setupClickListeners() {
        spinnerTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                tutorForm.setVisibility(selected.equals("Tutor") ? View.VISIBLE : View.GONE);
                petForm.setVisibility(selected.equals("Pet") ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnTutorRegister.setOnClickListener(v -> registerTutor());
        btnPetRegister.setOnClickListener(v -> registerPet());

        setupImageButtons();
        setupDatePicker();
    }

    private void setupImageButtons() {
        petImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 1001);
        });

        tutorImageButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 2001);
        });
    }

    private void setupDatePicker() {
        petAge.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                String dateString = day + "/" + (month + 1) + "/" + year;
                petAge.setText(dateString);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void registerTutor() {
        String name = tutorName.getText().toString().trim();
        String email = tutorEmail.getText().toString().trim();
        String password = tutorPassword.getText().toString().trim();
        String phone = tutorPhone.getText().toString().trim();
        String clinic = tutorClinic.getSelectedItem().toString();

        if (validateTutorInput(name, email, password, phone, clinic)) {
            String imageBase64 = tutorImageUri != null ? encodeImageToBase64(tutorImageUri) : null;
            presenter.registerTutor(email, password, name, phone, clinic, imageBase64);
        }
    }

    private void registerPet() {
        if (validatePetInput()) {
            Pet pet = new Pet(
                    petName.getText().toString().trim(),
                    petBreed.getText().toString().trim(),
                    petAge.getText().toString().trim(),
                    petSpecies.getText().toString().trim(),
                    petGender.getSelectedItem().toString(),
                    petObservations.getText().toString().trim(),
                    tutorEmailPet.getText().toString().trim(),
                    petWeight.getText().toString().trim(),
                    petHeight.getText().toString().trim(),
                    petColor.getText().toString().trim()
            );

            if (petImageUri != null) {
                pet.setImageBase64(encodeImageToBase64(petImageUri));
            }

            presenter.registerPet(pet);
        }
    }

    private boolean validateTutorInput(String name, String email, String password,
                                       String phone, String clinic) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) ||
                TextUtils.isEmpty(password) || TextUtils.isEmpty(phone) ||
                clinic.equals("Select Clinic")) {
            showError("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return false;
        }
        return true;
    }

    private boolean validatePetInput() {
        if (TextUtils.isEmpty(petName.getText()) ||
                TextUtils.isEmpty(petBreed.getText()) ||
                TextUtils.isEmpty(petAge.getText()) ||
                TextUtils.isEmpty(petSpecies.getText()) ||
                petGender.getSelectedItem().toString().equals("Select Gender") ||
                TextUtils.isEmpty(tutorEmailPet.getText()) ||
                TextUtils.isEmpty(petWeight.getText()) ||
                TextUtils.isEmpty(petHeight.getText()) ||
                TextUtils.isEmpty(petColor.getText())) {

            showError("Campos obrigatórios", "Por favor, preencha todos os campos.");
            return false;
        }
        return true;
    }

    private String encodeImageToBase64(Uri imageUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1001) {
                petImageUri = data.getData();
                petImageView.setImageURI(petImageUri);
            } else if (requestCode == 2001) {
                tutorImageUri = data.getData();
                tutorImageView.setImageURI(tutorImageUri);
            }
        }
    }

    // SignUpPresenter.SignUpView Implementation
    @Override
    public void showLoading() {
        progressDialog.show();
    }

    @Override
    public void hideLoading() {
        progressDialog.dismiss();
    }

    @Override
    public void showError(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public void onTutorRegistrationSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage("Tutor cadastrado com sucesso!")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(this, HomePetActivity.class));
                    finish();
                })
                .show();
    }

    @Override
    public void onPetRegistrationSuccess() {
        new AlertDialog.Builder(this)
                .setTitle("Sucesso")
                .setMessage("Pet cadastrado com sucesso!")
                .setPositiveButton("OK", (dialog, which) -> {
                    startActivity(new Intent(this, PetProfileActivity.class));
                    finish();
                })
                .show();
    }

    @Override
    public void updateClinicsList(List<String> clinics) {
        CustomSpinnerAdapter clinicAdapter = new CustomSpinnerAdapter(this, R.layout.spinner_item, clinics);
        tutorClinic.setAdapter(clinicAdapter);
    }
}