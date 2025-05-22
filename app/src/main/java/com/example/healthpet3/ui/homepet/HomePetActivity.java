/*
    Activity que exibe a lista de pets associados ao tutor logado,
    permitindo a navegação para o perfil individual de cada pet.
    Utiliza um RecyclerView com PetAdapter para mostrar os pets,
    e obtém os dados do PetRepository.
    Essa tela serve como home provisória para o tutor,
    sendo substituída futuramente pela HomeActivity principal.
*/

package com.example.healthpet3.ui.homepet;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthpet3.ui.pet.PetProfileActivity;
import com.example.healthpet3.R;
import com.example.healthpet3.adapters.PetAdapter;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.repositories.PetRepository;
import com.example.healthpet3.repositories.StatusRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HomePetActivity extends AppCompatActivity {

    private PetAdapter adapter;
    private final List<Pet> fullpetList = new ArrayList<>();
    private final PetRepository petRepository = new PetRepository();
    private final StatusRepository statusRepository = new StatusRepository();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_pet);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        EditText searchEditText = findViewById(R.id.searchEditText);
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterPets(s.toString().trim());
            }

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
        });

        searchEditText.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) ->
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)
        );

        RecyclerView recyclerView = findViewById(R.id.recyclerPets);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new PetAdapter(new ArrayList<>(), this, pet -> {
            Intent intent = new Intent(HomePetActivity.this, PetProfileActivity.class);
            intent.putExtra("petId", pet.getId());
            startActivity(intent);
        }, statusRepository);

        recyclerView.setAdapter(adapter);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String tutorEmail = mAuth.getCurrentUser().getEmail();
            if (tutorEmail != null) {
                loadPets(tutorEmail);
            }
        }
    }

    private void loadPets(String email) {
        petRepository.getPetsByTutor(email, pets -> {
            fullpetList.clear();
            fullpetList.addAll(pets);

            filterPets("");
        });
    }

    private void filterPets(String text) {
        List<Pet> result = new ArrayList<>();

        if (text.isEmpty()) {
            result.addAll(fullpetList);
        } else {
            String query = text.toLowerCase(Locale.ROOT);
            for (Pet pet : fullpetList) {
                if (pet.getName().toLowerCase(Locale.ROOT).startsWith(query)) {
                    result.add(pet);
                }
            }
        }

        if (result.isEmpty()) {
            Toast.makeText(this, "Pet não encontrado", Toast.LENGTH_SHORT).show();
        }

        adapter.updateList(result);
    }

}