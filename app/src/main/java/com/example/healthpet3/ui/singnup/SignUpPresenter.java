package com.example.healthpet3.ui.singnup;

import com.example.healthpet3.data.AuthService;
import com.example.healthpet3.data.StorageService;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.models.Tutor;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.text.ParseException;

public class SignUpPresenter {
    private final AuthService authService;
    private final StorageService storageService;
    private final SignUpView view;

    public interface SignUpView {
        void showLoading();
        void hideLoading();
        void showError(String title, String message);
        void onTutorRegistrationSuccess();
        void onPetRegistrationSuccess();
        void updateClinicsList(java.util.List<String> clinics);
    }

    public SignUpPresenter(SignUpView view, AuthService authService, StorageService storageService) {
        this.view = view;
        this.authService = authService;
        this.storageService = storageService;
    }

    public void loadClinics() {
        storageService.getClinicsList(new StorageService.StorageCallback<java.util.List<String>>() {
            @Override
            public void onSuccess(java.util.List<String> clinics) {
                view.updateClinicsList(clinics);
            }

            @Override
            public void onError(String message) {
                view.showError("Erro", "Falha ao carregar clínicas: " + message);
            }
        });
    }

    public void registerTutor(String email, String password, String name, String phone, String clinic, String imageBase64) {
        view.showLoading();

        authService.registerWithEmail(email, password, new AuthService.AuthCallback() {
            @Override
            public void onSuccess() {
                String userId = authService.getCurrentUserId();
                Tutor tutor = new Tutor(name, email, phone, clinic);

                if (imageBase64 != null && !imageBase64.isEmpty()) {
                    tutor.setImageBase64(imageBase64);
                }

                storageService.saveTutor(userId, tutor, new StorageService.StorageCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        view.hideLoading();
                        view.onTutorRegistrationSuccess();
                    }

                    @Override
                    public void onError(String message) {
                        view.hideLoading();
                        view.showError("Erro", "Falha ao salvar tutor: " + message);
                    }
                });
            }

            @Override
            public void onError(String title, String message) {
                view.hideLoading();
                view.showError(title, message);
            }
        });
    }

    private String convertDateFormat(String inputDate) {
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date date = inputFormat.parse(inputDate);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return inputDate; // Retorna a data original em caso de erro
        }
    }

    public void registerPet(Pet pet) {
        view.showLoading();

        // Converte a data para o formato correto antes de salvar
        String formattedDate = convertDateFormat(pet.getAge());
        pet.setAge(formattedDate);

        storageService.getTutorByEmail(pet.getTutorEmail(), new StorageService.StorageCallback<Tutor>() {
            @Override
            public void onSuccess(Tutor tutor) {
                storageService.savePet(pet, new StorageService.StorageCallback<String>() {
                    @Override
                    public void onSuccess(String petId) {
                        view.hideLoading();
                        view.onPetRegistrationSuccess();
                    }

                    @Override
                    public void onError(String message) {
                        view.hideLoading();
                        view.showError("Erro", "Falha ao salvar pet: " + message);
                    }
                });
            }

            @Override
            public void onError(String message) {
                view.hideLoading();
                view.showError("Erro", "Tutor não encontrado: " + message);
            }
        });
    }
}