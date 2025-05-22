package com.example.healthpet3.ui.pet;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.healthpet3.models.Pet;
import com.example.healthpet3.models.Status;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.repositories.StatusRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PetProfileViewModel extends ViewModel {
    private final PetRepository petRepository;
    private final StatusRepository statusRepository;
    private final MutableLiveData<Pet> petData = new MutableLiveData<>();
    private final MutableLiveData<List<Status>> statusList = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<String> tutorImage = new MutableLiveData<>();

    public PetProfileViewModel() {
        this.petRepository = new PetRepository();
        this.statusRepository = new StatusRepository();
    }

    public LiveData<Pet> getPetData() {
        return petData;
    }

    public LiveData<List<Status>> getStatusList() {
        return statusList;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<String> getTutorImage() {
        return tutorImage;
    }

    public void loadPetData(String petId) {
        if (petId == null || petId.isEmpty()) {
            errorMessage.setValue("ID do pet não foi fornecido.");
            return;
        }

        // Load pet data from repository
        petRepository.getPetById(petId, pet -> {
            if (pet != null) {
                petData.setValue(pet);
            } else {
                errorMessage.setValue("Pet não encontrado no banco de dados.");
            }
        });

        // Load status data
        statusRepository.getAllStatusForPet(petId, updates -> {
            statusList.setValue(updates);
        });

        // Load tutor image
        String currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        petRepository.getTutorImage(currentUserId, image -> {
            tutorImage.setValue(image);
        });
    }

    public String calculateAge(String birthDateString) {
        try {
            if (birthDateString != null && !birthDateString.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date birthDate = sdf.parse(birthDateString);
                Calendar birth = Calendar.getInstance();
                birth.setTime(birthDate);

                Calendar today = Calendar.getInstance();

                int years = today.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                int months = today.get(Calendar.MONTH) - birth.get(Calendar.MONTH);
                int days = today.get(Calendar.DAY_OF_MONTH) - birth.get(Calendar.DAY_OF_MONTH);

                if (days < 0) {
                    months--;
                    Calendar temp = (Calendar) today.clone();
                    temp.add(Calendar.MONTH, -1);
                    days += temp.getActualMaximum(Calendar.DAY_OF_MONTH);
                }

                if (months < 0) {
                    years--;
                    months += 12;
                }

                return years + "y " + months + "m " + days + "d";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "N/A";
    }
}