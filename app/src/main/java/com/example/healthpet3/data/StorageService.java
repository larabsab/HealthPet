package com.example.healthpet3.data;

import com.example.healthpet3.models.Tutor;
import com.example.healthpet3.models.Pet;

public interface StorageService {
    interface StorageCallback<T> {
        void onSuccess(T result);
        void onError(String message);
    }

    void saveTutor(String userId, Tutor tutor, StorageCallback<Void> callback);
    void savePet(Pet pet, StorageCallback<String> callback);
    void getTutorByEmail(String email, StorageCallback<Tutor> callback);
    void getClinicsList(StorageCallback<java.util.List<String>> callback);
}