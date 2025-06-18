/*
    Tela responsável por exibir o perfil do tutor logado,
    carregando seus dados do Firebase Firestore e exibindo opções
    para editar as informações do tutor ou dos pets vinculados.
    Esta Activity comunica-se com o TutorRepository para obter os dados.
    !!!!! AINDA EM DESENVOLVIMENTO
 */


package com.example.healthpet3.ui.tutor;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.healthpet3.R;
import com.example.healthpet3.models.Tutor;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.models.Status;
import com.example.healthpet3.repositories.TutorRepository;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.repositories.StatusRepository;
import com.example.healthpet3.ui.pet.EditPetActivity;
import com.example.healthpet3.ui.homepet.HomePetActivity;
import com.example.healthpet3.ui.pet.PetProfileActivity;
import com.example.healthpet3.utils.ImageUtils;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import androidx.lifecycle.ViewModelProvider;
import com.example.healthpet3.ui.pet.PetProfileViewModel;
import com.example.healthpet3.ui.notifications.StatusNotificationsActivity;
import com.example.healthpet3.ui.common.EditOptionsMenuFragment;
import com.example.healthpet3.ui.chatbot.ChatbotActivity;
import com.example.healthpet3.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.util.Log;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.Locale;

public class TutorProfileActivity extends AppCompatActivity implements EditOptionsMenuFragment.EditOptionsListener {

    private ImageView menuIcon;
    private de.hdodenhof.circleimageview.CircleImageView avatarImage;
    private TextView txtTutorName, txtEmail, seeAllPets ;
    private FirebaseAuth mAuth;
    private TutorRepository tutorRepository;
    private PetRepository petRepository;
    private StatusRepository statusRepository;
    private de.hdodenhof.circleimageview.CircleImageView petImageGreen, petImagePurple;
    private ImageView pillMenuGreen, pillMenuPurple;
    private List<Pet> petsList = new ArrayList<>();
    private Map<String, Long> petLastUpdateMap = new HashMap<>();
    private PetProfileViewModel petProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_profile);

        mAuth = FirebaseAuth.getInstance();
        tutorRepository = new TutorRepository();
        petRepository = new PetRepository();
        statusRepository = new StatusRepository();
        petProfileViewModel = new ViewModelProvider(this).get(PetProfileViewModel.class);

        menuIcon = findViewById(R.id.menuIcon);
        avatarImage = findViewById(R.id.avatarImage);
        txtTutorName = findViewById(R.id.txtTutorName);
        txtEmail = findViewById(R.id.txtEmail);
        seeAllPets = findViewById(R.id.seeAllPets);
        // Pills com ids únicos
        petImageGreen = findViewById(R.id.petImageGreen);
        pillMenuGreen = findViewById(R.id.pillMenuGreen);
        petImagePurple = findViewById(R.id.petImagePurple);
        pillMenuPurple = findViewById(R.id.pillMenuPurple);

        TextView seeAllUpdates = findViewById(R.id.seeAllUpdates);
        if (seeAllUpdates != null) {
            seeAllUpdates.setOnClickListener(v -> {
                Intent intent = new Intent(TutorProfileActivity.this, StatusNotificationsActivity.class);
                startActivity(intent);
            });
        }

        menuIcon.setOnClickListener(v -> showEditOptions());
        seeAllPets.setOnClickListener(v -> {
            Intent intent = new Intent(TutorProfileActivity.this, HomePetActivity.class);
            startActivity(intent);
        });
        loadTutorData();
        loadRecentPets();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadRecentPets();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK) {
            loadTutorData();
        }
    }

    private void loadTutorData() {
        if (mAuth.getCurrentUser() == null) {
            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        String uid = mAuth.getCurrentUser().getUid();
        String email = mAuth.getCurrentUser().getEmail();

        // Show loading state
        txtTutorName.setText("Carregando...");
        txtEmail.setText("Carregando...");
        avatarImage.setImageResource(R.drawable.tutoricon2);

        // Load tutor data
        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
            @Override
            public void onSuccess(Tutor tutor) {
                if (tutor != null) {
                    updateTutorUI(tutor);
                } else {
                    handleTutorNotFound();
                }
            }

            @Override
            public void onFailure(Exception e) {
                handleTutorLoadError(e);
            }
        });

        // Load tutor image directly
        tutorRepository.getTutorImage(uid, imageBase64 -> {
            if (imageBase64 != null && !imageBase64.isEmpty()) {
                ImageUtils.setBase64Image(avatarImage, imageBase64, true);
            }
        });
    }

    private void loadRecentPets() {
        if (mAuth.getCurrentUser() == null) return;
        String email = mAuth.getCurrentUser().getEmail();
        petRepository.getPetsByTutor(email, pets -> {
            petsList = pets;
            if (petsList.isEmpty()) {
                setDefaultPills();
                return;
            }
            petLastUpdateMap.clear();
            final int[] count = {0};
            for (Pet pet : petsList) {
                // Para cada pet, buscar o último status
                statusRepository.getLastStatusUpdates(pet.getId(), 1, statusList -> {
                    long lastStatus = 0L;
                    if (statusList != null && !statusList.isEmpty() && statusList.get(0).getTimestamp() != null) {
                        lastStatus = statusList.get(0).getTimestamp().toDate().getTime();
                    }
                    long createdAt = 0L;
                    if (pet.getCreatedAt() != null) {
                        createdAt = pet.getCreatedAt().toDate().getTime();
                    }
                    long updatedAt = 0L;
                    if (pet.getUpdatedAt() != null) {
                        updatedAt = pet.getUpdatedAt().toDate().getTime();
                    }
                    // O timestamp mais recente entre cadastro, edição e status
                    long lastActivity = Math.max(Math.max(createdAt, updatedAt), lastStatus);
                    petLastUpdateMap.put(pet.getId(), lastActivity);
                    count[0]++;
                    if (count[0] == petsList.size()) {
                        runOnUiThread(this::updatePillsUI);
                    }
                });
            }
        });
    }


    private void updatePillsUI() {
        // Ordena pets por data de atualização (desc)
        List<Pet> sortedPets = new ArrayList<>(petsList);
        Collections.sort(sortedPets, (p1, p2) -> {
            long t1 = petLastUpdateMap.getOrDefault(p1.getId(), 0L);
            long t2 = petLastUpdateMap.getOrDefault(p2.getId(), 0L);
            return Long.compare(t2, t1);
        });
        // Pill verde: mais recente
        if (!sortedPets.isEmpty()) {
            setPill(sortedPets.get(0), petImageGreen, pillMenuGreen);
        } else {
            setDefaultPill(petImageGreen, pillMenuGreen);
        }
        // Pill roxo: segundo mais recente
        if (sortedPets.size() > 1) {
            setPill(sortedPets.get(1), petImagePurple, pillMenuPurple);
        } else {
            setDefaultPill(petImagePurple, pillMenuPurple);
        }
        // Atualizar os mini cards traçados
        updatePetCardsUI(sortedPets);
        // Atualizar o card traçado único do pet mais recente
        updateDashedCard(sortedPets);
    }

    private void updatePetCardsUI(List<Pet> sortedPets) {
        // Card 1
        LinearLayout cardsRow = findViewById(R.id.cardsRow);
        View card1 = cardsRow.getChildAt(0);
        TextView name1 = card1.findViewById(R.id.textPetName);
        TextView breed1 = card1.findViewById(R.id.textPetBreed);
        TextView age1 = card1.findViewById(R.id.textPetAge);

        // Card 2
        View card2 = cardsRow.getChildAt(1);
        TextView name2 = card2.findViewById(R.id.textPetName);
        TextView breed2 = card2.findViewById(R.id.textPetBreed);
        TextView age2 = card2.findViewById(R.id.textPetAge);

        // Preencher com os dois pets mais recentes
        if (!sortedPets.isEmpty()) {
            Pet pet1 = sortedPets.get(0);
            name1.setText(pet1.getName());
            breed1.setText(pet1.getBreed());
            age1.setText(petProfileViewModel.calculateAge(pet1.getAge()));
        } else {
            name1.setText("");
            breed1.setText("");
            age1.setText("");
        }
        if (sortedPets.size() > 1) {
            Pet pet2 = sortedPets.get(1);
            name2.setText(pet2.getName());
            breed2.setText(pet2.getBreed());
            age2.setText(petProfileViewModel.calculateAge(pet2.getAge()));
        } else {
            name2.setText("");
            breed2.setText("");
            age2.setText("");
        }
    }
    private void updateDashedCard(List<Pet> sortedPets) {
        View dashedCard = findViewById(R.id.tutorPetDashedCard);
        if (dashedCard == null) return;
        if (sortedPets == null || sortedPets.isEmpty()) {
            dashedCard.setVisibility(View.GONE);
            return;
        }
        Pet pet = sortedPets.get(0);
        dashedCard.setVisibility(View.VISIBLE);
        // Preencher imagem e textos
        de.hdodenhof.circleimageview.CircleImageView imagePet = dashedCard.findViewById(R.id.imagePet);
        TextView textPetName = dashedCard.findViewById(R.id.textPetName);
        TextView textVetName = dashedCard.findViewById(R.id.textVetName);
        TextView status1 = dashedCard.findViewById(R.id.status1);
        TextView status2 = dashedCard.findViewById(R.id.status2);
        TextView status3 = dashedCard.findViewById(R.id.status3);
        com.example.healthpet3.utils.ImageUtils.setBase64Image(imagePet, pet.getImageUrl());
        textPetName.setText(pet.getName());
        // Limpa status
        status1.setVisibility(View.GONE);
        status2.setVisibility(View.GONE);
        status3.setVisibility(View.GONE);
        // Buscar as 3 últimas atualizações de status do pet
        statusRepository.getLastStatusUpdates(pet.getId(), 3, updates -> {
            if (!updates.isEmpty()) {
                textVetName.setText(updates.get(0).getVetName());
            } else {
                textVetName.setText(R.string.no_updates);
            }
            for (int i = 0; i < updates.size(); i++) {
                com.example.healthpet3.models.Status update = updates.get(i);
                String relativeTime = com.example.healthpet3.utils.TimeUtils.getRelativeTime(update.getTimestamp());
                String text = update.getType().toLowerCase() + "\n" + relativeTime;
                switch (i) {
                    case 0:
                        status1.setText(text);
                        status1.setVisibility(View.VISIBLE);
                        status1.setBackgroundResource(R.drawable.status_black_background);
                        status1.setTextColor(android.graphics.Color.WHITE);
                        break;
                    case 1:
                        status2.setText(text);
                        status2.setVisibility(View.VISIBLE);
                        status2.setBackgroundResource(R.drawable.status_border_background);
                        status2.setTextColor(android.graphics.Color.BLACK);
                        break;
                    case 2:
                        status3.setText(text);
                        status3.setVisibility(View.VISIBLE);
                        status3.setBackgroundResource(R.drawable.status_border_background);
                        status3.setTextColor(android.graphics.Color.BLACK);
                        break;
                }
            }
        });
        // Clique no card abre o perfil do pet
        dashedCard.setOnClickListener(v -> {
            Intent intent = new Intent(TutorProfileActivity.this, PetProfileActivity.class);
            intent.putExtra("petId", pet.getId());
            startActivity(intent);
        });
    }

    private void setPill(Pet pet, de.hdodenhof.circleimageview.CircleImageView imageView, ImageView menuView) {
        if (pet.getImageUrl() != null && !pet.getImageUrl().isEmpty()) {
            ImageUtils.setBase64Image(imageView, pet.getImageUrl(), false);
        } else {
            imageView.setImageResource(R.drawable.peticon4);
        }
        Log.d("PILL_DEBUG", "Setando pill para pet: " + pet.getName() + ", id: " + pet.getId());
        menuView.setOnClickListener(v -> showPetOptionsPopup(pet));
    }

    private void setDefaultPills() {
        setDefaultPill(petImageGreen, pillMenuGreen);
        setDefaultPill(petImagePurple, pillMenuPurple);
    }

    private void setDefaultPill(de.hdodenhof.circleimageview.CircleImageView imageView, ImageView menuView) {
        imageView.setImageResource(R.drawable.peticon4);
        menuView.setOnClickListener(null);
    }

    private void showPetOptionsPopup(Pet pet) {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_pet_options, null);
        view.findViewById(R.id.btnPetPage).setOnClickListener(v -> {
            dialog.dismiss();
            if (pet.getId() == null || pet.getId().isEmpty()) {
                Toast.makeText(this, "ID do pet não foi fornecido.", Toast.LENGTH_SHORT).show();
                Log.e("PILL_DEBUG", "Tentou abrir página do pet sem id! Pet: " + pet.getName());
                return;
            }
            Intent intent = new Intent(this, PetProfileActivity.class);
            intent.putExtra("petId", pet.getId());
            startActivity(intent);
        });
        view.findViewById(R.id.btnEditPet).setOnClickListener(v -> {
            dialog.dismiss();
            if (pet.getId() == null || pet.getId().isEmpty()) {
                Toast.makeText(this, "ID do pet não foi fornecido.", Toast.LENGTH_SHORT).show();
                Log.e("PILL_DEBUG", "Tentou editar pet sem id! Pet: " + pet.getName());
                return;
            }
            Intent intent = new Intent(this, EditPetActivity.class);
            intent.putExtra("petId", pet.getId());
            startActivity(intent);
        });
        dialog.setContentView(view);
        dialog.show();
    }



    private void updateTutorUI(Tutor tutor) {
        if (tutor == null) {
            handleTutorNotFound();
            return;
        }

        // Update name with validation
        String tutorName = tutor.getName();
        if (tutorName != null && !tutorName.trim().isEmpty()) {
            txtTutorName.setText(tutorName);
        } else {
            txtTutorName.setText("Nome não disponível");
        }

        // Update email with validation
        String tutorEmail = tutor.getEmail();
        if (tutorEmail != null && !tutorEmail.trim().isEmpty()) {
            txtEmail.setText(tutorEmail);
        } else {
            txtEmail.setText("Email não disponível");
        }
    }

    private void handleTutorNotFound() {
        Toast.makeText(TutorProfileActivity.this, "Dados do tutor não encontrados.", Toast.LENGTH_SHORT).show();
        txtTutorName.setText("Nome não encontrado");
        txtEmail.setText("Email não encontrado");
        setDefaultTutorImage();
    }

    private void handleTutorLoadError(Exception e) {
        Toast.makeText(TutorProfileActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
        txtTutorName.setText("Erro ao carregar");
        txtEmail.setText("Erro ao carregar");
        setDefaultTutorImage();
    }

    private void setDefaultTutorImage() {
        avatarImage.setImageResource(R.drawable.tutoricon2);
        avatarImage.setBorderWidth(2);
        avatarImage.setBorderColor(getResources().getColor(R.color.border_color, null));
    }

    private void showEditOptions() {
        EditOptionsMenuFragment.newInstance().show(getSupportFragmentManager(), "edit_options");
    }

    @Override
    public void onEditTutor() {
        startActivity(new Intent(this, EditTutorActivity.class));
    }
    @Override
    public void onEditPet() {
        startActivity(new Intent(this, EditPetActivity.class));
    }
    @Override
    public void onChatbot() {
        startActivity(new Intent(this, ChatbotActivity.class));
    }
    @Override
    public void onLogout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

//public class TutorProfileActivity extends AppCompatActivity {
//
//    private ImageView menuIcon;
//    private de.hdodenhof.circleimageview.CircleImageView avatarImage;
//    private TextView txtTutorName, txtEmail;
//    private FirebaseAuth mAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tutor_profile);
//
//        mAuth = FirebaseAuth.getInstance();
//
//        menuIcon = findViewById(R.id.menuIcon);
//        avatarImage = findViewById(R.id.avatarImage);
//        txtTutorName = findViewById(R.id.txtTutorName);
//        txtEmail = findViewById(R.id.txtEmail);
//
//        menuIcon.setOnClickListener(v -> showEditOptions());
//        loadTutorData();
//    }
//
//    private void loadTutorData() {
//        if (mAuth.getCurrentUser() == null) {
//            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
//            finish();
//            return;
//        }
//
//        String uid = mAuth.getCurrentUser().getUid();
//        String email = mAuth.getCurrentUser().getEmail();
//
//        // Show loading state
//        txtTutorName.setText("Carregando...");
//        txtEmail.setText("Carregando...");
//        avatarImage.setImageResource(R.drawable.tutoricon2);
//
//        TutorRepository tutorRepository = new TutorRepository();
//        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
//            @Override
//            public void onSuccess(Tutor tutor) {
//                if (tutor != null) {
//                    updateTutorUI(tutor);
//                } else {
//                    handleTutorNotFound();
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                handleTutorLoadError(e);
//            }
//        });
//    }
//
//    private void loadTutorImage(String imageBase64) {
//        // Set default image first
//        setDefaultTutorImage();
//
//        // Try to load tutor's image if available
//        if (imageBase64 != null && !imageBase64.isEmpty()) {
//            try {
//                loadTutorImageFromBase64(imageBase64);
//            } catch (Exception e) {
//                handleImageLoadError();
//            }
//        }
//    }
//
//    private void setDefaultTutorImage() {
//        avatarImage.setImageResource(R.drawable.tutoricon2);
//        avatarImage.setBorderWidth(2);
//        avatarImage.setBorderColor(getResources().getColor(R.color.border_color, null));
//    }
//
//    private void loadTutorImageFromBase64(String base64Image) {
//        ImageUtils.setBase64Image(avatarImage, base64Image, true);
//        avatarImage.setBorderWidth(2);
//        avatarImage.setBorderColor(getResources().getColor(R.color.border_color, null));
//    }
//
//    private void handleImageLoadError() {
//        setDefaultTutorImage();
//        Toast.makeText(this, "Erro ao carregar imagem do tutor", Toast.LENGTH_SHORT).show();
//    }
//
//    private void updateTutorUI(Tutor tutor) {
//        if (tutor == null) {
//            handleTutorNotFound();
//            return;
//        }
//
//        // Update name with validation
//        String tutorName = tutor.getName();
//        if (tutorName != null && !tutorName.trim().isEmpty()) {
//            txtTutorName.setText(tutorName);
//        } else {
//            txtTutorName.setText("Nome não disponível");
//        }
//
//        // Update email with validation
//        String tutorEmail = tutor.getEmail();
//        if (tutorEmail != null && !tutorEmail.trim().isEmpty()) {
//            txtEmail.setText(tutorEmail);
//        } else {
//            txtEmail.setText("Email não disponível");
//        }
//
//        // Load tutor image
//        loadTutorImage(tutor.getImageBase64());
//    }
//
//    private void handleTutorNotFound() {
//        Toast.makeText(TutorProfileActivity.this, "Dados do tutor não encontrados.", Toast.LENGTH_SHORT).show();
//        txtTutorName.setText("Nome não encontrado");
//        txtEmail.setText("Email não encontrado");
//        setDefaultTutorImage();
//    }
//
//    private void handleTutorLoadError(Exception e) {
//        Toast.makeText(TutorProfileActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
//        txtTutorName.setText("Erro ao carregar");
//        txtEmail.setText("Erro ao carregar");
//        setDefaultTutorImage();
//    }
//
//    private void showEditOptions() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = LayoutInflater.from(this).inflate(
//                R.layout.dialog_edit_options,
//                findViewById(R.id.bottomSheetContainer)
//        );
//
//        Button btnEditTutor = bottomSheetView.findViewById(R.id.btnEditTutor);
//        Button btnEditPets = bottomSheetView.findViewById(R.id.btnEditPets);
//
//        btnEditTutor.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditTutorActivity.class));
//        });
//
//        btnEditPets.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditPetActivity.class));
//        });
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//    }
//}


//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.example.healthpet3.R;
//import com.example.healthpet3.models.Tutor;
//import com.example.healthpet3.models.Pet;
//import com.example.healthpet3.models.Status;
//import com.example.healthpet3.repositories.TutorRepository;
//import com.example.healthpet3.repositories.PetRepository;
//import com.example.healthpet3.repositories.StatusRepository;
//import com.example.healthpet3.ui.pet.EditPetActivity;
//import com.example.healthpet3.ui.homepet.HomePetActivity;
//import com.example.healthpet3.utils.ImageUtils;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.firebase.auth.FirebaseAuth;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//public class TutorProfileActivity extends AppCompatActivity {
//
//    private ImageView menuIcon;
//    private de.hdodenhof.circleimageview.CircleImageView avatarImage;
//    private TextView txtTutorName, txtEmail;
//    private RecyclerView recyclerRecentPets;
//    private TextView seeAllPets, seeAllUpdates;
//    private PetRepository petRepository;
//    private StatusRepository statusRepository;
//    private FirebaseAuth mAuth;
//    private List<Pet> allPets = new ArrayList<>();
//    private Map<String, Status> lastStatusMap = new HashMap<>();
//    private RecentPetsAdapter recentPetsAdapter;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tutor_profile);
//
//        mAuth = FirebaseAuth.getInstance();
//        petRepository = new PetRepository();
//        statusRepository = new StatusRepository();
//
//        menuIcon = findViewById(R.id.menuIcon);
//        avatarImage = findViewById(R.id.avatarImage);
//        txtTutorName = findViewById(R.id.txtTutorName);
//        txtEmail = findViewById(R.id.txtEmail);
//        recyclerRecentPets = findViewById(R.id.recyclerRecentPets);
//        seeAllPets = findViewById(R.id.seeAllPets);
//        seeAllUpdates = findViewById(R.id.seeAllUpdates);
//
//        menuIcon.setOnClickListener(v -> showEditOptions());
//        seeAllPets.setOnClickListener(v -> startActivity(new Intent(this, HomePetActivity.class)));
//        seeAllUpdates.setOnClickListener(v -> startActivity(new Intent(this, HomePetActivity.class)));
//
//        setupRecentPetsRecycler();
//        loadTutorData();
//    }
//
//    private void setupRecentPetsRecycler() {
//        recentPetsAdapter = new RecentPetsAdapter(new ArrayList<>());
//        recyclerRecentPets.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
//        recyclerRecentPets.setAdapter(recentPetsAdapter);
//    }
//
//    private void loadTutorData() {
//        if (mAuth.getCurrentUser() == null) {
//            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
//            // redirecionar para login se quiser
//            finish();
//            return;
//        }
//
//        String uid = mAuth.getCurrentUser().getUid();
//        String email = mAuth.getCurrentUser().getEmail();
//
//        TutorRepository tutorRepository = new TutorRepository();
//        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
//            @Override
//            public void onSuccess(Tutor tutor) {
//                if (tutor != null) {
//                    txtTutorName.setText(tutor.getName());
//                    txtEmail.setText(tutor.getEmail());
//                    if (tutor.getImageBase64() != null && !tutor.getImageBase64().isEmpty()) {
//                        ImageUtils.setBase64Image(avatarImage, tutor.getImageBase64(), true);
//                    } else {
//                        avatarImage.setImageResource(R.drawable.tutoricon2);
//                    }
//                } else {
//                    Toast.makeText(TutorProfileActivity.this, "Dados do tutor não encontrados.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(TutorProfileActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//
//        if (email != null) {
//            petRepository.getPetsByTutor(email, pets -> {
//                allPets = pets;
//                if (allPets.isEmpty()) {
//                    recentPetsAdapter.updateList(new ArrayList<>());
//                    updateLastUpdateCard(null, null);
//                    return;
//                }
//                fetchLastStatusForPets();
//            });
//        }
//    }
//
//    private void fetchLastStatusForPets() {
//        lastStatusMap.clear();
//        if (allPets.isEmpty()) return;
//        final int[] counter = {0};
//        for (Pet pet : allPets) {
//            statusRepository.getLastStatusUpdates(pet.getId(), 3, updates -> {
//                if (!updates.isEmpty()) {
//                    lastStatusMap.put(pet.getId(), updates.get(0));
//                } else {
//                    lastStatusMap.put(pet.getId(), null);
//                }
//                counter[0]++;
//                if (counter[0] == allPets.size()) {
//                    updatePetsDisplay();
//                }
//            });
//        }
//    }
//
//    private void updatePetsDisplay() {
//        // Ordena pets pelo timestamp do último status (desc)
//        List<Pet> sortedPets = new ArrayList<>(allPets);
//        Collections.sort(sortedPets, (p1, p2) -> {
//            Status s1 = lastStatusMap.get(p1.getId());
//            Status s2 = lastStatusMap.get(p2.getId());
//            if (s1 == null && s2 == null) return 0;
//            if (s1 == null) return 1;
//            if (s2 == null) return -1;
//            return s2.getTimestamp().compareTo(s1.getTimestamp());
//        });
//        // Atualiza lista horizontal com os 2 mais recentes
//        List<Pet> top2 = sortedPets.size() > 2 ? sortedPets.subList(0, 2) : sortedPets;
//        recentPetsAdapter.updateList(top2);
//        // Atualiza card do último update
//        if (!sortedPets.isEmpty()) {
//            Pet lastPet = sortedPets.get(0);
//            Status lastStatus = lastStatusMap.get(lastPet.getId());
//            updateLastUpdateCard(lastPet, lastStatus);
//        } else {
//            updateLastUpdateCard(null, null);
//        }
//    }
//
//    private void updateLastUpdateCard(Pet pet, Status lastStatus) {
//        de.hdodenhof.circleimageview.CircleImageView petImage = findViewById(R.id.lastUpdatePetImage);
//        TextView petName = findViewById(R.id.lastUpdatePetName);
//        TextView vetName = findViewById(R.id.lastUpdateVetName);
//        LinearLayout miniCards = findViewById(R.id.lastUpdateMiniCards);
//        if (pet == null) {
//            petImage.setImageResource(R.drawable.peticon4);
//            petName.setText("-");
//            vetName.setText("");
//            miniCards.removeAllViews();
//            return;
//        }
//        if (pet.getImageBase64() != null && !pet.getImageBase64().isEmpty()) {
//            ImageUtils.setBase64Image(petImage, pet.getImageBase64(), true);
//        } else {
//            petImage.setImageResource(R.drawable.peticon4);
//        }
//        petName.setText(pet.getName());
//        vetName.setText(lastStatus != null && lastStatus.getVetName() != null ? lastStatus.getVetName() : "");
//        // Mini cards
//        miniCards.removeAllViews();
//        statusRepository.getLastStatusUpdates(pet.getId(), 3, updates -> {
//            for (int i = 0; i < updates.size(); i++) {
//                Status status = updates.get(i);
//                View card = getLayoutInflater().inflate(R.layout.item_mini_status, miniCards, false);
//                TextView textStatus = card.findViewById(R.id.textStatusMini);
//                TextView textTempo = card.findViewById(R.id.textTempo);
//                textStatus.setText(status.getType());
//                textTempo.setText(com.example.healthpet3.utils.TimeUtils.getRelativeTime(status.getTimestamp()));
//                miniCards.addView(card);
//            }
//        });
//    }
//
//    private void showEditOptions() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = LayoutInflater.from(this).inflate(
//                R.layout.dialog_edit_options,
//                findViewById(R.id.bottomSheetContainer)
//        );
//
//        Button btnEditTutor = bottomSheetView.findViewById(R.id.btnEditTutor);
//        Button btnEditPets = bottomSheetView.findViewById(R.id.btnEditPets);
//
//        btnEditTutor.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditTutorActivity.class));
//        });
//
//        btnEditPets.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditPetActivity.class));
//        });
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//    }
//}
//
//class RecentPetsAdapter extends RecyclerView.Adapter<RecentPetsAdapter.PetViewHolder> {
//    private List<Pet> pets;
//    public RecentPetsAdapter(List<Pet> pets) { this.pets = pets; }
//    @Override
//    public PetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pet_card, parent, false);
//        return new PetViewHolder(v);
//    }
//    @Override
//    public void onBindViewHolder(PetViewHolder holder, int position) {
//        Pet pet = pets.get(position);
//        holder.tvPetName.setText(pet.getName());
//        if (pet.getImageBase64() != null && !pet.getImageBase64().isEmpty()) {
//            ImageUtils.setBase64Image(holder.ivPetImage, pet.getImageBase64(), true);
//        } else {
//            holder.ivPetImage.setImageResource(R.drawable.peticon4);
//        }
//        holder.tvVetName.setText(""); // Vet name não aparece aqui
//        holder.itemView.setOnClickListener(v -> {
//            // Abrir perfil do pet se quiser
//        });
//    }
//    @Override
//    public int getItemCount() { return pets.size(); }
//    public void updateList(List<Pet> pets) {
//        this.pets = pets;
//        notifyDataSetChanged();
//    }
//    static class PetViewHolder extends RecyclerView.ViewHolder {
//        de.hdodenhof.circleimageview.CircleImageView ivPetImage;
//        TextView tvPetName, tvVetName;
//        public PetViewHolder(View itemView) {
//            super(itemView);
//            ivPetImage = itemView.findViewById(R.id.imagePet);
//            tvPetName = itemView.findViewById(R.id.textPetName);
//            tvVetName = itemView.findViewById(R.id.textVetName);
//        }
//    }
//}














//import android.content.Intent;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.healthpet3.R;
//import com.example.healthpet3.models.Tutor;
//import com.example.healthpet3.repositories.TutorRepository;
//import com.example.healthpet3.ui.pet.EditPetActivity;
//import com.google.android.material.bottomsheet.BottomSheetDialog;
//import com.google.firebase.auth.FirebaseAuth;
//
//public class TutorProfileActivity extends AppCompatActivity {
//
//    private ImageView menuIcon;
//    private TextView txtTutorName, txtEmail, txtPhone, txtCPF;
//
//    private FirebaseAuth mAuth;
//    private TutorRepository tutorRepository;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_tutor_profile);
//
//        // Firebase Auth e repositório
//        mAuth = FirebaseAuth.getInstance();
//        tutorRepository = new TutorRepository();
//
//        // Views
////        menuIcon = findViewById(R.id.menuIcon);
////        txtTutorName = findViewById(R.id.txtTutorName);
////        txtEmail = findViewById(R.id.txtEmail);
////        txtPhone = findViewById(R.id.txtPhone);
////        txtCPF = findViewById(R.id.txtCPF);
//
//        menuIcon.setOnClickListener(v -> showEditOptions());
//
//        loadTutorData();
//    }
//
//    private void loadTutorData() {
//        if (mAuth.getCurrentUser() == null) {
//            Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show();
//            // redirecionar para login se quiser
//            finish();
//            return;
//        }
//
//        String uid = mAuth.getCurrentUser().getUid();
//
//        tutorRepository.getTutorById(uid, new TutorRepository.TutorCallback() {
//            @Override
//            public void onSuccess(Tutor tutor) {
//                if (tutor != null) {
////                    txtTutorName.setText(tutor.getFullName());
////                    txtEmail.setText(tutor.getEmail());
////                    txtPhone.setText(tutor.getPhone());
////                    txtCPF.setText(tutor.getCpf());
//                } else {
//                    Toast.makeText(TutorProfileActivity.this, "Dados do tutor não encontrados.", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Exception e) {
//                Toast.makeText(TutorProfileActivity.this, "Erro ao carregar dados: " + e.getMessage(), Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    private void showEditOptions() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
//        View bottomSheetView = LayoutInflater.from(this).inflate(
//                R.layout.dialog_edit_options,
//                findViewById(R.id.bottomSheetContainer)
//        );
//
//        Button btnEditTutor = bottomSheetView.findViewById(R.id.btnEditTutor);
//        Button btnEditPets = bottomSheetView.findViewById(R.id.btnEditPets);
//
//        btnEditTutor.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditTutorActivity.class));
//        });
//
//        btnEditPets.setOnClickListener(v -> {
//            bottomSheetDialog.dismiss();
//            startActivity(new Intent(TutorProfileActivity.this, EditPetActivity.class));
//        });
//
//        bottomSheetDialog.setContentView(bottomSheetView);
//        bottomSheetDialog.show();
//    }
//}