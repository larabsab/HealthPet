package com.example.healthpet3;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private LinearLayout tutorForm, petForm;
    private Button btnTutor, btnPet, btnTutorRegister, btnPetRegister;
    private EditText tutorName, tutorEmail, tutorPassword, petName, petBreed, tutorEmailPet;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        tutorForm = findViewById(R.id.tutorForm);
        petForm = findViewById(R.id.petForm);
        btnTutor = findViewById(R.id.btnTutor);
        btnPet = findViewById(R.id.btnPet);
        btnTutorRegister = findViewById(R.id.btnTutorRegister);
        btnPetRegister = findViewById(R.id.btnPetRegister);

        tutorName = findViewById(R.id.tutorName);
        tutorEmail = findViewById(R.id.tutorEmail);
        tutorPassword = findViewById(R.id.tutorPassword);

        petName = findViewById(R.id.petName);
        petBreed = findViewById(R.id.petBreed);
        tutorEmailPet = findViewById(R.id.tutorEmailPet);

        TextView tvLoginRedirect = findViewById(R.id.tvLoginRedirect);
        tvLoginRedirect.setOnClickListener(v -> {
            // Finaliza a tela atual e volta para a LoginActivity
            finish();
        });

        btnTutor.setOnClickListener(v -> {
            tutorForm.setVisibility(View.VISIBLE);
            petForm.setVisibility(View.GONE);
        });

        btnPet.setOnClickListener(v -> {
            tutorForm.setVisibility(View.GONE);
            petForm.setVisibility(View.VISIBLE);
        });

        btnTutorRegister.setOnClickListener(v -> registerTutor());
        btnPetRegister.setOnClickListener(v -> registerPet());
    }

    private void registerTutor() {
        String name = tutorName.getText().toString().trim();
        String email = tutorEmail.getText().toString().trim();
        String password = tutorPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "A senha deve ter pelo menos 6 caracteres", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Map<String, Object> tutor = new HashMap<>();
                tutor.put("name", name);
                tutor.put("email", email);

                db.collection("tutors").document(mAuth.getCurrentUser().getUid())
                        .set(tutor)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Cadastro de Tutor feito com sucesso!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar no Firestore", Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Erro: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void registerPet() {
        String name = petName.getText().toString().trim();
        String breed = petBreed.getText().toString().trim();
        String tutorEmail = tutorEmailPet.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(breed) || TextUtils.isEmpty(tutorEmail)) {
            Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> pet = new HashMap<>();
        pet.put("name", name);
        pet.put("breed", breed);
        pet.put("tutorEmail", tutorEmail);

        db.collection("pets")
                .add(pet)
                .addOnSuccessListener(documentReference -> Toast.makeText(this, "Cadastro de Pet feito com sucesso!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(this, "Erro ao salvar no Firestore", Toast.LENGTH_SHORT).show());
    }
}
