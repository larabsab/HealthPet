package com.example.healthpet3.data;

import com.example.healthpet3.models.Tutor;
import com.example.healthpet3.models.Pet;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FirebaseStorageService implements StorageService {
    private final FirebaseFirestore db;
    private static final String COLLECTION_TUTORS = "tutors";
    private static final String COLLECTION_PETS = "pets";
    private static final String COLLECTION_CLINICS = "clinics";

    public FirebaseStorageService() {
        this.db = FirebaseFirestore.getInstance();
    }

    @Override
    public void saveTutor(String userId, Tutor tutor, StorageCallback<Void> callback) {
        Map<String, Object> tutorMap = new HashMap<>();
        tutorMap.put("name", tutor.getName());
        tutorMap.put("email", tutor.getEmail());
        tutorMap.put("phone", tutor.getPhone());
        tutorMap.put("clinic", tutor.getClinic());

        // Adiciona a imagem em base64 se existir
        if (tutor.getImageBase64() != null && !tutor.getImageBase64().isEmpty()) {
            tutorMap.put("imageUrl", tutor.getImageBase64());
        }

        db.collection(COLLECTION_TUTORS).document(userId)
                .set(tutorMap)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void savePet(Pet pet, StorageCallback<String> callback) {
        // Primeiro, verifica se o tutor existe
        db.collection(COLLECTION_TUTORS)
                .whereEqualTo("email", pet.getTutorEmail())
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (queryDocumentSnapshots.isEmpty()) {
                        callback.onError("Tutor não encontrado");
                        return;
                    }

                    // Gera um ID único para o pet
                    String petId = UUID.randomUUID().toString();

                    Map<String, Object> petMap = new HashMap<>();
                    petMap.put("id", petId);
                    petMap.put("name", pet.getName());
                    petMap.put("breed", pet.getBreed());
                    petMap.put("age", pet.getAge());
                    petMap.put("species", pet.getSpecies());
                    petMap.put("gender", pet.getGender());
                    petMap.put("observations", pet.getObservations());
                    petMap.put("tutorEmail", pet.getTutorEmail());
                    petMap.put("weight", pet.getWeight());
                    petMap.put("height", pet.getHeight());
                    petMap.put("color", pet.getColor());

                    // Adiciona a imagem em base64 se existir
                    if (pet.getImageBase64() != null && !pet.getImageBase64().isEmpty()) {
                        petMap.put("imageUrl", pet.getImageBase64());
                    }

                    // Pega a clínica do tutor
                    String tutorClinic = queryDocumentSnapshots.getDocuments().get(0).getString("clinic");
                    petMap.put("clinic", tutorClinic);

                    // Salva o pet com o ID gerado
                    db.collection(COLLECTION_PETS)
                            .document(petId)
                            .set(petMap)
                            .addOnSuccessListener(aVoid -> callback.onSuccess(petId))
                            .addOnFailureListener(e -> callback.onError(e.getMessage()));
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getTutorByEmail(String email, StorageCallback<Tutor> callback) {
        db.collection(COLLECTION_TUTORS)
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Tutor tutor = queryDocumentSnapshots.getDocuments().get(0).toObject(Tutor.class);
                        callback.onSuccess(tutor);
                    } else {
                        callback.onError("Tutor não encontrado");
                    }
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }

    @Override
    public void getClinicsList(StorageCallback<List<String>> callback) {
        List<String> clinicsList = new ArrayList<>();
        clinicsList.add("Select Clinic");

        db.collection(COLLECTION_CLINICS)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String nome = document.getString("nome");
                        if (nome != null && !clinicsList.contains(nome)) {
                            clinicsList.add(nome);
                        }
                    }
                    callback.onSuccess(clinicsList);
                })
                .addOnFailureListener(e -> callback.onError(e.getMessage()));
    }
}