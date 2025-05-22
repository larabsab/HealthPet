/*
    Repositório responsável por operações com atualizações de status dos pets.
    Inclui busca das últimas atualizações, todas as atualizações e criação de novas.
*/

package com.example.healthpet3.repositories;

import com.example.healthpet3.models.Status;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

public class StatusRepository {

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface StatusCallback {
        void onResult(List<Status> updates);
    }

    // Retorna as últimas N atualizações de status de um pet.
    public void getLastStatusUpdates(String petId, int limit, StatusCallback callback) {
        db.collection("pets")
                .document(petId)
                .collection("status")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(limit)
                .get()
                .addOnSuccessListener(docs -> callback.onResult(mapToStatusList(docs)))
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }

    // Adiciona uma nova atualização de status para um pet.
    public void addStatusUpdate(String petId, Status update) {
        db.collection("pets")
                .document(petId)
                .collection("status")
                .add(update);
    }

    // Retorna todas as atualizações de status de um pet.
    public void getAllStatusForPet(String petId, StatusCallback callback) {
        db.collection("pets")
                .document(petId)
                .collection("status")
                .get()
                .addOnSuccessListener(docs -> callback.onResult(mapToStatusList(docs)))
                .addOnFailureListener(e -> callback.onResult(new ArrayList<>()));
    }


    // Converte documentos do Firestore em uma lista de objetos Status.
    private List<Status> mapToStatusList(Iterable<? extends DocumentSnapshot> docs) {
        List<Status> statusList = new ArrayList<>();
        for (DocumentSnapshot doc : docs) {
            Status status = doc.toObject(Status.class);
            if (status != null) {
                statusList.add(status);
            }
        }
        return statusList;
    }

}