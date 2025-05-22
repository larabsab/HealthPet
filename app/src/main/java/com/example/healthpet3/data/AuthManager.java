package com.example.healthpet3.data;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.healthpet3.R;
import com.google.android.gms.auth.api.signin.*;
import com.google.android.gms.common.api.ApiException;
import com.google.firebase.auth.*;
import com.google.android.gms.tasks.Task;

public class AuthManager {

    private final FirebaseAuth mAuth;
    private final GoogleSignInClient googleSignInClient;
    private final Context context;
    private final Runnable onSuccess;
    private final BiCallback<String, String> onError;

    public static final int RC_SIGN_IN = 9001;

    public AuthManager(Activity activity, Runnable onSuccess, BiCallback<String, String> onError) {
        this.context = activity;
        this.onSuccess = onSuccess;
        this.onError = onError;

        mAuth = FirebaseAuth.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(activity.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(activity, gso);
    }

    public void loginWithGoogle() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void handleGoogleSignInResult(Intent data, Callback<String> onSuccess, Callback<String> onError) {
        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

        try {
            GoogleSignInAccount account = task.getResult(ApiException.class);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken(), onSuccess, onError);
            } else {
                onError.call("Conta Google nula");
            }
        } catch (ApiException e) {
            onError.call("Erro no login com Google: " + e.getMessage());
        }
    }
    private void firebaseAuthWithGoogle(String idToken, Callback<String> onSuccess, Callback<String> onError) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onSuccess.call("Login com Google bem-sucedido");
            } else {
                onError.call("Falha no login com Google");
            }
        });
    }

    public void loginWithEmail(String email, String password) {
        if (email.isEmpty() || password.isEmpty()) {
            onError.call("Campos obrigatórios", "Por favor, preencha e-mail e senha.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                onSuccess.run();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "";
                if (errorMessage.contains("no user record")) {
                    onError.call("Conta não encontrada", "Verifique o e-mail ou cadastre-se.");
                } else if (errorMessage.contains("The password is invalid")) {
                    onError.call("Senha incorreta", "Tente novamente com a senha correta.");
                } else {
                    onError.call("Erro no login", "Falha no login: " + errorMessage);
                }
            }
        });
    }

    public void sendPasswordReset(String email) {
        if (email.isEmpty()) {
            onError.call("Campo vazio", "Por favor, insira um e-mail.");
            return;
        }

        mAuth.sendPasswordResetEmail(email)
                .addOnSuccessListener(unused -> Toast.makeText(context, "Verifique sua caixa de entrada", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> onError.call("Erro", "Falha ao enviar e-mail: " + e.getMessage()));
    }
}
