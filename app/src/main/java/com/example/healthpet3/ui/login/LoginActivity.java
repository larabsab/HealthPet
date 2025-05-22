/*
    Classe responsável por gerenciar a tela de login do aplicativo.
    Suporta autenticação via e-mail/senha e Google, além de permitir
    recuperação de senha e redirecionamento para a tela inicial (HomePet)
    ao encontrar o pet do tutor autenticado.
 */


package com.example.healthpet3.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.R;
import com.example.healthpet3.data.AuthManager;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.ui.homepet.HomePetActivity;
import com.example.healthpet3.ui.singnup.SignUpActivity;

public class LoginActivity extends AppCompatActivity {

    private AuthManager authManager;
    private PetRepository petRepository;
    private EditText emailField, passwordField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        authManager = new AuthManager(this, this::onLoginSuccess, this::showErrorDialog);
        petRepository = new PetRepository();

        LinearLayout googleLogin = findViewById(R.id.googleLogin);
        emailField = findViewById(R.id.emailInput);
        passwordField = findViewById(R.id.passwordInput);
        Button getStartedButton = findViewById(R.id.loginButton);
        TextView signUpText = findViewById(R.id.signUpText);
        TextView forgotPasswordText = findViewById(R.id.forgotPasswordText);

        googleLogin.setOnClickListener(v -> authManager.loginWithGoogle());

        getStartedButton.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Preencha todos os campos");
                return;
            }

            authManager.loginWithEmail(email, password);
        });

        signUpText.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));

        forgotPasswordText.setOnClickListener(v -> showRecoveryDialog());

    }

    private void onLoginSuccess() {
        petRepository.buscarPetDoTutor(this, petId -> {
            Intent intent = new Intent(this, HomePetActivity.class);
            intent.putExtra("petId", petId);
            startActivity(intent);
            finish();
        }, error -> showToast("Erro ao buscar pet: " + error));
    }

    private void showRecoveryDialog() {
        EditText inputEmail = new EditText(this);
        inputEmail.setHint("Digite seu e-mail");
        inputEmail.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        inputEmail.setPadding(32, 48, 32, 48);

        new AlertDialog.Builder(this)
                .setTitle("Recuperar senha")
                .setMessage("Digite seu e-mail para receber o link de recuperação:")
                .setView(inputEmail)
                .setPositiveButton("Enviar", (dialog, which) -> {
                    String email = inputEmail.getText().toString().trim();
                    if (email.isEmpty()) {
                        showToast("O campo de e-mail está vazio");
                        return;
                    }
                    authManager.sendPasswordReset(email);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showErrorDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}