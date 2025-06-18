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

    public interface ImageCallback {
        void onCallback(String imageBase64);
    }

    public interface UpdateCallback {
        void onSuccess();
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


    public void getTutorImage(String tutorId, ImageCallback callback) {
        db.collection(COLLECTION_TUTORS)
                .document(tutorId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String base64Image = documentSnapshot.getString("imageUrl");
                    callback.onCallback(base64Image != null ? base64Image : "");
                })
                .addOnFailureListener(e -> callback.onCallback(""));
    }

    public void updateTutor(String uid, Tutor tutor, UpdateCallback callback) {
        db.collection(COLLECTION_TUTORS).document(uid)
                .set(tutor)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }

    public void updateTutorImageUrl(String uid, String imageUrl, UpdateCallback callback) {
        db.collection(COLLECTION_TUTORS).document(uid)
                .update("imageUrl", imageUrl)
                .addOnSuccessListener(aVoid -> callback.onSuccess())
                .addOnFailureListener(callback::onFailure);
    }
}















