package com.example.healthpet3.data;

public interface AuthService {
    interface AuthCallback {
        void onSuccess();
        void onError(String title, String message);
    }

    void registerWithEmail(String email, String password, AuthCallback callback);
    void registerWithGoogle(String idToken, AuthCallback callback);
    String getCurrentUserId();
}
