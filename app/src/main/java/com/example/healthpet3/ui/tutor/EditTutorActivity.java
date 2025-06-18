/*
    EditTutorActivity permite que o tutor edite suas informações pessoais,
    como nome, e-mail ou senha.
    Os dados são atualizados no Firebase Authentication e Firestore, com validação.
*/

package com.example.healthpet3.ui.tutor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.healthpet3.R;
import com.example.healthpet3.models.Tutor;
import com.example.healthpet3.repositories.TutorRepository;
import com.example.healthpet3.utils.ImageUtils;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.healthpet3.data.FirebaseStorageService;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.adapters.CustomSpinnerAdapter;
import java.util.List;
import java.util.ArrayList;

public class EditTutorActivity extends AppCompatActivity {
    private EditText tutorName, tutorEmail, tutorPhone;
    private Spinner tutorClinic;
    private ShapeableImageView tutorImageView;
    private View tutorImageButton;
    private AppCompatButton btnSaveTutor, btnChangePassword;
    private ImageView btnBack;
    private Uri tutorImageUri;
    private String imageBase64;
    private TutorRepository tutorRepository;
    private String uid;
    private Tutor oldTutor;
    private FirebaseStorageService storageService;
    private PetRepository petRepository = new PetRepository();
    private List<String> clinicsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutor);

        tutorName = findViewById(R.id.tutorName);
        tutorEmail = findViewById(R.id.tutorEmail);
        tutorPhone = findViewById(R.id.tutorPhone);
        tutorClinic = findViewById(R.id.tutorClinic);
        tutorImageView = findViewById(R.id.tutorImageView);
        tutorImageButton = findViewById(R.id.tutorImageButton);
        btnSaveTutor = findViewById(R.id.btnSaveTutor);
        btnChangePassword = findViewById(R.id.btnChangePassword);
        btnBack = findViewById(R.id.btnBack);

        tutorRepository = new TutorRepository();
        uid = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        btnBack.setOnClickListener(v -> finish());
        tutorImageButton.setOnClickListener(v -> selectImage());
        btnSaveTutor.setOnClickListener(v -> saveTutor());
        btnChangePassword.setOnClickListener(v -> showChangePasswordDialog());

        loadTutorData();
        storageService = new FirebaseStorageService();
        carregarClinicas();
    }

    private void loadTutorData() {
        if (uid == null) return;
        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
            @Override
            public void onSuccess(Tutor tutor) {
                if (tutor != null) {
                    oldTutor = tutor;
                    tutorName.setText(tutor.getName());
                    tutorEmail.setText(tutor.getEmail());
                    tutorPhone.setText(tutor.getPhone());
                    selecionarClinicaDoTutor(tutor.getClinic());
                    imageBase64 = tutor.getImageBase64();
                    if (imageBase64 != null && !imageBase64.isEmpty()) {
                        ImageUtils.setBase64Image(tutorImageView, imageBase64, true);
                    } else {
                        tutorImageView.setImageResource(R.drawable.tutoricon2);
                    }
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditTutorActivity.this, "Erro ao carregar dados", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 2001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2001 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            tutorImageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), tutorImageUri);
                tutorImageView.setImageBitmap(bitmap);
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


    private void saveTutor() {
        String name = tutorName.getText().toString().trim();
        String email = tutorEmail.getText().toString().trim();
        String phone = tutorPhone.getText().toString().trim();
        String clinic = tutorClinic.getSelectedItem() != null ? tutorClinic.getSelectedItem().toString() : "";
        String oldEmail = oldTutor != null ? oldTutor.getEmail() : "";
        String oldClinic = oldTutor != null ? oldTutor.getClinic() : "";
        boolean emailChanged = !TextUtils.isEmpty(email) && !email.equals(oldEmail);
        boolean clinicChanged = !TextUtils.isEmpty(clinic) && !clinic.equals(oldClinic);
        if ((TextUtils.isEmpty(name) || name.equals(oldTutor != null ? oldTutor.getName() : "")) &&
                (TextUtils.isEmpty(email) || email.equals(oldEmail)) &&
                (TextUtils.isEmpty(phone) || phone.equals(oldTutor != null ? oldTutor.getPhone() : "")) &&
                (TextUtils.isEmpty(clinic) || clinic.equals(oldClinic)) &&
                (TextUtils.isEmpty(imageBase64) || (oldTutor != null && imageBase64.equals(oldTutor.getImageBase64())))) {
            Toast.makeText(this, "Altere pelo menos um campo", Toast.LENGTH_SHORT).show();
            return;
        }
        Tutor tutor = new Tutor(
                !TextUtils.isEmpty(name) ? name : oldTutor.getName(),
                !TextUtils.isEmpty(email) ? email : oldEmail,
                !TextUtils.isEmpty(phone) ? phone : oldTutor.getPhone(),
                !TextUtils.isEmpty(clinic) ? clinic : oldClinic
        );
        tutor.setImageBase64(!TextUtils.isEmpty(imageBase64) ? imageBase64 : oldTutor.getImageBase64());
        if (emailChanged) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.updateEmail(email)
                        .addOnSuccessListener(aVoid -> {
                            atualizarTutorEBuscarPets(tutor, oldEmail, email, clinicChanged, clinic);
                        })
                        .addOnFailureListener(e -> {
                            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("recent login")) {
                                solicitarReautenticacaoParaEmail(user, oldEmail, email, tutor, clinicChanged, clinic);
                            } else {
                                Toast.makeText(EditTutorActivity.this, "Erro ao atualizar e-mail no Auth: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });
            }
        } else {
            atualizarTutorApenas(tutor, clinicChanged, clinic, email);
        }
    }

    private void atualizarTutorEBuscarPets(Tutor tutor, String oldEmail, String newEmail, boolean clinicChanged, String newClinic) {
        tutorRepository.updateTutor(uid, tutor, new TutorRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                petRepository.updatePetsTutorEmail(oldEmail, newEmail, new PetRepository.UpdatePetsEmailCallback() {
                    @Override
                    public void onSuccess() {
                        if (clinicChanged) {
                            petRepository.updatePetsClinicByTutorEmail(newEmail, newClinic, new PetRepository.UpdatePetsEmailCallback() {
                                @Override
                                public void onSuccess() {
                                    Toast.makeText(EditTutorActivity.this, "Dados, pets e clínica atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                                    setResult(Activity.RESULT_OK);
                                    finish();
                                }
                                @Override
                                public void onFailure(Exception e) {
                                    Toast.makeText(EditTutorActivity.this, "Erro ao atualizar clínica dos pets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            Toast.makeText(EditTutorActivity.this, "Dados e pets atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    }
                    @Override
                    public void onFailure(Exception e) {
                        Toast.makeText(EditTutorActivity.this, "Erro ao atualizar pets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditTutorActivity.this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarTutorApenas(Tutor tutor, boolean clinicChanged, String newClinic, String email) {
        tutorRepository.updateTutor(uid, tutor, new TutorRepository.UpdateCallback() {
            @Override
            public void onSuccess() {
                if (clinicChanged) {
                    petRepository.updatePetsClinicByTutorEmail(email, newClinic, new PetRepository.UpdatePetsEmailCallback() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(EditTutorActivity.this, "Dados e clínica dos pets atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                        @Override
                        public void onFailure(Exception e) {
                            Toast.makeText(EditTutorActivity.this, "Erro ao atualizar clínica dos pets: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(EditTutorActivity.this, "Dados atualizados com sucesso!", Toast.LENGTH_SHORT).show();
                    setResult(Activity.RESULT_OK);
                    finish();
                }
            }
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(EditTutorActivity.this, "Erro ao atualizar: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void solicitarReautenticacaoParaEmail(FirebaseUser user, String oldEmail, String newEmail, Tutor tutor, boolean clinicChanged, String newClinic) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Reautenticação necessária");
        builder.setMessage("Por segurança, digite sua senha atual para alterar o e-mail.");
        final EditText input = new EditText(this);
        input.setHint("Senha atual");
        builder.setView(input);
        builder.setPositiveButton("Confirmar", (dialog, which) -> {
            String senhaAtual = input.getText().toString();
            if (TextUtils.isEmpty(senhaAtual)) {
                Toast.makeText(this, "Digite sua senha.", Toast.LENGTH_SHORT).show();
                return;
            }
            user.reauthenticate(EmailAuthProvider.getCredential(oldEmail, senhaAtual))
                    .addOnSuccessListener(aVoid -> {
                        user.updateEmail(newEmail)
                                .addOnSuccessListener(aVoid2 -> {
                                    atualizarTutorEBuscarPets(tutor, oldEmail, newEmail, clinicChanged, newClinic);
                                })
                                .addOnFailureListener(e2 -> Toast.makeText(EditTutorActivity.this, "Erro ao atualizar e-mail após reautenticação: " + e2.getMessage(), Toast.LENGTH_LONG).show());
                    })
                    .addOnFailureListener(e1 -> Toast.makeText(EditTutorActivity.this, "Reautenticação falhou: " + e1.getMessage(), Toast.LENGTH_LONG).show());
        });
        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showChangePasswordDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_password, null);
        EditText edtCurrent = dialogView.findViewById(R.id.edtCurrentPassword);
        EditText edtNew = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirm = dialogView.findViewById(R.id.edtConfirmNewPassword);
        AppCompatButton btnCancel = dialogView.findViewById(R.id.btnCancelChangePassword);
        AppCompatButton btnSave = dialogView.findViewById(R.id.btnSaveChangePassword);
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();
        btnCancel.setOnClickListener(v -> dialog.dismiss());
        btnSave.setOnClickListener(v -> {
            String current = edtCurrent.getText().toString();
            String newPass = edtNew.getText().toString();
            String confirm = edtConfirm.getText().toString();
            if (TextUtils.isEmpty(current) || TextUtils.isEmpty(newPass) || TextUtils.isEmpty(confirm)) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newPass.equals(confirm)) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show();
                return;
            }
            changePassword(current, newPass, dialog);
        });
        dialog.show();
    }

    private void changePassword(String currentPassword, String newPassword, AlertDialog dialog) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            return;
        }
        user.reauthenticate(EmailAuthProvider.getCredential(user.getEmail(), currentPassword))
                .addOnSuccessListener(aVoid -> {
                    user.updatePassword(newPassword)
                            .addOnSuccessListener(aVoid1 -> {
                                Toast.makeText(this, "Senha alterada com sucesso!", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Erro ao alterar senha: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Senha atual incorreta", Toast.LENGTH_SHORT).show());
    }

    private void carregarClinicas() {
        storageService.getClinicsList(new com.example.healthpet3.data.StorageService.StorageCallback<List<String>>() {
            @Override
            public void onSuccess(List<String> clinics) {
                clinicsList = clinics;
                CustomSpinnerAdapter clinicAdapter = new CustomSpinnerAdapter(EditTutorActivity.this, R.layout.spinner_item, clinicsList);
                tutorClinic.setAdapter(clinicAdapter);
                if (oldTutor != null) {
                    selecionarClinicaDoTutor(oldTutor.getClinic());
                }
            }
            @Override
            public void onError(String message) {
                Toast.makeText(EditTutorActivity.this, "Erro ao carregar clínicas: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void selecionarClinicaDoTutor(String clinic) {
        if (clinic == null || clinicsList == null) return;
        int pos = clinicsList.indexOf(clinic);
        if (pos >= 0) {
            tutorClinic.setSelection(pos);
        }
    }
}
