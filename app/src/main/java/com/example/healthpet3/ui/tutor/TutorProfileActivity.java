/*
    Tela responsável por exibir o perfil do tutor logado,
    carregando seus dados do Firebase Firestore e exibindo opções
    para editar as informações do tutor ou dos pets vinculados.
    Esta Activity comunica-se com o TutorRepository para obter os dados.
    !!!!! AINDA EM DESENVOLVIMENTO
 */


package com.example.healthpet3.ui.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.R;
import com.example.healthpet3.models.Tutor;
import com.example.healthpet3.repositories.TutorRepository;
import com.example.healthpet3.ui.pet.EditPetActivity;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;

public class TutorProfileActivity extends AppCompatActivity {

    private ImageView menuIcon;
    private TextView txtTutorName, txtEmail, txtPhone, txtCPF;

    private FirebaseAuth mAuth;
    private TutorRepository tutorRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        // Firebase Auth e repositório
        mAuth = FirebaseAuth.getInstance();
        tutorRepository = new TutorRepository();

        // Views
//        menuIcon = findViewById(R.id.menuIcon);
//        txtTutorName = findViewById(R.id.txtTutorName);
//        txtEmail = findViewById(R.id.txtEmail);
//        txtPhone = findViewById(R.id.txtPhone);
//        txtCPF = findViewById(R.id.txtCPF);

        menuIcon.setOnClickListener(v -> showEditOptions());

        loadTutorData();
    }

    private void loadTutorData() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            // redirecionar para login se quiser
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();

        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
            @Override
            public void onSuccess(Tutor tutor) {
                if (tutor != null) {
//                    txtTutorName.setText(tutor.getFullName());
//                    txtEmail.setText(tutor.getEmail());
//                    txtPhone.setText(tutor.getPhone());
//                    txtCPF.setText(tutor.getCpf());
                } else {
                    Toast.makeText(TutorProfileActivity.this, "Dados do tutor não encontrados.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(TutorProfileActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showEditOptions() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View bottomSheetView = LayoutInflater.from(this).inflate(
                R.layout.dialog_edit_options,
                findViewById(R.id.bottomSheetContainer)
        );

        Button btnEditTutor = bottomSheetView.findViewById(R.id.btnEditTutor);
        Button btnEditPets = bottomSheetView.findViewById(R.id.btnEditPets);

        btnEditTutor.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(TutorProfileActivity.this, EditTutorActivity.class));
        });

        btnEditPets.setOnClickListener(v -> {
            bottomSheetDialog.dismiss();
            startActivity(new Intent(TutorProfileActivity.this, EditPetActivity.class));
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();
    }
}