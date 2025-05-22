/*
    Adapter responsável por exibir a lista de pets no RecyclerView,
    incluindo nome, imagem e últimas atualizações de status.
    Também trata o clique em cada pet para abrir seu perfil.
*/

package com.example.healthpet3.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.healthpet3.R;
import com.example.healthpet3.models.Pet;
import com.example.healthpet3.models.Status;
import com.example.healthpet3.utils.ImageUtils;
import com.example.healthpet3.utils.TimeUtils;
import com.example.healthpet3.repositories.StatusRepository;

import java.util.List;

public class PetAdapter extends RecyclerView.Adapter<PetAdapter.PetViewHolder> {

    private final Context context;
    private final List<Pet> petList;
    private final OnPetClickListener listener;
    private final StatusRepository statusRepository;

    public interface OnPetClickListener {
        void onPetClick(Pet pet);
    }

    public PetAdapter(List<Pet> petList, Context context, OnPetClickListener listener, StatusRepository statusRepository) {
        this.context = context;
        this.petList = petList;
        this.listener = listener;
        this.statusRepository = statusRepository;
    }

    public void updateList(List<Pet> pets) {
        this.petList.clear();
        this.petList.addAll(pets);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pet_card, parent, false);
        return new PetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PetViewHolder holder, int position) {
        Pet pet = petList.get(position);

        // Nome e imagem do pet
        holder.tvPetName.setText(pet.getName());
        ImageUtils.setBase64Image(holder.ivPetImage, pet.getImageUrl());

        //Limpa status antes de carregar
        holder.clearStatusViews();

        // Buscar as 3 últimas atualizações de status do pet
        statusRepository.getLastStatusUpdates(pet.getId(), 3, updates -> {

            if (!updates.isEmpty()) {
                holder.tvVetName.setText(updates.get(0).getVetName());
            }
            else {
                holder.tvVetName.setText(R.string.no_updates);
            }

            // Mini cards
            for (int i = 0; i < updates.size(); i++) {
                Status update = updates.get(i);
                String relativeTime = TimeUtils.getRelativeTime(update.getTimestamp());
                String text = update.getType().toLowerCase() + "\n" + relativeTime;
                switch (i) {
                    case 0:
                        holder.tvStatus1.setText(text);
                        holder.tvStatus1.setVisibility(View.VISIBLE);
                        holder.tvStatus1.setBackgroundResource(R.drawable.status_black_background);
                        break;
                    case 1:
                        holder.tvStatus2.setText(text);
                        holder.tvStatus2.setVisibility(View.VISIBLE);
                        holder.tvStatus2.setBackgroundResource(R.drawable.status_border_background);
                        break;
                    case 2:
                        holder.tvStatus3.setText(text);
                        holder.tvStatus3.setVisibility(View.VISIBLE);
                        holder.tvStatus3.setBackgroundResource(R.drawable.status_border_background);
                        break;
                }
            }
        });

        holder.itemView.setOnClickListener(v -> listener.onPetClick(pet));
    }

    @Override
    public int getItemCount() {
        return petList.size();
    }

    public static class PetViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPetImage;
        TextView tvPetName, tvVetName;
        TextView tvStatus1, tvStatus2, tvStatus3;

        public PetViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPetImage = itemView.findViewById(R.id.imagePet);
            tvPetName = itemView.findViewById(R.id.textPetName);
            tvVetName = itemView.findViewById(R.id.textVetName);
            tvStatus1 = itemView.findViewById(R.id.status1);
            tvStatus2 = itemView.findViewById(R.id.status2);
            tvStatus3 = itemView.findViewById(R.id.status3);
        }

        public void clearStatusViews() {
            tvStatus1.setVisibility(View.GONE);
            tvStatus2.setVisibility(View.GONE);
            tvStatus3.setVisibility(View.GONE);
        }
    }
}