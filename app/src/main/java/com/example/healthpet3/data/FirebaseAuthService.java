package com.example.healthpet3.data;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;

public class FirebaseAuthService implements AuthService {
    private final FirebaseAuth auth;

    public FirebaseAuthService() {
        this.auth = FirebaseAuth.getInstance();
    }

    @Override
    public void registerWithEmail(String email, String password, AuthCallback callback) {
        auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Erro no registro", e.getMessage()));
    }

    @Override
    public void registerWithGoogle(String idToken, AuthCallback callback) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        auth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onError("Erro no registro Google", e.getMessage()));
    }

    @Override
    public String getCurrentUserId() {
        return auth.getCurrentUser() != null ? auth.getCurrentUser().getUid() : null;
    }
}