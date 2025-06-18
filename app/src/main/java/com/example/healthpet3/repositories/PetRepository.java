/*
    Repositório responsável por operações com a coleção "pets".
    Responsável por recuperar pets do tutor logado.
*/

package com.example.healthpet3.repositories;

import com.example.healthpet3.models.Pet;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import com.google.firebase.auth.FirebaseAuth;
import android.content.Context;
import com.example.healthpet3.data.Callback;

public class PetRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface PetCallback {
        void onCallback(List<Pet> pets);
    }

    public interface SinglePetCallback {
        void onCallback(Pet pet);
    }

    public interface UpdatePetsEmailCallback {
        void onSuccess();
        void onFailure(Exception e);
    }

    public interface ImageCallback {
        void onCallback(String base64Image);
    }

    public void getPetsByTutor(String email, PetCallback callback) {
        db.collection("pets")
                .whereEqualTo("tutorEmail", email)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Pet> pets = new ArrayList<>();
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        Pet pet = doc.toObject(Pet.class);
                        if (pet != null) {
                            pet.setId(doc.getId());
                            pets.add(pet);
                        }
                    }
                    callback.onCallback(pets);
                })
                .addOnFailureListener(e -> callback.onCallback(new ArrayList<>()));
    }

    public void buscarPetDoTutor(Context context, Callback<String> onSuccess, Callback<String> onFailure) {
        String tutorEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        FirebaseFirestore.getInstance().collection("pets")
                .whereEqualTo("tutorEmail", tutorEmail)
                .get()
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        String petId = query.getDocuments().get(0).getId();
                        onSuccess.call(petId);
                    } else {
                        onFailure.call("Nenhum pet encontrado");
                    }
                })
                .addOnFailureListener(e -> onFailure.call(e.getMessage()));
    }

    public void getPetById(String petId, SinglePetCallback callback) {
        db.collection("pets")
                .document(petId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    Pet pet = documentSnapshot.toObject(Pet.class);
                    if (pet != null) {
                        pet.setId(documentSnapshot.getId());
                    }
                    callback.onCallback(pet);
                })
                .addOnFailureListener(e -> callback.onCallback(null));
    }

    public void getTutorImage(String tutorId, ImageCallback callback) {
        db.collection("tutors")
                .document(tutorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String base64Image = documentSnapshot.getString("imageUrl");
                    callback.onCallback(base64Image != null ? base64Image : "");
                })
                .addOnFailureListener(e -> callback.onCallback(""));
    }

    public void updatePetsTutorEmail(String oldEmail, String newEmail, UpdatePetsEmailCallback callback) {
        db.collection("pets")
                .whereEqualTo("tutorEmail", oldEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().update("tutorEmail", newEmail);
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updatePetsClinicByTutorEmail(String tutorEmail, String newClinic, UpdatePetsEmailCallback callback) {
        db.collection("pets")
                .whereEqualTo("tutorEmail", tutorEmail)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        doc.getReference().update("clinic", newClinic);
                    }
                    callback.onSuccess();
                })
                .addOnFailureListener(callback::onFailure);
    }

    public void updatePet(Pet pet, UpdatePetsEmailCallback callback) {
        db.collection("pets")
                .document(pet.getId())
                .set(pet)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}
