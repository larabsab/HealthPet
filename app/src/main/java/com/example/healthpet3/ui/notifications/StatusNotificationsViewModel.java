package com.example.healthpet3.ui.notifications;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.models.Status;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.repositories.StatusRepository;
import com.google.firebase.auth.FirebaseAuth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class StatusNotificationsViewModel extends ViewModel {
    private final MutableLiveData<List<StatusNotificationItem>> notifications = new MutableLiveData<>(new ArrayList<>());
    private final PetRepository petRepository = new PetRepository();
    private final StatusRepository statusRepository = new StatusRepository();

    public LiveData<List<StatusNotificationItem>> getNotifications() {
        return notifications;
    }

    public void loadNotifications() {
        String email = FirebaseAuth.getInstance().getCurrentUser() != null ? FirebaseAuth.getInstance().getCurrentUser().getEmail() : null;
        if (email == null) return;
        petRepository.getPetsByTutor(email, pets -> {
            List<StatusNotificationItem> allNotifications = new ArrayList<>();
            if (pets == null || pets.isEmpty()) {
                notifications.postValue(allNotifications);
                return;
            }
            final int[] counter = {0};
            for (Pet pet : pets) {
                statusRepository.getAllStatusForPet(pet.getId(), statusList -> {
                    if (statusList != null) {
                        for (Status status : statusList) {
                            allNotifications.add(new StatusNotificationItem(pet.getId(), pet.getName(), status));
                        }
                    }
                    counter[0]++;
                    if (counter[0] == pets.size()) {
                        Collections.sort(allNotifications, (a, b) -> b.status.getTimestamp().compareTo(a.status.getTimestamp()));
                        notifications.postValue(allNotifications);
                    }
                });
            }
        });
    }

    public static class StatusNotificationItem {
        public final String petId;
        public final String petName;
        public final Status status;
        public StatusNotificationItem(String petId, String petName, Status status) {
            this.petId = petId;
            this.petName = petName;
            this.status = status;
        }
    }
}