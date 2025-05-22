package com.example.healthpet3.repositories;

import com.example.healthpet3.models.Tutor;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

public class TutorRepository {

    private static final String COLLECTION_TUTORS = "tutors";
    private final FirebaseFirestore db;

    public TutorRepository() {
        this.db = FirebaseFirestore.getInstance();
    }

    public interface TutorCallback {
        void onSuccess(Tutor tutor);
        void onFailure(Exception e);
    }

    public void getTutorById(String uid, TutorCallback callback) {
        db.collection(COLLECTION_TUTORS).document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Tutor tutor = documentSnapshot.toObject(Tutor.class);
                        callback.onSuccess(tutor);
                    } else {
                        callback.onSuccess(null);
                    }
                })
                .addOnFailureListener(callback::onFailure);
    }
}
